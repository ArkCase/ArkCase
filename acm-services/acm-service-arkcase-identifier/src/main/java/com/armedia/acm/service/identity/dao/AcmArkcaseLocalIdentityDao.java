package com.armedia.acm.service.identity.dao;

/*-
 * #%L
 * ACM Service: Arkcase Identity
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.service.identity.exceptions.AcmIdentityException;
import com.armedia.acm.service.identity.model.AcmInstanceIdentityConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

public class AcmArkcaseLocalIdentityDao implements AcmArkcaseIdentityDao
{
    public static final String HASH_ALGORITHM = "SHA-256";

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmInstanceIdentityConfig instanceIdentityConfig;
    private ConfigurationPropertyService configurationPropertyService;

    @Override
    public String getIdentity() throws AcmIdentityException
    {
        String identity = instanceIdentityConfig.getIdentityId();
        byte[] digest = Base64.decodeBase64(instanceIdentityConfig.getDigest());
        /*
         * FIXME using hash is not secure at all, anyone with some knowledge can change identity and change the
         * hash.
         * If needs to be more secure, than we should handle it differently like using encryption or signing the
         * file.
         */
        // check if identity is valid
        if (!Arrays.equals(digest, getIdentityDigest(identity)))
        {
            // identity has been changed
            throw new AcmIdentityException("Identity has been changed.");
        }
        return identity;
    }

    public String createIdentityIfNotExists() throws AcmIdentityException
    {
        String identity = UUID.randomUUID().toString();

        if (instanceIdentityConfig.getIdentityId().isEmpty())
        {
            AcmInstanceIdentityConfig configuration = new AcmInstanceIdentityConfig();
            configuration.setIdentityId(identity);
            configuration.setDateCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            configuration.setDigest(Base64.encodeBase64String(getIdentityDigest(identity)));

            configurationPropertyService.updateProperties(configuration);
        }
        else
        {
            return getIdentity();
        }

        return identity;
    }

    private byte[] getIdentityDigest(String identity)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance(HASH_ALGORITHM);
            return md.digest(identity.getBytes());
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Error generating digest for arkcase identity. [{}]", e.getMessage());
        }

        return new byte[0];
    }

    public AcmInstanceIdentityConfig getInstanceIdentityConfig()
    {
        return instanceIdentityConfig;
    }

    public void setInstanceIdentityConfig(AcmInstanceIdentityConfig instanceIdentityConfig)
    {
        this.instanceIdentityConfig = instanceIdentityConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
