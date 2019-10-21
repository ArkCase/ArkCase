package com.armedia.acm.camelcontext.arkcase.cmis;

/*-
 * #%L
 * acm-camel-context-manager
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

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Aug, 2019
 */
public enum ArkCaseCMISActions
{
    CREATE_DOCUMENT("CreateDocumentQueue"),
    CREATE_FOLDER("CreateFolderQueue"),
    DELETE_DOCUMENT("DeleteDocumentQueue"),
    DELETE_FOLDER("DeleteFolderQueue"),
    MOVE_DOCUMENT("MoveDocumentQueue"),
    MOVE_FOLDER("MoveFolderQueue"),
    COPY_DOCUMENT("CopyDocumentQueue"),
    COPY_FOLDER("CopyFolderQueue"),
    RENAME_DOCUMENT("RenameDocumentQueue"),
    RENAME_FOLDER("RenameFolderQueue"),
    CHECK_IN("CheckInQueue"),
    CHECK_OUT("CheckOutQueue"),
    CANCEL_CHECK_OUT("CancelCheckOutQueue");

    private final String queueName;
    public String queuePackage = "com.armedia.acm.camelcontext.flow.queue.";

    ArkCaseCMISActions(String queueName)
    {
        this.queueName = queueName;
    }

    public String getQueueName()
    {
        return queuePackage + queueName;
    }
}
