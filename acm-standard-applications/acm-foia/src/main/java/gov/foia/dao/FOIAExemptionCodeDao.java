package gov.foia.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.services.exemption.dao.ExemptionCodeDao;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.model.ExemptionConstants;

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

        List<ExemptionCode> combineResult = new ArrayList<>();
        FOIARequest request = (FOIARequest) caseFileDao.find(parentObjectId);

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
                List<ExemptionCode> listCodesOnDocuments;
                for (Long fileId : fileIds)
                {
                    listCodesOnDocuments = getApprovedAndManualExemptionCodesByFileId(fileId);
                    List<ExemptionCode> filterDocumentCodesList = filterExemptionCodes(listCodesOnDocuments);
                    combineResult.addAll(filterDocumentCodesList);
                }
            }
        }

        List<ExemptionCode> listCodesOnRequest = getManuallyAddedCodesOnRequestLevel(parentObjectId, parentObjectType);
        combineResult.addAll(listCodesOnRequest);
        List<ExemptionCode> finalList = filterExemptionCodes(combineResult);
        return finalList;

    }

    public List<ExemptionCode> filterExemptionCodes(List<ExemptionCode> exemptionCodeList)
    {
        List<ExemptionCode> uniqueExemptionCodesList = new ArrayList<>();
        exemptionCodeList.forEach(item -> {
            boolean isDuplicate = uniqueExemptionCodesList.stream()
                    .anyMatch(newItem -> newItem.getExemptionCode().equals(item.getExemptionCode()));

            if (isDuplicate)
            {
                if (item.getExemptionStatus().equals(ExemptionConstants.EXEMPTION_STATUS_APPROVED))
                {
                    List<String> codesArray = uniqueExemptionCodesList.stream()
                            .map(ExemptionCode::getExemptionCode).collect(Collectors.toList());
                    int index = codesArray.indexOf(item.getExemptionCode());
                    uniqueExemptionCodesList.set(index, item);
                }
            }
            else
            {
                uniqueExemptionCodesList.add(item);
            }
        });
        return uniqueExemptionCodesList;
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
