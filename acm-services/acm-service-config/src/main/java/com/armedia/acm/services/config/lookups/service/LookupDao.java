package com.armedia.acm.services.config.lookups.service;

import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;

import java.io.IOException;

/**
 * Created by bojan.milenkoski on 25.8.2017
 */
public interface LookupDao
{
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
    public String updateLookup(LookupDefinition lookupDefinition) throws InvalidLookupException, IOException;

    /**
     * Returns {@link AcmLookup} with the given name. Returns null if no such lookup is defined.
     *
     * @param name
     *            the name of the lookup to find
     * @return the {@link AcmLookup} found
     */
    public AcmLookup<?> getLookupByName(String name);
}
