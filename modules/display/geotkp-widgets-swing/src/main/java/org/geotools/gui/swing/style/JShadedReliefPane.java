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
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotools.gui.swing.style.StyleElementEditor;
import org.geotools.map.MapLayer;
import org.opengis.style.ShadedRelief;

/**
 * ShadedRelief panel
 * 
 * @author  Johann Sorel
 */
public class JShadedReliefPane extends StyleElementEditor<ShadedRelief>{

    private MapLayer layer = null;

    /** 
     * Creates new form JFillPanel 
     */
    public JShadedReliefPane() {
        initComponents();
    }

    @Override
    public void setLayer(MapLayer layer) {
        this.layer = layer;
        guiFactor.setLayer(layer);
    }

    @Override
    public MapLayer getLayer(){
        return layer;
    }
    
    @Override
    public void parse(ShadedRelief relief) {
        if (relief != null) {
            guiFactor.parse(relief.getReliefFactor());
            guiBrightness.setSelected(relief.isBrightnessOnly());
        }
    }

    public ShadedRelief create() {

        return getStyleFactory().shadedRelief(
                guiFactor.create(),
                guiBrightness.isSelected());
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new JLabel();
        guiBrightness = new JCheckBox();
        guiFactor = new JNumberExpressionPane();

        setOpaque(false);


        jLabel1.setText(MessageBundle.getString("factor")); // NOI18N
        guiBrightness.setText(MessageBundle.getString("brightnessonly")); // NOI18N
        guiBrightness.setOpaque(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiFactor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(guiBrightness)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(guiFactor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiBrightness))
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiFactor, jLabel1});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox guiBrightness;
    private JNumberExpressionPane guiFactor;
    private JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
