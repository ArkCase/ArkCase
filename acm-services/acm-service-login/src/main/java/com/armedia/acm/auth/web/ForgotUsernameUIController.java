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

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/forgot-username")
public class ForgotUsernameUIController implements ApplicationEventPublisherAware
{
    private UserDao userDao;
    private ApplicationEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> setEmailAddress(@RequestParam String email)
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
