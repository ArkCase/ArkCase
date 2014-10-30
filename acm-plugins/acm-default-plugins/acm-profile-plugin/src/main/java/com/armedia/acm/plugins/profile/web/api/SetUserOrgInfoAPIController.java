package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.exception.AcmProfileException;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.service.ProfileEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/profile/userOrgInfo/set","/api/latest/plugin/profile/userOrgInfo/set"})
public class SetUserOrgInfoAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());
    private ProfileEventPublisher eventPublisher;
    private UserDao userDao;
    private UserOrgDao userOrgDao;
    private OrganizationDao organizationDao;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProfileDTO setUserOrgInfo(
            @RequestBody ProfileDTO in,
            Authentication auth
    ) throws AcmCreateObjectFailedException {
        String userId = (String) auth.getName();
        AcmUser user = getUserDao().findByUserId(userId);
        if ( log.isInfoEnabled()) {
            log.info("Updating Profile Information for user '" + userId + "'");
        }
        UserOrg userOrg = null;
        Organization org = null;
        try {
          userOrg = getUserOrgDao().getUserOrgForUser(user);
        } catch (AcmObjectNotFoundException e) {
           if(log.isInfoEnabled()) {
               log.info("There are no user and company info for the user: "+userId +", new record will be created and info will be added");
           }
            //create new userOrg
            userOrg = new UserOrg();
            try {
                //check if the company name already exists
                org = getOrganizationDao().getOrganizationByOrganizationName(in.getCompanyName().trim());
            } catch (AcmObjectNotFoundException e1) {
                if(log.isInfoEnabled()) {
                    log.info("Organization with name: "+in.getCompanyName()+" is not found in the DB");
                }
                //if company name doesn't exist create new organization object
                org = prepareNewOrg(in);
                org = getOrganizationDao().save(org);
            }
            userOrg.setOrganization(org);
            userOrg.setUser(user);
        }

       //case when user changed  his company name
        if(userOrg.getOrganization().getOrganizationValue()!=null && !userOrg.getOrganization().getOrganizationValue().equals(in.getCompanyName().trim())) {
            if(log.isInfoEnabled()){
                log.info("User "+userId+" changed the name of his company");
            }
            try {
                //check if the company name already exists
                org = getOrganizationDao().getOrganizationByOrganizationName(in.getCompanyName().trim());
            } catch (AcmObjectNotFoundException e1) {
                if (log.isInfoEnabled()) {
                    log.info("Organization with name: " + in.getCompanyName() + " is not found in the DB");
                }
                //create new company for the user
                org = prepareNewOrg(in);
                org = getOrganizationDao().save(org);
                userOrg.setOrganization(org);

                //TODO "check if there is records with id of the old company, if not delete the organization record"
            }
         userOrg.setOrganization(org);
         userOrg.setUser(user);
        }
        userOrg = createUserOrgForUpdate(in, userOrg);
        getUserOrgDao().updateUserInfo(userOrg);
        return in;
    }

    private Organization prepareNewOrg(ProfileDTO profileDTO){
        Organization org = new Organization();
        //posible org types
        //complaint.organizationTypes=Non-profit=Non-profit,Government=Government,Corporation=Corporation
        org.setOrganizationType("Corporation");
        org.setOrganizationValue(profileDTO.getCompanyName());
        org.setCreator(profileDTO.getFullName());
        org.setModifier(profileDTO.getFullName());
        return org;
    }
    private UserOrg createUserOrgForUpdate(ProfileDTO in,UserOrg userOrgOld){

            userOrgOld.setWebsite(in.getWebsite());
            userOrgOld.setZip(in.getZip());
            userOrgOld.setState(in.getState());
            userOrgOld.setSecondAddress(in.getSecondAddress());
            userOrgOld.setCity(in.getCity());
            userOrgOld.setCompanyName(in.getCompanyName().trim());
            userOrgOld.setFax(in.getFax());
            userOrgOld.setFirstAddress(in.getFirstAddress());
            userOrgOld.setImAccount(in.getImAccount());
            userOrgOld.setImSystem(in.getImSystem());
            userOrgOld.setLocation(in.getLocation());
            userOrgOld.setMainOfficePhone(in.getMainOfficePhone());
            userOrgOld.setOfficePhoneNumber(in.getOfficePhoneNumber());
            userOrgOld.setMobilePhoneNumber(in.getMobilePhoneNumber());

        return userOrgOld;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public UserOrgDao getUserOrgDao() {
        return userOrgDao;
    }

    public void setUserOrgDao(UserOrgDao userOrgDao) {
        this.userOrgDao = userOrgDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public ProfileEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(ProfileEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
