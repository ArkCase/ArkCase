package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

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

    private final String uploadFileType = "user_profile";

    @RequestMapping(value = "/img", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    public ResponseEntity<? extends Object> uploadProfileImage(
            @RequestParam("userId") String userId,
            @RequestParam("files[]") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException {

                if ( log.isInfoEnabled() ) {
                    log.info("Adding profile picture for user " + userId);
                }

                AcmUser user = getUserDao().findByUserId(userId);
                if( user==null ) {
                    throw new AcmObjectNotFoundException("user",null, "Object not found", null);
                }
                UserOrg in = null;
                try {
                      in  = getUserOrgDao().getUserOrgForUser(user);
                    if ( in == null ) {
                        throw new AcmObjectNotFoundException("userOrg", in.getUserOrgId(), "No info found for Profile", null);
                    }

                    String folderId = in.getEcmFolderId();
                    String objectType = "PROFILE_IMG";
                    Long objectId = in.getUserOrgId();
                    String objectName = in.getUser().getFullName();

                    String contextPath = request.getServletContext().getContextPath();

                    return getEcmFileService().upload(uploadFileType, file, acceptType, contextPath, authentication,
                            folderId, objectType, objectId, objectName);
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
}
