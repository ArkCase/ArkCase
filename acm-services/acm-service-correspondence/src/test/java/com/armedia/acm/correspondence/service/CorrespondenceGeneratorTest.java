package com.armedia.acm.correspondence.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.model.QueryType;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.spring.SpringContextHolder;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 12/15/14.
 */
public class CorrespondenceGeneratorTest extends EasyMockSupport
{
    private CorrespondenceGenerator unit;

    private CorrespondenceQuery correspondenceQuery;
    private CorrespondenceTemplate correspondenceTemplate;

    private EntityManager mockEntityManager;
    private Query mockQuery;
    private PoiWordGenerator mockWordGenerator;
    private OutputStream mockOutputStream;
    private InputStream mockInputStream;
    private Authentication mockAuthentication;
    private EcmFileService mockEcmFileService;
    private SpringContextHolder mockSpringContextHolder;
    private CorrespondenceService mockCorrespondenceService;

    private String key1;
    private String key2;
    private String key3;

    private String var1;
    private String var2;
    private String var3;

    private String correspondenceFolder = "/correspondenceFolder";

    @Before
    public void setUp() throws Exception
    {
        mockEntityManager = createMock(EntityManager.class);
        mockQuery = createMock(Query.class);
        mockWordGenerator = createMock(PoiWordGenerator.class);
        mockOutputStream = createMock(OutputStream.class);
        mockInputStream = createMock(InputStream.class);
        mockAuthentication = createMock(Authentication.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);
        mockCorrespondenceService = createMock(CorrespondenceService.class);

        unit = new CorrespondenceGenerator();
        unit.setEntityManager(mockEntityManager);
        unit.setWordGenerator(mockWordGenerator);
        unit.setEcmFileService(mockEcmFileService);
        unit.setCorrespondenceFolderName(correspondenceFolder);
        unit.setSpringContextHolder(mockSpringContextHolder);
        unit.setCorrespondenceService(mockCorrespondenceService);

        String doctype = "doctype";
        String templateName = "templateName";
        String jpaQuery = "jpaQuery";
        String dateFormat = "MM/dd/YYYY";
        String numberFormat = "#,###";

        key1 = "key1";
        key2 = "key2";
        key3 = "key3";

        var1 = "var1";
        var2 = "var2";
        var3 = "var3";

        List<String> fieldNames = Arrays.asList(key1, key2, key3);

        Map<String, String> substitutionVars = new HashMap<>();
        substitutionVars.put(key1, var1);
        substitutionVars.put(key2, var2);
        substitutionVars.put(key3, var3);

        correspondenceQuery = new CorrespondenceQuery();
        correspondenceQuery.setJpaQuery(jpaQuery);
        correspondenceQuery.setFieldNames(fieldNames);
        correspondenceQuery.setType(QueryType.CASE_FILE);

        correspondenceTemplate = new CorrespondenceTemplate();
        correspondenceTemplate.setDocumentType(doctype);
        correspondenceTemplate.setTemplateFilename(templateName);
        correspondenceTemplate.setObjectType("CASE_FILE");
        correspondenceTemplate.setDateFormatString(dateFormat);
        correspondenceTemplate.setNumberFormatString(numberFormat);
    }

    @Test
    public void generate() throws Exception
    {
        String targetFolderCmisId = "targetFolderCmisId";
        Object[] queryArgs = { 500L };

        List<Object[]> results = new ArrayList<>();

        Date column1 = new Date();
        String column2 = "Subject Name";
        Number column3 = 123456L;

        Object[] row = { column1, column2, column3 };
        results.add(row);

        Capture<Resource> captureResourceTemplate = new Capture<>();

        SimpleDateFormat sdf = new SimpleDateFormat(correspondenceTemplate.getDateFormatString());
        String expectedDate = sdf.format(column1);

        NumberFormat nf = new DecimalFormat(correspondenceTemplate.getNumberFormatString());
        String expectedNumber = nf.format(column3);

        Map<String, String> substitutions = new HashMap<>();
        substitutions.put(var1, expectedDate);
        substitutions.put(var2, column2);
        substitutions.put(var3, expectedNumber);

        List<CorrespondenceMergeField> mergeFields = new ArrayList<>();
        CorrespondenceMergeField mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key1);
        mergeField.setFieldValue(var1);
        mergeField.setFieldType("CASE_FILE");
        mergeFields.add(mergeField);

        mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key2);
        mergeField.setFieldValue(var2);
        mergeField.setFieldType("CASE_FILE");
        mergeFields.add(mergeField);

        mergeField = new CorrespondenceMergeField();
        mergeField.setFieldId(key3);
        mergeField.setFieldValue(var3);
        mergeField.setFieldType("CASE_FILE");
        mergeFields.add(mergeField);

        Map<String, CorrespondenceQuery> correspondenceQueryBeansMap = new HashMap<>();
        correspondenceQueryBeansMap.put("caseFileCorrespondenceQueryBean", correspondenceQuery);

        Capture<String> filename = new Capture<>();

        expect(mockSpringContextHolder.getAllBeansOfType(CorrespondenceQuery.class)).andReturn(correspondenceQueryBeansMap);
        correspondenceQueryBeansMap.values().stream()
                .filter(cQuery -> cQuery.getType().toString().equals(correspondenceTemplate.getObjectType())).findFirst().get();
        expect(mockEntityManager.createQuery(correspondenceQuery.getJpaQuery())).andReturn(mockQuery);
        expect(mockQuery.setParameter(1, queryArgs[0])).andReturn(mockQuery);
        expect(mockQuery.getResultList()).andReturn(results);

        expect(mockCorrespondenceService.getActiveVersionMergeFieldsByType(correspondenceTemplate.getObjectType())).andReturn(mergeFields);

        mockWordGenerator.generate(capture(captureResourceTemplate), eq(mockOutputStream), eq(substitutions));

        expect(mockEcmFileService.upload(eq(correspondenceTemplate.getDocumentType() + ".docx"),
                eq(correspondenceTemplate.getDocumentType()), eq(CorrespondenceGenerator.CORRESPONDENCE_CATEGORY), eq(mockInputStream),
                eq(CorrespondenceGenerator.WORD_MIME_TYPE), capture(filename), eq(mockAuthentication), eq(targetFolderCmisId),
                eq("CASE_FILE"), eq(500L))).andReturn(null);

        replayAll();

        unit.generateCorrespondence(mockAuthentication, "CASE_FILE", 500L, targetFolderCmisId, correspondenceTemplate, queryArgs,
                mockOutputStream, mockInputStream);

        verifyAll();

        Resource capturedResource = captureResourceTemplate.getValue();

        assertEquals(correspondenceTemplate.getTemplateFilename(), capturedResource.getFilename());

    }

}
