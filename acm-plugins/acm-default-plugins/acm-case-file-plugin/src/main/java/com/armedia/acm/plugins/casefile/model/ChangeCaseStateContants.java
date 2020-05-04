package com.armedia.acm.plugins.casefile.model;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

public interface ChangeCaseStateContants
{
    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    // TODO '/acm-config-server-repo' is for temporal compatibility with current configuration.
    String CHANGE_CASE_STATUS_STYLESHEET = System.getProperty("user.home")
            + "/.arkcase/acm/acm-config-server-repo/pdf-stylesheets/change-case-file-state-document.xsl";
    String CHANGE_CASE_STATUS_DOCUMENT = "CHANGE_CASE";
    String CHANGE_CASE_STATUS_FILENAMEFORMAT = "Change Case Status.pdf";
    String CHANGE_CASE_STATUS = "CHANGE_CASE_STATUS";
}
