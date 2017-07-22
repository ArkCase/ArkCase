package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.service.EcmTikaFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
                videoResource.getInputStream(), videoResource.getFile().getCanonicalPath());
        assertEquals("video/mp4", video.getContentType());
        assertEquals(".mp4", video.getNameExtension());
        assertEquals("39°18'00\"N 77°48'55\"W", video.getGpsReadable());
        assertEquals("+39.30010-077.81540/", video.getGpsIso6709());
        assertNull(video.getCameraMake());
        assertNull(video.getCameraModel());
        assertEquals(Integer.valueOf(1080), video.getHeightPixels());
        assertEquals(Integer.valueOf(1920), video.getWidthPixels());
        assertEquals(39.3001, video.getGpsLatitudeDegrees(), 0.005);
        assertEquals(-77.8154, video.getGpsLongitudeDegrees(), 0.005);
        assertEquals(4.86, video.getVideoDurationSeconds(), 0.001);

        Calendar videoCreated = Calendar.getInstance();
        videoCreated.setTime(video.getCreated());
        assertEquals(2017, videoCreated.get(Calendar.YEAR));

        logger.info(video.toString());

        logger.info("------------------- Picture (Camera) ------------------------");
        Resource imageResource = new ClassPathResource("media-files/IMG_20170721_125157844.jpg");
        EcmTikaFile image = ecmTikaFileService.detectFileUsingTika(
                imageResource.getInputStream(), imageResource.getFile().getCanonicalPath());
        assertEquals("image/jpeg", image.getContentType());
        assertEquals(".jpg", image.getNameExtension());
        assertEquals("39°18'00\"N 77°48'56\"W", image.getGpsReadable());
        assertEquals("+39.30007-077.81552/", image.getGpsIso6709());
        assertEquals("Motorola", image.getCameraMake());
        assertEquals("XT1254", image.getCameraModel());
        assertEquals(Integer.valueOf(5248), image.getHeightPixels());
        assertEquals(Integer.valueOf(2952), image.getWidthPixels());
        assertEquals(39.300068, image.getGpsLatitudeDegrees(), 0.005);
        assertEquals(-77.815521, image.getGpsLongitudeDegrees(), 0.005);
        assertEquals(0.0, image.getVideoDurationSeconds(), 0.001);

        Calendar imageCreated = Calendar.getInstance();
        imageCreated.setTime(image.getCreated());
        assertEquals(2017, imageCreated.get(Calendar.YEAR));

        logger.info(image.toString());

    }


}
