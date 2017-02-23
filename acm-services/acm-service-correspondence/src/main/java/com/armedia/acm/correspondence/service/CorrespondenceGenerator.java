package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.spring.SpringContextHolder;

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

    private SpringContextHolder springContextHolder;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    protected static final String WORD_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    protected static final String CORRESPONDENCE_CATEGORY = "Correspondence";

    /**
     * Generate correspondence based on the supplied template, and store the correspondence in the ECM repository under
     * the supplied parent object.
     *
     * @param user
     *            User who has caused the correspondence to be generated.
     * @param parentObjectType
     *            Parent object type, e.g. CASE_FILE, COMPLAINT, TASK.
     * @param parentObjectId
     *            Parent object ID
     * @param targetFolderCmisId
     *            CMIS object ID of the folder in which to file the correspondence; usually the folder belonging to the
     *            parent object.
     * @param template
     *            Correspondence template (which stores information on the Word template file name, JPA query to find
     *            the correspondence data fields, etc).
     * @param queryArguments
     *            Actual arguments to pass to the JPA query. Currently only an object ID is supported.
     * @param correspondenceOutputStream
     *            Output stream to write the template to; the correspondenceOutputStream and correspondenceInputStream
     *            must be based on the same object, e.g. the same Java File object.
     * @param correspondenceInputStream
     *            Input stream used to upload the correspondence into the CMIS repository. The
     *            correspondenceOutputStream and correspondenceInputStream must be based on the same object, e.g. the
     *            same Java File object.
     * @return
     * @throws IOException
     *             If the correspondence could be be written to the correspondenceOutputStream.
     * @throws AcmCreateObjectFailedException
     *             If the correspondence could not be uploaded to the ECM repository.
     */
    public EcmFile generateCorrespondence(Authentication user, String parentObjectType, Long parentObjectId, String targetFolderCmisId,
            CorrespondenceTemplate template, Object[] queryArguments, OutputStream correspondenceOutputStream,
            InputStream correspondenceInputStream) throws IOException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        Map<String, Object> queryResult = query(template, queryArguments);

        if (queryResult == null || queryResult.isEmpty())
        {
            throw new IllegalStateException("Database query returned no results");
        }

        Map<String, String> substitutions = prepareSubstitutionMap(template, queryResult);

        Resource templateFile = new FileSystemResource(getCorrespondenceFolderName() + File.separator + template.getTemplateFilename());

        log.debug("Generating correspondence from template '{}'", templateFile.getFile().getAbsolutePath());

        getWordGenerator().generate(templateFile, correspondenceOutputStream, substitutions);

        String fileName = generateUniqueFilename(template);
        EcmFile retval = ecmFileService.upload(template.getDocumentType() + ".docx", template.getDocumentType(), CORRESPONDENCE_CATEGORY,
                correspondenceInputStream, WORD_MIME_TYPE, fileName, user, targetFolderCmisId, parentObjectType, parentObjectId);

        return retval;
    }

    private String generateUniqueFilename(CorrespondenceTemplate template)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMdd-HHmmss-SSS");
        return template.getDocumentType() + " " + sdf.format(new Date()) + ".docx";
    }

    private Map<String, String> prepareSubstitutionMap(CorrespondenceTemplate template, Map<String, Object> queryResult)
    {
        Map<String, String> retval = new HashMap<>();

        // TODO: Implement mapping mechanism
        /*
         * for (String key : template.getTemplateSubstitutionVariables().keySet()) { Object value =
         * queryResult.get(key); value = formatValue(value, Date.class, new
         * SimpleDateFormat(template.getDateFormatString())); value = formatValue(value, Number.class, new
         * DecimalFormat(template.getNumberFormatString()));
         * 
         * String columnValue = value == null ? null : value.toString();
         * 
         * retval.put(template.getTemplateSubstitutionVariables().get(key), columnValue); }
         */

        return retval;
    }

    private Object formatValue(Object result, Class toBeFormatted, Format format)
    {

        if (result != null && toBeFormatted.isAssignableFrom(result.getClass()))
        {
            result = format.format(result);
        }

        return result;
    }

    private Map<String, Object> query(CorrespondenceTemplate template, Object[] queryArguments)
    {
        Map<String, CorrespondenceQuery> correspondenceQueryBeansMap = springContextHolder.getAllBeansOfType(CorrespondenceQuery.class);
        CorrespondenceQuery correspondenceQuery = correspondenceQueryBeansMap.values().stream()
                .filter(cQuery -> cQuery.getType().toString().equals(template.getObjectType())).findFirst().get();

        Query select = getEntityManager().createQuery(correspondenceQuery.getJpaQuery());

        for (int a = 0; a < queryArguments.length; a++)
        {
            // parameter indexes are 1-based
            select = select.setParameter(a + 1, queryArguments[a]);
        }

        List<Object[]> results = select.getResultList();

        Map<String, Object> resultMap = new HashMap<>();
        List<String> queryFields = correspondenceQuery.getFieldNames();
        if (results != null && !results.isEmpty() && queryFields != null && !queryFields.isEmpty())
        {
            Object[] queryValues = results.get(0);
            if (queryValues != null)
            {
                if (queryValues.length != queryFields.size())
                {
                    throw new IllegalStateException("Query must have as many columns as defined fieldNames.");
                }

                for (int i = 0; i < queryValues.length; i++)
                {
                    resultMap.put(queryFields.get(i), queryValues[i]);
                }
            }
        }
        return resultMap;
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

    /**
     * @param springContextHolder
     *            the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}
