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
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.impl.CreateAdHocTaskService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.service.group.GroupService;
import gov.foia.dao.FOIARequestDao;
import gov.foia.dao.PortalFOIAPersonDao;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.PortalFOIAPerson;
import gov.foia.model.PortalFOIAReadingRoom;
import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestFile;
import gov.foia.model.PortalFOIARequestStatus;
import gov.foia.model.WithdrawRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sasko.tanaskoski
 *
 */
public class PortalRequestService
{
    private static final int MAX_READING_ROOM_RESULTS = 500;

    private final Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao requestDao;

    private CaseFileDao caseFileDao;

    private ExecuteSolrQuery executeSolrQuery;

    private GetCaseByNumberService getCaseByNumberService;

    private PortalFOIAPersonDao portalFOIAPersonDao;

    private PersonAssociationDao personAssociationDao;

    private UserDao userDao;

    private LookupDao lookupDao;

    private NotificationService notificationService;

    private GroupService groupService;

    private SearchResults searchResults;

    private TranslationService translationService;

    private CreateAdHocTaskService createAdHocTaskService;

    private HolidayConfigurationService holidayConfigurationService;

    private final String WITHDRAW_REQUEST_TITLE = "Withdraw Request";

    private CorrespondenceTemplateManager templateManager;

    public List<PortalFOIARequestStatus> getExternalRequests(PortalFOIARequestStatus portalRequestStatus) throws AcmObjectNotFoundException
    {
        List<PortalFOIARequestStatus> responseRequests = getRequestDao().getExternalRequests(portalRequestStatus);
        if (responseRequests.isEmpty())
        {
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
        if (foiaRequest != null)
        {
            populateResponseRequest(foiaRequest, responseRequest);
        }
        return responseRequest;
    }

    public void populateResponseRequest(FOIARequest foiaRequest, PortalFOIARequest portalFOIARequest)
    {
        FOIAPerson person = (FOIAPerson) foiaRequest.getOriginator().getPerson();
        portalFOIARequest.setOriginalRequestNumber(foiaRequest.getCaseNumber());
        portalFOIARequest.setTitle(foiaRequest.getTitle());
        portalFOIARequest.setSubject(foiaRequest.getDetails());
        portalFOIARequest.setRequestType(foiaRequest.getRequestType());
        portalFOIARequest.setRequestCategory(foiaRequest.getRequestCategory());
        portalFOIARequest.setDeliveryMethodOfResponse(foiaRequest.getDeliveryMethodOfResponse());
        portalFOIARequest.setPrefix(person.getTitle());
        portalFOIARequest.setFirstName(person.getGivenName());
        portalFOIARequest.setMiddleName(person.getMiddleName());
        portalFOIARequest.setLastName(person.getFamilyName());
        String position = person.getDefaultOrganization() != null ?
                person.getDefaultOrganization().getPersonToOrganizationAssociationType() : "unknown";
        portalFOIARequest.setPosition(position);
        portalFOIARequest.setOrganization(person.getCompany());

        if (person.getDefaultEmail() != null)
        {
            portalFOIARequest.setEmail(person.getDefaultEmail().getValue());
        }

        if (person.getDefaultPhone() != null)
        {
            portalFOIARequest.setPhone(person.getDefaultPhone().getValue());
        }

        if (!person.getAddresses().isEmpty())
        {
            portalFOIARequest.setCity(person.getAddresses().get(0).getCity());
            portalFOIARequest.setCountry(person.getAddresses().get(0).getCountry());
            portalFOIARequest.setState(person.getAddresses().get(0).getState());
            portalFOIARequest.setZip(person.getAddresses().get(0).getZip());
            portalFOIARequest.setAddress1(person.getAddresses().get(0).getStreetAddress());
            portalFOIARequest.setAddress2(person.getAddresses().get(0).getStreetAddress2());
        }
    }

    public List<PortalFOIAReadingRoom> getReadingRoom(PortalFOIAReadingRoom readingRoom, Authentication auth)
            throws SolrException
    {

        List<PortalFOIAReadingRoom> readingRoomList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        log.info("Searching for content '{}'", readingRoom.getContentSearch());

        String query = readingRoom.getContentSearch();

        query += "+AND+object_type_s:FILE+AND+parent_ref_s:*CASE_FILE+AND+public_flag_b:true";

        query += "&fl=object_id_s,title_parseable,ext_s,parent_ref_s,modified_date_tdt";

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, MAX_READING_ROOM_RESULTS, "", true, "",
                false, false, "catch_all");

        SearchResults searchResults = new SearchResults();
        JSONArray docFiles = searchResults.getDocuments(results);

        for (int i = 0; i < docFiles.length(); i++)
        {
            try
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
            catch (JSONException | ParseException e)
            {
                log.warn("Error processing JSON data retieved from Solr", e);
            }

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

        List<String> members = new ArrayList<>();
        try
        {
            List<StandardLookupEntry> downloadResponseNotificationGroup = (List<StandardLookupEntry>) getLookupDao()
                    .getLookupByName("downloadResponseNotificationGroup").getEntries();
            for (StandardLookupEntry lookupEntry : downloadResponseNotificationGroup)
            {
                members.add(getGroupService().getUserMembersForGroup(lookupEntry.getValue(), Optional.empty(),
                        SecurityContextHolder.getContext().getAuthentication()));
            }

        }
        catch (SolrException e)
        {
            log.warn("Could not read members of request download notification group");
        }

        if (!members.isEmpty())
        {
            for (String groupMembers : members)
            {
                JSONArray membersArray = getSearchResults().getDocuments(groupMembers);

                for (int i = 0; i < membersArray.length(); i++)
                {
                    JSONObject memberObject = membersArray.getJSONObject(i);
                    String memberState = getSearchResults().extractString(memberObject, "status_lcs");
                    if (memberState.equals(AcmUserState.VALID.name()))
                    {
                        String emailAddress = getSearchResults().extractString(memberObject, "email_lcs");
                        officersGroupMemberEmailAddresses.add(emailAddress);
                    }
                }
            }
        }

        if (!officersGroupMemberEmailAddresses.isEmpty())
        {
            String emailAddresses = officersGroupMemberEmailAddresses.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(","));

            log.debug("Preparing requestDownload notification to [{}]", emailAddresses);

            OffsetDateTime downloadedDateTime = OffsetDateTime.now(ZoneOffset.UTC);
            String downloadedDateTimeFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss").format(downloadedDateTime);

            String emailSubject = "";
            Template template = templateManager.findTemplate("requestDownloaded.html");
            if (template != null)
            {
                emailSubject = template.getEmailSubject();
            }
            Notification notification = notificationService.getNotificationBuilder()
                    .newNotification("requestDownloaded",
                            String.format(translationService.translate(NotificationConstants.REQUEST_DOWNLOADED), request.getCaseNumber()),
                            request.getObjectType(), request.getId(), SecurityContextHolder.getContext().getAuthentication().getName())
                    .forObjectWithNumber(request.getCaseNumber())
                    .forObjectWithTitle(StringUtils.left(request.getDetails(), 1000))
                    .withEmailAddresses(emailAddresses)
                    .withNote(downloadedDateTimeFormatted)
                    .withSubject(emailSubject)
                    .build();

            notificationService.saveNotification(notification);
        }
    }

