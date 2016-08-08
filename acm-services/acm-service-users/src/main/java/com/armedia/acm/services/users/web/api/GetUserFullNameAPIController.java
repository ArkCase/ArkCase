package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = { "/api/v1/users", "/api/latest/users" } )
public class GetUserFullNameAPIController
{
    private UserDao userDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/fullname/{userId}",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    String getFullName(
            @PathVariable("userId") String userId,
            Authentication auth
    ) throws AcmObjectNotFoundException
    {
        log.debug("Getting user full name for {}", userId);

        AcmUser user = userDao.quietFindByUserId(userId);

        if (user == null)
        {
            throw new AcmObjectNotFoundException("USER", null, "Object not found", null);
        }
        else
        {
            return user.getFullName();
        }
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao) { this.userDao = userDao; }
}
