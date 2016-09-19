package com.armedia.acm.compressfolder;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 14, 2016
 *
 */
@RunWith(EasyMockRunner.class)
public class FolderCompressorTest extends EasyMockSupport
{

    /**
     *
     */
    private static final String COMPRESSED_FILENAME_FORMAT = "%1$sacm-%2$d-%3$s.zip";

    private FolderCompressor compressor;

    private File responseFolder;

    private File bigFile;

    @Mock
    private AcmFolderService mockedFolderService;

    @Mock
    private EcmFileService mockedFileService;

    @Mock
    private AcmFolder mockedResponseFolder;

    @Mock
    private AcmFolder mockedLevel1Folder;

    @Mock
    private EcmFile mockedLevel1File;

    @Mock
    private AcmFolder mockedLevel2Folder;

    @Mock
    private EcmFile mockedLevel2File;

    @Mock
    private EcmFile mockedLevel3File;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws IOException
    {
        responseFolder = new ClassPathResource("response_folder").getFile();
        bigFile = new ClassPathResource("big_file.txt").getFile();
        assertNotNull(responseFolder);
        compressor = new FolderCompressor();
        compressor.setFolderService(mockedFolderService);
        compressor.setFileService(mockedFileService);
        compressor.setCompressedFileNameFormat(COMPRESSED_FILENAME_FORMAT);
    }

    @After
    public void cleanUp() throws Exception
    {
        FileUtils
                .deleteQuietly(new File(String.format(COMPRESSED_FILENAME_FORMAT, System.getProperty("java.io.tmpdir"), 101l, "Response")));
        FileUtils.deleteQuietly(new File(System.getProperty("java.io.tmpdir"), "folder_level_1"));
        FileUtils.deleteQuietly(new File(System.getProperty("java.io.tmpdir"), "file_level_1.txt"));

        compressor.setMaxSize(2);
        compressor.setSizeUnit(SizeUnit.GIGA.name());
    }