    private void setParentData(PortalFOIAReadingRoom portalReadingRoom, String parent_ref, Authentication auth)
            throws SolrException, JSONException
    {
        log.info("Searching for corresponding request of file '{}'", portalReadingRoom.getFile().getFileName());

        String query = "object_type_s:CASE_FILE+AND+id:\"" + parent_ref + "\"";

        query += "&fl=name,title_parseable,description_no_html_tags_parseable";

        // a query based on id can only have 1 result, so we only need max_rows of 1 below.
        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 1, "");

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

    public void createRequestWithdrawalTask(WithdrawRequest withdrawRequestDetails, Authentication auth)
    {
        FOIARequest request = (FOIARequest) getCaseFileDao().findByCaseNumber(withdrawRequestDetails.getOriginalRequestNumber());

        AcmTask requestWithdrawalTask = populateWithdrawalTask(withdrawRequestDetails, request);

        List<MultipartFile> files = addWithdrawalFiles(withdrawRequestDetails);

        try
        {
            AcmTask task = getCreateAdHocTaskService().createAdHocTask(requestWithdrawalTask, files, auth,
                    withdrawRequestDetails.getIpAddress());

            createTaskPersonAssociation(auth, request, task);

            request.setWithdrawRequestedFlag(true);
            getRequestDao().save(request);
        }
        catch (Exception e)
        {
            log.error("Withdraw task not created!", e);
        }

    }

    private List<MultipartFile> addWithdrawalFiles(WithdrawRequest withdrawRequestDetails)
    {
        if (withdrawRequestDetails.getDocuments() == null || withdrawRequestDetails.getDocuments().isEmpty())
        {
            return new ArrayList<>();
        }

        List<MultipartFile> files = new ArrayList<>();
        for (PortalFOIARequestFile portalFile : withdrawRequestDetails.getDocuments())
        {
            try
            {
                files.add(convertPortalRequestFileToMultipartFile(portalFile));
            } catch (IOException e)
            {
                log.error("Failed to receive file {}, {}", portalFile.getFileName(), e.getMessage());
            }
        }
        return files;
    }

