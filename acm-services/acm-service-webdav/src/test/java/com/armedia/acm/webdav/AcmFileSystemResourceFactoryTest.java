package com.armedia.acm.webdav;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import io.milton.resource.Resource;

/**
 * Created by dmiller on 7/20/16.
 */
public class AcmFileSystemResourceFactoryTest extends EasyMockSupport
{
    private AcmFileSystemResourceFactory unit;
    private EcmFileDao mockEcmFileDao;

    private AcmWebDAVSecurityManager mockAcmWebDAVSecurityManager;

    @Before
    public void setUp() throws Exception
    {
        mockEcmFileDao = createMock(EcmFileDao.class);
        mockAcmWebDAVSecurityManager = createMock(AcmWebDAVSecurityManager.class);

        unit = new AcmFileSystemResourceFactory();
        unit.setFileDao(mockEcmFileDao);
        unit.setSecurityManager(mockAcmWebDAVSecurityManager);
    }

    @Test
    public void getResource_returnsOptionsResourceForNonFileRequests() throws Exception
    {
        String host = "www.dead.net";
        String path = "/123-456-78-90/FILE/EDIT_WORD_LOCK";

        Resource resource = unit.getResource(host, path);

        assertTrue(resource instanceof AcmOptionsResource);
    }

    @Test
    public void getResource_returnsOptionsForUrlThatHasAStringBeforeTheNumber() throws Exception
    {
        String host = "www.dead.net";
        String path = "/123-456-78-90/FILE/EDIT_WORD_LOCK/jgarcia12345.docx";

        Resource resource = unit.getResource(host, path);

        assertTrue(resource instanceof AcmOptionsResource);
    }

    @Test
    public void getResource_returnsFileResourceForFileRequest() throws Exception
    {
        String host = "www.dead.net";
        String path = "/webdav/123-456-78-90/FILE/EDIT_WORD_LOCK/12345.docx";

        unit.setWordFileExtensionPattern(Pattern.compile("\\.(doc|dot|docx|dotx|docm|dotm|docb)$"));
        unit.setFilterMapping("webdav");

        expect(mockEcmFileDao.find(12345L)).andReturn(new EcmFile());

        replayAll();

        Resource resource = unit.getResource(host, path);

        verifyAll();

        assertTrue(resource instanceof AcmFileResource);
    }
}
