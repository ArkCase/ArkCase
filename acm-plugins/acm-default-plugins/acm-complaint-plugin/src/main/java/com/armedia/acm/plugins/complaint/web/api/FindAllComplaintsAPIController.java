package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class FindAllComplaintsAPIController
{
    private ComplaintDao complaintDao;
    private ComplaintEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    /*
     * NOTE:  TOBEREMOVED This has been replaced with the generic SearchObjectByTypeAPI to grab all complaints
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ComplaintListView> retrieveListOfComplaints(
            Authentication authentication,
            HttpSession session
    ) throws AcmListObjectsFailedException
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");

        try
        {
            List<ComplaintListView> complaints = getComplaintDao().listAllComplaints();

            for ( ComplaintListView clv : complaints )
            {
                getEventPublisher().publishComplaintSearchResultEvent(clv, authentication, ipAddress);
            }
            return complaints;
        }
        catch (PersistenceException e)
        {
            log.error("Could not list complaints: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("complaint", e.getMessage(), e);
        }
    }


    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public ComplaintEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
}
