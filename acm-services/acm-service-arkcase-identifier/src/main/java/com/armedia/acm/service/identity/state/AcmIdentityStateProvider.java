package com.armedia.acm.service.identity.state;

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;
import com.armedia.acm.service.identity.model.AcmArkcaseIdentity;
import com.armedia.acm.service.identity.service.AcmArkcaseIdentityService;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class AcmIdentityStateProvider implements StateOfModuleProvider
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmArkcaseIdentityService acmArkcaseIdentityService;

    @Override
    public String getModuleName()
    {
        return "acm-identity";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmIdentityState acmIdentityState = new AcmIdentityState();
        try
        {
            AcmArkcaseIdentity identity = acmArkcaseIdentityService.getIdentity();
            acmIdentityState.setGlobalID(identity.getGlobalID());
            acmIdentityState.setInstanceID(identity.getInstanceID());
        }
        catch (AcmIdentityException e)
        {
            log.error("Not able to provide identity state.", e.getMessage());
        }
        return acmIdentityState;
    }

    public void setAcmArkcaseIdentityService(AcmArkcaseIdentityService acmArkcaseIdentityService)
    {
        this.acmArkcaseIdentityService = acmArkcaseIdentityService;
    }
}
