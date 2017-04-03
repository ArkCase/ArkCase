package com.armedia.acm.plugins.documentrepository.web;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Objects;

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
        if (existingDocRepo != null && (in.getId() == null || !Objects.equals(in.getId(), existingDocRepo.getId())))
        {
            log.warn("Duplicate Document Repository name: {}", in.getName());
            throw new AcmAppErrorJsonMsg(String.format(
                    "Document Repository with name: [%s] already exists!", in.getName()),
                    DocumentRepositoryConstants.OBJECT_TYPE, "name", null);
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

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DocumentRepository findById(@PathVariable(value = "id") Long id) throws AcmObjectNotFoundException
    {
        DocumentRepository docRepo = getDocumentRepositoryService().findById(id);

        if (docRepo == null)
        {
            log.warn("Document Repository with id: [{}] not found!", id);
            throw new AcmObjectNotFoundException(DocumentRepositoryConstants.OBJECT_TYPE, id,
                    String.format("Document Repository with id: [%d] not found!", id));
        }
        return docRepo;
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
