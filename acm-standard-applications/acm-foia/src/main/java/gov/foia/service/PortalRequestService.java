package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.service.GetCaseByNumberService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.PortalFOIAReadingRoom;
import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestStatus;

/**
 * @author sasko.tanaskoski
 *
 */
public class PortalRequestService
{
    private final Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao requestDao;

    private CaseFileDao caseFileDao;

    private ExecuteSolrQuery executeSolrQuery;

    private GetCaseByNumberService getCaseByNumberService;

    private UserDao userDao;

    private LookupDao lookupDao;

    private NotificationDao notificationDao;

    private GroupService groupService;

    private SearchResults searchResults;

    public List<PortalFOIARequestStatus> getExternalRequests(PortalFOIARequestStatus portalRequestStatus) throws AcmObjectNotFoundException
    {
        List<PortalFOIARequestStatus> responseRequests = getRequestDao().getExternalRequests(portalRequestStatus);
        if (responseRequests.isEmpty())
        {
            log.info("FOIA Requests not found for the caseNumber [{}], lastName [{}]", portalRequestStatus.getRequestId(),
                    portalRequestStatus.getLastName());
            throw new AcmObjectNotFoundException("PortalFOIARequestStatus", null,
                    "FOIA Requests not found for the caseNumber [" + portalRequestStatus.getRequestId() + "], and lastName ["
                            + portalRequestStatus.getLastName() + "]");
        }
        return responseRequests;
    }

    /**
     * @param portalUserId
     * @return
     * @throws AcmObjectNotFoundException
     */
    public List<PortalFOIARequestStatus> getExternalRequests(String portalUserId) throws AcmObjectNotFoundException
    {
        List<PortalFOIARequestStatus> responseRequests = requestDao.getExternalRequests(portalUserId);
        if (responseRequests.isEmpty())
        {
            log.info("FOIA Requests not found for user with id [{}].", portalUserId);
            throw new AcmObjectNotFoundException("PortalFOIARequestStatus", null,
                    "FOIA Requests not found for the user with id [" + portalUserId + "].");
        }
        return responseRequests;
    }

    /**
     * @param portalUserId
     * @param requestId
     * @return
     */
    public PortalFOIARequestStatus getExternalRequest(String portalUserId, String requestId)
    {
        PortalFOIARequestStatus status = requestDao.getExternalRequest(portalUserId, requestId);
        return status;
    }

    public PortalFOIARequest checkRequestStatus(PortalFOIARequest portalFOIARequest)
    {
        FOIARequest foiaRequest = (FOIARequest) getCaseByNumberService.getCaseByNumber(portalFOIARequest.getOriginalRequestNumber());
        PortalFOIARequest responseRequest = new PortalFOIARequest();
        if (foiaRequest != null && foiaRequest.getStatus().equals("Released") && foiaRequest.getRequestType().equals("New Request"))
        {
            populateResponseRequest(foiaRequest, responseRequest);
        }
        return responseRequest;
    }

    public void populateResponseRequest(FOIARequest foiaRequest, PortalFOIARequest portalFOIARequest)
    {
        portalFOIARequest.setOriginalRequestNumber(foiaRequest.getCaseNumber());
        portalFOIARequest.setTitle(foiaRequest.getTitle());
        portalFOIARequest.setSubject(foiaRequest.getDetails());
        portalFOIARequest.setRequestCategory(foiaRequest.getRequestCategory());
        portalFOIARequest.setDeliveryMethodOfResponse(foiaRequest.getDeliveryMethodOfResponse());
        portalFOIARequest.setAddress1(foiaRequest.getOriginator().getPerson().getAddresses().get(0).getStreetAddress());
        portalFOIARequest.setCity(foiaRequest.getOriginator().getPerson().getAddresses().get(0).getCity());
        portalFOIARequest.setCountry(foiaRequest.getOriginator().getPerson().getAddresses().get(0).getCountry());
        portalFOIARequest.setState(foiaRequest.getOriginator().getPerson().getAddresses().get(0).getState());
        portalFOIARequest.setZip(foiaRequest.getOriginator().getPerson().getAddresses().get(0).getZip());
    }

    public List<PortalFOIAReadingRoom> getReadingRoom(PortalFOIAReadingRoom readingRoom, Authentication auth)
            throws MuleException, JSONException, ParseException
    {

        List<PortalFOIAReadingRoom> readingRoomList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        log.info("Searching for content '{}'", readingRoom.getContentSearch());

        String query = readingRoom.getContentSearch();

        query += "+AND+object_type_s:FILE+AND+parent_ref_s:*CASE_FILE+AND+public_flag_b:true";

        query += "&fl=object_id_s,title_parseable,ext_s,parent_ref_s,modified_date_tdt";

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 99999, "", true, "",
                false, false, "catch_all");

        SearchResults searchResults = new SearchResults();
        JSONArray docFiles = searchResults.getDocuments(results);

