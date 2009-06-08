/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.gui.swing;

import java.util.Locale;
import org.junit.*;


/**
 * Tests the {@link DisjointLists}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 */
public final class DisjointListsTest extends WidgetTestCase {
    /**
     * Constructs the test case.
     */
    public DisjointListsTest() {
        super(DisjointLists.class);
    }

    /**
     * Creates the widget. If {@link #displayEnabled} is {@code true}, then the widget is shown.
     */
    @Test
    public void display() {
        final DisjointLists test = new DisjointLists();
        test.addElements(Locale.getAvailableLocales());
        component = test;
        show();
    }
}
