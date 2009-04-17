/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2007, GeoTools Project Managment Committee (PMC)
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
package org.geotools.gui.swing.go.control.navigation;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geotools.gui.swing.go.GoMap2D;
import org.geotools.gui.swing.go.control.navigation.ZoomInHandler;
import org.geotools.gui.swing.resource.IconBundle;


/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ZoomInAction extends AbstractAction {

    private static final ImageIcon ICON_ZOOM_IN = IconBundle.getInstance().getIcon("16_zoom_in");

    private GoMap2D map = null;

    public ZoomInAction() {
        super("",ICON_ZOOM_IN);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null ) {
            map.setHandler(new ZoomInHandler(map));
//            map.getCanvas().getController().scale(2);
        }
    }

    public GoMap2D getMap() {
        return map;
    }

    public void setMap(GoMap2D map) {
        this.map = map;
        setEnabled(map != null);
    }
}
