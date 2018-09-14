package com.armedia.acm.plugins.onlyoffice.model.callback;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CallBackData
{
    /**
     * Defines the object received if the new user connected to the document co-editing or disconnected from it. In the
     * first case the type field value is 1, in the other case - 0. The userid field value is the identifier of the user
     * who connected to or disconnected from the document co-editing.
     */
    @JsonProperty("actions")
    private List<Action> actions;
    /**
     * Defines the array of objects with the document changes history. The object is present when the status value is
     * equal to 2 or 3 only. Must be sent as a property changes of the object sent as the argument to the refreshHistory
     * method. Deprecated since version 4.2, please use history instead.
     */
    @JsonProperty("changeshistory")
    private List<ChangeHistory> changesHistory;
    /**
     * Defines the link to the file with the document editing data used to track and display the document changes
     * history. The link is present when the status value is equal to 2 or 3 only. The file must be saved and its
     * address must be sent as changesUrl parameter using the setHistoryData method to show the changes corresponding to
     * the specific document version.
     */
    @JsonProperty("changesurl")
    private String changesUrl;
    /**
     * Defines the type of initiator when the force saving request is performed. Can have the following values:
     * 0 - the force saving request is performed to the command service,
     * 1 - the force saving request is performed each time the saving is done (e.g. the Save button is clicked), which
     * is only available when the forcesave option is set to true.
     * 2 - the force saving request is performed by timer with the settings from the server config.
     * The type is present when the status value is equal to 6 or 7 only.
     */
    @JsonProperty("forcesavetype")
    private Integer forceSaveType;
    /**
     * Defines the object with the document changes history. The object is present when the status value is equal to 2
     * or 3 only. It contains the object serverVersion and changes, which must be sent as properties serverVersion and
     * changes of the object sent as the argument to the refreshHistory method.
     */
    @JsonProperty("history")
    private History history;
    /**
     * Defines the edited document identifier.
     */
    @JsonProperty("key")
    private String key;
    /**
     * Defines the status of the document. Can have the following values:
     * 0 - no document with the key identifier could be found,
     * 1 - document is being edited,
     * 2 - document is ready for saving,
     * 3 - document saving error has occurred,
     * 4 - document is closed with no changes,
     * 6 - document is being edited, but the current document state is saved,
     * 7 - error has occurred while force saving the document.
     */
    @JsonProperty("status")
    private Integer status;
    /**
     * Defines the link to the edited document to be saved with the document storage service. The link is present when
     * the status value is equal to 2 or 3 only.
     */
    @JsonProperty("url")
    private String url;
    /**
     * Defines the custom information sent to the command service in case it was present in the request.
     */
    @JsonProperty(value = "userdata")
    private String userData;
    /**
     * Defines the list of the identifiers of the users who opened the document for editing; when the document has been
     * changed the users will return the identifier of the user who was the last to edit the document (for status 2 and
     * status 6 replies).
     */
    @JsonProperty("users")
    private List<String> users;

    public List<Action> getActions()
    {
        return actions;
    }

    public void setActions(List<Action> actions)
    {
        this.actions = actions;
    }

    public List<ChangeHistory> getChangesHistory()
    {
        return changesHistory;
    }

    public void setChangesHistory(List<ChangeHistory> changesHistory)
    {
        this.changesHistory = changesHistory;
    }

    public String getChangesUrl()
    {
        return changesUrl;
    }

    public void setChangesUrl(String changesUrl)
    {
        this.changesUrl = changesUrl;
    }

    public Integer getForceSaveType()
    {
        return forceSaveType;
    }

    public void setForceSaveType(Integer forceSaveType)
    {
        this.forceSaveType = forceSaveType;
    }

    public History getHistory()
    {
        return history;
    }

    public void setHistory(History history)
    {
        this.history = history;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUserData()
    {
        return userData;
    }

    public void setUserData(String userData)
    {
        this.userData = userData;
    }

    public List<String> getUsers()
    {
        return users;
    }

    public void setUsers(List<String> users)
    {
        this.users = users;
    }

    @Override
    public String toString()
    {
        return "CallBackData{" +
                "actions=" + actions +
                ", changesHistory=" + changesHistory +
                ", changesUrl='" + changesUrl + '\'' +
                ", forceSaveType=" + forceSaveType +
                ", history=" + history +
                ", key='" + key + '\'' +
                ", status=" + status +
                ", url='" + url + '\'' +
                ", userData='" + userData + '\'' +
                ", users=" + users +
                '}';
    }
}
