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
package org.geotoolkit.gui.swing.style;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Graphic;

/**
 * Graphic panel
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JGraphicPane extends StyleElementEditor<Graphic> {

    private MapLayer layer = null;

    /** Creates new form JGraphicPanel */
    public JGraphicPane() {
        super(Graphic.class);
        initComponents();
        init();
    }

    private void init() {
        guiAlpha.setModel(1d, 0d, 1d, 0.1d);
        guiRotation.setModel(1d, 0d, 360d, 1d);
        guiSize.setModel(1d, 0d, Double.MAX_VALUE, 1d);
    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiDisplacement.setLayer(layer);
        guiAlpha.setLayer(layer);
        guiRotation.setLayer(layer);
        guiSize.setLayer(layer);
        guiGraphics.setLayer(layer);
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(final Graphic graphic) {

        if (graphic != null) {
            guiDisplacement.parse(graphic.getDisplacement());
            guiAlpha.parse(graphic.getOpacity());
            guiRotation.parse(graphic.getRotation());
            guiSize.parse(graphic.getSize());
            guiGraphics.parse(graphic.graphicalSymbols());
        }
    }

    @Override
    public Graphic create() {
        return getStyleFactory().graphic(
                guiGraphics.create(), 
                guiAlpha.create(), 
                guiSize.create(), 
                guiRotation.create(),
                null, 
                guiDisplacement.create());
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{guiLabelAlpha,guiLabelRotation,guiLabelSize,guiDisplacement};
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        guiLabelSize = new JLabel();
        guiLabelRotation = new JLabel();
        guiLabelAlpha = new JLabel();
        guiDisplacement = new JDisplacementPane();
        guiSize = new JNumberExpressionPane();
        guiRotation = new JNumberExpressionPane();
        guiAlpha = new JNumberExpressionPane();
        guiGraphics = new JGraphicSymbolTable();

        setOpaque(false);

        jPanel1.setOpaque(false);

        guiLabelSize.setText(MessageBundle.getString("size")); // NOI18N

        guiLabelRotation.setText(MessageBundle.getString("rotation")); // NOI18N

        guiLabelAlpha.setText(MessageBundle.getString("opacity")); // NOI18N

        guiDisplacement.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JGraphicPane.this.propertyChange(evt);
            }
        });

        guiSize.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JGraphicPane.this.propertyChange(evt);
            }
        });

        guiRotation.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JGraphicPane.this.propertyChange(evt);
            }
        });

        guiAlpha.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JGraphicPane.this.propertyChange(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(guiLabelAlpha)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(guiLabelRotation)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(guiLabelSize)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(guiDisplacement, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiLabelAlpha, guiLabelRotation, guiLabelSize});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiLabelSize)
                    .addComponent(guiSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiLabelRotation)
                    .addComponent(guiRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(guiLabelAlpha)
                    .addComponent(guiAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiDisplacement, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiAlpha, guiLabelAlpha});

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiLabelRotation, guiRotation});

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiLabelSize, guiSize});

        guiGraphics.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JGraphicPane.this.propertyChange(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
            .addComponent(guiGraphics, GroupLayout.PREFERRED_SIZE, 181, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiGraphics, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void propertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        // TODO add your handling code here:
        if (PROPERTY_UPDATED.equalsIgnoreCase(evt.getPropertyName())) {            
            firePropertyChange(PROPERTY_UPDATED, null, create());
            parse(create());
        }
    }//GEN-LAST:event_propertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JNumberExpressionPane guiAlpha;
    private JDisplacementPane guiDisplacement;
    private JGraphicSymbolTable guiGraphics;
    private JLabel guiLabelAlpha;
    private JLabel guiLabelRotation;
    private JLabel guiLabelSize;
    private JNumberExpressionPane guiRotation;
    private JNumberExpressionPane guiSize;
    private JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