    protected AcmTask populateWithdrawalTask(WithdrawRequest withdrawRequestDetails, FOIARequest request)
    {
        AcmTask requestWithdrawalTask = new AcmTask();

        String requestTitle = withdrawRequestDetails.getSubject() != null ?
                String.format("%s %s: %s", WITHDRAW_REQUEST_TITLE, withdrawRequestDetails.getOriginalRequestNumber(),
                        withdrawRequestDetails.getSubject()) :
                String.format("%s %s", WITHDRAW_REQUEST_TITLE, withdrawRequestDetails.getOriginalRequestNumber());
        requestWithdrawalTask.setTitle(requestTitle);
        requestWithdrawalTask.setType("web-portal-withdrawal");
        requestWithdrawalTask.setDetails(withdrawRequestDetails.getDescription());
        requestWithdrawalTask.setAttachedToObjectType("CASE_FILE");
        requestWithdrawalTask.setParentObjectType("CASE_FILE");
        requestWithdrawalTask.setAttachedToObjectName(withdrawRequestDetails.getOriginalRequestNumber());
        requestWithdrawalTask.setParentObjectName(withdrawRequestDetails.getOriginalRequestNumber());
        requestWithdrawalTask.setAdhocTask(true);
        requestWithdrawalTask.setCompleted(false);
        requestWithdrawalTask.setPriority("High");
        requestWithdrawalTask.setAssignee(request.getAssigneeLdapId());


        //Setting the request owning group as the request withdrawal task owning group
        List<AcmParticipant> owningGroup = request.getParticipants().stream()
                .filter(part -> part.getParticipantType().equals("owning group")).collect(Collectors.toList());
        requestWithdrawalTask.setParticipants(owningGroup);
        requestWithdrawalTask.setCandidateGroups(Arrays.asList(owningGroup.get(0).getParticipantLdapId()));

        //Setting task due date
        requestWithdrawalTask.setDueDate(getHolidayConfigurationService().addWorkingDaysAndWorkingHoursToDateWithBusinessHours(new Date(), 3));


        if (request != null)
        {
            requestWithdrawalTask.setAttachedToObjectId(request.getId());
            requestWithdrawalTask.setAttachedToObjectType(request.getObjectType());
            requestWithdrawalTask.setAttachedToObjectName(request.getCaseNumber());
            requestWithdrawalTask.setParentObjectId(request.getId());
            requestWithdrawalTask.setParentObjectType(request.getObjectType());
            requestWithdrawalTask.setParentObjectName(request.getCaseNumber());
        }
        return requestWithdrawalTask;
    }

    private void createTaskPersonAssociation(Authentication auth, FOIARequest request, AcmTask task)
    {
        FOIARequesterAssociation personAssociation = new FOIARequesterAssociation();

        FOIAPerson person = (FOIAPerson) request.getOriginator().getPerson();

        personAssociation.setParentId(task.getTaskId());
        personAssociation.setParentType("TASK");
        personAssociation.setPersonType("Creator");
        personAssociation.setCreator(auth.getName());
        personAssociation.setPerson(person);
        personAssociation.setPersonType("Creator");

        getPersonAssociationDao().save(personAssociation);
    }

    public MultipartFile convertPortalRequestFileToMultipartFile(PortalFOIARequestFile requestFile) throws IOException
    {
        byte[] content = Base64.getDecoder().decode(requestFile.getContent());

        File file = new File(requestFile.getFileName());
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, content);

        FileItem fileItem = new DiskFileItem("", requestFile.getContentType(), false, file.getName(), (int) file.length(),
                file.getParentFile());

        try (InputStream input = new FileInputStream(file))
        {
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
        }

        return new CommonsMultipartFile(fileItem);
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

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
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

    public List<PortalFOIARequestStatus> getLoggedUserExternalRequests(String emailAddress, String requestId)
            throws AcmObjectNotFoundException
    {
        PortalFOIAPerson person = getPortalFOIAPersonDao().findByEmail(emailAddress).get();
        List<PortalFOIARequestStatus> responseRequests = getRequestDao().getLoggedUserExternalRequests(person.getId(), requestId);
        if (responseRequests.isEmpty())
        {
            log.info("FOIA Requests not found for the logged user [{}]]", emailAddress);
            throw new AcmObjectNotFoundException("PortalFOIARequestStatus", null,
                    "FOIA Requests not found for the logged user " + emailAddress + " not found");

        }
        return responseRequests;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }

    public CreateAdHocTaskService getCreateAdHocTaskService()
    {
        return createAdHocTaskService;
    }

    public void setCreateAdHocTaskService(CreateAdHocTaskService createAdHocTaskService)
    {
        this.createAdHocTaskService = createAdHocTaskService;
    }

    public PortalFOIAPersonDao getPortalFOIAPersonDao()
    {
        return portalFOIAPersonDao;
    }

    public void setPortalFOIAPersonDao(PortalFOIAPersonDao portalFOIAPersonDao)
    {
        this.portalFOIAPersonDao = portalFOIAPersonDao;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public HolidayConfigurationService getHolidayConfigurationService() {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService) {
        this.holidayConfigurationService = holidayConfigurationService;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }

}
