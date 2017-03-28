package com.armedia.acm.plugins.documentrepository.web;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import java.util.Date;

@Controller
@RequestMapping({"/api/v1/plugin/documentrepository", "/api/latest/plugin/documentrepository"})
public class DocumentRepositoryAPIController
{

    private DocumentRepositoryService documentRepositoryService;


    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DocumentRepository saveDocumentRepository(@RequestBody DocumentRepository in, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        try
        {
            // explicitly set modifier and modified to trigger transformer to reindex data
            // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
            in.setModifier(AuthenticationUtils.getUsername());
            in.setModified(new Date());
            return getDocumentRepositoryService().save(in, auth);
        } catch (PipelineProcessException | PersistenceException e)
        {
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
