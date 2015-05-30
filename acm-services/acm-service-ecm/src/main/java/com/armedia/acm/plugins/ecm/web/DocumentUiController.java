package com.armedia.acm.plugins.ecm.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;
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

//    @RequestMapping(method = RequestMethod.GET)
//    public ModelAndView openComplaints(Authentication auth) {
//        ModelAndView mv = new ModelAndView();
//        mv.setViewName("doclist");
//        return mv;
//    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    public ModelAndView openComplaint(HttpServletRequest req, Authentication auth, @PathVariable(value = "fileId") Long fileId
    ) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("document");
        mv.addObject("objId", fileId);

        EcmFile file = getFileService().findById(fileId);
        String mimeFileType = file.getFileMimeType();
        String title = file.getFileName();
        String type = "odt";

        //for viewerJs
        if (mimeFileType.contains("pdf")){
            type = "pdf";
        }

        mv.addObject("type", type);
        mv.addObject("title", title);
        mv.addObject("context", req.getContextPath());

        String participantTypes = getEcmFileServiceProperties().getProperty("ecm.participantTypes");
        String ticket = getAuthenticationTokenService().getTokenForAuthentication(auth);
        String viewer = getEcmFileServiceProperties().getProperty("ecm.viewer");
        String srcLink = getEcmFileServiceProperties().getProperty("ecm.viewer."+viewer);
        switch (viewer){
            case "js":
                srcLink = srcLink.replace("${context}", req.getContextPath());
                srcLink = srcLink.replace("${type}", type);
                srcLink = srcLink.replace("${title}", title);
                srcLink = srcLink.replace("${fileId}", fileId.toString());
                mv.addObject("link",srcLink);
                break;
            case "snowbound" :
                srcLink = srcLink.replace("${ticket}", ticket);
                srcLink = srcLink.replace("${fileId}", fileId.toString());
                mv.addObject("link",srcLink);
                break;
            default:
                srcLink = srcLink.replace("${context}",req.getContextPath());
                srcLink = srcLink.replace("${type}",type);
                srcLink = srcLink.replace("${title}",title);
                srcLink = srcLink.replace("${fileId}",fileId.toString());
                mv.addObject("link",srcLink);
                break;
        }
        if(participantTypes != null){
            mv.addObject("participantTypes", participantTypes);
        }

        return mv;
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }

    public Properties getEcmFileServiceProperties() {
        return ecmFileServiceProperties;
    }

    public void setEcmFileServiceProperties(Properties ecmFileServiceProperties) {
        this.ecmFileServiceProperties = ecmFileServiceProperties;
    }

    public AuthenticationTokenService getAuthenticationTokenService() {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }
}
