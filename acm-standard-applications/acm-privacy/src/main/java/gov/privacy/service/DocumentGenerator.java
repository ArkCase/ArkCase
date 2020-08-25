package gov.privacy.service;

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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.util.Map;

import gov.privacy.model.SARDocumentDescriptor;
import gov.privacy.model.SARObject;

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
     *            the {@link SARDocumentDescriptor} containing the template
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
    EcmFile generateAndUpload(SARDocumentDescriptor documentDescriptor, SARObject acmObject, String targetCmisFolderId,
                              String targetFilename, Map<String, String> substitutions)
            throws DocumentGeneratorException;
}
