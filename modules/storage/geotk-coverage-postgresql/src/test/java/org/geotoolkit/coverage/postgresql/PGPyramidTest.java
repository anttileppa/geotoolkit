/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.postgresql;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import static org.geotoolkit.coverage.postgresql.PGCoverageStoreFactory.*;
import org.geotoolkit.data.multires.DefiningMosaic;
import org.geotoolkit.data.multires.DefiningPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGPyramidTest extends org.geotoolkit.test.TestBase {

    private PGCoverageStore store;

    private static ParameterValueGroup params;

    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = System.getProperty("user.home");
        path += "/.geotoolkit.org/test-pgcoverage.properties";
        final File f = new File(path);
        Assume.assumeTrue(f.exists());
        final Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        params = Parameters.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false);
    }

    public PGPyramidTest(){
    }

    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        final PGCoverageStoreFactory factory = (PGCoverageStoreFactory) DataStores.getFactoryById("pgraster");
        try{
            store = factory.create(params);
        }catch(DataStoreException ex){
            //it may already exist
            store = factory.open(params);
        }

        for (GridCoverageResource r : DataStores.flatten(store, true, GridCoverageResource.class)) {
            store.remove(r);
        }
        assertTrue(store.getNames().isEmpty());
    }

    @Test
    public void testInsertUpdateDelete() throws DataStoreException, VersioningException, IOException {
        reload();

        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(CommonCRS.WGS84.geographic());
        final Dimension dimension = new Dimension(20, 20);
        upperLeft.setOrdinate(0, -90);
        upperLeft.setOrdinate(1, +180);
        PyramidalCoverageResource cref;
        Pyramid pyramid;
        Mosaic mosaic;
        RenderedImage image;

        final GenericName name = NamesExt.create(null, "versLayer");
        store.add(new DefiningCoverageResource(name));

        //create version 1 -----------------------------------------------------
        cref = (PyramidalCoverageResource) store.findResource(name.toString());
        assertNotNull(cref);

        //test create pyramid
        pyramid = (Pyramid) cref.createModel(new DefiningPyramid(CommonCRS.WGS84.geographic()));
        assertEquals(1,cref.getModels().size());

        //test create mosaic
        mosaic = pyramid.createMosaic(
                new DefiningMosaic(null, upperLeft, 1, dimension, new Dimension(1, 4)));
        pyramid = Pyramids.getPyramid(cref, pyramid.getIdentifier());
        assertEquals(1,pyramid.getMosaics().size());

        //test insert tile
        mosaic.writeTiles(Stream.of(
                new DefaultImageTile(createImage(dimension, Color.RED),    new Point(0, 0)),
                new DefaultImageTile(createImage(dimension, Color.GREEN),  new Point(0, 1)),
                new DefaultImageTile(createImage(dimension, Color.BLUE),   new Point(0, 2)),
                new DefaultImageTile(createImage(dimension, Color.YELLOW), new Point(0, 3)))
                , null);
        image = ((ImageTile) mosaic.getTile(0, 0)).getImage();
        assertImageColor(image, Color.RED);
        image = ((ImageTile) mosaic.getTile(0, 1)).getImage();
        assertImageColor(image, Color.GREEN);
        image = ((ImageTile) mosaic.getTile(0, 2)).getImage();
        assertImageColor(image, Color.BLUE);
        image = ((ImageTile) mosaic.getTile(0, 3)).getImage();
        assertImageColor(image, Color.YELLOW);

        //test delete tile
        mosaic.deleteTile(0, 1);
        assertNotNull(((ImageTile) mosaic.getTile(0, 2)).getInput());
        mosaic.deleteTile(0, 2);
        assertNull(((ImageTile) mosaic.getTile(0, 2)).getInput());

        //test update tile
        mosaic.writeTiles(Stream.of(new DefaultImageTile(createImage(dimension, Color.PINK), new Point(0, 3))), null);
        image = ((ImageTile) mosaic.getTile(0, 3)).getImageReader().read(3);
        assertImageColor(image, Color.PINK);

        //test delete mosaic
        pyramid.deleteMosaic(mosaic.getIdentifier());
        pyramid = Pyramids.getPyramid(cref, pyramid.getIdentifier());
        assertTrue(pyramid.getMosaics().isEmpty());

        //test delete pyramid
        cref.removeModel(pyramid.getIdentifier());
        assertTrue(cref.getModels().isEmpty());

    }

    @Test
    public void testSampleDimensions() throws DataStoreException, VersioningException, IOException, TransformException {
        reload();

        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(CommonCRS.WGS84.geographic());
        final Dimension dimension = new Dimension(20, 20);
        upperLeft.setOrdinate(0, -90);
        upperLeft.setOrdinate(1, +180);
        PyramidalCoverageResource cref;
        Pyramid pyramid;
        Mosaic mosaic;
        BufferedImage image;

        final GenericName name = NamesExt.create(null, "sampleTestLayer");
        store.add(new DefiningCoverageResource(name));

        //create version 1 -----------------------------------------------------
        cref = (PyramidalCoverageResource) store.findResource(name.toString());
        assertNotNull(cref);

        //test create pyramid
        pyramid = (Pyramid) cref.createModel(new DefiningPyramid(CommonCRS.WGS84.geographic()));
        assertEquals(1,cref.getModels().size());

        final List<GridSampleDimension> dimensions = new LinkedList<>();
        // dim 1
        final Category dataCat = new Category("data", new Color[]{Color.WHITE, Color.BLACK},
                NumberRange.create(1, true, 100, true), NumberRange.create(-50.0, true, 45.6, true));
        final Category nodataCat = new Category(
                Vocabulary.formatInternational(Vocabulary.Keys.Nodata), new Color(0,0,0,0), Double.NaN);
        final GridSampleDimension dim1 = new GridSampleDimension("dim0",new Category[]{dataCat,nodataCat}, Units.CELSIUS);
        dimensions.add(0, dim1);

        // dim 2
        final Category dataCat2 = new Category("data", new Color[]{Color.WHITE, Color.BLACK}, 1, 55, 2.0, 0.0);
        final Category nodataCat2 = Category.NODATA;
        final GridSampleDimension dim2 = new GridSampleDimension("dim1",new Category[]{dataCat2,nodataCat2}, Units.METRE);
        dimensions.add(1, dim2);

        //test create SampleDimensions
        cref.setGridSampleDimensions(dimensions);

        List<GridSampleDimension> resultSamples = cref.getGridSampleDimensions();
        assertNotNull(resultSamples);
        assertEquals(2, resultSamples.size());

        GridSampleDimension resultDim1 = resultSamples.get(0);
        GridSampleDimension resultDim2 = resultSamples.get(1);
        assertNotNull(resultDim1);
        assertNotNull(resultDim2);

        assertEquals("dim0", resultDim1.getDescription().toString());
        assertEquals("dim1", resultDim2.getDescription().toString());
        assertEquals(Units.CELSIUS, resultDim1.getUnits());
        assertEquals(Units.METRE, resultDim2.getUnits());


        List<Category> resultCat1 = resultDim1.getCategories();
        List<Category> resultCat2 = resultDim2.getCategories();
        assertEquals(2, resultCat1.size());
        assertEquals(2, resultCat2.size());
        assertCategoryEquals(dataCat, resultCat1);
        assertCategoryEquals(nodataCat, resultCat1);
        assertCategoryEquals(dataCat2, resultCat2);
        assertCategoryEquals(nodataCat2, resultCat2);
    }

    private void assertCategoryEquals(Category expected, List<Category> result) {
        boolean found = false;
        for (Category resultCategory : result) {
            if (resultCategory.getName().toString().equals(expected.getName().toString())) {
                assertArrayEquals(expected.getColors(), resultCategory.getColors());
                assertEquals(expected.getSampleToGeophysics(), resultCategory.getSampleToGeophysics());

                NumberRange<?> expectedRange = expected.getRange();
                NumberRange<?> resultRange = resultCategory.getRange();

                assertEquals(expectedRange.getMinDouble(), resultRange.getMinDouble(), 0.00000001);
                assertEquals(expectedRange.getMaxDouble(), resultRange.getMaxDouble(), 0.00000001);
                found = true;
            }
        }

        if (!found) {
            fail("Category "+expected.getName().toString()+" not found.");
        }
    }

    private static BufferedImage createImage(Dimension tileSize, Color color){
        final BufferedImage image = new BufferedImage(tileSize.width, tileSize.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, tileSize.width, tileSize.height);
        return image;
    }

    private static void assertImageColor(RenderedImage image, Color color){
        final BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.createGraphics().drawRenderedImage(image, new AffineTransform());
        image = img;
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int refargb = color.getRGB();;

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int argb = ((BufferedImage)image).getRGB(x, y);
                assertEquals(refargb, argb);
            }
        }
    }
}
