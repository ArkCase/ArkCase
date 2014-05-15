package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.service.SaveComplaintEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class FindComplaintAPIController
{
    private ComplaintDao complaintDao;
    private SaveComplaintEventPublisher eventPublisher;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ComplaintListView> retrieveListOfComplaints(
            Authentication authentication,
            HttpSession session
    )
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");

        List<ComplaintListView> complaints = getComplaintDao().listAllComplaints();

        for ( ComplaintListView clv : complaints )
        {
            getEventPublisher().publishComplaintSearchResultEvent(clv, authentication, ipAddress);
        }
        return complaints;
    }


    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public SaveComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(SaveComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
}
