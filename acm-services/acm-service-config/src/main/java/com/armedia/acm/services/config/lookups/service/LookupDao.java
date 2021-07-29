package com.armedia.acm.services.config.lookups.service;

/*-
 * #%L
 * ACM Service: Config
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;

import java.io.IOException;
import java.util.Map;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public interface LookupDao
{
    /**
     * Returns all the lookups as json.
     */
    String getMergedLookups();

    /**
     * Returns {@link AcmLookup} with the given name. Returns null if no such lookup is defined.
     *
     * @param name
     *            the name of the lookup to find
     * @return the {@link AcmLookup} found
     */
    AcmLookup<?> getLookupByName(String name);

    /**
     * Returns all the lookups as json, with the updated lookup.
     *
     * @param lookupDefinition
     *            the {@link LookupDefinition} for the lookup to update
     * @return all the updated lookups as json
     * @throws InvalidLookupException
     *             when the json is invalid or when null or duplicate keys or values exist
     * @throws IOException
     *             when the underlying store cannot be accessed
     */
    String saveLookup(LookupDefinition lookupDefinition) throws InvalidLookupException, IOException, AcmResourceNotModifiableException;

    /**
     * Delete lookup with given name.
     *
     * @param name
     *            the {@link name} for the lookup to be deleted
     * @return all the updated lookups as json
     * @throws AcmResourceNotFoundException
     *             when the lookup cannot be found
     * @throws AcmResourceNotModifiableException
     *             when the lookup cannot be modified
     * @throws IOException
     *             when the underlying store cannot be accessed
     */
    String deleteLookup(String name, String lookupType) throws AcmResourceNotFoundException, AcmResourceNotModifiableException, IOException;

    /**
     * Delete lookup with given name.
     *
     * @param subLookupName
     *            the {@link subLookupName} for the lookup to be deleted
     * @param parentName
     *            the {@link parentName} for the lookup to be deleted
     * @param lookupDefinition
     *            the {@link LookupDefinition} for the lookup to be deleted
     * @return all the updated lookups as json
     * @throws AcmResourceNotFoundException
     *             when the lookup cannot be found
     * @throws AcmResourceNotModifiableException
     *             when the lookup cannot be modified
     * @throws IOException
     *             when the underlying store cannot be accessed
     */
    String deleteSubLookup(String subLookupName, String parentName, LookupDefinition lookupDefinition)
            throws AcmResourceNotFoundException, AcmResourceNotModifiableException, IOException;

    Map<String, Object> convertInAcmLookupMap(Map<String, Object> lookups);
}
