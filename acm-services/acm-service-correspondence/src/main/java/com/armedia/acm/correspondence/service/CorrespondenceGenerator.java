package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 12/15/14.
 */
public class CorrespondenceGenerator
{
    @PersistenceContext
    private EntityManager entityManager;

    private PoiWordGenerator wordGenerator;

    private EcmFileService ecmFileService;

    private String correspondenceFolderName;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    protected static final String WORD_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    protected static final String CORRESPONDENCE_CATEGORY = "CORRESPONDENCE";

    /**
     * Generate correspondence based on the supplied template, and store the correspondence in the ECM repository
     * under the supplied parent object.
     *
     * @param user User who has caused the correspondence to be generated.
     * @param parentObjectType Parent object type, e.g. CASE_FILE, COMPLAINT, TASK.
     * @param parentObjectId Parent object ID
     * @param parentObjectName Parent object name.
     * @param targetFolderCmisId CMIS object ID of the folder in which to file the correspondence; usually the folder
     *                           belonging to the parent object.
     * @param template Correspondence template (which stores information on the Word template file name, JPA query
     *                 to find the correspondence data fields, etc).
     * @param queryArguments Actual arguments to pass to the JPA query.  Currently only an object ID is supported.
     * @param correspondenceOutputStream Output stream to write the template to; the correspondenceOutputStream and
     *                                   correspondenceInputStream must be based on the same object, e.g. the same
     *                                   Java File object.
     * @param correspondenceInputStream Input stream used to upload the correspondence into the CMIS repository.  The
     *                                  correspondenceOutputStream and correspondenceInputStream must be based on the
     *                                  same object, e.g. the same Java File object.
     * @return
     * @throws IOException If the correspondence could be be written to the correspondenceOutputStream.
     * @throws AcmCreateObjectFailedException If the correspondence could not be uploaded to the ECM repository.
     */
    public EcmFile generateCorrespondence(
            Authentication user,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName,
            String targetFolderCmisId,
            CorrespondenceTemplate template,
            Object[] queryArguments,
            OutputStream correspondenceOutputStream,
            InputStream correspondenceInputStream) throws IOException, AcmCreateObjectFailedException
    {
        List<Object[]> results = query(template, queryArguments);

        if ( results == null || results.isEmpty() )
        {
            throw new IllegalStateException("Database query returned no results");
        }

        Object[] firstResult = results.get(0);

        if ( firstResult.length > template.getTemplateSubstitutionVariables().size() )
        {
            throw new IllegalStateException("Must have at least as many query columns as substitution variables.");
        }

        Map<String, String> substitutions = prepareSubstitutionMap(template, firstResult);

        Resource templateFile = new FileSystemResource(getCorrespondenceFolderName() + File.separator + template.getTemplateFilename());

        log.debug("Generating correspondence from template '" + templateFile.getFile().getAbsolutePath() + "'" );

        getWordGenerator().generate(templateFile, correspondenceOutputStream, substitutions);

        String fileName = generateUniqueFilename(template);

        EcmFile retval = ecmFileService.upload(
                template.getDocumentType(),
                CORRESPONDENCE_CATEGORY,
                correspondenceInputStream,
                WORD_MIME_TYPE,
                fileName,
                user,
                targetFolderCmisId,
                parentObjectType,
                parentObjectId,
                parentObjectName);

        return retval;
    }

    private String generateUniqueFilename(CorrespondenceTemplate template)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMdd-HHmmss-SSS");
        return template.getDocumentType() + " " + sdf.format(new Date()) + ".docx";
    }

    private Map<String, String> prepareSubstitutionMap(CorrespondenceTemplate template, Object[] firstResult)
    {
        Map<String, String> retval = new HashMap<>();

        List<String> substitutionVariables = template.getTemplateSubstitutionVariables();

        Object[] withFormattedDates = formatArrayMembers(firstResult, Date.class, new SimpleDateFormat(template.getDateFormatString()));
        Object[] withFormattedNumbers = formatArrayMembers(withFormattedDates, Number.class, new DecimalFormat(template.getNumberFormatString()));

        for ( int a = 0; a < firstResult.length; a++ )
        {
            Object column = withFormattedNumbers[a];

            String columnValue = column == null ? "null" : column.toString();

            retval.put(substitutionVariables.get(a), columnValue);
        }

        return retval;
    }

    private Object[] formatArrayMembers(Object[] result, Class toBeFormatted, Format format)
    {
        Object[] retval = new Object[result.length];
        System.arraycopy(result, 0, retval, 0, result.length);

        for ( int a = 0; a < retval.length; a++ )
        {
            if ( retval[a] != null && toBeFormatted.isAssignableFrom(retval[a].getClass()) )
            {
                retval[a] = format.format(retval[a]);
            }
        }

        return retval;
    }

    private List<Object[]> query(CorrespondenceTemplate template, Object[] queryArguments)
    {
        Query select = getEntityManager().createQuery(template.getJpaQuery());

        for ( int a = 0; a < queryArguments.length; a++ )
        {
            // parameter indexes are 1-based
            select = select.setParameter(a + 1, queryArguments[a]);
        }

        List<Object[]> results = (List<Object[]>) select.getResultList();

        return results;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setWordGenerator(PoiWordGenerator wordGenerator)
    {
        this.wordGenerator = wordGenerator;
    }

    public PoiWordGenerator getWordGenerator()
    {
        return wordGenerator;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public String getCorrespondenceFolderName()
    {
        return correspondenceFolderName;
    }

    public void setCorrespondenceFolderName(String correspondenceFolderName)
    {
        this.correspondenceFolderName = correspondenceFolderName;
    }
}
