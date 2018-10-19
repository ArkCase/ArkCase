package gov.foia.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAObject;

public class CorrespondenceDocumentGenerator implements DocumentGenerator
{

    private CorrespondenceService correspondenceService;

    @Override
    public EcmFile generateAndUpload(FOIADocumentDescriptor documentDescriptor, FOIAObject acmObject, String targetCmisFolderId,
            String targetFilename, Map<String, String> substitutions) throws DocumentGeneratorException
    {
        try
        {        
            Collection<CorrespondenceTemplate> templates = getCorrespondenceService().getActiveVersionTemplates();
            CorrespondenceTemplate template = templates.stream().filter(t -> t.getLabel().equals(documentDescriptor.getTemplate())).findFirst().get();

            return getCorrespondenceService().generate(template.getTemplateFilename(), acmObject.getObjectType(),
                    acmObject.getId(),
                    targetCmisFolderId);
        }
        catch (IllegalArgumentException | IOException | AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            throw new DocumentGeneratorException(
                    "Failed to generate correspondence document for objectId: [" + acmObject.getId() + "], objectType: ["
                            + acmObject.getObjectType() + "] and template:[" + documentDescriptor.getTemplate() + "]",
                    e);
        }
    }

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }
}
