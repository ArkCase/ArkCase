package com.armedia.acm.auth.web;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
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
        List<AcmUser> users = userDao.findByEmailAddress(email);
        if (users.size() == 0)
        {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        } else
        {
            List<String> userAccounts = users.stream()
                    .map(AcmUser::getUserId)
                    .collect(Collectors.toList());

            AbstractMap.SimpleImmutableEntry<String, List<String>> emailToUserAccount = new AbstractMap.SimpleImmutableEntry<>(email,
                    userAccounts);

            ForgotUsernameEvent forgotUsernameEvent = new ForgotUsernameEvent(emailToUserAccount);
            forgotUsernameEvent.setSucceeded(true);
            eventPublisher.publishEvent(forgotUsernameEvent);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ResponseEntity<String> publishForgotPasswordEvent(@RequestParam String userId, @RequestParam String email)
    {
        AcmUser user = userDao.findByUserIdAndEmailAddress(userId, email);
        if (user == null)
        {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        } else
        {
            ForgotPasswordEvent forgotPasswordEvent = new ForgotPasswordEvent(user);
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
