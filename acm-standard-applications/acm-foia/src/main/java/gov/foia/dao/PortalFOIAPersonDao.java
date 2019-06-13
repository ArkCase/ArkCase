package gov.foia.dao;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import static gov.foia.model.PortalFOIAPerson.FIND_BY_EMAIL;

import com.armedia.acm.data.AcmAbstractDao;

import java.util.List;
import java.util.Optional;

import gov.foia.model.PortalFOIAPerson;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 23, 2018
 *
 */
public class PortalFOIAPersonDao extends AcmAbstractDao<PortalFOIAPerson>
{

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<PortalFOIAPerson> getPersistenceClass()
    {
        return PortalFOIAPerson.class;
    }

    /**
     * @param emailAddress
     * @return
     */
    public Optional<PortalFOIAPerson> findByEmail(String emailAddress)
    {
        List<PortalFOIAPerson> resultList = getEm().createNamedQuery(FIND_BY_EMAIL, PortalFOIAPerson.class)
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

}
