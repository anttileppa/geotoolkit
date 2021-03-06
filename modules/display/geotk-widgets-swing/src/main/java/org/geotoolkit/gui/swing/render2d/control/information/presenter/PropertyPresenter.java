/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Johann Sorel
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.render2d.control.information.presenter;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

/**
 * JComponent for Features and Properties.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PropertyPresenter extends AbstractInformationPresenter{

    public PropertyPresenter() {
        super(0);
    }



    @Override
    public JComponent createComponent(Object graphic, RenderingContext2D context, SearchAreaJ2D area) {

        final Object candidate;
        if (graphic instanceof ProjectedFeature) {
            final ProjectedFeature gra = (ProjectedFeature) graphic;
            candidate = gra.getCandidate();
        } else if (graphic instanceof GraphicJ2D) {
            final GraphicJ2D gra = (GraphicJ2D) graphic;
            candidate = gra.getUserObject();
        } else {
            candidate = null;
        }

        if (candidate != null && candidate instanceof Feature) {
            final JFeatureOutLine outline = new JFeatureOutLine();
            outline.setEdited((Feature) candidate);
            // As outline looks to have difficulties computing its better size, we give it an hint by setting row height.
            outline.setRowHeight(36);
            final JScrollPane pane = new JScrollPane(outline);
            pane.setBorder(null);
            return pane;
        }

        return null;
    }

}
