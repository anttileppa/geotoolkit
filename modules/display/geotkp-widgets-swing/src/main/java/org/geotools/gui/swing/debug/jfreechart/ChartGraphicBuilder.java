/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.gui.swing.debug.jfreechart;

import java.util.Collection;
import java.util.Collections;
import org.geotools.display.canvas.ReferencedCanvas2D;
import org.geotools.display2d.primitive.GraphicJ2D;
import org.geotools.map.FeatureMapLayer;
import org.geotools.map.GraphicBuilder;
import org.geotools.map.MapLayer;
import org.opengis.display.canvas.Canvas;

/**
 *
 * @author sorel
 */
public class ChartGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    @Override
    public Collection<GraphicJ2D> createGraphics(MapLayer layer, Canvas canvas) {

        if(layer instanceof FeatureMapLayer){
            GraphicJ2D j2d = new ChartFeatureLayerGraphic((ReferencedCanvas2D)canvas, (FeatureMapLayer)layer);
            return Collections.singleton(j2d);

        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

}
