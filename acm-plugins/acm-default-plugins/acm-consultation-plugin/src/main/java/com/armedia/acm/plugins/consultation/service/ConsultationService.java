package com.armedia.acm.plugins.consultation.service;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationsByStatusDto;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public interface ConsultationService
{
    Consultation getConsultationById(long id);

    Consultation getConsultationByNumber(String consultationNumber);

    List<ConsultationsByStatusDto> getConsultationsByStatusAndByTimePeriod(TimePeriod numberOfDaysFromToday);

    List<Consultation> getNotClosedConsultationsByUser(String user) throws AcmObjectNotFoundException;

    @Transactional
    Consultation saveConsultation(Consultation in, Authentication auth, String ipAddress) throws PipelineProcessException;

    @Transactional
    Consultation saveConsultation(Consultation consultation, List<MultipartFile> files, Authentication authentication, String ipAddress)
            throws AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException, PipelineProcessException,
            IOException;

    @Transactional
    Consultation saveConsultation(Consultation consultation, Map<String, List<MultipartFile>> filesMap, Authentication authentication, String ipAddress)
            throws AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException, PipelineProcessException,
            IOException;
}