        for (int i = 0; i < docFiles.length(); i++)
        {
            JSONObject docFile = docFiles.getJSONObject(i);
            PortalFOIAReadingRoom room = new PortalFOIAReadingRoom();
            PortalFOIAReadingRoom.File file = new PortalFOIAReadingRoom.File();
            file.setFileId(docFile.getString("object_id_s"));
            file.setFileName(docFile.getString("title_parseable") + docFile.getString("ext_s"));
            room.setFile(file);
            room.setPublishedDate(formatter.parse(docFile.getString("modified_date_tdt")));
            setParentData(room, docFile.getString("parent_ref_s"), auth);
            readingRoomList.add(room);
        }

        return readingRoomList;

    }

    public void sendRequestDownloadedEmailToOfficersGroup(String requestNumber)
    {
        FOIARequest request = (FOIARequest) getCaseFileDao().findByCaseNumber(requestNumber);

        if (Objects.isNull(request))
        {
            return;
        }

        Set<String> officersGroupMemberEmailAddresses = new HashSet<>();

        String members = "";
        try
        {
            List<StandardLookupEntry> downloadResponseNotificationGroup = (List<StandardLookupEntry>) getLookupDao()
                    .getLookupByName("downloadResponseNotificationGroup").getEntries();
            StandardLookupEntry groupNameLookupEntry = downloadResponseNotificationGroup.stream()
                    .filter(standardLookupEntry -> standardLookupEntry.getKey().equals("groupName")).findFirst().orElse(null);

            if (Objects.nonNull(groupNameLookupEntry))
            {
                members = getGroupService().getUserMembersForGroup(groupNameLookupEntry.getValue(), Optional.empty(),
                        SecurityContextHolder.getContext().getAuthentication());
            }

        }
        catch (MuleException e)
        {
            log.warn("Could not read members of request download notification group");
        }

        if (StringUtils.isNotBlank(members))
        {
            JSONArray membersArray = getSearchResults().getDocuments(members);

            for (int i = 0; i < membersArray.length(); i++)
            {
                JSONObject memberObject = membersArray.getJSONObject(i);
                String emailAddress = getSearchResults().extractString(memberObject, "email_lcs");

                officersGroupMemberEmailAddresses.add(emailAddress);
            }
        }

        if (!officersGroupMemberEmailAddresses.isEmpty())
        {
            Notification notification = new Notification();

            OffsetDateTime downloadedDateTime = OffsetDateTime.now(ZoneOffset.UTC);
            String downloadedDateTimeFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss").format(downloadedDateTime);

            notification.setTitle(String.format("Request:%s Downloaded", request.getCaseNumber()));
            notification.setTemplateModelName("requestDownloaded");
            notification.setParentId(request.getId());
            notification.setParentType(request.getRequestType());
            notification.setParentName(request.getCaseNumber());
            notification.setParentTitle(StringUtils.left(request.getDetails(), 1000));
            notification.setNote(downloadedDateTimeFormatted);
            notification.setEmailAddresses(officersGroupMemberEmailAddresses.stream().collect(Collectors.joining(",")));
            notification.setUser(SecurityContextHolder.getContext().getAuthentication().getName());

            getNotificationDao().save(notification);
        }
    }

    private void setParentData(PortalFOIAReadingRoom portalReadingRoom, String parent_ref, Authentication auth) throws MuleException
    {
        log.info("Searching for corresponding request of file '{}'", portalReadingRoom.getFile().getFileName());

        String query = "object_type_s:CASE_FILE+AND+id:" + parent_ref;

        query += "&fl=name,title_parseable,description_no_html_tags_parseable";

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 99999, "", true,
                "", false, false, "");

        SearchResults searchResults = new SearchResults();
        JSONArray docRequests = searchResults.getDocuments(results);
        JSONObject docRequest = docRequests.getJSONObject(0);
        portalReadingRoom.setRequestId(docRequest.getString("name"));
        portalReadingRoom.setRequestTitle(docRequest.getString("title_parseable"));
        if (!docRequest.isNull("description_no_html_tags_parseable"))
        {
            portalReadingRoom.setDescription(docRequest.getString("description_no_html_tags_parseable"));
        }
        else
        {
            portalReadingRoom.setDescription("");
        }

    }

    /**
     * @return the requestDao
     */
    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    /**
     * @param requestDao
     *            the requestDao to set
     */
    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    /**
     * @return the executeSolrQuery
     */
    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    /**
     * @param executeSolrQuery
     *            the executeSolrQuery to set
     */
    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public void setGetCaseByNumberService(GetCaseByNumberService getCaseByNumberService)
    {
        this.getCaseByNumberService = getCaseByNumberService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public List<PortalFOIARequestStatus> getLoggedUserExternalRequests(String emailAddress) throws AcmObjectNotFoundException
    {
        List<PortalFOIARequestStatus> responseRequests = getRequestDao().getLoggedUserExternalRequests(emailAddress);
        if (responseRequests.isEmpty())
        {
            log.info("FOIA Requests not found for the logged user [{}]]", emailAddress);
            throw new AcmObjectNotFoundException("PortalFOIARequestStatus", null,
                    "FOIA Requests not found for the logged user " + emailAddress + " not found");

        }
        return responseRequests;
    }
}
