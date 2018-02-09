package com.armedia.acm.plugins.dashboard.site.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.dashboard.site.dao.SiteDao;
import com.armedia.acm.plugins.dashboard.site.model.DeleteSiteResult;
import com.armedia.acm.plugins.dashboard.site.model.Site;
import com.armedia.acm.plugins.dashboard.site.model.SiteEvent;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/dashboard/widgets/site", "/api/latest/plugin/dashboard/widgets/site"})
public class DeleteSiteAPIController implements ApplicationEventPublisherAware
{
    private SiteDao siteDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteSiteAPIController.class);

    @RequestMapping(value = "byUser", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeleteSiteResult deleteSiteByUser(
            @RequestParam("user") String user, Authentication authentication, HttpSession httpSession)
            throws AcmUserActionFailedException
    {
        Preconditions.checkNotNull(user, "User cannot be null");
        Site site = getSiteDao().findByUser(user);
        Preconditions.checkNotNull(site, "Site cannot be null");

        try
        {
            getSiteDao().deleteByUser(user);
            publishSiteEvent(httpSession, authentication, site, "deleted", true);
            DeleteSiteResult result = new DeleteSiteResult();
            result.setDeletedSiteId(site.getId());
            return result;
        } catch (Exception e)
        {
            LOGGER.error("Failed to delete site [{}]", site, e);
            publishSiteEvent(httpSession, authentication, site, "deleted", false);
            throw new AcmUserActionFailedException("Delete Site", "Site", site.getId(), e.getMessage(), e);
        }
    }

    @RequestMapping(value = "byId/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeleteSiteResult deleteSiteById(
            @PathVariable("id") Long id, Authentication authentication, HttpSession httpSession)
            throws AcmUserActionFailedException
    {
        Preconditions.checkNotNull(id, "Id cannot be null");
        Site site = getSiteDao().findById(id);
        Preconditions.checkNotNull(site, "Site cannot be null");
        try
        {
            getSiteDao().deleteById(id);
            publishSiteEvent(httpSession, authentication, site, "deleted", true);
            DeleteSiteResult result = new DeleteSiteResult();
            result.setDeletedSiteId(id);
            return result;
        } catch (Exception e)
        {
            LOGGER.error("Failed to delete site [{}]", site, e);
            publishSiteEvent(httpSession, authentication, site, "deleted", false);
            throw new AcmUserActionFailedException("Delete", "site", id, e.getMessage(), e);
        }
    }

    private void publishSiteEvent(HttpSession httpSession, Authentication authentication, Site site, String eventType, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        SiteEvent event = new SiteEvent(site, eventType, succeeded, ipAddress);
        event.setUserId(authentication.getName());
        applicationEventPublisher.publishEvent(event);
    }

    public SiteDao getSiteDao()
    {
        return siteDao;
    }

    public void setSiteDao(SiteDao siteDao)
    {
        this.siteDao = siteDao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}