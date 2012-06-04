/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.cs;

import java.util.Map;
import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.geometry.MismatchedDimensionException;

import org.geotoolkit.math.XMath;
import org.geotoolkit.measure.Units;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.IdentifiedObjects;


/**
 * A 1-, 2-, or 3-dimensional coordinate system. Gives the position of points relative to
 * orthogonal straight axes in the 2- and 3-dimensional cases. In the 1-dimensional case,
 * it contains a single straight coordinate axis. In the multi-dimensional case, all axes
 * shall have the same length unit of measure. A {@code CartesianCS} shall have one,
 * two, or three {@linkplain #getAxis axis}.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultGeocentricCRS  Geocentric},
 *   {@link org.geotoolkit.referencing.crs.DefaultProjectedCRS   Projected},
 *   {@link org.geotoolkit.referencing.crs.DefaultEngineeringCRS Engineering},
 *   {@link org.geotoolkit.referencing.crs.DefaultImageCRS       Image}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see DefaultAffineCS
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultCartesianCS extends DefaultAffineCS implements CartesianCS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -6182037957705712945L;

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#EASTING Easting}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#NORTHING Northing}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS PROJECTED = new DefaultCartesianCS(
                    name(Vocabulary.Keys.PROJECTED),
                    DefaultCoordinateSystemAxis.EASTING,
                    DefaultCoordinateSystemAxis.NORTHING);

    /**
     * A three-dimensional Cartesian CS with geocentric
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_X x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_Y y}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_Z z}</var>
     * axis in metres.
     *
     * @see DefaultSphericalCS#GEOCENTRIC
     */
    public static final DefaultCartesianCS GEOCENTRIC = new DefaultCartesianCS(
                    name(Vocabulary.Keys.GEOCENTRIC),
                    DefaultCoordinateSystemAxis.GEOCENTRIC_X,
                    DefaultCoordinateSystemAxis.GEOCENTRIC_Y,
                    DefaultCoordinateSystemAxis.GEOCENTRIC_Z);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#X x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#Y y}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS GENERIC_2D = new DefaultCartesianCS(
                    name(Vocabulary.Keys.CARTESIAN_2D),
                    DefaultCoordinateSystemAxis.X,
                    DefaultCoordinateSystemAxis.Y);

    /**
     * A three-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#X x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#Y y}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#Z z}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS GENERIC_3D = new DefaultCartesianCS(
                    name(Vocabulary.Keys.CARTESIAN_3D),
                    DefaultCoordinateSystemAxis.X,
                    DefaultCoordinateSystemAxis.Y,
                    DefaultCoordinateSystemAxis.Z);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#COLUMN column}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#ROW row}</var>
     * axis.
     */
    public static final DefaultCartesianCS GRID = new DefaultCartesianCS(
                    name(Vocabulary.Keys.GRID),
                    DefaultCoordinateSystemAxis.COLUMN,
                    DefaultCoordinateSystemAxis.ROW);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain DefaultCoordinateSystemAxis#DISPLAY_X display x}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#DISPLAY_Y display y}</var>
     * axis.
     *
     * @since 2.2
     */
    public static final DefaultCartesianCS DISPLAY = new DefaultCartesianCS(
                    name(Vocabulary.Keys.DISPLAY),
                    DefaultCoordinateSystemAxis.DISPLAY_X,
                    DefaultCoordinateSystemAxis.DISPLAY_Y);

    /**
     * The unit for measuring distance in this coordinate system, or {@code null} if none.
     * Will be computed only when first needed.
     */
    private transient volatile Unit<?> distanceUnit;

    /**
     * Converters from {@linkplain CoordinateSystemAxis#getUnit axis units} to
     * {@linkplain #getDistanceUnit() distance unit}. Will be constructed only
     * when first needed.
     */
    private transient volatile UnitConverter[] converters;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultCartesianCS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new coordinate system with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param cs The coordinate system to copy.
     *
     * @since 2.2
     */
    public DefaultCartesianCS(final CartesianCS cs) {
        super(cs);
        ensurePerpendicularAxis();
    }

    /**
     * Constructs a two-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     */
    public DefaultCartesianCS(final String               name,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1)
    {
        super(name, axis0, axis1);
        ensurePerpendicularAxis();
    }

    /**
     * Constructs a three-dimensional coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public DefaultCartesianCS(final String               name,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1,
                              final CoordinateSystemAxis axis2)
    {
        super(name, axis0, axis1, axis2);
        ensurePerpendicularAxis();
    }

    /**
     * Constructs a two-dimensional coordinate system from a set of properties.
     * The properties map is given unchanged to the
     * {@linkplain AbstractCS#AbstractCS(Map,CoordinateSystemAxis[]) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     */
    public DefaultCartesianCS(final Map<String,?>   properties,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1)
    {
        super(properties, axis0, axis1);
        ensurePerpendicularAxis();
    }

    /**
     * Constructs a three-dimensional coordinate system from a set of properties.
     * The properties map is given unchanged to the
     * {@linkplain AbstractCS#AbstractCS(Map,CoordinateSystemAxis[]) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param axis0 The first axis.
     * @param axis1 The second axis.
     * @param axis2 The third axis.
     */
    public DefaultCartesianCS(final Map<String,?>   properties,
                              final CoordinateSystemAxis axis0,
                              final CoordinateSystemAxis axis1,
                              final CoordinateSystemAxis axis2)
    {
        super(properties, axis0, axis1, axis2);
        ensurePerpendicularAxis();
    }

    /**
     * For {@link #usingUnit} and {@link PredefinedCS#rightHanded} usage only.
     */
    DefaultCartesianCS(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        super(properties, axis);
        ensurePerpendicularAxis();
    }

    /**
     * Ensures that all axis are perpendicular.
     */
    private void ensurePerpendicularAxis() throws IllegalArgumentException {
        final int dimension = getDimension();
        for (int i=0; i<dimension; i++) {
            final AxisDirection axis0 = getAxis(i).getDirection();
            for (int j=i; ++j<dimension;) {
                final AxisDirection axis1 = getAxis(j).getDirection();
                final double angle = DefaultCoordinateSystemAxis.getAngle(axis0, axis1);
                if (Math.abs(Math.abs(angle) - 90) > DirectionAlongMeridian.EPS) {
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.NON_PERPENDICULAR_AXIS_$2, axis0.name(), axis1.name()));
                }
            }
        }
    }

    /**
     * Returns a Geotk coordinate system implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultCartesianCS castOrCopy(final CartesianCS object) {
        return (object == null) || (object instanceof DefaultCartesianCS)
                ? (DefaultCartesianCS) object : new DefaultCartesianCS(object);
    }

    /**
     * Computes the distance between two points.
     *
     * @param  coord1 Coordinates of the first point.
     * @param  coord2 Coordinates of the second point.
     * @return The distance between {@code coord1} and {@code coord2}.
     * @throws UnsupportedOperationException if this coordinate system can't compute distances.
     * @throws MismatchedDimensionException if a coordinate doesn't have the expected dimension.
     */
    @Override
    public Measure distance(final double[] coord1, final double[] coord2)
            throws UnsupportedOperationException, MismatchedDimensionException
    {
        ensureDimensionMatch("coord1", coord1);
        ensureDimensionMatch("coord2", coord2);
        final Unit<?> unit = getDistanceUnit();
        UnitConverter[] converters = this.converters; // Avoid the need for synchronization.
        if (converters == null) {
            converters = new UnitConverter[getDimension()];
            for (int i=0; i<converters.length; i++) {
                final Unit<?> axisUnit = getAxis(i).getUnit();
                try {
                    converters[i] = axisUnit.getConverterToAny(unit);
                } catch (ConversionException e) {
                    throw new UnsupportedOperationException(Errors.format(
                            Errors.Keys.INCOMPATIBLE_UNIT_$1, axisUnit), e);
                }
            }
            this.converters = converters;
        }
        final double[] delta = new double[converters.length];
        for (int i=0; i<converters.length; i++) {
            final UnitConverter  c = converters[i];
            delta[i] = c.convert(coord1[i]) - c.convert(coord2[i]);
        }
        return new Measure(XMath.magnitude(delta), unit);
    }

    /**
     * Suggests an unit for measuring distances in this coordinate system. The default
     * implementation scans all {@linkplain CoordinateSystemAxis#getUnit() axis units},
     * then returns the "largest" one (e.g. kilometre instead of metre).
     *
     * @return Suggested distance unit.
     */
    private Unit<?> getDistanceUnit() {
        Unit<?> unit = distanceUnit;  // Avoid the need for synchronization.
        if (unit == null) {
            double maxScale = 0;
            for (int i=getDimension(); --i>=0;) {
                final Unit<?> candidate = getAxis(i).getUnit();
                if (candidate != null) {
                    final double scale = Math.abs(Units.toStandardUnit(candidate));
                    if (scale > maxScale) {
                        unit = candidate;
                        maxScale = scale;
                    }
                }
            }
            distanceUnit = unit;
        }
        return unit;
    }

    /**
     * Returns a new coordinate system with the same properties than the current one except for
     * axis units.
     *
     * @param  unit The unit for the new axis.
     * @return A coordinate system with axis using the specified units.
     * @throws IllegalArgumentException If the specified unit is incompatible with the expected one.
     *
     * @since 2.2
     */
    @Override
    public DefaultCartesianCS usingUnit(final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axes;
        try {
            axes = axisUsingUnit(unit);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_$1, unit), e);
        }
        if (axes == null) {
            return this;
        }
        return new DefaultCartesianCS(IdentifiedObjects.getProperties(this, null), axes);
    }
}
