package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.dataaccess.dao.AcmAccessControlDefaultDao;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.enums.DefaultAccessControlSavePolicy;
import com.armedia.acm.services.dataaccess.service.DataAccessDefaultGenerator;
import com.armedia.acm.services.dataaccess.service.DataAccessDefaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by armdev on 7/9/14.
 */
public class DataAccessDefaultServiceImpl implements DataAccessDefaultService
{

    private DataAccessDefaultGenerator generator;
    private AcmAccessControlDefaultDao accessControlDefaultDao;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public AcmAccessControlDefault save(
            Long defaultAccessId,
            AcmAccessControlDefault toSave,
            Authentication authentication) throws AcmUserActionFailedException
    {
        AcmAccessControlDefault existing = getAccessControlDefaultDao().find(AcmAccessControlDefault.class, defaultAccessId);
        if ( existing == null )
        {
            throw new AcmUserActionFailedException("update", "default access", defaultAccessId,
                    "Default access with id '" + defaultAccessId + "' does not exist.", null);
        }

        // only the access decision, the discretionary access flag, and the modifier can be changed.
        existing.setModifier(authentication.getName());
        existing.setAccessDecision(toSave.getAccessDecision());
        existing.setAllowDiscretionaryUpdate(toSave.getAllowDiscretionaryUpdate());

        return getAccessControlDefaultDao().save(existing, DefaultAccessControlSavePolicy.OVERWRITE_EXISTING);
    }

    @Override
    @Transactional
    public void persistDefaultDataAccessControls(ApplicationContext applicationContext)
    {
        AcmApplication acmApplication = applicationContext.getBean(AcmApplication.class);

        log.info("Generating and saving default access control entries.");
        boolean debug = log.isDebugEnabled();

        List<AcmAccessControlDefault> defaultEntries = getGenerator().generateDefaultAccessFromApplication(acmApplication);

        if ( debug )
        {
            log.debug(defaultEntries.size() + " default entries to save; if they exist already they will not be updated.");
        }

        for ( AcmAccessControlDefault accessControlDefault : defaultEntries )
        {
            if ( debug )
            {
                log.debug("Persisting default ACL (" + accessControlDefault.getObjectType() + " | " +
                    accessControlDefault.getObjectState() + " | " + accessControlDefault.getAccessorType() + " | " +
                    accessControlDefault.getAccessLevel() + ")");
            }

            getAccessControlDefaultDao().save(accessControlDefault, DefaultAccessControlSavePolicy.KEEP_EXISTING);
        }
    }

    public DataAccessDefaultGenerator getGenerator()
    {
        return generator;
    }

    public void setGenerator(DataAccessDefaultGenerator generator)
    {
        this.generator = generator;
    }

    public AcmAccessControlDefaultDao getAccessControlDefaultDao()
    {
        return accessControlDefaultDao;
    }

    public void setAccessControlDefaultDao(AcmAccessControlDefaultDao accessControlDefaultDao)
    {
        this.accessControlDefaultDao = accessControlDefaultDao;
    }
}
