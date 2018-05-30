package com.armedia.acm.services.dataaccess.service;

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

import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.util.AcmSolrUtil;

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
                .map(s -> AcmSolrUtil.encodeSpecialCharactersForACL(s))
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
