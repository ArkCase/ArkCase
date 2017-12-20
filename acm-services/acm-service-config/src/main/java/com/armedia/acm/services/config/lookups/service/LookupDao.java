package com.armedia.acm.services.config.lookups.service;

import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.services.config.lookups.model.AcmLookup;
import com.armedia.acm.services.config.lookups.model.LookupDefinition;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;

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
     * @param name the name of the lookup to find
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
    String saveLookup(LookupDefinition lookupDefinition) throws InvalidLookupException, IOException;

    String deleteLookup(String name) throws AcmResourceNotFoundException, AcmResourceNotModifiableException, IOException;
}
