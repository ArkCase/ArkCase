package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

/**
 * Created by nick.ferguson on 3/22/2017.
 */
public interface CmisConfigurationConstants
{
    String CMIS_ID = "cmis.id";
    String CMIS_BASEURL = "cmis.baseUrl";
    String CMIS_USERNAME = "cmis.username";
    String CMIS_PASSWORD = "cmis.password";
    String CMIS_USEALFRESCOEXTENSION = "cmis.useAlfrescoExtension";
    String CMIS_ENDPOINT = "ATOM";
    String CMIS_MAXIDLE = "cmis.maxIdle";
    String CMIS_MAXACTIVE = "cmis.maxActive";
    String CMIS_MAXWAIT = "cmis.maxWait";
    String CMIS_MINEVICTIONMILLIS = "cmis.minEvictionMillis";
    String CMIS_EVICTIONCHECKINTERVALMILLIS = "cmis.evictionCheckIntervalMillis";
    String CMIS_RECONNECTCOUNT = "cmis.reconnectCount";
    String CMIS_RECONNECTFREQUENCY = "cmis.reconnectFrequency";
    String CMIS_REPOSITORYID = "cmis.repositoryId";
    String CMIS_VERSIONINGSTATE = "cmis.cmisVersioningState";
}
