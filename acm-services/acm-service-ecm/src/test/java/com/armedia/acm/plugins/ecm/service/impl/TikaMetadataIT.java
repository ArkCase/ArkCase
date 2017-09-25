package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.service.EcmTikaFileService;
import org.apache.tika.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ecm-tika.xml"
})
public class TikaMetadataIT
{
    @Autowired
    private EcmTikaFileService ecmTikaFileService;

    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void extractMetadata() throws Exception
    {
        // see the loop below to understand each of the values in the below arrays.  One array per test file.

        Object[][] testData = {
                {
                        "Video (ArkCase FOIA)",
                        "media-files/ArkCase _ FOIA.MP4",
                        "video/mp4",
                        ".mp4",
                        null,
                        null,
                        null,
                        null,
                        224,
                        398,
                        0.0,
                        0.0,
                        131.88,
                        0,
                        0,
                        0
                },
                {
                        "Video (Police body camera)",
                        "media-files/Police Bodycam.mp4",
                        "video/mp4",
                        ".mp4",
                        null,
                        null,
                        null,
                        null,
                        360,
                        640,
                        0.0,
                        0.0,
                        44.0,
                        2016,
                        Calendar.OCTOBER,
                        22
                },
                {
                        "Video (Evidence)",
                        "media-files/Evidence Video.mp4",
                        "video/mp4",
                        ".mp4",
                        null,
                        null,
                        null,
                        null,
                        480,
                        854,
                        0.0,
                        0.0,
                        60.6,
                        2017,
                        Calendar.JULY,
                        25
                },
                {
                        "Video (Camera)",
                        "media-files/VID_20170721_130803497.mp4",
                        "video/mp4",
                        ".mp4",
                        "39°18'00\"N 77°48'55\"W",
                        "+39.30010-077.81540/",
                        null,
                        null,
                        1080,
                        1920,
                        39.3001,
                        -77.8154,
                        4.86,
                        2017,
                        Calendar.JULY,
                        21
                },
                {
                        "Picture (Camera)",
                        "media-files/IMG_20170721_125157844.jpg",
                        "image/jpeg",
                        ".jpg",
                        "39°18'00\"N 77°48'56\"W",
                        "+39.30007-077.81552/",
                        "Motorola",
                        "XT1254",
                        5248,
                        2952,
                        39.300068,
                        -77.815521,
                        0.0,
                        2017,
                        Calendar.JULY,
                        21
                }
        };

        for (Object[] test : testData)
        {
            String testType = (String) test[0];
            String filePath = (String) test[1];
            Object mimeType = test[2];
            Object extension = test[3];
            Object gpsReadable = test[4];
            Object gpsIso6709 = test[5];
            Object deviceMake = test[6];
            Object deviceModel = test[7];
            int height = (int) test[8];
            int width = (int) test[9];
            double gpsLatDegrees = (double) test[10];
            double gpsLongDegrees = (double) test[11];
            double duration = (double) test[12];
            int year = (int) test[13];
            int month = (int) test[14];
            int day = (int) test[15];

            logger.info("------------------- {} ------------------------", testType);

            Resource resource = new ClassPathResource(filePath);
            EcmTikaFile multimedia = ecmTikaFileService.detectFileUsingTika(
                    IOUtils.toByteArray(resource.getInputStream()),
                    resource.getFile().getName());
            assertEquals(mimeType, multimedia.getContentType());
            assertEquals(extension, multimedia.getNameExtension());
            assertEquals(gpsReadable, multimedia.getGpsReadable());
            assertEquals(gpsIso6709, multimedia.getGpsIso6709());
            assertEquals(deviceMake, multimedia.getDeviceMake());
            assertEquals(deviceModel, multimedia.getDeviceModel());
            assertEquals(Integer.valueOf(height), multimedia.getHeightPixels());
            assertEquals(Integer.valueOf(width), multimedia.getWidthPixels());
            assertEquals(gpsLatDegrees, multimedia.getGpsLatitudeDegrees(), 0.005);
            assertEquals(gpsLongDegrees, multimedia.getGpsLongitudeDegrees(), 0.005);
            assertEquals(duration, multimedia.getDurationSeconds(), 0.001);

            if (year > 0)
            {
                Calendar multimediaCreated = Calendar.getInstance();
                multimediaCreated.setTime(multimedia.getCreated());
                assertEquals(year, multimediaCreated.get(Calendar.YEAR));
                assertEquals(month, multimediaCreated.get(Calendar.MONTH));
                assertEquals(day, multimediaCreated.get(Calendar.DAY_OF_MONTH));
            }
            else
            {
                assertNull(multimedia.getCreated());
            }

            logger.info(multimedia.toString());
        }

    }

    @Test
    public void detectJsonFile() throws Exception
    {
        Resource resource = new ClassPathResource("json/simple.json");

        EcmTikaFile file = ecmTikaFileService.detectFileUsingTika(
                IOUtils.toByteArray(resource.getInputStream()), resource.getFile().getName());
        assertEquals("application/json", file.getContentType());
    }

    @Test
    public void detectPDFFile() throws Exception
    {
        Resource resource = new ClassPathResource("adobe/xfa.pdf");
        EcmTikaFile file = ecmTikaFileService.detectFileUsingTika(
                IOUtils.toByteArray(resource.getInputStream()), resource.getFile().getName());
        assertEquals("application/pdf", file.getContentType());
    }

    @Test
    public void detectExcelFile() throws Exception
    {
        List<Resource> resources = Arrays.asList(new ClassPathResource("office/excel_1.xls")
                , new ClassPathResource("office/excel_2.xls")
                , new ClassPathResource("office/excel_3.xls")
                , new ClassPathResource("office/excel_4.xlsx"));

        List<String> expectedMimeTypes = Arrays.asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                , "application/vnd.openxmlformats-officedocument.spreadsheetml.template", "application/vnd.ms-excel.sheet.macroenabled.12"
                , "application/vnd.ms-excel.template.macroenabled.12", "application/vnd.ms-excel.addin.macroenabled.12", "application/vnd.ms-excel.sheet.binary.macroenabled.12");

        for (Resource resource : resources)
        {
            try (InputStream is = resource.getInputStream())
            {
                EcmTikaFile file = ecmTikaFileService.detectFileUsingTika(IOUtils.toByteArray(is), resource.getFile().getName());
                assertTrue(expectedMimeTypes.contains(file.getContentType()));
            } catch (Exception e)
            {
                logger.error("could not process " + resource.getFilename(), e);
                fail("could not process " + resource.getFilename());
            }
        }
    }


}
