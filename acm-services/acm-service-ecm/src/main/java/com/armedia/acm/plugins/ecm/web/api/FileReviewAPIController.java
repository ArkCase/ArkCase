//package com.armedia.acm.plugins.ecm.web.api;
//
//import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
//import com.armedia.acm.plugins.ecm.service.EcmFileService;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import com.armedia.acm.plugins.task.model.AcmTask;
//
//import javax.servlet.http.HttpSession;
//import java.util.List;
//
///**
// * Created by vladimir.radeski on 11/15/2017.
// */
//@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
//public class FileReviewAPIController
//{
//    private EcmFileService ecmFileService;
//
//    @RequestMapping(value = "/files/review/{fileIds}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public List<AcmTask> reviewFiles(@RequestBody AcmTask task, @PathVariable Long[] fileIds, @RequestParam(value = "businessProcessName", defaultValue = "acmDocumentWorkflow") String businessProcessName, Authentication authentication, HttpSession httpSession)
//            throws AcmCreateObjectFailedException
//    {
//        return getEcmFileService().reviewDocuments(fileIds, task, businessProcessName, authentication);
//    }
//
//    public EcmFileService getEcmFileService()
//    {
//        return ecmFileService;
//    }
//
//    public void setEcmFileService(EcmFileService ecmFileService)
//    {
//        this.ecmFileService = ecmFileService;
//    }
//}
