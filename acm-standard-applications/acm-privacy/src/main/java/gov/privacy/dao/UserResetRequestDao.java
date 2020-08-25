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

import static gov.privacy.model.UserResetRequestRecord.FIND_BY_EMAIL;
import static gov.privacy.model.UserResetRequestRecord.FIND_BY_RESET_KEY;

import com.armedia.acm.data.AcmAbstractDao;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import gov.privacy.model.UserResetRequestRecord;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 */
@Transactional
public class UserResetRequestDao extends AcmAbstractDao<UserResetRequestRecord>
{

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<UserResetRequestRecord> getPersistenceClass()
    {
        return UserResetRequestRecord.class;
    }

    /**
     * @param emailAddress
     * @return
     */
    public Optional<UserResetRequestRecord> findByEmail(String emailAddress)
    {
        List<UserResetRequestRecord> resultList = getEm().createNamedQuery(FIND_BY_EMAIL, UserResetRequestRecord.class)
                .setParameter("emailAddress", emailAddress).getResultList();
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
     * @param resetId
     * @return
     */
    public Optional<UserResetRequestRecord> findByResetId(String resetId)
    {
        List<UserResetRequestRecord> resultList = getEm()
                .createNamedQuery(FIND_BY_RESET_KEY, UserResetRequestRecord.class)
                .setParameter("resetKey", resetId).getResultList();
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
     * @param resetRecord
     */
    public void delete(UserResetRequestRecord resetRecord)
    {
        UserResetRequestRecord merged = getEm().merge(resetRecord);
        getEm().remove(merged);
    }

}
