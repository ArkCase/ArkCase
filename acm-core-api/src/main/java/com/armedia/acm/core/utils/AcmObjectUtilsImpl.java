package com.armedia.acm.core.utils;

/*-
 * #%L
 * ACM Core API
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.AcmObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Set;


/**
 * @author aleksandar.bujaroski
 */
public class AcmObjectUtilsImpl implements AcmObjectUtils
{
    private String packagesToScan;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public Class<? extends AcmObject> getClassFromObjectType(String objectType)
    {
        Object[] packages = Arrays.stream(packagesToScan.split(","))
                .map(it -> StringUtils.substringBeforeLast(it, ".*"))
                .toArray();

        Reflections reflections = new Reflections(packages);

        Set<Class<? extends AcmObject>> acmObjects = reflections.getSubTypesOf(AcmObject.class);
        for (Class acmObject : acmObjects)
        {
            try {
                if(!acmObject.isInterface() && ((AcmObject)acmObject.newInstance()).getObjectType().equals(upperUnderScoreToCamelCase(objectType)))
                    return acmObject;
            }
            catch (Exception e){
                log.error("Error while casting to AcmObject ", e);
            }
        }
        return AcmObject.class;
    }

    /**
     *
     * @param propertyName ex: CaseFile
     * @return upperUnderScore of property ex: CASE_FILE
     */
    private String upperUnderScoreToCamelCase(String propertyName)
    {
        String goodPropertyName = propertyName.replace(" ", "_");
        goodPropertyName = goodPropertyName.replaceAll("(?<!^)([a-z])([A-Z])", "$1_$2");
        goodPropertyName = goodPropertyName.replaceAll("\\+", "_");
        return goodPropertyName.toUpperCase();
    }

    public String getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
