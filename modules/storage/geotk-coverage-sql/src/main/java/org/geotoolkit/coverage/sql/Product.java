/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.sql.SQLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

import org.opengis.util.FactoryException;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.metadata.iso.DefaultMetadata;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.resources.Errors;


/**
 * A coverage product.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public final class Product {
    /**
     * The name of this product.
     */
    private final String name;

    /**
     * Typical resolution in metres, or {@link Double#NaN} if unknown.
     * The value is only approximate.
     */
    private final double spatialResolution;

    /**
     * Typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * For example a product of weekly <cite>Sea Surface Temperature</cite> (SST) coverages
     * may set this field to 7, while a product of monthly SST coverage may set this field
     * to 30. The value is only approximate.
     */
    private final double temporalResolution;

    /**
     * Identifier to an entry in {@code metadata.Metadata} table, or {@code null} if none.
     *
     * @todo not yet used.
     */
    private final String metadata;

    /**
     * The domain for this product, or {@code null} if not yet computed.
     * Will be computed only when first needed.
     */
    private DomainOfProductTable.Entry domain;

    /**
     * The database that produced this entry.
     */
    private final Database database;

    /**
     * Creates a new product.
     *
     * @param name                the product name.
     * @param spatialResolution   typical resolution in metres, or {@link Double#NaN} if unknown.
     * @param temporalResolution  typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * @param metadata            optional entry in {@code metadata.Metadata} table, or {@code null}.
     */
    Product(final Database database, final String name, final double spatialResolution, final double temporalResolution, final String metadata) {
        this.name               = name;
        this.spatialResolution  = spatialResolution;
        this.temporalResolution = temporalResolution;
        this.metadata           = metadata;
        this.database           = database;
    }

    /**
     * Returns a string representation of this product for debugging purpose.
     *
     * @return string representation (may change in any future version).
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns the resources bundle for error messages.
     */
    private Errors errors() {
        return Errors.getResources(database.locale);
    }

    /**
     * Creates metadata (potentially costly operation). Contains:
     *
     * <ul>
     *   <li>A time range encompassing all coverages in this product.</li>
     *   <li>The geographic bounding box. If the CRS used by the database is not geographic
     *       (for example if it is a projected CRS), then this method will transform the product
     *       envelope from the product CRS to a geographic CRS.
     * </ul>
     */
    synchronized final DefaultMetadata createMetadata(final Transaction transaction) throws SQLException, TransformException {
        ensureDomainIsKnown(transaction);
        final MetadataBuilder md = new MetadataBuilder();
        md.addIdentifier(null, name, MetadataBuilder.Scope.RESOURCE);
        if (domain != null) {
            md.addExtent(domain.bbox);
            if (domain.startTime != null && domain.endTime != null) {
                md.addTemporalExtent(Date.from(domain.startTime), Date.from(domain.endTime));
            }
        }
        md.addResolution(spatialResolution);
        md.addTemporalResolution(temporalResolution);
        return md.build(true);
    }

    /**
     * Ensures that the domain of this product is initialized.
     *
     * @throws SQLException if an error occurred while fetching the domain.
     */
    private void ensureDomainIsKnown(final Transaction transaction) throws SQLException {
        assert Thread.holdsLock(this);
        if (domain == null) {
            try (DomainOfProductTable d = new DomainOfProductTable(transaction)) {
                domain = d.query(name);
            }
        }
    }

    synchronized GridGeometry getGridGeometry(final Transaction transaction) throws SQLException, CatalogException, TransformException {
        final List<GridGeometryEntry> geometries;
        try (final GridCoverageTable table = new GridCoverageTable(transaction)) {
            geometries = table.getGridGeometries(name);
        }
        if (geometries.isEmpty()) {
            return null;
        }
        ensureDomainIsKnown(transaction);
        int index = 0;      // TODO: select the "best" geometry.
        return geometries.get(index).getGridGeometry(domain.startTime, domain.endTime);
    }

    /**
     * Returns a reference to every coverages available in this product which intersect the given envelope.
     * If the given envelope is {@code null}, then this method returns the references to every coverages
     * available for this product regardless of their envelope.
     *
     * @param  areaOfInterest  the envelope for filtering the coverages, or {@code null} for no filtering.
     * @return the set of coverages of this product which intersect the given envelope.
     * @throws CatalogException if an error occurred while querying the database.
     */
    List<GridCoverageStack> getCoverageReferences(final Transaction transaction, final Envelope areaOfInterest) throws CatalogException {
        final List<GridCoverageStack> entries;
        try (final GridCoverageTable table = new GridCoverageTable(transaction)) {
            entries = table.find(name, areaOfInterest);
        } catch (SQLException exception) {
            throw new CatalogException(exception);
        } catch (TransformException exception) {
            throw new MismatchedReferenceSystemException(errors()
                    .getString(Errors.Keys.IllegalCoordinateReferenceSystem, exception));
        }
        return entries;
    }

    /**
     * Adds new coverage references in the database.
     */
    void addCoverageReference(final Transaction transaction, final List<NewRaster> rasters) throws CatalogException {
        try (final GridCoverageTable table = new GridCoverageTable(transaction)) {
            for (final NewRaster r : rasters) {
                table.add(name, r);
            }
        } catch (SQLException | FactoryException | TransformException exception) {
            throw new CatalogException(exception);
        }
    }

    static final class NewRaster {
        /**
         * The format name as given by the data store provider. Example: "NetCDF".
         */
        final String driver;

        /**
         * Path to the new file to add.
         */
        final Path path;

        /**
         * 0-based index of the image in the file.
         */
        final int imageIndex;

        /**
         * Grid geometry of the file to add.
         */
        GridGeometry geometry;

        /**
         * Description of bands.
         */
        List<SampleDimension> bands;

        /**
         * Creates information about a new raster to be added to the catalog.
         */
        NewRaster(final String driver, final Path file, final int index) {
            this.driver = driver;
            this.path   = file;
            imageIndex  = index;
        }

        /**
         * Returns a string representation of this raster entry for debugging purpose.
         *
         * @return string representation (may change in any future version).
         */
        @Override
        public String toString() {
            return path.getFileName().toString() + " @ " + imageIndex;
        }

        static List<NewRaster> list(final Path... files) throws DataStoreException {
            final List<NewRaster> rasters = new ArrayList<>(files.length);
            for (final Path file : files) {
                try (final DataStore ds = DataStores.open(file)) {
                    String driver = ds.getProvider().getShortName();
                    collect(driver, file, ds, rasters, 0);
                } catch (TransformException e) {
                    throw new CatalogException(e);
                }
            }
            return rasters;
        }

        /**
         * Adds to the given {@code rasters} list information about all resources found in the given node.
         * This method add recursively all children resources.
         *
         * @param  driver    the format name as given by the data store provider. Example: "NetCDF".
         * @param  file      path to the file, including parent directories and file extension.
         * @param  resource  the resource to add to the given {@code rasters} list.
         * @param  rasters   where to add the information about rasters.
         * @param  index     zero-based index of the image to read.
         */
        private static void collect(final String driver, final Path file, final Resource resource,
                final List<NewRaster> rasters, int index) throws DataStoreException, TransformException
        {
            if (resource instanceof Aggregate) {
                for (final Resource child : ((Aggregate) resource).components()) {
                    collect(driver, file, child, rasters, index++);
                }
            } else if (resource instanceof GridCoverageResource) {
                final NewRaster r = new NewRaster(driver, file, index);
                final GridCoverageResource gr = (GridCoverageResource) resource;
                r.geometry = gr.getGridGeometry();
                r.bands = gr.getSampleDimensions();
                rasters.add(r);
            } else if (resource instanceof org.geotoolkit.storage.coverage.GridCoverageResource) {
                final NewRaster r = new NewRaster(driver, file, index);
                GridCoverageReader reader = ((org.geotoolkit.storage.coverage.GridCoverageResource) resource).acquireReader();
                r.geometry = CoverageUtilities.toSIS(reader.getGridGeometry(r.imageIndex));
                r.bands = CoverageUtilities.toSIS(reader.getSampleDimensions(r.imageIndex));
                reader.dispose();
                rasters.add(r);
            }
        }
    }
}
