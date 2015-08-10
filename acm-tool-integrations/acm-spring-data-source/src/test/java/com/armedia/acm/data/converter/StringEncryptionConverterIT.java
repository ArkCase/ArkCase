package com.armedia.acm.data.converter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class StringEncryptionConverterIT {

    @Test
    public void testStaticMethodInject() {
        //just compare if they are not null
        assertNotNull(StringEncryptionConverter.getEncryptionEnabled());
        assertNotNull(StringEncryptionConverter.getEncryptionPassphrase());
        assertNotNull(StringEncryptionConverter.getAcmCryptoUtils());
    }

    @Test
    public void testConvert() throws IOException {
        StringEncryptionConverter converter = new StringEncryptionConverter();
        String attribute = "value to be encrypted";
        byte[] encryptedBytes = converter.convertToDatabaseColumn(attribute);

        String decrypted = converter.convertToEntityAttribute(encryptedBytes);
        assertEquals(attribute, decrypted);

    }


}