package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;

import java.util.List;
import java.util.stream.Collectors;

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
            readers = encode(readers);
            doc.setAllow_acl_ss(readers);
        }

        List<String> denied = getParticipantAccessChecker().getDenied(object);
        denied = encode(denied);
        doc.setDeny_acl_ss(denied);
    }

    private List<String> encode(List<String> toBeEncoded)
    {
        return toBeEncoded.stream()
                .map(s -> s.replace(" ", DataAccessControlConstants.SPACE_REPLACE))
                .map(s -> s.replace(",", DataAccessControlConstants.COMMA_REPLACE))
                .map(s -> s.replace("(", DataAccessControlConstants.OPENING_PARENTHESIS_REPLACE))
                .map(s -> s.replace(")", DataAccessControlConstants.CLOSING_PARENTHESIS_REPLACE))
                .collect(Collectors.toList());
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
