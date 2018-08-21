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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.admin.service.JsonPropertiesManagementService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.PDFChangeCaseFileStateDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFChangeCaseFileStateDocumentGeneratorHandler extends PDFChangeCaseFileStateDocumentGenerator<CaseFileDao, CaseFile>
        implements PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private JsonPropertiesManagementService jsonPropertiesManagementService;
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private PDFChangeCaseFileStateDocumentGenerator pdfChangeCaseFileStateDocumentGenerator;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {

        String formsType = "";
        try
        {
            formsType = jsonPropertiesManagementService.getProperty("formsType").get("formsType").toString();
        }
        catch (Exception e)
        {
            log.error("Can't retrieve application property", e);
        }

        if (!formsType.equals("frevvo"))
        {
            generatePdf("CASE_FILE", form.getCaseId(), ctx);
        }
    }

    @Override
    public void rollback(ChangeCaseStatus form, CaseFilePipelineContext ctx) throws PipelineProcessException
    {
        if (ctx.hasProperty(ChangeCaseStateContants.NEW_FILE))
        {
            boolean newFile = (boolean) ctx.getPropertyValue(ChangeCaseStateContants.NEW_FILE);
            if (newFile)
            {
                if (ctx.hasProperty(ChangeCaseStateContants.FILE_ID))
                {
                    Long fileId = (Long) ctx.getPropertyValue(ChangeCaseStateContants.FILE_ID);
                    try
                    {
                        getEcmFileService().deleteFile(fileId);
                    }
                    catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                    {
                        log.warn("Unable to delete ecm file with id [{}] for the case file with id [{}]", fileId,
                                form.getId());
                        throw new PipelineProcessException(e);
                    }
                }
            }
        }
    }

    public PDFChangeCaseFileStateDocumentGenerator getPdfChangeCaseFileStateDocumentGenerator()
    {
        return pdfChangeCaseFileStateDocumentGenerator;
    }

    public void setPdfChangeCaseFileStateDocumentGenerator(PDFChangeCaseFileStateDocumentGenerator pdfChangeCaseFileStateDocumentGenerator)
    {
        this.pdfChangeCaseFileStateDocumentGenerator = pdfChangeCaseFileStateDocumentGenerator;
    }

    public JsonPropertiesManagementService getJsonPropertiesManagementService()
    {
        return jsonPropertiesManagementService;
    }

    public void setJsonPropertiesManagementService(JsonPropertiesManagementService jsonPropertiesManagementService)
    {
        this.jsonPropertiesManagementService = jsonPropertiesManagementService;
    }

}
