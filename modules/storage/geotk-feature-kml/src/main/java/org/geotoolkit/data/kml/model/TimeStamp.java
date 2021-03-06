/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.util.Calendar;

/**
 * <p>This interface maps TimeStamp element.</p>
 *
 * <pre>
 * &lt;element name="TimeStamp" type="kml:TimeStampType" substitutionGroup="kml:AbstractTimePrimitiveGroup"/>
 *
 * &lt;complexType name="TimeStampType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractTimePrimitiveType">
 *          &lt;sequence>
 *              &lt;element ref="kml:when" minOccurs="0"/>
 *              &lt;element ref="kml:TimeStampSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:TimeStampObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="TimeStampSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="TimeStampObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module
 */
public interface TimeStamp extends AbstractTimePrimitive{

    /**
     *
     * @return
     */
    Calendar getWhen();

    /**
     *
     * @param when
     */
    void setWhen(Calendar when);

}
