package com.armedia.acm.auth.web;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import com.armedia.acm.web.api.MDCConstants;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ForgotUsernamePasswordUIController implements ApplicationEventPublisherAware
{
    private UserDao userDao;
    private ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = "/forgot-username", method = RequestMethod.POST)
    public ResponseEntity<String> publishForgotUsernameEvent(@RequestParam String email)
    {
        List<AcmUser> users = userDao.findByEmailAddress(email).stream()
                .filter(user -> user.getUserState() == AcmUserState.VALID)
                .collect(Collectors.toList());
        if (users.size() == 0)
        {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        }
        else
        {
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, users.get(0).getUserId());
            ForgotUsernameEvent forgotUsernameEvent = new ForgotUsernameEvent(users.get(0), AuthenticationUtils.getUserIpAddress());
            forgotUsernameEvent.setSucceeded(true);
            eventPublisher.publishEvent(forgotUsernameEvent);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ResponseEntity<String> publishForgotPasswordEvent(@RequestParam String userId, @RequestParam String email)
    {
        AcmUser user = userDao.findByUserIdAndEmailAddress(userId, email);
        if (user == null || user.getUserState() != AcmUserState.VALID)
        {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        }
        else
        {
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, user.getUserId());
            ForgotPasswordEvent forgotPasswordEvent = new ForgotPasswordEvent(user, AuthenticationUtils.getUserIpAddress());
            forgotPasswordEvent.setSucceeded(true);
            eventPublisher.publishEvent(forgotPasswordEvent);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }
}
