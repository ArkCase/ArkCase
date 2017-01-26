package com.armedia.acm.services.participants.service;

import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by marjan.stefanoski on 01.04.2015.
 */
public class AcmParticipantService
{

    private AcmParticipantDao participantDao;
    private ParticipantsBusinessRule participantsBusinessRule;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmParticipant saveParticipant(String userId, String participantType, Long objectId, String objectType)
    {

        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantLdapId(userId);
        participant.setParticipantType(participantType);
        participant.setObjectId(objectId);
        participant.setObjectType(objectType);

        try
        {
            applyParticipantRules(participant);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        AcmParticipant savedParticipant = getParticipantDao().save(participant);

        log.debug("Added participant [{}] to object type [{}] with object id [{}]", userId, objectType, objectId);

        return savedParticipant;
    }

    private void applyParticipantRules(Object obj)
    {
        if (obj instanceof CheckParticipantListModel)
        {
            CheckParticipantListModel model = (CheckParticipantListModel) obj;
            participantsBusinessRule.applyRules(model);
        }
    }

    public AcmParticipant getParticipantByParticipantTypeAndObjectTypeAndId(String userId, String participantType, String objectType, Long objectId)
    {
        return getParticipantDao().getParticipantByParticipantTypeAndObjectTypeAndId(userId, participantType, objectType, objectId);
    }

    public AcmParticipant changeParticipantRole(AcmParticipant participant, String newRole) throws Exception
    {
        participant.setParticipantType(newRole);
        applyParticipantRules(participant);

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
