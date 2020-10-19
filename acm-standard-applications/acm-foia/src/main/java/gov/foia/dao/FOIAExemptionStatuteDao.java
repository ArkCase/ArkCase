package gov.foia.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import gov.foia.model.FOIARequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FOIAExemptionStatuteDao extends AcmAbstractDao<ExemptionStatute>
{

    private AcmFolderService acmFolderService;
    private EntityManager entityManager;
    private CaseFileDao caseFileDao;

    @Override
    protected Class<ExemptionStatute> getPersistenceClass()
    {
        return ExemptionStatute.class;
    }

    @Transactional
    public void deleteExemptionStatute(Long id)
    {
        ExemptionStatute exemptionStatute = getEm().find(ExemptionStatute.class, id);
        if (exemptionStatute != null)
        {
            getEm().remove(exemptionStatute);
        }
    }

    public List<ExemptionStatute> getExemptionStatutesByParentObjectIdAndType(Long parentObjectId, String parentObjectType) {

        FOIARequest request = (FOIARequest) caseFileDao.find(parentObjectId);
        List<ExemptionStatute> listStatutesOnDocuments = new ArrayList<>();

        if (request.getQueue().getName().equals("Release")
                || (request.getGeneratedZipFlag() != null && request.getGeneratedZipFlag() == true)
                || request.getDispositionClosedDate() != null) {

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

            if (files != null) {
                List<Long> fileIds = files.stream()
                        .map(EcmFile::getFileId)
                        .collect(Collectors.toList());

                for (Long fileId : fileIds) {
                    listStatutesOnDocuments.addAll(getApprovedAndManualExemptionStatutesByFileId(fileId));
                }
            }
        }
        return listStatutesOnDocuments;

    }

    public List<ExemptionStatute> getApprovedAndManualExemptionStatutesByFileId(Long fileId)
    {
        String queryText = "SELECT statutes FROM ExemptionStatute statutes WHERE statutes.fileId = :fileId " +
                "AND statutes.exemptionStatus <> 'DRAFT' " +
                "GROUP BY statutes.exemptionStatute, statutes.exemptionStatus";
        TypedQuery<ExemptionStatute> query = getEm().createQuery(queryText, ExemptionStatute.class);
        query.setParameter("fileId", fileId);

        List<ExemptionStatute> exemptionStatuteList = query.getResultList();
        if (exemptionStatuteList == null)
        {
            exemptionStatuteList = new ArrayList<>();
        }
        return exemptionStatuteList;
    }

    public List<ExemptionStatute> getManuallyAddedStatuteOnRequestLevel(Long parentObjectId, String parentObjectType)
    {
        String queryText = "SELECT exemptionStatute " +
                "FROM ExemptionStatute exemptionStatute " +
                "JOIN FOIARequest request ON request.id = exemptionStatute.parentObjectId " +
                "WHERE exemptionStatute.parentObjectType = :parentObjectType " +
                "AND exemptionStatute.parentObjectId = :parentObjectId " +
                "ORDER BY exemptionStatute.exemptionStatute";

        TypedQuery<ExemptionStatute> exemptionStatuteTypedQuery = getEm().createQuery(queryText,
                ExemptionStatute.class);

        exemptionStatuteTypedQuery.setParameter("parentObjectType", parentObjectType.toUpperCase());
        exemptionStatuteTypedQuery.setParameter("parentObjectId", parentObjectId);
        List<ExemptionStatute> exemptionStatuteList = exemptionStatuteTypedQuery.getResultList();
        if (null == exemptionStatuteList)
        {
            exemptionStatuteList = new ArrayList<>();
        }
        return exemptionStatuteList;

    }

    public List<ExemptionStatute> findExemptionStatutesByFileId(Long fileId)
    {
        String queryText = "SELECT statutes FROM ExemptionStatute statutes WHERE statutes.fileId = :fileId";
        TypedQuery<ExemptionStatute> query = getEm().createQuery(queryText, ExemptionStatute.class);
        query.setParameter("fileId", fileId);

        List<ExemptionStatute> exemptionStatuteList = query.getResultList();
        if (exemptionStatuteList == null)
        {
            exemptionStatuteList = new ArrayList<>();
        }
        return exemptionStatuteList;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public AcmFolderService getAcmFolderService() {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }
}
