package com.armedia.acm.plugins.admin.web.api;


import com.armedia.acm.plugins.admin.exception.AcmCustomLogoException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * Created by sergey.kolomiets on 6/22/15.
 */
public class CustomLogoService {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String brandingFilesLocation;
    private String headerLogoFile;
    private String loginLogoFile;


    /**
     * Return Header logo
     * @return
     * @throws AcmCustomLogoException
     */
    public byte[] getHeaderLogo() throws AcmCustomLogoException {
        try {
            File headerLogo = new File(brandingFilesLocation + headerLogoFile);
            byte[] result =  FileUtils.readFileToByteArray(headerLogo);
            return result;
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("Can't get custom Header Logo file", e);
            }
            throw new AcmCustomLogoException("Can't get custom Header Logo file", e);
        }
    }

    /**
     * Return Login logo
     * @return
     * @throws AcmCustomLogoException
     */
    public byte[] getLoginLogo() throws AcmCustomLogoException {
        try {
            File headerLogo = new File(brandingFilesLocation + loginLogoFile);
            byte[] result =  FileUtils.readFileToByteArray(headerLogo);
            return result;
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("Can't get custom Login Logo file", e);
            }
            throw new AcmCustomLogoException("Can't get custom Login Logo file", e);
        }
    }

    /**
     * Update Login logo
     * @param logoStream
     * @throws AcmCustomLogoException
     */
    public void updateLoginLogo(InputStream logoStream) throws AcmCustomLogoException {
        File logoFile = null;
        try {
            try {
                logoFile = new File(brandingFilesLocation + loginLogoFile);
                FileUtils.copyInputStreamToFile(logoStream, logoFile);
            } finally {
                logoStream.close();
            }
        } catch (Exception e) {
            throw new AcmCustomLogoException("Can't update logo file");
        }
    }

    /**
     * Update Header Logo
     * @param logoStream
     * @throws AcmCustomLogoException
     */
    public void updateHeaderLogo(InputStream logoStream) throws AcmCustomLogoException {
        File logoFile = null;
        try {
            try {
                logoFile = new File(brandingFilesLocation + headerLogoFile);
                FileUtils.copyInputStreamToFile(logoStream, logoFile);
            } finally {
                logoStream.close();
            }
        } catch (Exception e) {
            throw new AcmCustomLogoException("Can't update logo file");
        }
    }

    public void setBrandingFilesLocation(String brandingFilesLocation) {
        this.brandingFilesLocation = brandingFilesLocation;
    }

    public void setHeaderLogoFile(String headerLogoFile) {
        this.headerLogoFile = headerLogoFile;
    }

    public void setLoginLogoFile(String loginLogoFile) {
        this.loginLogoFile = loginLogoFile;
    }
}
