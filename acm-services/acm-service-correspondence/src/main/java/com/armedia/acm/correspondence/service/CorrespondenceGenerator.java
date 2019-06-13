package com.armedia.acm.correspondence.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.exceptions.CorrespondenceMergeFieldVersionException;
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.spring.SpringContextHolder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
import java.util.Optional;

/*-
 * #%L
 * ACM Service: Correspondence Library
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

/**
 * Created by armdev on 12/15/14.
 */
public class CorrespondenceGenerator
{
    protected static final String WORD_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    protected static final String CORRESPONDENCE_CATEGORY = "Correspondence";
    private transient final Logger log = LogManager.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;
    private PoiWordGenerator wordGenerator;
    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;
    private String correspondenceFolderName;
    private SpringContextHolder springContextHolder;
    private CorrespondenceService correspondenceService;
    private LookupDao lookupDao;
    private TranslationService translationService;

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
            InputStream correspondenceInputStream)
            throws IOException, AcmCreateObjectFailedException, AcmUserActionFailedException, CorrespondenceMergeFieldVersionException
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

        EcmFile retval = null;

        // Check for existing correspondence template in same folder. If so, update the ecm file (new version) instead
        // of creating new ecm file.
        EcmFile existing = ecmFileDao.findSingleFileByParentObjectAndFolderCmisIdAndFileType(parentObjectType, parentObjectId,
                targetFolderCmisId,
                template.getDocumentType());

        if (existing == null)
        {
            String fileName = generateUniqueFilename(template);
            retval = ecmFileService.upload(template.getDocumentType() + ".docx", template.getDocumentType(),
                    CORRESPONDENCE_CATEGORY,
                    correspondenceInputStream, WORD_MIME_TYPE, fileName, user, targetFolderCmisId, parentObjectType, parentObjectId);
        }
        else
        {
            retval = ecmFileService.update(existing, correspondenceInputStream, SecurityContextHolder.getContext().getAuthentication());
        }

        return retval;
    }

    public OutputStream generateCorrespondenceOutputStream(CorrespondenceTemplate template, Object[] queryArguments,
            OutputStream correspondenceOutputStream) throws IOException, CorrespondenceMergeFieldVersionException
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

        return correspondenceOutputStream;
    }

    private String generateUniqueFilename(CorrespondenceTemplate template)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMdd-HHmmss-SSS");
        return template.getDocumentType() + " " + sdf.format(new Date()) + ".docx";
    }

    private Map<String, String> prepareSubstitutionMap(CorrespondenceTemplate template, Map<String, Object> queryResult)
            throws IOException, CorrespondenceMergeFieldVersionException
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
            if (columnValue != null && !columnValue.isEmpty())
            {
                columnValue = getTranslationService().translate(getLookupValue(columnValue));
            }
            retval.put(mergeField.getFieldValue(), columnValue);

        }

        return retval;
    }

    private String getLookupValue(String key)
    {
        JSONObject jsonObject = new JSONObject(getLookupDao().getMergedLookups());

        String lookupSearch = searchByLookupType("standardLookup", key, jsonObject);
        if (!lookupSearch.equals(key))
        {
            return lookupSearch;
        }
        else
        {
            lookupSearch = searchByLookupType("nestedLookup", key, jsonObject);
            if (!lookupSearch.equals(key))
            {
                return lookupSearch;
            }
            else
            {
                lookupSearch = searchByLookupType("inverseValuesLookup", key, jsonObject);
                if (!lookupSearch.equals(key))
                {
                    return lookupSearch;
                }
            }
        }
        return key;
    }

    private String searchByLookupType(String lookupType, String key, JSONObject jsonObject)
    {
        if (jsonObject.get(lookupType) != null)
        {
            JSONArray lookupArray = jsonObject.getJSONArray(lookupType);
            for (int i = 0; i < lookupArray.length(); i++)
            {
                if (((JSONObject) lookupArray.get(i)).get("entries") != null)
                {
                    JSONArray entries = ((JSONObject) lookupArray.get(i)).getJSONArray("entries");
                    for (int j = 0; j < entries.length(); j++)
                    {
                        if (((JSONObject) entries.get(j)).get("key").equals(key))
                        {
                            return ((JSONObject) entries.get(j)).getString("value");
                        }
                        if (lookupType.equals("nestedLookup"))
                        {
                            if (((JSONObject) entries.get(j)).get("subLookup") != null)
                            {
                                JSONArray subLookup = ((JSONObject) entries.get(j)).getJSONArray("subLookup");
                                for (int k = 0; k < subLookup.length(); k++)
                                {
                                    if (((JSONObject) subLookup.get(k)).get("key").equals(key))
                                    {
                                        return ((JSONObject) subLookup.get(k)).getString("value");
                                    }
                                }
                            }
                        }
                        else if (lookupType.equals("inverseValuesLookup"))
                        {
                            if (((JSONObject) entries.get(j)).get("inverseKey").equals(key))
                            {
                                return ((JSONObject) entries.get(j)).getString("inverseValue");
                            }
                        }
                    }
                }
            }
        }
        return key;
    }

    private Object formatValue(Object result, Class toBeFormatted, Format format, CorrespondenceMergeField mergeField)
    {

        if (result != null && toBeFormatted.isAssignableFrom(result.getClass()))
        {
            result = format.format(result);
        }

        return result;
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
        Optional<CorrespondenceQuery> optionalCorrespondenceQuery = correspondenceQueryBeansMap.values().stream()
                .filter(cQuery -> cQuery.getType().toString().equals(template.getObjectType())).findFirst();

        CorrespondenceQuery correspondenceQuery;
        if (optionalCorrespondenceQuery.isPresent())
        {
            correspondenceQuery = optionalCorrespondenceQuery.get();
        }
        else
        {
            return new HashMap<>();
        }

        Query select = getEntityManager().createNativeQuery(correspondenceQuery.getSqlQuery());

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

    /**
     * @return the ecmFileDao
     */
    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    /**
     * @param ecmFileDao
     *            the ecmFileDao to set
     */
    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
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

    /**
     * @return the lookupDao
     */
    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    /**
     * @param lookupDao
     *            the lookupDao to set
     */
    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    /**
     * @return the translationService
     */
    public TranslationService getTranslationService()
    {
        return translationService;
    }

    /**
     * @param translationService
     *            the translationService to set
     */
    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}
