/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.xml.bind.annotation.*;

import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.FormattableObject;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractPyramidSet;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.util.Classes;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLPyramidSet extends AbstractPyramidSet{

    public static final String GEOPHYSICS = "geophysics";
    public static final String NATIVE = "native";
    

    @XmlElement(name="Pyramid")
    private List<XMLPyramid> pyramids;
    @XmlElement(name="FormatName")
    private String formatName;

    @XmlTransient
    private ImageReaderSpi spi;
    @XmlTransient
    private XMLCoverageReference ref;

    public XMLPyramidSet() {
    }

    public XMLPyramidSet(String formatName){
        this.formatName = formatName;
    }

    public String getFormatName() {
        return formatName;
    }

    public XMLCoverageReference getRef() {
        return ref;
    }

    public void setRef(XMLCoverageReference ref) {
        this.ref = ref;
    }

    public ImageReaderSpi getReaderSpi() throws DataStoreException{
        if(spi == null){
            try {
                final ImageReader reader = XImageIO.getReaderByFormatName(formatName, null, Boolean.TRUE, Boolean.TRUE);
                spi = reader.getOriginatingProvider();
                reader.dispose();
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        return spi;
    }

    public List<XMLPyramid> pyramids() {
        if(pyramids == null){
            pyramids = new ArrayList<>();
        }
        return pyramids;
    }

    @Override
    public String getId() {
        return ref.getId();
    }

    @Override
    public Collection<Pyramid> getPyramids() {
        return (Collection)pyramids();
    }

    @Override
    public List<String> getFormats() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Envelope getEnvelope() {
        for(XMLPyramid pyramid : pyramids()){
            final List<XMLMosaic> mosaics = pyramid.mosaics();
            if(!mosaics.isEmpty()){
                return mosaics.get(mosaics.size()-1).getEnvelope();
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return Trees.toString(Classes.getShortClassName(this)+" "+getId(), getPyramids());
    }

    /**
     * Create and register a new pyramid in the set.
     *
     * @param crs
     * @return
     */
    Pyramid createPyramid(CoordinateReferenceSystem crs) {
        final XMLPyramid pyramid = new XMLPyramid();
        pyramid.crs = ((FormattableObject)crs).toString(Convention.WKT1);
        try {
            pyramid.id = URLEncoder.encode(IdentifiedObjects.getIdentifier(crs),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        pyramid.initialize(this);
        pyramids().add(pyramid);
        return pyramid;
    }

}