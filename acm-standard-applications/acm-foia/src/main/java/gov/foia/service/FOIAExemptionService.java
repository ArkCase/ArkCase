package gov.foia.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

import java.util.Date;
import java.util.List;

import gov.foia.dao.FOIAExemptionCodeDao;

/**
 * Created by ana.serafimoska
 */
public class FOIAExemptionService
{

    private Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionCodeDao foiaExemptionCodeDao;

    public List<ExemptionCode> getExemptionCodes(Long parentObjectId, String parentObjectType) throws GetExemptionCodeException
    {
        log.info("Finding  exemption codes for objectId: {}", parentObjectId);
        try
        {
            return getFoiaExemptionCodeDao().getExemptionCodesByParentObjectIdAndType(parentObjectId, parentObjectType);
        }
        catch (Exception e)
        {
            log.error("Finding  exemption codes for objectId: {} failed", parentObjectId);
            throw new GetExemptionCodeException("Unable to get exemption codes for objectId: {}" + parentObjectId, e);
        }
    }

    @Transactional
    public void copyFileWithExemptionCodes(EcmFile originalFile, EcmFile copiedFile)
    {
        Long copiedFileId = copiedFile.getFileId();
        List<ExemptionCode> exemptionCodeList = getFoiaExemptionCodeDao().findExemptionCodesByFileId(originalFile.getFileId());

            for (ExemptionCode exemptionCode : exemptionCodeList)
            {
                ExemptionCode exemptionCodeObj = new ExemptionCode();
                exemptionCodeObj.setParentObjectType(exemptionCode.getParentObjectType());
                exemptionCodeObj.setExemptionCode(exemptionCode.getExemptionCode());
                exemptionCodeObj.setExemptionStatus(exemptionCode.getExemptionStatus());
                exemptionCodeObj.setCreated(exemptionCode.getCreated());
                exemptionCodeObj.setCreator(exemptionCode.getCreator());
                exemptionCodeObj.setExemptionStatute(exemptionCode.getExemptionStatute());
                exemptionCodeObj.setFileId(copiedFileId);
                exemptionCodeObj.setFileVersion(exemptionCode.getFileVersion());
                exemptionCodeObj.setManuallyFlag(exemptionCode.getManuallyFlag());
                getFoiaExemptionCodeDao().save(exemptionCodeObj);
            }

    }

    @Transactional
    public void migrateExemptionCodes()
    {
        Query query = getFoiaExemptionCodeDao().queryExistingCodes();

        List<Object[]> resultList = query.getResultList();
        for (Object[] record : resultList)
        {
            ExemptionCode exemptionCodeObj = new ExemptionCode();
            exemptionCodeObj.setParentObjectType("DOCUMENT");
            exemptionCodeObj.setExemptionCode((String) record[0]);
            exemptionCodeObj.setExemptionStatus((String) record[1]);
            exemptionCodeObj.setCreator((String) record[2]);
            exemptionCodeObj.setCreated((Date) record[3]);
            exemptionCodeObj.setExemptionStatute((String) record[4]);
            exemptionCodeObj.setFileId((Long) record[5]);
            exemptionCodeObj.setFileVersion((String) record[6]);
            exemptionCodeObj.setManuallyFlag((Boolean) record[7]);
            getFoiaExemptionCodeDao().save(exemptionCodeObj);
        }

    }

    public FOIAExemptionCodeDao getFoiaExemptionCodeDao()
    {
        return foiaExemptionCodeDao;
    }

    public void setFoiaExemptionCodeDao(FOIAExemptionCodeDao foiaExemptionCodeDao)
    {
        this.foiaExemptionCodeDao = foiaExemptionCodeDao;
    }
}
