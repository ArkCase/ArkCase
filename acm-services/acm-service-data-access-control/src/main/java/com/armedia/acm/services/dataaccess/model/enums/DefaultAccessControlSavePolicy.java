package com.armedia.acm.services.dataaccess.model.enums;

import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created by armdev on 7/9/14.
 */
public enum DefaultAccessControlSavePolicy
{
    KEEP_EXISTING
    {
        @Override
        public AcmAccessControlDefault persist(AcmAccessControlDefault in, AcmAccessControlDefault existing, EntityManager entityManager)
        {
            if ( existing != null )
            {
                return existing;
            }

            entityManager.persist(in);
            return in;
        }
    },
    OVERWRITE_EXISTING
    {
        @Override
        public AcmAccessControlDefault persist(AcmAccessControlDefault in, AcmAccessControlDefault existing, EntityManager entityManager)
        {
            AcmAccessControlDefault toSave;
            if ( existing != null )
            {
                toSave = existing;
                toSave.setAllowDiscretionaryUpdate(in.getAllowDiscretionaryUpdate());
                toSave.setModifier(in.getModifier());
                toSave.setAccessDecision(in.getAccessDecision());
                toSave.setModified(new Date());
                // other fields cannot be changed
            }
            else
            {
                toSave = in;
            }

            entityManager.persist(toSave);
            return toSave;
        }
    };

    public abstract AcmAccessControlDefault persist(
            AcmAccessControlDefault in,
            AcmAccessControlDefault existing,
            EntityManager entityManager);
}
