package com.armedia.acm.frevvo.config;

import com.armedia.acm.file.AcmMultipartFile;
import com.armedia.acm.frevvo.config.service.FrevvoTestService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by armdev on 11/3/14.
 */
public class FrevvoFormAbstractServiceTest extends EasyMockSupport
{
    private FrevvoTestService unit;
    private InputStream mockIs;
    private EcmFileService mockFileService;
    private Authentication mockAuthentication;

    @Before
    public void setUp() throws Exception
    {
        mockIs = createMock(InputStream.class);
        mockAuthentication = createMock(Authentication.class);

        unit = new FrevvoTestService();

        mockFileService = createMock(EcmFileService.class);
        unit.setEcmFileService(mockFileService);

        unit.setAuthentication(mockAuthentication);
    }

    @Test
    public void saveAttachments_formXml() throws Exception
    {

        MultiValueMap<String, MultipartFile> attachments = new LinkedMultiValueMap<>();
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile formXml = new AcmMultipartFile(
                "form.xml",
                "form-orig.xml",
                "text/xml",
                false,
                250L,
                "xml string".getBytes(),
                mockIs,
                true);
        files.add(formXml);
        attachments.put("form_" + unit.getFormName(), files);

        Capture<MultipartFile> capturedFile = new Capture<>();

        expect(mockFileService.upload(
                eq(unit.getFormName() + "_xml"),
                capture(capturedFile),
                eq(unit.getAuthentication()),
                eq("cmisId"),
                eq("parentType"),
                eq(500L))).andReturn(null);

        replayAll();

        unit.saveAttachments(attachments, "cmisId", "parentType", 500L);

        verifyAll();

        MultipartFile found = capturedFile.getValue();

        assertEquals(found.getSize(), formXml.getSize());
    }
}
