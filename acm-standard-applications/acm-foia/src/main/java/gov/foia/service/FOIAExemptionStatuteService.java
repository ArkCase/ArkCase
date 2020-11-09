package gov.foia.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.exemption.model.ExemptionConstants;
import gov.foia.dao.FOIAExemptionStatuteDao;
import gov.foia.exception.DeleteExemptionStatuteException;
import gov.foia.exception.GetExemptionStatuteException;
import gov.foia.exception.SaveExemptionStatuteException;
import gov.foia.model.ExemptionStatute;
import gov.foia.model.ExemptionStatuteConstants;
import gov.foia.model.ExemptionStatuteEventPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class FOIAExemptionStatuteService
{
    private Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionStatuteDao foiaExemptionStatuteDao;
    private ExemptionStatuteEventPublisher exemptionStatuteEventPublisher;
    private EcmFileDao ecmFileDao;

    public List<ExemptionStatute> getExemptionStatutesOnRequest(Long parentObjectId, String parentObjectType)
            throws GetExemptionStatuteException
    {
        log.info("Finding  exemption statutes for objectId: {}", parentObjectId);
        try
        {
            List<ExemptionStatute> combineResult = new ArrayList<>();
            List<ExemptionStatute> listStatutesOnDocument = getFoiaExemptionStatuteDao()
                    .getExemptionStatutesForFilesInResponseFolder(parentObjectId, parentObjectType);
            combineResult.addAll(listStatutesOnDocument);

            List<ExemptionStatute> listStatutesOnRequest = getFoiaExemptionStatuteDao()
                    .getExemptionStatutesOnRequestLevel(parentObjectId);
            combineResult.addAll(listStatutesOnRequest);
            return combineResult;
        }
        catch (Exception e)
        {
            log.error("Finding  exemption statutes for objectId: {} failed", parentObjectId);
            throw new GetExemptionStatuteException("Unable to get exemption statutes for objectId: {}" + parentObjectId, e);
        }
    }

    public ExemptionStatute saveExemptionStatute(ExemptionStatute exemptionStatute) throws SaveExemptionStatuteException
    {

        log.info("Saving Exemption statutes [{}]", exemptionStatute.getExemptionStatute());
        try
        {
            ExemptionStatute exStatute = new ExemptionStatute();
            exStatute.setExemptionStatute(exemptionStatute.getExemptionStatute());
            exStatute.setExemptionStatus(ExemptionStatuteConstants.EXEMPTION_STATUTE_STATUS_MANUAL);
            exStatute.setParentObjectId(exemptionStatute.getParentObjectId());
            exStatute.setParentObjectType(exemptionStatute.getParentObjectType());
            ExemptionStatute saved = getFoiaExemptionStatuteDao().save(exStatute);
            getExemptionStatuteEventPublisher().publishExemptionStatuteCreatedEvent(saved);
            return exStatute;
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Statutes [{}] failed", exemptionStatute.getExemptionStatute());
            throw new SaveExemptionStatuteException("Unable to save exemption statute " + exemptionStatute.getExemptionStatute(), e);
        }
    }

    @Transactional
    public List<ExemptionStatute> getExemptionStatutesOnDocument(Long fileId) throws GetExemptionStatuteException
    {
        log.info("Finding  exemption statutes for file: {}", fileId);
        try
        {
            return getFoiaExemptionStatuteDao().findExemptionStatutesByFileId(fileId);
        }
        catch (Exception e)
        {
            log.error("Finding  exemption statutes for file: {} failed", fileId);
            throw new GetExemptionStatuteException("Unable to get exemption statutes for objectId: " + fileId, e);
        }
    }

    @Transactional
    public ExemptionStatute saveExemptionStatutesOnDocument(Long fileId, ExemptionStatute exemptionStatute)
            throws SaveExemptionStatuteException
    {
        log.info("Saving Exemption statutes [{}] manually", exemptionStatute.getExemptionStatute());
        try
        {
            // check if such database record exists
            EcmFile ecmFile = ecmFileDao.find(fileId);

            ExemptionStatute exStatute = new ExemptionStatute();
            exStatute.setExemptionStatute(exemptionStatute.getExemptionStatute());
            exStatute.setExemptionStatus(ExemptionStatuteConstants.EXEMPTION_STATUTE_STATUS_MANUAL);
            exStatute.setParentObjectType(exemptionStatute.getParentObjectType());
            exStatute.setFileId(fileId);
            exStatute.setFileVersion(ecmFile.getActiveVersionTag());
            ExemptionStatute saved = getFoiaExemptionStatuteDao().save(exStatute);
            getExemptionStatuteEventPublisher().publishExemptionStatuteCreatedEvent(saved);
            return exStatute;
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Statute [{}] manually failed", exemptionStatute);
            throw new SaveExemptionStatuteException("Unable to save exemption statute " + exemptionStatute + " manually", e);
        }
    }

    public void deleteExemptionStatute(Long statuteId) throws DeleteExemptionStatuteException
    {

        log.info("Deleting exemption statute with id: {}", statuteId);
        try
        {
            ExemptionStatute exemptionStatute = getFoiaExemptionStatuteDao().find(statuteId);
            getFoiaExemptionStatuteDao().deleteExemptionStatute(statuteId);
            getExemptionStatuteEventPublisher().publishExemptionStatuteDeletedEvent(exemptionStatute);
        }
        catch (Exception e)
        {
            log.error("Delete failed for exemption statute with id: " + statuteId);
            throw new DeleteExemptionStatuteException("Unable to delete exemption statute with id: " + statuteId, e);
        }

    }

    public FOIAExemptionStatuteDao getFoiaExemptionStatuteDao()
    {
        return foiaExemptionStatuteDao;
    }

    public void setFoiaExemptionStatuteDao(FOIAExemptionStatuteDao foiaExemptionStatuteDao)
    {
        this.foiaExemptionStatuteDao = foiaExemptionStatuteDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ExemptionStatuteEventPublisher getExemptionStatuteEventPublisher()
    {
        return exemptionStatuteEventPublisher;
    }

    public void setExemptionStatuteEventPublisher(ExemptionStatuteEventPublisher exemptionStatuteEventPublisher)
    {
        this.exemptionStatuteEventPublisher = exemptionStatuteEventPublisher;
    }
}
