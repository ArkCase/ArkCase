package com.armedia.acm.services.dataaccess.model.enums;

import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEntry;

import javax.persistence.EntityManager;
import java.util.Date;

public enum EntryAccessControlSavePolicy
{
    OVERWRITE_EXISTING
    {
        @Override
        public AcmAccessControlEntry persist(AcmAccessControlEntry in, AcmAccessControlEntry existing, EntityManager entityManager)
        {
            AcmAccessControlEntry toSave;
            if ( existing != null )
            {
                toSave = existing;
                toSave.setObjectState(in.getObjectState());
                toSave.setAccessorType(in.getAccessorType());
                toSave.setAccessDecision(in.getAccessDecision());
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

    public abstract AcmAccessControlEntry persist(
            AcmAccessControlEntry in,
            AcmAccessControlEntry existing,
            EntityManager entityManager);
}
