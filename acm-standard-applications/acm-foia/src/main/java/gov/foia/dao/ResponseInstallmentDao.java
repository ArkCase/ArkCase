package gov.foia.dao;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.ResponseInstallment;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class ResponseInstallmentDao extends AcmAbstractDao<ResponseInstallment>
{

    @Override
    protected Class<ResponseInstallment> getPersistenceClass() {
        return ResponseInstallment.class;
    }

    @Transactional
    public List<ResponseInstallment> getResponseInstallmentByParentNumber(String parentNumber)
    {
        String queryText = "SELECT responseInstallment FROM ResponseInstallment responseInstallment WHERE responseInstallment.parentNumber = :parentNumber";

        TypedQuery<ResponseInstallment> query = getEm().createQuery(queryText, ResponseInstallment.class);
        query.setParameter("parentNumber", parentNumber);

        return  query.getResultList();

    }

    @Transactional
    public Boolean checkIfInstallmentIsAvailableForDwonload(String requestNumber)
    {
        List<ResponseInstallment> responseInstallments = getResponseInstallmentByParentNumber(requestNumber);
        ResponseInstallment responseInstallment = responseInstallments.get(responseInstallments.size()-1);
        Integer downloadAttempts = responseInstallment.getNumDownloadAttempts();

        if(responseInstallment.getMaxDownloadAttempts() >= 1 && responseInstallment.getNumDownloadAttempts()+1 <= responseInstallment.getMaxDownloadAttempts()
                && responseInstallment.getDueDate().after(new Date()))
        {
            downloadAttempts += 1;
            updateResponseInstallment(requestNumber, downloadAttempts);
            return true;
        }
        else
        {
            return false;
        }
    }


    private int updateResponseInstallment(String requestNumber, Integer downloadAttempts)
    {
        Query update = getEm().createQuery("UPDATE ResponseInstallment ri"
                + " SET ri.numDownloadAttempts = :downloadAttempts"
                + " WHERE ri.parentNumber = :requestNumber");
        update.setParameter("downloadAttempts", downloadAttempts);
        update.setParameter("requestNumber", requestNumber);
        return update.executeUpdate();
    }

}
