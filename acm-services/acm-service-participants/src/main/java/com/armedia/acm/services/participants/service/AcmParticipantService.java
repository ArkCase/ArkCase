package com.armedia.acm.services.participants.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by marjan.stefanoski on 01.04.2015.
 */
public class AcmParticipantService
{

    private AcmParticipantDao participantDao;
    private ParticipantsBusinessRule participantsBusinessRule;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmParticipant saveParticipant(String userId, String participantType, Long objectId, String objectType) throws AcmAccessControlException
    {

        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(userId);
        participant.setParticipantType(participantType);
        participant.setObjectId(objectId);
        participant.setObjectType(objectType);

        CheckParticipantListModel model = new CheckParticipantListModel();
        List<String> errorListAfterRules = applyParticipantRules(participant, model);
        if (errorListAfterRules != null)
        {
            throw new AcmAccessControlException(errorListAfterRules, "Conflict permissions combination has occurred for the chosen participants");
        }

        AcmParticipant savedParticipant = getParticipantDao().save(participant);

        log.debug("Added participant [{}] to object type [{}] with object id [{}]", userId, objectType, objectId);

        return savedParticipant;
    }

    private List<String> applyParticipantRules(AcmParticipant participant, CheckParticipantListModel model)
    {
        List<AcmParticipant> allParticipantsFromParentObject = participantDao.findParticipantsForObject(participant.getObjectType(), participant.getObjectId());
        if (allParticipantsFromParentObject != null)
        {
            for (AcmParticipant someParticipant : allParticipantsFromParentObject)
            {
                if (someParticipant.getId().equals(participant.getId()))
                {
                    allParticipantsFromParentObject.remove(someParticipant);
                }
            }
            allParticipantsFromParentObject.add(participant);

            model.setParticipantList(allParticipantsFromParentObject);
            model.setObjectType(participant.getObjectType());
            model = participantsBusinessRule.applyRules(model);

            List<String> listOfErrors = new ArrayList<>();
            if (!model.getErrorsList().isEmpty())
            {
                listOfErrors = model.getErrorsList();
            }
            return listOfErrors;
        }
        return null;
    }

    public AcmParticipant getParticipantByParticipantTypeAndObjectTypeAndId(String userId, String participantType, String objectType, Long objectId)
    {
        return getParticipantDao().getParticipantByParticipantTypeAndObjectTypeAndId(userId, participantType, objectType, objectId);
    }

    public AcmParticipant changeParticipantRole(AcmParticipant participant, String newRole) throws Exception
    {
        participant.setParticipantType(newRole);
        CheckParticipantListModel model = new CheckParticipantListModel();

        applyParticipantRules(participant, model);

        AcmParticipant updatedParticipant = getParticipantDao().save(participant);
        return updatedParticipant;
    }

    public List<AcmParticipant> listAllParticipantsPerObjectTypeAndId(String objectType, Long objectId)
    {
        return getParticipantDao().findParticipantsForObject(objectType, objectId);
    }

    public void removeParticipant(Long participantId) throws Exception
    {
        getParticipantDao().deleteParticipant(participantId);
    }

    public void removeParticipant(AcmParticipant participant) throws Exception
    {
        getParticipantDao().deleteParticipant(participant.getId());
    }

    public AcmParticipant findParticipant(Long id)
    {
        return getParticipantDao().find(id);
    }

    public AcmParticipantDao getParticipantDao()
    {
        return participantDao;
    }

    public void setParticipantDao(AcmParticipantDao participantDao)
    {
        this.participantDao = participantDao;
    }

    public ParticipantsBusinessRule getParticipantsBusinessRule()
    {
        return participantsBusinessRule;
    }

    public void setParticipantsBusinessRule(ParticipantsBusinessRule participantsBusinessRule)
    {
        this.participantsBusinessRule = participantsBusinessRule;
    }
}
