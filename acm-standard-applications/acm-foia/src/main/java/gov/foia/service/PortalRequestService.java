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
import com.armedia.acm.plugins.casefile.service.GetCaseByNumberService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    private final Logger log = LoggerFactory.getLogger(getClass());

    private FOIARequestDao requestDao;

    private ExecuteSolrQuery executeSolrQuery;

    private GetCaseByNumberService getCaseByNumberService;

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
        portalFOIARequest.setSubject(foiaRequest.getTitle());
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
        portalReadingRoom.setDescription(docRequest.getString("description_no_html_tags_parseable"));
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
}
