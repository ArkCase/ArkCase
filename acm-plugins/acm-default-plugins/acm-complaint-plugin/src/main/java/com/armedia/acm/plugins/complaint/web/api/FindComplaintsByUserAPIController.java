package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.model.ComplaintSearchResultEvent;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by marjan.stefanoski on 8/20/2014.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class FindComplaintsByUserAPIController {

    private ComplaintDao complaintDao;
    private ComplaintEventPublisher eventPublisher;


    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/forUser/{user}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ComplaintListView> tasksForUser(
            @PathVariable("user") String user,
            Authentication authentication,
            HttpSession session
    ) throws AcmListObjectsFailedException {
        if ( log.isInfoEnabled() ) {
            log.info("Finding complaints created by user '" + user + "'");
        }
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        try {
            List<ComplaintListView> complaints = complaintDao.listAllUserComplaints(user);
            for ( ComplaintListView complaint : complaints ){
                getEventPublisher().publishComplaintSearchResultEvent(complaint,authentication,ipAddress);
            }
            return complaints;
        } catch (Exception e){
            log.error("Could not list complaints: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("complaint", e.getMessage(), e);
        }
    }

    public ComplaintEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public ComplaintDao getComplaintDao() {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao) {
        this.complaintDao = complaintDao;
    }

}
