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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.map.MapLayer;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JTextExpressionPane extends StyleElementEditor<Expression>{

    /** Creates new form JColorExpressionPane */
    public JTextExpressionPane() {
        super(Expression.class);
        initComponents();
    }
    
    public void setExpressionUnvisible(){
        guiSpecial.setPreferredSize(new Dimension(1, 1));
        guiSpecial.setVisible(false);
    } 

    @Override
    public void setLayer(final MapLayer layer) {
        super.setLayer(layer);
        guiSpecial.setLayer(layer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guiText = new JTextField();
        guiSpecial = new JSpecialExpressionButton();

        setOpaque(false);

        guiText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiTextActionPerformed(evt);
            }
        });

        guiSpecial.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                guiSpecialPropertyChange(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiText, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiSpecial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(guiSpecial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(guiText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiSpecial, guiText});

    }// </editor-fold>//GEN-END:initComponents

    private void guiSpecialPropertyChange(final PropertyChangeEvent evt) {//GEN-FIRST:event_guiSpecialPropertyChange
        if(evt.getPropertyName().equals(JSpecialExpressionButton.EXPRESSION_PROPERTY)) {
            parse(guiSpecial.get());
        }
    }//GEN-LAST:event_guiSpecialPropertyChange

    private void guiTextActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiTextActionPerformed
        firePropertyChange(PROPERTY_TARGET, null, create());
        parse( getFilterFactory().literal( guiText.getText()) );
    }//GEN-LAST:event_guiTextActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JSpecialExpressionButton guiSpecial;
    private JTextField guiText;
    // End of variables declaration//GEN-END:variables

    @Override
    public void parse(final Expression target) {
        if(target != null){
            if(isStatic(target)){
                guiSpecial.parse(null);
                guiText.setText(target.toString());
            }else{
                guiSpecial.parse(target);
            }
        }else{
            guiSpecial.parse(null);
        }
    }

    @Override
    public Expression create() {
        final Expression special = guiSpecial.get();
        if(special != null){
            return special;
        }else{
            return getFilterFactory().literal( guiText.getText() );
        }
    }

}
