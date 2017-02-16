package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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
public class GetUserInfoAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/info",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    AcmUserInfoDTO info(Authentication auth, HttpSession session)
    {
        log.debug("Getting info for user {}", auth.getName());

        List<String> authorities = auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());

        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        Map<String, Boolean> privilegeMap = (Map<String, Boolean>) session.getAttribute("acm_privileges");
        Set<String> privileges = privilegeMap.keySet();

        AcmUserInfoDTO retval = new AcmUserInfoDTO();

        retval.setUserId(auth.getName());
        retval.setFullName(user.getFullName());
        retval.setAuthorities(authorities);
        retval.setPrivileges(privileges);
        retval.setFirstName(user.getFirstName());
        retval.setLastName(user.getLastName());
        retval.setMail(user.getMail());
        retval.setDirectoryName(user.getUserDirectoryName());

        return retval;
    }

}
