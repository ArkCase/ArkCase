package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;

import java.util.List;

/**
 * Created by armdev on 1/14/15.
 */
public class SearchAccessControlFields
{
    private ParticipantAccessChecker participantAccessChecker;

    public void setAccessControlFields(SolrBaseDocument doc, AcmAssignedObject object)
    {
        // all protected objects must have protected_object_b
        doc.setProtected_object_b(true);

        boolean publicDoc = getParticipantAccessChecker().defaultUserHasRead(object);
        doc.setPublic_doc_b(publicDoc);

        if (!publicDoc)
        {
            List<String> readers = getParticipantAccessChecker().getReaders(object);

            // due to how Solr works we have to replace any spaces in the participant ids with an unusual character.
            doc.setAllow_acl_ss(readers);
        }

        List<String> denied = getParticipantAccessChecker().getDenied(object);
        doc.setDeny_acl_ss(denied);
    }

    public ParticipantAccessChecker getParticipantAccessChecker()
    {
        return participantAccessChecker;
    }

    public void setParticipantAccessChecker(ParticipantAccessChecker participantAccessChecker)
    {
        this.participantAccessChecker = participantAccessChecker;
    }
}
