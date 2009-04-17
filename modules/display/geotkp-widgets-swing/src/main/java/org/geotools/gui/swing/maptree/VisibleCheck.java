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
package org.geotools.gui.swing.maptree;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import org.geotools.gui.swing.resource.IconBundle;


/**
 * Component used to present layer visibility
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
final class VisibleCheck extends JCheckBox {

    private static final ImageIcon ICO_VISIBLE = IconBundle.getInstance().getIcon("16_visible");
    private static final ImageIcon ICO_NOVISIBLE = IconBundle.getInstance().getIcon("16_novisible");

    @Override
    public void paintComponent(Graphics g) {
        int x = (getWidth() - 16) / 2;
        int y = (getHeight() - 16) / 2;
        g.drawImage((isSelected()) ? ICO_VISIBLE.getImage() : ICO_NOVISIBLE.getImage(), x, y, this);
    }
}
