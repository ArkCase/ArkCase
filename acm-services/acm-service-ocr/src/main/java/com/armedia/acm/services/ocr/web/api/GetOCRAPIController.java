package com.armedia.acm.services.ocr.web.api;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.ocr.service.ArkCaseOCRService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Vladimir Cherepnalkovski
 */
@Controller
@RequestMapping({ "/api/v1/service/ocr", "/api/latest/service/ocr" })
public class GetOCRAPIController
{
    private ArkCaseOCRService arkCaseOCRService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine getOcr(@PathVariable(value = "id") Long id) throws GetMediaEngineException
    {
        return getArkCaseOCRService().get(id);
    }

    @RequestMapping(value = "/media/{mediaVersionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine getOcrByMediaId(@PathVariable(value = "mediaVersionId") Long mediaVersionId) throws GetMediaEngineException
    {
        return getArkCaseOCRService().getByMediaVersionId(mediaVersionId);
    }

    @RequestMapping(value = "/file/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MediaEngine getOcrByFileId(@PathVariable(value = "fileId") Long fileId) throws GetMediaEngineException
    {
        return getArkCaseOCRService().getByFileId(fileId);
    }

    public ArkCaseOCRService getArkCaseOCRService()
    {
        return arkCaseOCRService;
    }

    public void setArkCaseOCRService(ArkCaseOCRService arkCaseOCRService)
    {
        this.arkCaseOCRService = arkCaseOCRService;
    }
}
