package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.utils.PoiWordGenerator;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by armdev on 12/15/14.
 */
public class CorrespondenceGeneratorTest extends EasyMockSupport
{
    private CorrespondenceGenerator unit;

    private CorrespondenceTemplate correspondenceTemplate;

    private EntityManager mockEntityManager;
    private Query mockQuery;
    private PoiWordGenerator mockWordGenerator;
    private OutputStream mockOutputStream;
    private InputStream mockInputStream;
    private Authentication mockAuthentication;
    private EcmFileService mockEcmFileService;

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

        unit = new CorrespondenceGenerator();
        unit.setEntityManager(mockEntityManager);
        unit.setWordGenerator(mockWordGenerator);
        unit.setEcmFileService(mockEcmFileService);
        unit.setCorrespondenceFolderName(correspondenceFolder);

        String doctype = "doctype";
        String templateName = "templateName";
        String jpaQuery = "jpaQuery";
        String dateFormat = "MM/dd/YYYY";
        String numberFormat = "#,###";

        var1 = "var1";
        var2 = "var2";
        var3 = "var3";

        List<String> substitutionVars = Arrays.asList(var1, var2, var3);


        correspondenceTemplate = new CorrespondenceTemplate();
        correspondenceTemplate.setDocumentType(doctype);
        correspondenceTemplate.setTemplateFilename(templateName);
        correspondenceTemplate.setJpaQuery(jpaQuery);
        correspondenceTemplate.setTemplateSubstitutionVariables(substitutionVars);
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

        Capture<String> filename = new Capture<>();

        expect(mockEntityManager.createQuery(correspondenceTemplate.getJpaQuery())).andReturn(mockQuery);
        expect(mockQuery.setParameter(1, queryArgs[0])).andReturn(mockQuery);
        expect(mockQuery.getResultList()).andReturn(results);
        mockWordGenerator.generate(capture(captureResourceTemplate), eq(mockOutputStream), eq(substitutions));
        expect(mockEcmFileService.upload(
                eq(correspondenceTemplate.getTemplateFilename()),
                eq(correspondenceTemplate.getDocumentType()),
                eq(CorrespondenceGenerator.CORRESPONDENCE_CATEGORY),
                eq(mockInputStream),
                eq(CorrespondenceGenerator.WORD_MIME_TYPE),
                capture(filename),
                eq(mockAuthentication),
                eq(targetFolderCmisId),
                eq("CASE_FILE"),
                eq(500L)
        )).andReturn(null);

        replayAll();

        unit.generateCorrespondence(
                mockAuthentication,
                "CASE_FILE",
                500L,
                targetFolderCmisId,
                correspondenceTemplate,
                queryArgs,
                mockOutputStream,
                mockInputStream);

        verifyAll();

        Resource capturedResource = captureResourceTemplate.getValue();

        assertEquals(correspondenceTemplate.getTemplateFilename(), capturedResource.getFilename());
    }



}
