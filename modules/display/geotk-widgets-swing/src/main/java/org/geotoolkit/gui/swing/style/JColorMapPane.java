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

import org.geotoolkit.gui.swing.resource.MessageBundle;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.ColorMap;

/**
 * ColorMap panel
 * 
 * @author  Johann Sorel
 * @module pending
 */
public class JColorMapPane extends StyleElementEditor<ColorMap>{

    private MapLayer layer = null;
    private ColorMap colorMap = null;

    /** 
     * Creates new form JFillPanel 
     */
    public JColorMapPane() {
        super(ColorMap.class);
        initComponents();
    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public MapLayer getLayer(){
        return layer;
    }
    
    @Override
    public void parse(final ColorMap map) {
        this.colorMap = map;
        if (map != null) {
//            guiType.setValue(map.getType());            
//            guiEntrys.setEdited(map.getColorMapEntries());
        }
    }

    @Override
    public ColorMap create() {

//        if (colorMap == null) {
//            colorMap = new ColorMapImpl();
//        }

        apply();
        return colorMap;
    }

    public void apply() {
//        if (colorMap != null) {
//            colorMap.setType((Integer)guiType.getValue());
//        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new JLabel();
        guiType = new JSpinner();

        jLabel1.setText(MessageBundle.getString("type")); // NOI18N

        guiType.setModel(new SpinnerNumberModel());
        guiType.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                guiTypeStateChanged(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiType, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiTypeStateChanged(ChangeEvent evt) {//GEN-FIRST:event_guiTypeStateChanged
        // TODO add your handling code here:
        firePropertyChange(PROPERTY_TARGET, null, create());
    }//GEN-LAST:event_guiTypeStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JSpinner guiType;
    private JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
