package com.armedia.acm.form.config;

/*-
 * #%L
 * ACM Service: Form Configuration
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

public interface FormUrl
{
    /**
     * Retrieve the form server url based on the form name.
     * 
     * @param formName
     * @return
     */
    public String getNewFormUrl(String formName, boolean plain);

    /**
     * Retrieve the form server url for the PDF attachment base on the form name.
     * 
     * @param formName
     * @param docId
     * @return
     */
    public String getPdfRenditionUrl(String formName, String docId);

    public String enableFrevvoFormEngine(String formName);

    public String getProtocol();

    public String getHost();

    public String getPort();

    public Integer getPortAsInteger();

    public String getInternalProtocol();

    public String getInternalHost();

    public String getInternalPort();

    public Integer getInternalPortAsInteger();
}
