package com.armedia.acm.plugins.consultation.listener;

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

import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatusConstants;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.task.model.BuckslipProcessStateEvent;

import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ChangeConsultationStatusOnBuckslipEventListener implements ApplicationListener<BuckslipProcessStateEvent>
{
    private ConsultationDao consultationDao;

    @Override
    public void onApplicationEvent(BuckslipProcessStateEvent buckslipProcessStateEvent)
    {
        Map<String, Object> processVariables = (Map<String, Object>) buckslipProcessStateEvent.getSource();

        String parentObjectType = (String) processVariables.getOrDefault(ConsultationConstants.PARENT_OBJECT_TYPE, "");
        Long parentObjectId = (Long) processVariables.getOrDefault(ConsultationConstants.PARENT_OBJECT_ID, null);

        if (ConsultationConstants.OBJECT_TYPE.equals(parentObjectType) && Objects.nonNull(parentObjectId))
        {
            Consultation consultation = getConsultationDao().find(parentObjectId);

            switch (buckslipProcessStateEvent.getBuckslipProcessState())
            {
            case INITIALIZED:
                consultation.setStatus(ChangeConsultationStatusConstants.STATUS_IN_APPROVAL);
                break;
            case WITHDRAWN:
                consultation.setStatus(ChangeConsultationStatusConstants.STATUS_DRAFT);
                break;
            case COMPLETED:
                consultation.setStatus(ChangeConsultationStatusConstants.STATUS_APPROVED);
                break;
            }

            getConsultationDao().save(consultation);
        }
    }

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
    }
}
