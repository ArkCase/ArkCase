package com.armedia.acm.plugins.consultation.pipeline;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 * Store all the consultation saving-related references in this context.
 */
public class ConsultationPipelineContext extends AbstractPipelineContext
{
    /**
     * Flag showing whether new consultation is created.
     */
    private boolean newConsultation;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * IP Address.
     */
    private String ipAddress;

    /*
     * Consultation
     */
    private Consultation consultation;

    /*
     * Change Consultation Status
     */
    private ChangeConsultationStatus changeConsultationStatus;

    public boolean isNewConsultation()
    {
        return newConsultation;
    }

    public void setNewConsultation(boolean newConsultation)
    {
        this.newConsultation = newConsultation;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Consultation getConsultation()
    {
        return consultation;
    }

    public void setConsultation(Consultation consultation)
    {
        this.consultation = consultation;
    }

    public ChangeConsultationStatus getChangeConsultationStatus()
    {
        return changeConsultationStatus;
    }

    public void setChangeConsultationStatus(ChangeConsultationStatus changeConsultationStatus)
    {
        this.changeConsultationStatus = changeConsultationStatus;
    }
}
