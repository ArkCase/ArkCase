/**
 *
 */
package com.armedia.acm.plugins.complaint.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import static com.armedia.acm.plugins.complaint.model.ComplaintConstants.FILE_ID;
import static com.armedia.acm.plugins.complaint.model.ComplaintConstants.NEW_FILE;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.PDFComplaintDocumentGenerator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.xml.parsers.ParserConfigurationException;

public class ComplaintDocumentHandler extends PDFComplaintDocumentGenerator<ComplaintDao, Complaint>
        implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private FormsTypeCheckService formsTypeCheckService;

    @Override
    public void execute(Complaint complaint, ComplaintPipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {

            log.debug("Entering pipeline handler for complaint with id [{}] and title [{}]", complaint.getId(), complaint.getTitle());

            // ensure the SQL of all prior handlers is visible to this handler
            getDao().getEm().flush();

            try
            {
                generatePdf(complaint.getId(), ctx);
            }
            catch (ParserConfigurationException e)
            {
                log.warn("Unable to generate pdf document for the complaint with id [{}] and title [{}]", complaint.getId(),
                        complaint.getTitle());
                throw new PipelineProcessException(e);
            }

            log.debug("Exiting pipeline handler for object: [{}]", complaint);
        }
    }

    @Override
    public void rollback(Complaint complaint, ComplaintPipelineContext ctx) throws PipelineProcessException
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
                        log.warn("Unable to delete ecm file with id [{}] for the complaint with id [{}] and title [{}]", fileId,
                                complaint.getId(),
                                complaint.getTitle());
                        throw new PipelineProcessException(e);
                    }
                }
            }
        }
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService)
    {
        this.formsTypeCheckService = formsTypeCheckService;
    }
}
