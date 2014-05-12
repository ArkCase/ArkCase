package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.service.FindComplaintService;
import com.armedia.acm.plugins.complaint.model.Complaint;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class FindComplaintAPIController
{
    private FindComplaintService findComplaintService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Complaint> retrieveListOfComplaints()
    {
        List<Complaint> complaints = getFindComplaintService().listComplaint();
        return complaints;
    }


    public FindComplaintService getFindComplaintService() {
        return findComplaintService;
    }

    public void setFindComplaintService(FindComplaintService findComplaintService) {
        this.findComplaintService = findComplaintService;
    }
}
