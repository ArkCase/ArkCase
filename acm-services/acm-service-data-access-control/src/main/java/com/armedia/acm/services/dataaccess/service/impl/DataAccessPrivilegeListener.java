package com.armedia.acm.services.dataaccess.service.impl;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.data.AcmBeforeInsertListener;
import com.armedia.acm.data.AcmBeforeUpdateListener;
import com.armedia.acm.services.dataaccess.service.EntityParticipantsChangedEventPublisher;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.participants.service.ParticipantsBusinessRule;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by armdev on 1/6/15.
 */
public class DataAccessPrivilegeListener implements AcmBeforeUpdateListener, AcmBeforeInsertListener
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private AcmAssignedObjectBusinessRule assignmentBusinessRule;
    private AcmAssignedObjectBusinessRule accessControlBusinessRule;
    private ParticipantsBusinessRule participantsBusinessRule;
    private AcmParticipantService participantService;
    private EntityParticipantsChangedEventPublisher entityParticipantsChangedEventPublisher;
    private boolean documentACLEnabled;
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;
    private SendDocumentsToSolr solrSender;
    private SearchAccessControlFields searchAccessControlFields;

    @Override
    public void beforeInsert(Object object) throws AcmAccessControlException
    {
        log.trace("inserted: {}", object);
        applyAssignmentAndAccessRules(object);
    }

    @Override
    public void beforeUpdate(Object object) throws AcmAccessControlException
    {
        log.trace("updated: {}", object);
        applyAssignmentAndAccessRules(object);
    }

    public void applyAssignmentAndAccessRules(Object obj) throws AcmAccessControlException
    {
        if (obj instanceof AcmAssignedObject)
        {
            AcmAssignedObject assignedObject = (AcmAssignedObject) obj;
            applyAssignRules(assignedObject);
            applyDataAccessRules(assignedObject);
            updateParentPointers(assignedObject);
            validateParticipantAssignmentRules(assignedObject);

            // AFDP-5567 2018-03-07 quick fix to avoid updating file and folder participants when document ACL is
            // disabled.
            // The act of looking up the current participants seems to cause database deadlocks and unique key
            // violations in PostgreSQL environments; or possibly in situations where there are many participant
            // changes to be applied. This code should still be fixed such that the participant differences are found
            // via Nebojsha's object-diff algorithm, or by the acm_object_history table, or some other way that doesn't
            // issue a SELECT against the same rows that are being updated in this transaction.
            if (isDocumentACLEnabled())
            {
                handleParticipantsChanged(assignedObject);
            }
            setParentAclIndex(assignedObject);
        }
    }

    private void handleParticipantsChanged(AcmAssignedObject assignedObject)
    {
        Boolean originalRestricted = assignedObject.getRestricted();
        List<AcmParticipant> originalParticipants = new ArrayList<>();
        if (assignedObject.getId() != null)
        {
            originalParticipants = getParticipantService().listAllParticipantsPerObjectTypeAndId(assignedObject.getObjectType(),
                    assignedObject.getId(), FlushModeType.COMMIT);
            originalRestricted = getParticipantService().getOriginalRestrictedFlag(assignedObject);
        }

        // publish EntityParticipantsChangedEvent if the participants are not equal
        boolean hasInheritanceFlag = assignedObject.getParticipants().stream()
                .anyMatch(participant -> participant.isReplaceChildrenParticipant());

        boolean hasEqualParticipants = true;

        if (hasInheritanceFlag || assignedObject.getParticipants().size() != originalParticipants.size())
        {
            hasEqualParticipants = false;
        }
        else
        {
            for (AcmParticipant assignedObjectParticipant : assignedObject.getParticipants())
            {
                boolean found = false;
                for (AcmParticipant originalParticipant : originalParticipants)
                {

                    if (assignedObjectParticipant.getParticipantLdapId().equals(originalParticipant.getParticipantLdapId()) &&
                            assignedObjectParticipant.getParticipantType().equals(originalParticipant.getParticipantType()))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    hasEqualParticipants = false;
                    break;
                }
            }
        }

        boolean restrictedChanged = assignedObject.getRestricted() != originalRestricted;

        if (!hasEqualParticipants || restrictedChanged)
        {
            getEntityParticipantsChangedEventPublisher().publishEvent(assignedObject, originalParticipants);
        }

        // remove inherit participants flag
        assignedObject.getParticipants().forEach(participant -> participant.setReplaceChildrenParticipant(false));
    }

    private void setParentAclIndex(AcmAssignedObject assignedObject)
    {
        String query = String.format("parent_ref_s:%d-%s", assignedObject.getId(), assignedObject.getObjectType());
        AcmAuthentication authentication = (AcmAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
        {
            try
            {
                String result = executeSolrQuery.getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query,
                        0, 1000, "id asc", true);
                JSONArray documents = searchResults.getDocuments(result);
                Set<String> childObjects = new HashSet<>();
                for (int i = 0; i < documents.length(); i++)
                {
                    JSONObject doc = documents.getJSONObject(i);
                    String objectId = searchResults.extractString(doc, "id");
                    childObjects.add(objectId);
                }
                JSONArray docUpdates = new JSONArray();
                childObjects.forEach(objId -> {
                    JSONObject docUpdate = new JSONObject();
                    docUpdate.put("id", objId);
                    searchAccessControlFields.setParentAccessControlFields(docUpdate, assignedObject);
                    docUpdates.put(docUpdate);
                });
                solrSender.sendSolrDocuments("solrQuickSearch.in", docUpdates.toString());
                solrSender.sendSolrDocuments("solrAdvancedSearch.in", docUpdates.toString());
            }
            catch (MuleException e)
            {
                log.error("Parent access control list failed to be index", e);
            }
        }
    }

    private void validateParticipantAssignmentRules(AcmAssignedObject assignedObject) throws AcmAccessControlException
    {
        log.trace("Validating participants assingments for entity : [{}]", assignedObject);

        CheckParticipantListModel model = new CheckParticipantListModel();
        model.setParticipantList(assignedObject.getParticipants());
        model.setObjectType(assignedObject.getObjectType());

        model = participantsBusinessRule.applyRules(model);

        log.trace("Finished validating participants assingments for entity : [{}]", assignedObject);
        if (model.getErrorsList() != null && !model.getErrorsList().isEmpty())
        {
            throw new AcmAccessControlException(model.getErrorsList(),
                    "Conflict permissions combination has occurred for entity's participants");
        }
    }

    private void updateParentPointers(AcmAssignedObject assignedObject)
    {
        for (AcmParticipant participant : assignedObject.getParticipants())
        {
            participant.setObjectType(assignedObject.getObjectType());
            participant.setObjectId(assignedObject.getId());

            log.trace("participant '" + participant.getParticipantLdapId() + "'");
            for (AcmParticipantPrivilege priv : participant.getPrivileges())
            {
                log.trace("\t privilege: " + priv.getAccessType() + " " + priv.getObjectAction());
                priv.setParticipant(participant);
            }
        }
    }

    private void applyDataAccessRules(AcmAssignedObject assignedObject)
    {
        getAccessControlBusinessRule().applyRules(assignedObject);
    }

    private void applyAssignRules(AcmAssignedObject assignedObject)
    {
        getAssignmentBusinessRule().applyRules(assignedObject);
    }

    public AcmAssignedObjectBusinessRule getAssignmentBusinessRule()
    {
        return assignmentBusinessRule;
    }

    public void setAssignmentBusinessRule(AcmAssignedObjectBusinessRule assignmentBusinessRule)
    {
        this.assignmentBusinessRule = assignmentBusinessRule;
    }

    public AcmAssignedObjectBusinessRule getAccessControlBusinessRule()
    {
        return accessControlBusinessRule;
    }

    public void setAccessControlBusinessRule(AcmAssignedObjectBusinessRule accessControlBusinessRule)
    {
        this.accessControlBusinessRule = accessControlBusinessRule;
    }

    public ParticipantsBusinessRule getParticipantsBusinessRule()
    {
        return participantsBusinessRule;
    }

    public void setParticipantsBusinessRule(ParticipantsBusinessRule participantsRule)
    {
        this.participantsBusinessRule = participantsRule;
    }

    public AcmParticipantService getParticipantService()
    {
        return participantService;
    }

    public void setParticipantService(AcmParticipantService participantService)
    {
        this.participantService = participantService;
    }

    public EntityParticipantsChangedEventPublisher getEntityParticipantsChangedEventPublisher()
    {
        return entityParticipantsChangedEventPublisher;
    }

    public void setEntityParticipantsChangedEventPublisher(EntityParticipantsChangedEventPublisher entityParticipantsChangedEventPublisher)
    {
        this.entityParticipantsChangedEventPublisher = entityParticipantsChangedEventPublisher;
    }

    public boolean isDocumentACLEnabled()
    {
        return documentACLEnabled;
    }

    public void setDocumentACLEnabled(boolean documentACLEnabled)
    {
        this.documentACLEnabled = documentACLEnabled;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public SendDocumentsToSolr getSolrSender()
    {
        return solrSender;
    }

    public void setSolrSender(SendDocumentsToSolr solrSender)
    {
        this.solrSender = solrSender;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }
}
