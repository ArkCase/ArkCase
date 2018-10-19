/**
 *
 */
package gov.foia.service;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 20, 2016
 */
@RunWith(EasyMockRunner.class)
public class ResponseFolderCompressorServiceTest extends EasyMockSupport
{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private CaseFileDao mockedCaseFileDao;
    @Mock
    private ResponseFolderService mockedResponseFolderService;
    @Mock
    private FolderCompressor mockedCompressor;
    @Mock
    private CaseFile mockedCaseFile;
    @Mock
    private AcmContainer mockedContainer;
    @Mock
    private AcmFolder mockedRootFolder;
    @Mock
    private AcmFolder mockedRequestFolder;
    @Mock
    private AcmFolder mockedWorkingFolder;
    @Mock
    private AcmFolder mockedResponseFolder;
    private ResponseFolderCompressorService compresssorService;

    @Before
    public void setUp() throws Exception
    {
        compresssorService = new ResponseFolderCompressorService();
        compresssorService.setCaseFileDao(mockedCaseFileDao);
        compresssorService.setCompressor(mockedCompressor);
        compresssorService.setResponseFolderService(mockedResponseFolderService);
    }

    /**
     * Test method for {@link ResponseFolderCompressorService#compressResponseFolder(Long)}.
     *
     * @throws Exception
     */
    @Test
    public void testCompressResponseFolder() throws Exception
    {
        long requestId = 101l;
        long responseFolderId = 103l;
        String pathToCompressedFile = "path_to_compressed_file";

        expect(mockedCaseFileDao.find(requestId)).andReturn(mockedCaseFile);
        expect(mockedResponseFolderService.getResponseFolder(mockedCaseFile)).andReturn(mockedResponseFolder);
        expect(mockedResponseFolder.getId()).andReturn(responseFolderId);
        expect(mockedCompressor.compressFolder(responseFolderId)).andReturn(pathToCompressedFile);

        replayAll();

        String compressedFilePath = compresssorService.compressResponseFolder(requestId);

        assertThat(compressedFilePath, equalTo(pathToCompressedFile));

    }

}
