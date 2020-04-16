package gov.foia.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.services.exemption.dao.ExemptionCodeDao;
import com.armedia.acm.services.exemption.model.ExemptionCode;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gov.foia.model.FOIARequest;

/**
 * Created by ana.serafimoska
 */
public class FOIAExemptionCodeDao extends AcmAbstractDao<ExemptionCode>
{
    private AcmFolderService acmFolderService;
    private CaseFileDao caseFileDao;
    private ExemptionCodeDao exemptionCodeDao;

    @Override
    protected Class<ExemptionCode> getPersistenceClass()
    {
        return ExemptionCode.class;
    }

    public List<ExemptionCode> getExemptionCodesByParentObjectIdAndType(Long parentObjectId, String parentObjectType)
    {

        FOIARequest request = (FOIARequest) caseFileDao.find(parentObjectId);
        List<ExemptionCode> listCodesOnDocuments = new ArrayList<>();

        if (request.getQueue().getName().equals("Release")
                || (request.getGeneratedZipFlag() != null && request.getGeneratedZipFlag() == true)
                || request.getDispositionClosedDate() != null)
        {

            String queryText = "SELECT af.id " +
                    "FROM AcmContainer ac " +
                    "JOIN AcmFolder af ON af.parentFolder.id = ac.folder.id " +
                    "WHERE ac.containerObjectId = :parentObjectId " +
                    "AND ac.containerObjectType = :parentObjectType " +
                    "AND af.name = '03 Response'";

            Query query = getEm().createQuery(queryText);
            query.setParameter("parentObjectId", parentObjectId);
            query.setParameter("parentObjectType", parentObjectType);

            Long result = (Long) query.getSingleResult();
            List<EcmFile> files = getAcmFolderService().getFilesInFolderAndSubfolders(result);

            if (files != null)
            {
                List<Long> fileIds = files.stream()
                        .map(EcmFile::getFileId)
                        .collect(Collectors.toList());

                for (Long fileId : fileIds)
                {
                    listCodesOnDocuments = getApprovedAndManualExemptionCodesByFileId(fileId);
                }
            }
        }
        return listCodesOnDocuments;

    }

    public List<ExemptionCode> getManuallyAddedCodesOnRequestLevel(Long parentObjectId, String parentObjectType)
    {
        String queryText = "SELECT exemptionCode " +
                "FROM ExemptionCode exemptionCode " +
                "JOIN FOIARequest request ON request.id = exemptionCode.parentObjectId " +
                "WHERE exemptionCode.parentObjectType = :parentObjectType " +
                "AND exemptionCode.parentObjectId = :parentObjectId " +
                "ORDER BY exemptionCode.exemptionCode";

        TypedQuery<ExemptionCode> exemptionCodeTypedQuery = getEm().createQuery(queryText,
                ExemptionCode.class);

        exemptionCodeTypedQuery.setParameter("parentObjectType", parentObjectType.toUpperCase());
        exemptionCodeTypedQuery.setParameter("parentObjectId", parentObjectId);
        List<ExemptionCode> exemptionCodeList = exemptionCodeTypedQuery.getResultList();
        if (null == exemptionCodeList)
        {
            exemptionCodeList = new ArrayList<>();
        }
        return exemptionCodeList;

    }

    public List<ExemptionCode> getApprovedAndManualExemptionCodesByFileId(Long fileId)
    {
        String queryText = "SELECT codes FROM ExemptionCode codes WHERE codes.fileId = :fileId " +
                "AND codes.exemptionStatus <> 'DRAFT' " +
                "GROUP BY codes.exemptionCode, codes.exemptionStatus";
        TypedQuery<ExemptionCode> query = getEm().createQuery(queryText, ExemptionCode.class);
        query.setParameter("fileId", fileId);

        List<ExemptionCode> exemptionCodeList = query.getResultList();
        if (exemptionCodeList == null)
        {
            exemptionCodeList = new ArrayList<>();
        }
        return exemptionCodeList;
    }

    public List<ExemptionCode> findExemptionCodesByFileId(Long fileId)
    {
        String queryText = "SELECT codes FROM ExemptionCode codes WHERE codes.fileId = :fileId";
        TypedQuery<ExemptionCode> query = getEm().createQuery(queryText, ExemptionCode.class);
        query.setParameter("fileId", fileId);

        List<ExemptionCode> exemptionCodeList = query.getResultList();
        if (exemptionCodeList == null)
        {
            exemptionCodeList = new ArrayList<>();
        }
        return exemptionCodeList;
    }

    public Query queryExistingCodes()
    {
        String queryExistingCodesText = "SELECT fo_exemption_code, fo_exemption_status, fo_code_creator, fo_code_created, fo_exemption_statute, ecm_file_id, ecm_file_version, fo_exemption_code_manually_flag "
                +
                " FROM foia_file_exemption_code";

        return getEm().createNativeQuery(queryExistingCodesText);
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public ExemptionCodeDao getExemptionCodeDao()
    {
        return exemptionCodeDao;
    }

    public void setExemptionCodeDao(ExemptionCodeDao exemptionCodeDao)
    {
        this.exemptionCodeDao = exemptionCodeDao;
    }

}
