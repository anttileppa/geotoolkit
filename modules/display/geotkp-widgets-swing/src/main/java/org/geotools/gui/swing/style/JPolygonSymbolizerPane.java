/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.style;

import org.geotools.gui.swing.resource.MessageBundle;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotools.map.MapLayer;
import org.geotools.style.StyleConstants;

import org.jdesktop.swingx.JXTitledPanel;
import org.opengis.style.PolygonSymbolizer;

/**
 * Polygon symbolizer edition panel
 * 
 * @author  Johann Sorel
 */
public class JPolygonSymbolizerPane extends StyleElementEditor<PolygonSymbolizer> {

    
    private MapLayer layer = null;

    /** Creates new form LineStylePanel
     */
    public JPolygonSymbolizerPane() {
        initComponents();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(MapLayer layer) {
        this.layer = layer;
        guiGeom.setLayer(layer);
        guiFill.setLayer(layer);
        guiStroke.setLayer(layer);
        guiOffset.setLayer(layer);
        guiUOM.setLayer(layer);
        guiDisp.setLayer(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void parse(PolygonSymbolizer symbol) {
        if (symbol != null) {
            guiGeom.setGeom(symbol.getGeometryPropertyName());
            guiFill.parse(symbol.getFill());
            guiStroke.parse(symbol.getStroke());
            guiOffset.parse(symbol.getPerpendicularOffset());
            guiUOM.parse(symbol.getUnitOfMeasure());
            guiDisp.parse(symbol.getDisplacement());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PolygonSymbolizer create() {
        return getStyleFactory().polygonSymbolizer(
                "PolygonSymbolizer",
                guiGeom.getGeom(),
                StyleConstants.DEFAULT_DESCRIPTION,
                guiUOM.create(),
                guiStroke.create(), 
                guiFill.create(), 
                guiDisp.create(), 
                guiOffset.create() );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXTitledPanel1 = new JXTitledPanel();
        guiStroke = new JStrokePane();
        jXTitledPanel2 = new JXTitledPanel();
        guiFill = new JFillPane();
        jXTitledPanel3 = new JXTitledPanel();
        guiOffset = new JOffSetPane();
        guiDisp = new JDisplacementPane();
        jXTitledPanel4 = new JXTitledPanel();
        guiGeom = new JGeomPane();
        guiUOM = new JUOMPane();

        setOpaque(false);

        jXTitledPanel1.setBorder(BorderFactory.createEtchedBorder());
        jXTitledPanel1.setTitle(MessageBundle.getString("stroke")); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        jXTitledPanel1.getContentContainer().add(guiStroke);

        jXTitledPanel2.setBorder(BorderFactory.createEtchedBorder());
        jXTitledPanel2.setTitle(MessageBundle.getString("fill")); // NOI18N
        jXTitledPanel2.getContentContainer().setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        jXTitledPanel2.getContentContainer().add(guiFill);

        jXTitledPanel3.setBorder(BorderFactory.createEtchedBorder());
        jXTitledPanel3.setTitle(MessageBundle.getString("displacement")); // NOI18N
        jXTitledPanel3.getContentContainer().setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        jXTitledPanel3.getContentContainer().add(guiOffset);
        jXTitledPanel3.getContentContainer().add(guiDisp);


        jXTitledPanel4.setBorder(BorderFactory.createEtchedBorder());
        jXTitledPanel4.setTitle(MessageBundle.getString("general")); // NOI18N
        GroupLayout jXTitledPanel4Layout = new GroupLayout(jXTitledPanel4.getContentContainer());
        jXTitledPanel4.getContentContainer().setLayout(jXTitledPanel4Layout);
        jXTitledPanel4Layout.setHorizontalGroup(
            jXTitledPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jXTitledPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXTitledPanel4Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiGeom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiUOM, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(187, Short.MAX_VALUE))
        );

        jXTitledPanel4Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiGeom, guiUOM});

        jXTitledPanel4Layout.setVerticalGroup(
            jXTitledPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jXTitledPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiGeom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiUOM, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jXTitledPanel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jXTitledPanel2, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
            .addComponent(jXTitledPanel1, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
            .addComponent(jXTitledPanel3, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jXTitledPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel3, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JDisplacementPane guiDisp;
    private JFillPane guiFill;
    private JGeomPane guiGeom;
    private JOffSetPane guiOffset;
    private JStrokePane guiStroke;
    private JUOMPane guiUOM;
    private JXTitledPanel jXTitledPanel1;
    private JXTitledPanel jXTitledPanel2;
    private JXTitledPanel jXTitledPanel3;
    private JXTitledPanel jXTitledPanel4;
    // End of variables declaration//GEN-END:variables
}
