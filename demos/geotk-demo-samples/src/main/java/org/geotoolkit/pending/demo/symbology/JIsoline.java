
package org.geotoolkit.pending.demo.symbology;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.style.Fill;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.Font;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import javax.measure.Unit;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.ext.isoline.graphic.IsolineGraphicBuilder;
import org.geotoolkit.display2d.ext.isoline.graphic.ValueExtractor;
import org.geotoolkit.filter.function.math.MathFunctionFactory;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.measure.Units;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.style.MutableStyle;
import org.jdesktop.swingx.JXErrorPane;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Description;
import org.opengis.style.TextSymbolizer;

import static org.geotoolkit.style.StyleConstants.*;

/**
 *
 * @author sorel
 */
public class JIsoline extends JAbstractMapPane{


    public JIsoline() throws DataStoreException, MalformedURLException, URISyntaxException {
        super(createContext());
    }

    private static MapContext createContext() throws DataStoreException, MalformedURLException, URISyntaxException {
        final MapContext context = Styles.createWorldContext(null);
        Map<String,Serializable> params;
        FeatureStore store;
        FeatureCollection fs;
        MutableStyle style;

        MapLayer layer;

        //stations -------------------------------------------------------------
        try{
            store = new ShapefileFeatureStore(JAbstractMapPane.class.getResource("/data/weather/stations2.shp").toURI());
            fs = store.createSession(true).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));
            layer = MapBuilder.createFeatureLayer(fs, createStationStyle());
            layer.setDescription(SF.description("stations", ""));
            layer.setName("stations");
            context.layers().add(layer);
        }catch(Exception ex){
            JXErrorPane.showDialog(ex);
            return context;
        }

        //bonus : temp isoligne-------------------------------------------------
        final MapLayer isoTemplayer = MapBuilder.createFeatureLayer(fs, SF.style());
        final IsolineGraphicBuilder gb = new IsolineGraphicBuilder(new ValueExtractor() {
            private PropertyName prop = FF.property("A_temp");

            @Override
            public Coordinate getValues(RenderingContext2D context, Feature feature) throws IOException {
                final Geometry geom = FeatureExt.getDefaultGeometryValue(feature)
                        .filter(Geometry.class::isInstance)
                        .map(Geometry.class::cast)
                        .orElseThrow(() -> new IllegalArgumentException("No geometry in input feature."));
                final Point centroid = geom.getCentroid();
                final Coordinate c = new Coordinate();
                c.x = centroid.getX();
                c.y = centroid.getY();
                c.z = prop.evaluate(feature, Double.class);
                return c;
            }
        });

        gb.setInterpolateCoverageColor(true);
        gb.setIsoLineStyle(SF.style(
                SF.lineSymbolizer(SF.stroke(Color.BLUE, 1),null),
                SF.textSymbolizer(
                    "Temperature",
                    (String)null,
                    SF.description("Temperature", "Temperature"),
                    Units.POINT,
                    FF.function(MathFunctionFactory.ROUND, FF.property("value")),
                    SF.font(9),
                    SF.linePlacement(FF.literal(3),FF.literal(100),FF.literal(600),true,true,true),
                    SF.halo(Color.WHITE, 0),
                    SF.fill(Color.BLUE))
                    ));
        isoTemplayer.graphicBuilders().add(gb);
        context.layers().add(1,isoTemplayer);
        //----------------------------------------------------------------------

        return context;
    }

    private static MutableStyle createStationStyle(){

        //general informations
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression label = FF.property("A_temp");
        final Font font = SF.font(
                FF.literal("Arial"),
                FONT_STYLE_ITALIC,
                FONT_WEIGHT_BOLD,
                FF.literal(14));
        final LabelPlacement placement = SF.pointPlacement();
        final Halo halo = SF.halo(Color.WHITE, 1);
        final Fill fill = SF.fill(Color.BLUE);

        final TextSymbolizer symbol = SF.textSymbolizer(name, geometry, desc, unit, label, font, placement, halo, fill);
        final MutableStyle style = SF.style(DEFAULT_POINT_SYMBOLIZER,symbol);

        return style;
    }

    @Override
    protected JComponent createConfigPane() {
        return new JPanel();
    }

}
