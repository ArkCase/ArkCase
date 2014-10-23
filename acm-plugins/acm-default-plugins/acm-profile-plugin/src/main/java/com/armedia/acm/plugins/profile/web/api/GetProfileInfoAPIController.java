package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.profile.dao.UserInfoDao;
import com.armedia.acm.plugins.profile.exception.AcmProfileException;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.model.UserInfo;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */

@Controller
@RequestMapping({"/api/v1/plugin/profile", "/api/latest/plugin/profile"})
public class GetProfileInfoAPIController {

    private UserDao userDao;
    private UserInfoDao userInfoDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO getProfileInfo(
            Authentication authentication,
            HttpSession session
    ) throws AcmProfileException, AcmObjectNotFoundException {
        String userId = (String) authentication.getName().toLowerCase();
        if (log.isInfoEnabled()) {
            log.info("Finding Profile info for user '" + userId + "'");
        }
        AcmUser user = userDao.findByUserId(userId);
        if (user == null) {
            throw new AcmObjectNotFoundException("user",null, "Object not found", null);
        }
        UserInfo userInfo = null; //
        ProfileDTO profileDTO;
        try {
             userInfo =getUserInfoDao().getUserInfoForUser(user);
        } catch (AcmObjectNotFoundException e){

        }
        return null;
    }

    public UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

    public void setUserInfoDao(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
