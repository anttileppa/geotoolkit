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

import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Literal;
import org.opengis.style.Font;

/**
 * Font panel
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JFontPane extends StyleElementEditor<Font>{
    
    private MapLayer layer = null;
    private final Literal[] fontFamilies;
    
    /** Creates new form JfontPanel */
    public JFontPane() {
        super(Font.class);
        initComponents();

        //Initialize family font list with available font family
        final GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fontNames = environment.getAvailableFontFamilyNames();
        final List<String> fontNamesList = Arrays.asList(fontNames);

        final int nbFamilies = fontNamesList.size();
        fontFamilies = new Literal[nbFamilies];
        for (int i=0; i<nbFamilies; i++) {
            fontFamilies[i] = new DefaultLiteral(fontNamesList.get(i));
        }

        init();
    }
    
    private void init(){
        guiSize.setModel(12d, 0d, Double.POSITIVE_INFINITY, 1d);
        guiStyle.setModel(
                StyleConstants.FONT_STYLE_NORMAL,
                StyleConstants.FONT_STYLE_ITALIC,
                StyleConstants.FONT_STYLE_OBLIQUE);
        guiWeight.setModel(
                StyleConstants.FONT_WEIGHT_NORMAL,
                StyleConstants.FONT_WEIGHT_BOLD);

        guiFamily.setModel(fontFamilies);
        guiFamily.setSize(200, guiFamily.getPreferredSize().height);
//        guiFamily.setPrototypeDisplayValueToFirstItem();
    }
    
    @Override
    public void parse(final Font font){        
        
        if(font != null){
            if(!font.getFamily().isEmpty()){
                guiFamily.parse(font.getFamily().get(0));
            }else{
                guiFamily.parse(getFilterFactory().literal("Dialog"));
            }
            guiSize.parse(font.getSize());
            guiStyle.parse(font.getStyle());
            guiWeight.parse(font.getWeight());
        }
    }
    
    @Override
    public Font create(){
        return getStyleFactory().font(
                guiFamily.create(), 
                guiStyle.create(), 
                guiWeight.create(), 
                guiSize.create());
    }
    
    @Override
    public void setLayer(final MapLayer layer){
        guiFamily.setLayer(layer);
        guiSize.setLayer(layer);
        guiStyle.setLayer(layer);
        guiWeight.setLayer(layer);
        this.layer = layer;
    }
    
    @Override
    public MapLayer getLayer(){
        return layer;
    }

    public void setExpressionVisible(final boolean visible) {
        guiFamily.setExpressionVisible(visible);
        guiSize.setExpressionVisible(visible);
        guiStyle.setExpressionVisible(visible);
        guiWeight.setExpressionVisible(visible);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        jLabel1 = new JLabel();
        guiFamily = new JComboExpressionPane();
        jLabel2 = new JLabel();
        guiStyle = new JComboExpressionPane();
        jLabel3 = new JLabel();
        guiSize = new JNumberExpressionPane();
        jLabel4 = new JLabel();
        guiWeight = new JComboExpressionPane();

        setOpaque(false);
        setLayout(new GridBagLayout());

        jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel1.setText(MessageBundle.getString("family")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        add(guiFamily, gridBagConstraints);

        jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel2.setText(MessageBundle.getString("style")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(jLabel2, gridBagConstraints);

        guiStyle.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JFontPane.this.propertyChange(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        add(guiStyle, gridBagConstraints);

        jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel3.setText(MessageBundle.getString("size")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(jLabel3, gridBagConstraints);

        guiSize.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JFontPane.this.propertyChange(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        add(guiSize, gridBagConstraints);

        jLabel4.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel4.setText(MessageBundle.getString("weight")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        add(jLabel4, gridBagConstraints);

        guiWeight.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JFontPane.this.propertyChange(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        add(guiWeight, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void propertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        // TODO add your handling code here:
        if (PROPERTY_TARGET.equalsIgnoreCase(evt.getPropertyName())) {            
            firePropertyChange(PROPERTY_TARGET, null, create());
        }
    }//GEN-LAST:event_propertyChange
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboExpressionPane guiFamily;
    private JNumberExpressionPane guiSize;
    private JComboExpressionPane guiStyle;
    private JComboExpressionPane guiWeight;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
    
}
