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
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.complaint.service.PDFDocumentGenerator;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 18, 2016
 */
public class ComplaintDocumentHandler extends PDFDocumentGenerator<ComplaintDao, Complaint>
        implements PipelineHandler<Complaint, ComplaintPipelineContext>
{

    /**
     * Logger instance.
     */
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Complaint complaint, ComplaintPipelineContext ctx) throws PipelineProcessException
    {
        log.debug("Entering pipeline handler for object: [{}]", complaint);

        // ensure the SQL of all prior handlers is visible to this handler
        getDao().getEm().flush();

        try
        {
            generatePdf(complaint.getObjectType(), complaint.getId(), ctx);
        }
        catch (ParserConfigurationException e)
        {
            log.debug("Unable to generate pdf.");
            throw new PipelineProcessException(e);
        }

        log.debug("Exiting pipeline handler for object: [{}]", complaint);
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
                        log.warn("Unable to delete ecm file with id [{}]", fileId);
                        throw new PipelineProcessException(e);
                    }
                }
            }
            else
            {
                Long fileId = (Long) ctx.getPropertyValue(FILE_ID);
                EcmFile ecmFile = getEcmFileService().findById(fileId);
                Long currentFileVersion = Long.parseLong(ecmFile.getActiveVersionTag());
                log.warn("Trying to delete [{}] version of EcmFile with id [{}].", currentFileVersion, fileId);
            }
        }
    }

}
