package com.armedia.acm.plugins.documentrepository.web;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping({"/api/v1/plugin/documentrepository", "/api/latest/plugin/documentrepository"})
public class DocumentRepositoryAPIController
{

    final private Logger log = LoggerFactory.getLogger(getClass());
    private DocumentRepositoryService documentRepositoryService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DocumentRepository saveDocumentRepository(@RequestBody DocumentRepository in, Authentication auth)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg
    {
        DocumentRepository existingDocRepo = getDocumentRepositoryService().findByName(in.getName());
        if (existingDocRepo != null)
        {
            log.error("Duplicate Document Repository name: {}", in.getName());
            AcmAppErrorJsonMsg error = new AcmAppErrorJsonMsg(String.format(
                    "Document Repository with name: [%s] already exists!", in.getName()),
                    DocumentRepositoryConstants.OBJECT_TYPE, "name", null);
            throw error;
        }
        try
        {
            // explicitly set modifier and modified to trigger transformer to reindex data
            // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
            in.setModifier(AuthenticationUtils.getUsername());
            in.setModified(new Date());
            return getDocumentRepositoryService().save(in, auth);
        } catch (PipelineProcessException | TransactionException e)
        {
            log.error("Could not save Document Repository: {}", in.getName(), e);
            throw new AcmCreateObjectFailedException(DocumentRepositoryConstants.OBJECT_TYPE, e.getMessage(), e);
        }
    }

    public DocumentRepositoryService getDocumentRepositoryService()
    {
        return documentRepositoryService;
    }

    public void setDocumentRepositoryService(DocumentRepositoryService documentRepositoryService)
    {
        this.documentRepositoryService = documentRepositoryService;
    }
}
