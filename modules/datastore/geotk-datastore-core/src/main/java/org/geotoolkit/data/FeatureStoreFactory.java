/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
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
 */
package org.geotoolkit.data;

import java.io.Serializable;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Factory used to open instances of FeatureStore.
 * <br/>
 * Factories must be declared using standard java services.
 * <br/>
 * Example, for a factory : x.y.XYFeatureStoreFacotry<br/>
 * A service registration file must be declared like :<br/>
 * <code>META-INF/services/org.geotoolkit.data.FeatureStoreFactory</code>
 * And contain a single line :<br/>
 * <code>x.y.XYFeatureStoreFacotry</code>
 * 
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureStoreFactory {

    /**
     * General information about this factory. 
     * If a given ParameterValueGroup has an identifier parameter set, it's value must
     * be {@linkplain Identifier#getAuthority() identifier authority}, otherwise this
     * factory will not support this ParameterValueGroup.
     *
     * @return The identification of this factory.
     */
    Identification getIdentification();
    
    /**
     * Check if the factory has all requieres resources.
     * Some implementation may requiere some native library or jar file
     * available only at runtime.
     * 
     * @return ConformanceResult
     */
    ConformanceResult availability();

    /**
     * Name suitable for display to end user.
     *
     * <p>
     * A multilingual display name for this data store type.
     * </p>
     *
     * @return A short name suitable for display in a user interface.
     */
    CharSequence getDisplayName();

    /**
     * Description of the factory, suitable for user interface.
     *
     * <p>
     * A multilingual description of this factory.
     * </p>
     *
     * @return A description suitable for display in the user interface.
     */
    CharSequence getDescription();

    /**
     * Description of the requiered parameters to open or open a new instance
     * of feature store.
     * 
     * @return ParameterDescriptorGroup
     */
    ParameterDescriptorGroup getParametersDescriptor();

    /**
     * @see FeatureStoreFactory#canProcess(org.opengis.parameter.ParameterValueGroup) 
     */
    boolean canProcess(Map<String, ? extends Serializable> params);

    /**
     * Check if this factory can process the given set of parameters.
     * 
     * @return true if parameters are supported by this factory.
     */
    boolean canProcess(ParameterValueGroup params);

    /**
     * @see FeatureStoreFactory#open(org.opengis.parameter.ParameterValueGroup)
     */
    FeatureStore open(Map<String, ? extends Serializable> params) throws DataStoreException;

    /**
     * Open a link to the storage location.
     * This method is intended to open an existing storage.
     * <br/>
     * If the purpose is to create a new one storage use the create method :
     * @see FeatureStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     * 
     * @param params
     * @return FeatureStore opened store
     * @throws DataStoreException if parameters are incorrect or connexion failed.
     */
    FeatureStore open(ParameterValueGroup params) throws DataStoreException;

    /**
     * @see FeatureStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     */
    FeatureStore create(Map<String, ? extends Serializable> params) throws DataStoreException;

    /**
     * Create a new storage location.
     * This method is intended to create from scratch a new storage location.
     * <br/>
     * If the purpose is to open an already existing  storage use the open method :
     * @see FeatureStoreFactory#open(org.opengis.parameter.ParameterValueGroup)
     * 
     * @param params
     * @return FeatureStore created store
     * @throws DataStoreException if parameters are incorrect or creation failed.
     */
    FeatureStore create(ParameterValueGroup params) throws DataStoreException;

}