    /**
     * Test method for {@link com.armedia.acm.compressfolder.FolderCompressor#compressFolder(java.lang.Long)}.
     *
     * @throws AcmObjectNotFoundException
     * @throws AcmUserActionFailedException
     * @throws MuleException
     * @throws FileNotFoundException
     */
    @Test
    public void testCompressFolder() throws Exception
    {
        expect(mockedFolderService.findById(101l)).andReturn(mockedResponseFolder);
        expect(mockedResponseFolder.getName()).andReturn("Response").times(2);
        expect(mockedResponseFolder.getId()).andReturn(101l);
        expect(mockedFolderService.getFolderChildren(101l)).andReturn(new ArrayList<>(Arrays.asList(mockedLevel1Folder, mockedLevel1File)));
        expect(mockedLevel1Folder.getObjectType()).andReturn(EcmFileConstants.OBJECT_FOLDER_TYPE).times(2);
        expect(mockedLevel1File.getObjectType()).andReturn(EcmFileConstants.OBJECT_FILE_TYPE).times(2);
        File folder_level_1 = null;
        for (File child : responseFolder.listFiles())
        {
            if (child.isDirectory())
            {
                folder_level_1 = child;
                expect(mockedLevel1Folder.getName()).andReturn(child.getName());
            } else
            {
                expect(mockedLevel1File.getFileName()).andReturn(child.getName());
                expect(mockedLevel1File.getId()).andReturn(101l);
                expect(mockedFileService.downloadAsInputStream(101l)).andReturn(new FileInputStream(child));
            }
        }
        expect(mockedLevel1Folder.getId()).andReturn(102l);
        expect(mockedFolderService.getFolderChildren(102l)).andReturn(new ArrayList<>(Arrays.asList(mockedLevel2Folder, mockedLevel2File)));
        expect(mockedLevel2Folder.getObjectType()).andReturn(EcmFileConstants.OBJECT_FOLDER_TYPE).times(2);
        expect(mockedLevel2File.getObjectType()).andReturn(EcmFileConstants.OBJECT_FILE_TYPE).times(2);
        File folder_level_2 = null;
        for (File child : folder_level_1.listFiles())
        {
            if (child.isDirectory())
            {
                folder_level_2 = child;
                expect(mockedLevel2Folder.getName()).andReturn(child.getName());
            } else
            {
                expect(mockedLevel2File.getFileName()).andReturn(child.getName());
                expect(mockedLevel2File.getId()).andReturn(102l);
                expect(mockedFileService.downloadAsInputStream(102l)).andReturn(new FileInputStream(child));
            }
        }
        expect(mockedLevel2Folder.getId()).andReturn(103l);
        expect(mockedFolderService.getFolderChildren(103l)).andReturn(new ArrayList<>(Arrays.asList(mockedLevel3File)));
        expect(mockedLevel3File.getObjectType()).andReturn(EcmFileConstants.OBJECT_FILE_TYPE).times(2);
        for (File child : folder_level_2.listFiles())
        {
            if (!child.isDirectory())
            {
                expect(mockedLevel3File.getFileName()).andReturn(child.getName());
                expect(mockedLevel3File.getId()).andReturn(103l);
                expect(mockedFileService.downloadAsInputStream(103l)).andReturn(new FileInputStream(child));
            }
        }

        replayAll();

        String compressedFolderPath = compressor.compressFolder(101l);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(compressedFolderPath))))
        {
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null)
            {
                File file = new File(System.getProperty("java.io.tmpdir"), ze.getName());
                if (ze.isDirectory())
                {
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                } else
                {
                    if (!file.exists())
                    {
                        file.createNewFile();
                    }
                    try (FileOutputStream fos = new FileOutputStream(file))
                    {
                        IOUtils.copy(zis, fos);
                    }
                }

            }

        }

        File folder_level_1_unzipped = new File(System.getProperty("java.io.tmpdir"), "folder_level_1");
        assertTrue(folder_level_1_unzipped.exists());
        File file_level_1_unzipped = new File(System.getProperty("java.io.tmpdir"), "file_level_1.txt");
        assertTrue(file_level_1_unzipped.exists());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file_level_1_unzipped))))
        {
            List<String> readLines = IOUtils.readLines(reader);
            assertThat(readLines.size(), is(1));
            assertThat(readLines.get(0), is("level 1"));
        }
        File folder_level_2_unzipped = new File(folder_level_1_unzipped, "folder_level_2");
        assertTrue(folder_level_2_unzipped.exists());
        File file_level_2_unzipped = new File(folder_level_1_unzipped, "file_level_2.txt");
        assertTrue(file_level_2_unzipped.exists());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file_level_2_unzipped))))
        {
            List<String> readLines = IOUtils.readLines(reader);
            assertThat(readLines.size(), is(1));
            assertThat(readLines.get(0), is("level 2"));
        }
        File file_level_3_unzipped = new File(folder_level_2_unzipped, "file_level_3.txt");
        assertTrue(file_level_3_unzipped.exists());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file_level_3_unzipped))))
        {
            List<String> readLines = IOUtils.readLines(reader);
            assertThat(readLines.size(), is(1));
            assertThat(readLines.get(0), is("level 3"));
        }

    }

    @Test
    public void testCompressFolderNoFolderForId() throws Exception
    {

        long folderId = 101l;

        expect(mockedFolderService.findById(folderId)).andReturn(null);

        replayAll();

        expectedException.expect(FolderCompressorException.class);
        expectedException.expectMessage(is(String.format("No folder with id %d was found!", folderId)));

        compressor.compressFolder(folderId);

    }

    @Test
    public void testCompressFolderMaxSize() throws Exception
    {

        long folderId = 101l;

        expect(mockedFolderService.findById(folderId)).andReturn(mockedResponseFolder);
        expect(mockedResponseFolder.getName()).andReturn("Response").times(2);
        expect(mockedResponseFolder.getId()).andReturn(folderId);
        expect(mockedFolderService.getFolderChildren(folderId)).andReturn(new ArrayList<>(Arrays.asList(mockedLevel1File)));
        expect(mockedLevel1File.getObjectType()).andReturn(EcmFileConstants.OBJECT_FILE_TYPE).times(2);
        expect(mockedLevel1File.getFileName()).andReturn(bigFile.getName());
        expect(mockedLevel1File.getId()).andReturn(folderId);
        expect(mockedFileService.downloadAsInputStream(folderId)).andReturn(new FileInputStream(bigFile));

        replayAll();

        expectedException.expect(FolderCompressorException.class);
        expectedException.expectMessage(containsString(String.format("Resulting compressed file is bigger than %1$s", 1024)));

        compressor.setMaxSize(1);
        compressor.setSizeUnit(SizeUnit.KILO.name());

        compressor.compressFolder(folderId);

    }

}
