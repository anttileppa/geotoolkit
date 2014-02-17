/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sos.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v100.ObservationType;
import org.geotoolkit.observation.xml.v100.ProcessType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/om/1.0}Observation"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "observation"
})
@XmlRootElement(name = "ObservationTemplate")
public class ObservationTemplate implements org.geotoolkit.swes.xml.ObservationTemplate {

    @XmlElement(name = "Observation", namespace = "http://www.opengis.net/om/1.0", required = true)
    private ObservationType observation;

    /**
     * An empty constructor used by JAXB
     */
    ObservationTemplate(){
        
    }
    
    /**
     * Build a new ObservationTemplate
     */
    public ObservationTemplate(final ObservationType observation){
        this.observation = observation;
    }
    
    /**
     * Gets the value of the observation property.
     */
    @Override
    public ObservationType getObservation() {
        return observation;
    }

    @Override
    public String getProcedure() {
        if (observation != null && observation.getProcedure() instanceof ProcessType) {
            return ((ProcessType)observation.getProcedure()).getHref();
        }
        return null;
    }

    @Override
    public boolean isComplete() {
        if (observation != null) {
            return observation.isComplete();
        }
        return false;
    }

    @Override
    public boolean isTemplateSpecified() {
        return observation != null;
    }

    @Override
    public void setProcedure(final String id) {
        if (id != null) {
            final ProcessType p = new ProcessType(id);
            if (observation != null) {
                observation.setProcedure(p);
            }
        }
    }
    
    @Override
    public void setName(final String name) {
        if (name != null) {
            if (observation != null) {
                observation.setName(name);
            }
        }
    }
    
    @Override
    public List<String> getObservedProperties() {
        if (observation != null && observation.getObservedProperty() != null) {
            return Arrays.asList(observation.getObservedProperty().getId());
        }
        return new ArrayList<String>();
    }
    
    @Override
    public String getFeatureOfInterest() {
        if (observation != null && observation.getFeatureOfInterest() != null) {
            return observation.getFeatureOfInterest().getId();
        }
        return null;
    }
    
    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ObservationTemplate && super.equals(object)) {
            final ObservationTemplate that = (ObservationTemplate) object;
            return Objects.equals(this.observation, that.observation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.observation != null ? this.observation.hashCode() : 0);
        return hash;
    }
}
