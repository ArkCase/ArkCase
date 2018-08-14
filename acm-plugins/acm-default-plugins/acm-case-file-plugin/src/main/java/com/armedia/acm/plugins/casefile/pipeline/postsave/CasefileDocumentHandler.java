package com.armedia.acm.plugins.casefile.pipeline.postsave;
/*-
* #%L
* ACM Default Plugin: Case File
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

import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.FILE_ID;
import static com.armedia.acm.plugins.casefile.model.CaseFileConstants.NEW_FILE;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.admin.service.JsonPropertiesManagementService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.PDFCasefileDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;

public class CasefileDocumentHandler extends PDFCasefileDocumentGenerator<CaseFileDao, CaseFile>
        implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    /**
     * Logger instance.
     */
    private JsonPropertiesManagementService jsonPropertiesManagementService;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CaseFile casefile, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        String formsType = "";
        try
        {
            formsType = jsonPropertiesManagementService.getProperty("formsType").get("formsType").toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
        }

        if (!formsType.equals("frevvo"))
        {
            log.debug("Entering pipeline handler for case file with id [{}] and title [{}]", casefile.getId(), casefile.getTitle());

            // ensure the SQL of all prior handlers is visible to this handler
            getDao().getEm().flush();

            try
            {
                generatePdf(casefile.getId(), ctx);
            }
            catch (ParserConfigurationException e)
            {
                log.warn("Unable to generate pdf document for the case file with id [{}] and title [{}]", casefile.getId(),
                        casefile.getTitle());
                throw new PipelineProcessException(e);
            }

            log.debug("Exiting pipeline handler for object: [{}]", casefile);
        }
    }

    @Override
    public void rollback(CaseFile casefile, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        if (ctx.hasProperty(NEW_FILE))
        {
            boolean newFile = (boolean) ctx.getPropertyValue(NEW_FILE);
            if (newFile)
            {
                if (ctx.hasProperty(FILE_ID))
                {
                    Long fileId = (Long) ctx.getPropertyValue(FILE_ID);
                    try
                    {
                        getEcmFileService().deleteFile(fileId);
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.warn("Unable to delete ecm file with id [{}] for the case file with id [{}] and title [{}]", fileId,
                                casefile.getId(),
                                casefile.getTitle());
                        throw new PipelineProcessException(e);
                    }
                }
            }
        }
    }

    public void setJsonPropertiesManagementService(JsonPropertiesManagementService jsonPropertiesManagementService)
    {
        this.jsonPropertiesManagementService = jsonPropertiesManagementService;
    }

}
