package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;

/**
 * Created by nebojsha on 01.06.2015.
 */
public class SplitCaseServiceImpl implements SplitCaseService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;

    @Override
    public CaseFile splitCase(Authentication auth,
                              String ipAddress,
                              SplitCaseOptions splitCaseOptions) throws MuleException, SplitCaseFileException, AcmUserActionFailedException, AcmCreateObjectFailedException {
        CaseFile toBeSplitted = caseFileDao.find(splitCaseOptions.getCaseFileId());
        if (toBeSplitted == null)
            throw new SplitCaseFileException("Case file with id = (" + splitCaseOptions.getCaseFileId() + ") not found");


        //clean all created,creator,modified, modifier in sub objects as well
        try {
            cleanRecursivelyAcmEntity(toBeSplitted);
        } catch (IllegalAccessException e) {
            log.error("Clean AcmEntity failed", e);
            throw new SplitCaseFileException("clean AcmEntity failed", e);
        }

        toBeSplitted.setId(null);

        //clean container
        toBeSplitted.setContainer(null);

        CaseFile saved = saveCaseService.saveCase(toBeSplitted, auth, ipAddress);

        return saved;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService) {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    private void cleanRecursivelyAcmEntity(AcmEntity entity) throws IllegalAccessException {
        entity.setCreated(null);
        entity.setCreator(null);
        entity.setModified(null);
        entity.setModifier(null);
        for (Field filed : entity.getClass().getFields()) {
            if (filed.getType().isAssignableFrom(AcmEntity.class)) {
                cleanRecursivelyAcmEntity((AcmEntity) filed.get(entity));
            }
        }
    }
}
