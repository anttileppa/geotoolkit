/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.render2d.decoration;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.apache.sis.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.opengis.referencing.operation.TransformException;

/**
 * Abstract Decoration to easely render objective CRS geometries.
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public abstract class AbstractGeometryDecoration extends JPanel implements MapDecoration{

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gui.swing.render2d.decoration");

    protected final List<Geometry> geometries = new ArrayList<>();
    private RenderingContext2D context = null;
    private AffineTransform objToDisp = null;
    protected JMap2D map = null;

    protected AbstractGeometryDecoration(){
        setOpaque(false);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    protected double[] toDisplay(final Coordinate coord){
        double[] coords = new double[]{coord.x,coord.y};
        objToDisp.transform(coords, 0, coords, 0, 1);
        return coords;
    }

    public void setGeometries(final Collection<? extends Geometry> geoms){
        geometries.clear();

        if(geoms != null){
            geometries.addAll(geoms);
        }
        repaint();
    }

    public List<Geometry> getGeometries(){
        return new ArrayList<Geometry>(geometries);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setMap2D(final JMap2D map) {
        this.map = map;

        if(map != null && map.getCanvas() instanceof J2DCanvas){
            context = new RenderingContext2D(map.getCanvas());
        }else{
            context = null;
        }
    }

    @Override
    public JMap2D getMap2D() {
        return map;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        //enable anti-aliasing
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //check if the map has a Java2D canvas
        if(map == null) return;
        final AbstractCanvas2D candidate = map.getCanvas();
        if(!(candidate instanceof J2DCanvas)) return;

        final J2DCanvas canvas = (J2DCanvas) candidate;
        canvas.prepareContext(context,(Graphics2D) g.create(), null);
        objToDisp = context.getObjectiveToDisplay();
        if (objToDisp == null) return;

        final Graphics2D g2 = context.getGraphics();

        paintComponent(g2, context);
    }

    protected void paintComponent(final Graphics2D g2, final RenderingContext2D context){

        final StatelessContextParams params = new StatelessContextParams(null, null);
        final ProjectedGeometry projected = new ProjectedGeometry(params);
        params.update(context);

        //prepare datas for geometry painting
        for(final Geometry geo : geometries){
            if(geo == null) continue;

            CoordinateReferenceSystem dataCrs = null;
            try {
                dataCrs = JTS.findCoordinateReferenceSystem(geo);
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, ex.getMessage(), ex);
            }

            if(dataCrs == null){
                //we assume the geometry is in objective crs
                dataCrs = params.objectiveCRS;
            }

            projected.setDataGeometry(geo, dataCrs);

            try {
                paintGeometry(g2,context, projected);
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    }

    protected abstract void paintGeometry(Graphics2D g2, RenderingContext2D context, ProjectedGeometry geom) throws TransformException;


}
