
package org.geotoolkit.pending.demo.rendering.customgraphic;

import java.awt.BorderLayout;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotoolkit.coverage.io.CoverageIO;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.JNavigationBar;
import org.geotoolkit.gui.swing.render2d.decoration.JClassicNavigationDecoration;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.pending.demo.rendering.reliefShadow.ReliefShadowDemo;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.referencing.operation.TransformException;


public class GraphicDemo {


    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                                   new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    public static void main(String[] args) throws Exception {
        Demos.init();

        final MapContext context = createContext();


        final JMap2D jmap = new JMap2D();
        final JNavigationBar navBar = new JNavigationBar(jmap);
        final J2DCanvas canvas = jmap.getCanvas();
        jmap.getContainer().setContext(context);
        jmap.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.CLASSIC));
        jmap.getCanvas().setVisibleArea(context.getBounds());


        //our custom graphic object
        final SquaresGraphic graphic = new SquaresGraphic(canvas);

        //add the graphic in the scene
        canvas.getContainer().getRoot().getChildren().add(graphic);


        final JFrame frm = new JFrame();
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,jmap);
        panel.add(BorderLayout.NORTH,navBar);

        frm.setContentPane(panel);
        frm.setSize(800, 600);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);

    }

    private static MapContext createContext() throws DataStoreException, URISyntaxException {
        WorldFileImageReader.Spi.registerDefaults(null);

        //create a map context
        final MapContext context = MapBuilder.createContext();

        //create a coverage layer
        File cloudFile = new File(GraphicDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
        final CoverageMapLayer coverageLayer = MapBuilder.createCoverageLayer(cloudFile);

        //add all layers in the context
        context.layers().add(coverageLayer);
        return context;
    }

}
