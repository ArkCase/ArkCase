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

        logger.info("------------------- Video (Camera) ------------------------");
        Resource videoResource = new ClassPathResource("media-files/VID_20170721_130803497.mp4");
        EcmTikaFile video = ecmTikaFileService.detectFileUsingTika(
                IOUtils.toByteArray(videoResource.getInputStream()),
                videoResource.getFile().getName());
        assertEquals("video/mp4", video.getContentType());
        assertEquals(".mp4", video.getNameExtension());
        assertEquals("39°18'00\"N 77°48'55\"W", video.getGpsReadable());
        assertEquals("+39.30010-077.81540/", video.getGpsIso6709());
        assertNull(video.getDeviceMake());
        assertNull(video.getDeviceModel());
        assertEquals(Integer.valueOf(1080), video.getHeightPixels());
        assertEquals(Integer.valueOf(1920), video.getWidthPixels());
        assertEquals(39.3001, video.getGpsLatitudeDegrees(), 0.005);
        assertEquals(-77.8154, video.getGpsLongitudeDegrees(), 0.005);
        assertEquals(4.86, video.getDurationSeconds(), 0.001);

        Calendar videoCreated = Calendar.getInstance();
        videoCreated.setTime(video.getCreated());
        assertEquals(2017, videoCreated.get(Calendar.YEAR));

        logger.info(video.toString());

        logger.info("------------------- Picture (Camera) ------------------------");
        Resource imageResource = new ClassPathResource("media-files/IMG_20170721_125157844.jpg");
        EcmTikaFile image = ecmTikaFileService.detectFileUsingTika(
                IOUtils.toByteArray(imageResource.getInputStream()),
                imageResource.getFile().getName());
        assertEquals("image/jpeg", image.getContentType());
        assertEquals(".jpg", image.getNameExtension());
        assertEquals("39°18'00\"N 77°48'56\"W", image.getGpsReadable());
        assertEquals("+39.30007-077.81552/", image.getGpsIso6709());
        assertEquals("Motorola", image.getDeviceMake());
        assertEquals("XT1254", image.getDeviceModel());
        assertEquals(Integer.valueOf(5248), image.getHeightPixels());
        assertEquals(Integer.valueOf(2952), image.getWidthPixels());
        assertEquals(39.300068, image.getGpsLatitudeDegrees(), 0.005);
        assertEquals(-77.815521, image.getGpsLongitudeDegrees(), 0.005);
        assertEquals(0.0, image.getDurationSeconds(), 0.001);

        Calendar imageCreated = Calendar.getInstance();
        imageCreated.setTime(image.getCreated());
        assertEquals(2017, imageCreated.get(Calendar.YEAR));

        logger.info(image.toString());

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
