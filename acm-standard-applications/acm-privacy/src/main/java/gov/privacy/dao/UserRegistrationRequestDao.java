package gov.privacy.dao;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import static gov.privacy.model.UserRegistrationRequestRecord.FIND_BY_EMAIL_AND_PORTAL_ID;
import static gov.privacy.model.UserRegistrationRequestRecord.FIND_BY_REGISTRATION_KEY;

import com.armedia.acm.data.AcmAbstractDao;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import gov.privacy.model.UserRegistrationRequestRecord;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
@Transactional
public class UserRegistrationRequestDao extends AcmAbstractDao<UserRegistrationRequestRecord>
{

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<UserRegistrationRequestRecord> getPersistenceClass()
    {
        return UserRegistrationRequestRecord.class;
    }

    /**
     * @param emailAddress
     * @param portalId
     * @return
     */
    public Optional<UserRegistrationRequestRecord> findByEmail(String emailAddress, String portalId)
    {
        List<UserRegistrationRequestRecord> resultList = getEm()
                .createNamedQuery(FIND_BY_EMAIL_AND_PORTAL_ID, UserRegistrationRequestRecord.class)
                .setParameter("emailAddress", emailAddress).setParameter("portalId", portalId).getResultList();
        if (resultList.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(resultList.get(0));
        }
    }

    /**
     * @param registrationId
     * @return
     */
    public Optional<UserRegistrationRequestRecord> findByRegistrationId(String registrationId)
    {
        List<UserRegistrationRequestRecord> resultList = getEm()
                .createNamedQuery(FIND_BY_REGISTRATION_KEY, UserRegistrationRequestRecord.class)
                .setParameter("registrationKey", registrationId).getResultList();
        if (resultList.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(resultList.get(0));
        }
    }

    /**
     * @param registrationRecord
     */
    public void delete(UserRegistrationRequestRecord registrationRecord)
    {
        UserRegistrationRequestRecord merged = getEm().merge(registrationRecord);
        getEm().remove(merged);
    }

}
