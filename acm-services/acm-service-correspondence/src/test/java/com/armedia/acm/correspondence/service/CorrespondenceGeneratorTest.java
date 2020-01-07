package com.armedia.acm.correspondence.service;

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

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.utils.SpELWordEvaluator;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.spring.SpringContextHolder;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 12/15/14.
 */
public class CorrespondenceGeneratorTest extends EasyMockSupport
{
    private CorrespondenceGenerator unit;

    private CorrespondenceTemplate correspondenceTemplate;
    private EcmFile ecmFile;

    private EntityManager mockEntityManager;
    private SpELWordEvaluator mockWordGenerator;
    private OutputStream mockOutputStream;
    private InputStream mockInputStream;
    private Authentication mockAuthentication;
    private EcmFileService mockEcmFileService;
    private EcmFileDao mockEcmFileDao;
    private SpringContextHolder mockSpringContextHolder;
    private CorrespondenceService mockCorrespondenceService;
    private LookupDao mockLookupDao;
    private TranslationService mockTranslationService;

    private String key1;
    private String key2;
    private String key3;
    private String key4;

    private String var1;
    private String var2;
    private String var3;
    private String var4;

    private String correspondenceFolder = "/correspondenceFolder";

    private List<CorrespondenceMergeField> mergeFieldsData()
    {
        List<CorrespondenceMergeField> mergeFields = new ArrayList<>();
        CorrespondenceMergeField mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key1);
        mergeField.setFieldValue(var1);
        mergeField.setFieldObjectType("CASE_FILE");
        mergeFields.add(mergeField);

        mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key2);
        mergeField.setFieldValue(var2);
        mergeField.setFieldObjectType("CASE_FILE");
        mergeFields.add(mergeField);

        mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key3);
        mergeField.setFieldValue(var3);
        mergeField.setFieldObjectType("CASE_FILE");
        mergeFields.add(mergeField);

        mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key4);
        mergeField.setFieldValue(var4);
        mergeField.setFieldObjectType("CASE_FILE");
        mergeFields.add(mergeField);
        return mergeFields;
    }

    private String lookupData()
    {
        return "{\"standardLookup\":[{\"entries\": [{\"key\":\"key1\",\"value\":\"var1\"},{\"key\":\"key2\",\"value\":\"var2\"},{\"key\":\"key3\",\"value\":\"var3\"},{\"key\":\"key4\",\"value\":\"var4\"}]}],\"inverseValuesLookup\":[],\"nestedLookup\":[]}";
    }

    private List<Object[]> resultsData()
    {
        List<Object[]> results = new ArrayList<>();

        Date column1 = new Date();
        String column2 = "Subject Name";
        Number column3 = 123456L;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        Date column4 = calendar.getTime();

        Object[] row = { column1, column2, column3, column4 };
        results.add(row);
        return results;
    }

    @Before
    public void setUp() throws Exception
    {
        mockEntityManager = createMock(EntityManager.class);
        mockWordGenerator = createMock(SpELWordEvaluator.class);
        mockOutputStream = createMock(OutputStream.class);
        mockInputStream = createMock(InputStream.class);
        mockAuthentication = createMock(Authentication.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);
        mockCorrespondenceService = createMock(CorrespondenceService.class);
        mockLookupDao = createMock(LookupDao.class);
        mockTranslationService = createMock(TranslationService.class);

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        unit = new CorrespondenceGenerator();
        unit.setEntityManager(mockEntityManager);
        unit.setSpelWordGenerator(mockWordGenerator);
        unit.setEcmFileService(mockEcmFileService);
        unit.setEcmFileDao(mockEcmFileDao);
        unit.setCorrespondenceFolderName(correspondenceFolder);
        unit.setSpringContextHolder(mockSpringContextHolder);
        unit.setCorrespondenceService(mockCorrespondenceService);
        unit.setLookupDao(mockLookupDao);
        unit.setTranslationService(mockTranslationService);

        String doctype = "doctype";
        String templateName = "templateName";
        String dateFormat = "MM/dd/YYYY";
        String numberFormat = "#,###";

        key1 = "key1";
        key2 = "key2";
        key3 = "key3";
        key4 = "key4_PLUS_30_DAYS";

        var1 = "var1";
        var2 = "var2";
        var3 = "var3";
        var4 = "var4";

        List<String> fieldNames = Arrays.asList(key1, key2, key3, key4);

        Map<String, String> substitutionVars = new HashMap<>();
        substitutionVars.put(key1, var1);
        substitutionVars.put(key2, var2);
        substitutionVars.put(key3, var3);
        substitutionVars.put(key4, var4);

        correspondenceTemplate = new CorrespondenceTemplate();
        correspondenceTemplate.setDocumentType(doctype);
        correspondenceTemplate.setTemplateFilename(templateName);
        correspondenceTemplate.setObjectType("CASE_FILE");
        correspondenceTemplate.setDateFormatString(dateFormat);
        correspondenceTemplate.setNumberFormatString(numberFormat);

        ecmFile = new EcmFile();

    }

    @Test
    public void generateNewTemplate() throws Exception
    {
        String targetFolderCmisId = "targetFolderCmisId";
        Object[] queryArgs = { 500L };
        Long parentObjectId = 500L;
        String objectCaseFile = "CASE_FILE";

        Capture<Resource> captureResourceTemplate = new Capture<>();

        Capture<String> filename = new Capture<>();

        expect(mockLookupDao.getMergedLookups()).andReturn(lookupData()).anyTimes();

        mockWordGenerator.generate(capture(captureResourceTemplate), eq(mockOutputStream), eq(objectCaseFile), eq(parentObjectId));

        expect(mockEcmFileDao.findSingleFileByParentObjectAndFolderCmisIdAndFileType(eq(objectCaseFile), eq(parentObjectId), eq(targetFolderCmisId),
                eq(correspondenceTemplate.getDocumentType()))).andReturn(null);

        expect(mockEcmFileService.upload(eq(correspondenceTemplate.getDocumentType() + ".docx"),
                eq(correspondenceTemplate.getDocumentType()), eq(CorrespondenceGenerator.CORRESPONDENCE_CATEGORY), eq(mockInputStream),
                eq(CorrespondenceGenerator.WORD_MIME_TYPE), capture(filename), eq(mockAuthentication), eq(targetFolderCmisId),
                eq(objectCaseFile), eq(parentObjectId))).andReturn(null);

        replayAll();

        unit.generateCorrespondence(mockAuthentication, "CASE_FILE", 500L, targetFolderCmisId, correspondenceTemplate, queryArgs,
                mockOutputStream, mockInputStream);

        verifyAll();

        Resource capturedResource = captureResourceTemplate.getValue();

        assertEquals(correspondenceTemplate.getTemplateFilename(), capturedResource.getFilename());

    }

    @Test
    public void generateUpdatedTemplate() throws Exception
    {
        String targetFolderCmisId = "targetFolderCmisId";
        Object[] queryArgs = { 500L };
        Long parentObjectId = 500L;
        String parentObjectType = "CASE_FILE";

        Capture<Resource> captureResourceTemplate = new Capture<>();

        Capture<String> filename = new Capture<>();

        expect(mockLookupDao.getMergedLookups()).andReturn(lookupData()).anyTimes();

        mockWordGenerator.generate(capture(captureResourceTemplate), eq(mockOutputStream), eq(parentObjectType), eq(parentObjectId));

        expect(mockEcmFileDao.findSingleFileByParentObjectAndFolderCmisIdAndFileType(eq(parentObjectType), eq(parentObjectId), eq(targetFolderCmisId),
                eq(correspondenceTemplate.getDocumentType()))).andReturn(ecmFile);

        expect(mockEcmFileService.update(ecmFile, mockInputStream, mockAuthentication)).andReturn(null);

        replayAll();

        unit.generateCorrespondence(mockAuthentication, parentObjectType, parentObjectId, targetFolderCmisId, correspondenceTemplate, queryArgs,
                mockOutputStream, mockInputStream);

        verifyAll();

        Resource capturedResource = captureResourceTemplate.getValue();

        assertEquals(correspondenceTemplate.getTemplateFilename(), capturedResource.getFilename());

    }

}
