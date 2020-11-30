package gov.foia.dao;

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
