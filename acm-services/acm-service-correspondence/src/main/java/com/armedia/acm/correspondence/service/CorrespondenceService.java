package com.armedia.acm.correspondence.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class CorrespondenceService
{
    private SpringContextHolder springContextHolder;
    private CorrespondenceGenerator correspondenceGenerator;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private static final String TEMP_FILE_PREFIX = "template-";
    private static final String TEMP_FILE_SUFFIX = ".docx";



    /**
     * For use from MVC controllers and any other client with an Authentication object.
     * @param authentication
     * @param templateName
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws AcmCreateObjectFailedException
     */
    public EcmFile generate(
            Authentication authentication,
            String templateName,
            String parentObjectType,
            Long parentObjectId,
            String targetCmisFolderId) throws IOException, IllegalArgumentException, AcmCreateObjectFailedException
    {
        CorrespondenceTemplate template = findTemplate(templateName);

        File file = null;

        try
        {
            file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

            log.debug("writing correspondence to file: " + file.getCanonicalPath());

            FileOutputStream fosToWriteFile = new FileOutputStream(file);
            FileInputStream fisForUploadToEcm = new FileInputStream(file);

            EcmFile retval = getCorrespondenceGenerator().generateCorrespondence(
                    authentication,
                    parentObjectType,
                    parentObjectId,
                    targetCmisFolderId,
                    template,
                    new Object[] { parentObjectId },
                    fosToWriteFile,
                    fisForUploadToEcm);

            log.debug("Correspondence CMIS ID: " + retval.getEcmFileId());

            // TODO: raise event with IP address



            return retval;
        }
        finally
        {
            if ( file != null )
            {
                FileUtils.deleteQuietly(file);
            }
        }

    }

    private CorrespondenceTemplate findTemplate(String templateName)
    {
        Collection<CorrespondenceTemplate> templates =
                getSpringContextHolder().getAllBeansOfType(CorrespondenceTemplate.class).values();
        for ( CorrespondenceTemplate template : templates )
        {
            if ( templateName.equalsIgnoreCase(template.getTemplateFilename()))
            {
                return template;
            }
        }

        throw new IllegalArgumentException("Template '" + templateName + "' is not a registered template name!");
    }

    /**
     * Helper method for use from Activiti and other clients with no direct access to an Authentication, but in
     * the call stack of a Spring MVC authentication... so there is an Authentication in the Spring Security
     * context holder.
     */
    public EcmFile generate(
            String templateName,
            String parentObjectType,
            Long parentObjectId,
            String targetCmisFolderId
    ) throws IOException, IllegalArgumentException, AcmCreateObjectFailedException
    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        return generate(currentUser, templateName, parentObjectType, parentObjectId, targetCmisFolderId);
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public CorrespondenceGenerator getCorrespondenceGenerator()
    {
        return correspondenceGenerator;
    }

    public void setCorrespondenceGenerator(CorrespondenceGenerator correspondenceGenerator)
    {
        this.correspondenceGenerator = correspondenceGenerator;
    }
}
