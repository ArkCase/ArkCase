package gov.privacy.web.api;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.camelcontext.exception.ArkCaseFileNameAlreadyExistsException;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.privacy.service.SaveSARService;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/privacy", "/api/latest/plugin/privacy" })
public class SaveSubjectAccessRequestAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private SaveSARService saveSARService;

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CASE_FILE', 'saveCase')")
    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFile saveSAR(@RequestBody CaseFile in, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        return getSaveSARService().saveSAR(in, null, session, auth);
    }

    @RequestMapping(value = "/massAssigment", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFile saveMassAssignedSAR(@RequestBody CaseFile in, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        return getSaveSARService().saveSAR(in, null, session, auth);
    }

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CASE_FILE', 'saveCase')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public CaseFile saveSARMultipart(@RequestPart(name = "casefile") CaseFile in,
                                     MultipartHttpServletRequest request, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg
    {

        Map<String, List<MultipartFile>> files = new HashMap<>();
        MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();

        for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet()) {
            String type = entry.getKey();
            if (!"casefile".equalsIgnoreCase(type)){
                final List<MultipartFile> attachmentsList = entry.getValue();

                files.put(type, attachmentsList);
            }
        }

        try
        {
            return getSaveSARService().saveSAR(in, files, session, auth);
        }
        catch (ArkCaseFileNameAlreadyExistsException e)
        {
            throw new AcmAppErrorJsonMsg("file with that name already exists", "FILE", "duplicateName", e);
        }

    }

    /**
     * @return the saveSARService
     */
    public SaveSARService getSaveSARService()
    {
        return saveSARService;
    }

    /**
     * @param saveSARService
     *            the saveSARService to set
     */
    public void setSaveSARService(SaveSARService saveSARService)
    {
        this.saveSARService = saveSARService;
    }

}
