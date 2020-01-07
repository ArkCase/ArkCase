package com.armedia.acm.services.transcribe.service;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.mediaengine.service.MediaEngineService;
import com.armedia.acm.services.transcribe.exception.CompileMediaEngineException;
import com.armedia.acm.services.transcribe.model.Transcribe;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
public interface ArkCaseTranscribeService extends MediaEngineService<Transcribe>
{
    /**
     * This method will return if provided media duration is less than configured duration
     *
     * @param ecmFileVersion
     *            - Media file version
     * @return true/false
     */
    boolean isMediaDurationAllowed(EcmFileVersion ecmFileVersion);

    /**
     * This method will create word document for given MediaEngine object ID
     *
     * @param id
     *            - ID of the MediaEngine object
     * @return EcmFile object
     * @throws CompileMediaEngineException
     */
    EcmFile compile(Long id) throws CompileMediaEngineException;
}
