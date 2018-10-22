package gov.foia.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.util.Map;

import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAObject;

/**
 * Interface for document generation based on templates.
 * 
 * @author bojan.milenkoski
 */
public interface DocumentGenerator
{
    /**
     * Generates the file from the given template and given substitutions. Then uploads the file in the CMIS repository
     * in targetCmisFolderId.
     * 
     * @param documentDescriptor
     *            the {@link FOIADocumentDescriptor} containing the template
     * @param acmObject
     *            the {@link AcmObject} from which the template values are populated
     * @param targetCmisFolderId
     *            the CMIS folder ID where the generated file will be uploaded
     * @param targetFilename
     *            the uploaded file name
     * @param substitutions
     *            substitutions to use for the template. Some document generators have their own substitutions, so these
     *            won't be used
     * @return returns the saved {@link EcmFile} instance
     * @throws DocumentGeneratorException
     *             when an error occurs while generating the document
     */
    EcmFile generateAndUpload(FOIADocumentDescriptor documentDescriptor, FOIAObject acmObject, String targetCmisFolderId,
                              String targetFilename, Map<String, String> substitutions) throws DocumentGeneratorException;
}
