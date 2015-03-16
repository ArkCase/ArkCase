package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.file.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.exception.AcmProfileException;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.service.SaveUserOrgTransaction;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * Created by marjan.stefanoski on 31.10.2014.
 */

@Controller
@RequestMapping( { "/api/v1/plugin/profile", "/api/latest/plugin/profile"} )
public class UploadProfileImgAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private UserOrgDao userOrgDao;
    private UserDao userDao;
    private EcmFileService ecmFileService;
    private SaveUserOrgTransaction saveUserOrgTransaction;

    private final String uploadFileType = "user_profile";

    @RequestMapping(value = "/img", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    public ResponseEntity<? extends Object> uploadProfileImage(
            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmProfileException {

                if ( log.isInfoEnabled() ) {
                    log.info("Adding profile picture for user " + userId);
                }

                AcmUser user = getUserDao().findByUserId(userId);
                if( user==null ) {
                    throw new AcmObjectNotFoundException("user",null, "Object not found", null);
                }
                UserOrg in = null;
                try {
                        in = getUserOrgDao().getUserOrgForUser(user);
                        //this "if()" part is a workaround for already added rows in acm_user_org table but with no CMIS folder
                        if( in.getContainerFolder().getCmisFolderId() == null ){
                            try {
                                   in = getSaveUserOrgTransaction().saveUserOrg(in,authentication);
                            } catch ( MuleException e ) {
                                if( log.isErrorEnabled()){
                                    log.error("Saving of the info for user and organization throw an exception",e);
                                }
                                throw new AcmCreateObjectFailedException("user organization info",e.getMessage(),e.getCause());
                            }
                        }
                        String folderId = in.getContainerFolder().getCmisFolderId();
                        String objectType = "PROFILE_IMG";
                        Long objectId = in.getUserOrgId();

                    //creating a unique file that will be uploaded on alfresco.
                    AcmMultipartFile f = new AcmMultipartFile(
                            file.getName(),
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.isEmpty(),
                            file.getSize(),
                            file.getBytes(),
                            file.getInputStream(),
                            true);

                        String contextPath = request.getServletContext().getContextPath();

                        return getEcmFileService().upload(uploadFileType, f, acceptType, contextPath, authentication,
                            folderId, objectType, objectId);
                } catch (IOException e){
                    if(log.isErrorEnabled()){
                        log.error("Creating unique file name failed",e);
                    }
                    throw new AcmProfileException("Creating unique file name failed");
                }
                catch (AcmObjectNotFoundException e) {
                    throw new AcmObjectNotFoundException("profile",in.getUserOrgId() , e.getMessage(), e);
                }
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserOrgDao getUserOrgDao() {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao) {
        this.userOrgDao = userOrgDao;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public SaveUserOrgTransaction getSaveUserOrgTransaction() {
        return saveUserOrgTransaction;
    }

    public void setSaveUserOrgTransaction(SaveUserOrgTransaction saveUserOrgTransaction) {
        this.saveUserOrgTransaction = saveUserOrgTransaction;
    }
}
