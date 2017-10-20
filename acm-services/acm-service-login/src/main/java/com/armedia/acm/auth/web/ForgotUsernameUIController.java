package com.armedia.acm.auth.web;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/forgot-username")
public class ForgotUsernameUIController implements ApplicationEventPublisherAware
{
    private UserDao userDao;
    private ApplicationEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> setEmailAddress(@RequestParam String email)
    {
        AcmUser user = userDao.findByEmailAddress(email);
        if (user == null)
        {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        } else
        {
            ForgotUsernameEvent forgotUsernameEvent = new ForgotUsernameEvent(user);
            forgotUsernameEvent.setSucceeded(true);
            eventPublisher.publishEvent(forgotUsernameEvent);
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
