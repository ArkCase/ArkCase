package com.armedia.acm.plugins.dashboard.site.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.dashboard.site.dao.SiteDao;
import com.armedia.acm.plugins.dashboard.site.model.Site;
import com.armedia.acm.plugins.dashboard.site.model.SiteConstants;
import com.armedia.acm.plugins.dashboard.site.model.SiteEvent;
import com.google.common.base.Preconditions;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by joseph.mcgrady on 4/26/2017.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets/site", "/api/latest/plugin/dashboard/widgets/site" })
public class SaveSiteAPIController implements ApplicationEventPublisherAware
{
    private SiteDao siteDao;
    private ApplicationEventPublisher applicationEventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Site saveSite(@RequestBody Site site, Authentication authentication, HttpSession httpSession)
            throws AcmCreateObjectFailedException
    {
        Preconditions.checkNotNull(site, "Site cannot be null");
        Preconditions.checkNotNull(site.getUser(), "Site user cannot be null");
        String eventType = (site.getId() == null) ? SiteConstants.CREATED : SiteConstants.UPDATED;

        try
        {
            Site saved = getSiteDao().save(site);
            publishSiteEvent(httpSession, authentication, saved, eventType, true);
            return saved;
        }
        catch (Exception e)
        {
            publishSiteEvent(httpSession, authentication, site, eventType, false);
            throw new AcmCreateObjectFailedException("site", e.getMessage(), e);
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