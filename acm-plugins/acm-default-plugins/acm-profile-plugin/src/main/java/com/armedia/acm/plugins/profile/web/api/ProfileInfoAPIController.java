package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
@RequestMapping({"/api/v1/plugin/profile", "/api/latest/plugin/profile"})
public class ProfileInfoAPIController
{
    private UserOrgService userOrgService;

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Ecm file service.
     */
    private EcmFileService ecmFileService;


    @RequestMapping(value = "/get/{userId:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO getProfileInfo(@PathVariable("userId") String userId, Authentication authentication)
    {
        log.info("Finding Profile info for user [{}]", userId);
        return userOrgService.getProfileInfo(userId, authentication);
    }

    @RequestMapping(value = "/userOrgInfo/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO saveUserOrgInfo(@RequestBody ProfileDTO profile, Authentication auth)
    {
        return userOrgService.saveUserOrgInfo(profile, auth);
    }

    @RequestMapping(value = "/signature", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity downloadSignature(Authentication authentication, HttpServletResponse response)
            throws AcmUserActionFailedException, MuleException
    {
        log.info("Downloading signature file for user [{}]", authentication.getName());
        Long ecmSignatureFileId = userOrgService.getUserOrgForUserId(authentication.getName()).getEcmSignatureFileId();
        if (ecmSignatureFileId != null)
        {
            EcmFile ecmSignatureFile = ecmFileService.findById(ecmSignatureFileId);
            // MIME type of the file
            response.setContentType(ecmSignatureFile.getFileActiveVersionMimeType());
            // Read from the file and write into the response
            try (OutputStream os = response.getOutputStream(); InputStream is = ecmFileService.downloadAsInputStream(ecmSignatureFileId))
            {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1)
                {
                    os.write(buffer, 0, len);
                }
            } catch (IOException e)
            {
                log.error("Error downloading the signature file [{}]", ecmSignatureFileId, e);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    public UserOrgService getUserOrgService()
    {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService)
    {
        this.userOrgService = userOrgService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
