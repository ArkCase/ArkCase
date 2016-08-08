package com.armedia.acm.plugins.ecm.web;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * Created by jwu on 8/28/14.
 */
@RequestMapping("/plugin/document")
public class DocumentUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Properties ecmFileServiceProperties;
    private AuthenticationTokenService authenticationTokenService;

    private EcmFileService fileService;

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    public ModelAndView viewFile(HttpServletRequest req, Authentication auth, @PathVariable(value = "fileId") Long fileId)
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("document");
        mv.addObject("objId", fileId);

        EcmFile file = getFileService().findById(fileId);
        String mimeFileType = file.getFileActiveVersionMimeType();
        String title = file.getFileName();
        String type = "odt";

        // for viewerJs
        if (mimeFileType.contains("pdf"))
        {
            type = "pdf";
        }

        // The title can have some characters that should be URL encoded because the title
        // is used in the URL for taking the file. If contains some characters that are not allowed in the URL,
        // the request will fail
        String encodedTitle = title;
        try
        {
            encodedTitle = URLEncoder.encode(title, Charsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e)
        {
            log.error("Cannot encode title=" + title + ". The original format will be used.", e);
        }

        mv.addObject("type", type);
        mv.addObject("title", encodedTitle);
        mv.addObject("context", req.getContextPath());

        String participantTypes = getEcmFileServiceProperties().getProperty("ecm.participantTypes");
        String ticket = getAuthenticationTokenService().getTokenForAuthentication(auth);
        String viewer = getEcmFileServiceProperties().getProperty("ecm.viewer");
        String srcLink = getEcmFileServiceProperties().getProperty("ecm.viewer." + viewer);
        switch (viewer)
        {
        case "js":
            srcLink = srcLink.replace("${context}", req.getContextPath());
            srcLink = srcLink.replace("${type}", type);
            srcLink = srcLink.replace("${title}", title);
            srcLink = srcLink.replace("${fileId}", fileId.toString());
            mv.addObject("link", srcLink);
            break;
        case "snowbound":
            srcLink = srcLink.replace("${ticket}", ticket);
            srcLink = srcLink.replace("${fileId}", fileId.toString());
            srcLink = srcLink.replace("${userid}", auth.getName());
            mv.addObject("link", srcLink);
            break;
        default:
            srcLink = srcLink.replace("${context}", req.getContextPath());
            srcLink = srcLink.replace("${type}", type);
            srcLink = srcLink.replace("${title}", title);
            srcLink = srcLink.replace("${fileId}", fileId.toString());
            mv.addObject("link", srcLink);
            break;
        }
        if (participantTypes != null)
        {
            mv.addObject("participantTypes", participantTypes);
        }

        return mv;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public Properties getEcmFileServiceProperties()
    {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties)
    {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }
}
