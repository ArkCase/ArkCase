/**
 * 
 */
package com.armedia.acm.services.costsheet.model;

/*-
 * #%L
 * ACM Service: Costsheet
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
 * @author riste.tutureski
 *
 */
public interface CostsheetConstants
{

    /**
     * Object type
     */
    String OBJECT_TYPE = "COSTSHEET";

    /**
     * Statuses
     */
    String DRAFT = "DRAFT";
    String IN_APPROVAL = "IN_APPROVAL";
    String APPROVED = "APPROVED";

    /**
     * Event type (root name)
     */
    String EVENT_TYPE = "com.armedia.acm." + OBJECT_TYPE.toLowerCase();

    String ROOT_FOLDER_KEY = "root.folder";
    String SEARCH_TREE_SORT = "search.tree.sort";

    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";

    // TODO '/acm-config-server-repo' is for temporal compatibility with current configuration.
    String COSTSHEET_STYLESHEET = System.getProperty("user.home") + "/.arkcase/acm/acm-config-server-repo/pdf-stylesheets/costsheet-document.xsl";
    String COSTSHEET_DOCUMENT = "Costsheet";
    String COSTSHEET_FILENAMEFORMAT = "Costsheet.pdf";
}
