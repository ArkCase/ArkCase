package com.armedia.acm.services.ocr.service;

/*-
 * #%L
 * acm-ocr
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.SaveConfigurationException;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.service.MediaEngineService;
import com.armedia.acm.services.ocr.model.OCR;

/**
 * Created by Vladimir Cherepnalkovski
 */
public interface ArkCaseOCRService extends MediaEngineService<OCR>
{
    /**
     * This method will return OCR object for given fileId
     * *
     *
     * @param fileId
     *            - ID of the file
     * @return OCR object
     */
    @Override
    OCR getByFileId(Long fileId) throws GetMediaEngineException;

    /**
     * This method will return OCR object from QUEUE for given fileId
     * *
     *
     * @param fileId
     *            - ID of the file
     * @param statusType
     *            - OCR status type
     * @return OCR object
     */
    OCR getByFileIdAndStatus(Long fileId, MediaEngineStatusType statusType) throws GetMediaEngineException;

    /**
     * This method will verify that all required libraries are installed in order to enable OCR.
     *
     * @throws SaveConfigurationException
     */
    void verifyOCR() throws SaveConfigurationException;

}
