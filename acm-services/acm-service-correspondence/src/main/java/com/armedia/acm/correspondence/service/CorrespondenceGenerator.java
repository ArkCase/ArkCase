package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.spring.SpringContextHolder;

import org.jsoup.Jsoup;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 12/15/14.
 */
public class CorrespondenceGenerator
{
    protected static final String WORD_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    protected static final String CORRESPONDENCE_CATEGORY = "Correspondence";
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;
    private PoiWordGenerator wordGenerator;
    private EcmFileService ecmFileService;
    private String correspondenceFolderName;
    private SpringContextHolder springContextHolder;
    private CorrespondenceService correspondenceService;

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

    private Map<String, String> prepareSubstitutionMap(CorrespondenceTemplate template, Map<String, Object> queryResult) throws IOException
    {
        Map<String, String> retval = new HashMap<>();

        List<CorrespondenceMergeField> mergeFields = getCorrespondenceService().getActiveVersionMergeFieldsByType(template.getObjectType());

        for (CorrespondenceMergeField mergeField : mergeFields)
        {
            Object value = queryResult.get(mergeField.getFieldId());
            value = formatValue(value, Date.class, new SimpleDateFormat(template.getDateFormatString()), mergeField);
            value = formatValue(value, Number.class, new DecimalFormat(template.getNumberFormatString()), mergeField);

            // Remove all HTML elements if the value is not null
            String columnValue = value == null ? null : Jsoup.parse(value.toString()).text();
            retval.put(mergeField.getFieldValue(), columnValue);

        }

        return retval;
    }

    private Object formatValue(Object result, Class toBeFormatted, Format format, CorrespondenceMergeField mergeField)
    {

        if (result != null && toBeFormatted.isAssignableFrom(result.getClass()))
        {
            if (result instanceof Date)
            {
                result = addOrRemoveAmountOfTime((Date) result, mergeField);
            }
            result = format.format(result);
        }

        return result;
    }

    private Date addOrRemoveAmountOfTime(Date date, CorrespondenceMergeField mergeField)
    {
        if (date != null && mergeField != null && mergeField.getFieldId() != null)
        {
            // ID must be in this format: <FIELD_ID_NAME>_<ACTION>_<AMOUNT>_<UNIT>
            // Where:
            // <FIELD_ID_NAME> is the name that will not have "_" in the name
            // <ACTION> can have two values: PLUS or MINUS (can be any case sensitive)
            // <AMOUNT> any positive integer number
            // <UNIT> can have these values: MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS, YEARS (can be any case
            // sensitive)
            String id = mergeField.getFieldId();
            String[] idArray = id.split("_");
            String action = idArray.length > 1 ? idArray[1] : "";
            int amount = idArray.length > 2 ? stringToInt(idArray[2]) : 0;
            String unit = idArray.length > 3 ? idArray[3] : "";
            int unitInInt = getUnitInInt(unit);

            if ("PLUS".equalsIgnoreCase(action) && unitInInt != 0)
            {
                // Maybe this is not necessary, but just to be sure that there is a PLUS or MINUS
                amount = Math.abs(amount);
            }
            else if ("MINUS".equalsIgnoreCase(action) && unitInInt != 0)
            {
                amount = Math.abs(amount) * (-1);
            }
            else
            {
                return date;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(unitInInt, amount);

            return cal.getTime();
        }

        return date;
    }

    private int stringToInt(String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            log.warn("[{}] is not valid string representation of an integer.", str);
        }

        return 0;
    }

    private int getUnitInInt(String str)
    {
        if (str != null)
        {
            switch (str.toUpperCase())
            {
            case "MILLISECONDS":
                return Calendar.MILLISECOND;
            case "SECONDS":
                return Calendar.SECOND;
            case "MINUTES":
                return Calendar.MINUTE;
            case "HOURS":
                return Calendar.HOUR;
            case "DAYS":
                return Calendar.DATE;
            case "YEARS":
                return Calendar.YEAR;
            }
        }

        return 0;
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

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public PoiWordGenerator getWordGenerator()
    {
        return wordGenerator;
    }

    public void setWordGenerator(PoiWordGenerator wordGenerator)
    {
        this.wordGenerator = wordGenerator;
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

    /**
     * @param correspondenceFolderName
     *            the correspondenceFolderName to set
     */
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

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    /**
     * @param correspondenceService
     *            the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }
}
