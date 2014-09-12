package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.model.ComplaintsByTimePeriod;
import com.armedia.acm.plugins.complaint.model.TimePeriod;
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

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/12/2014.
 */

@Controller
@RequestMapping({"api/v1/plugin/complaint", "api/latest/plugin/complaint"})
public class ListComplaintsAPIController {

    private ComplaintDao complaintDao;
    private ComplaintEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/list/{timePeriod}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ComplaintListView> retrieveListOfComplaints(
            @PathVariable("timePeriod") String timePeriod,
            Authentication authentication,
            HttpSession session
    ) throws AcmListObjectsFailedException{

        String ipAddress = (String) session.getAttribute("acm_ip_address");
        try {
            List<ComplaintListView> complaints = null;
            switch (ComplaintsByTimePeriod.getTimePeriod(timePeriod)){
                case LAST_WEEK:
                    complaints = getComplaintDao().listComplaintsByTimePeriod(TimePeriod.SEVEN_DAYS);
                    break;
                case LAST_MONTH:
                        complaints = getComplaintDao().listComplaintsByTimePeriod(TimePeriod.THIRTY_DAYS);
                    break;
                case LAST_THREE_MONTH:
                    complaints = getComplaintDao().listComplaintsByTimePeriod(TimePeriod.TRHEE_MONTHS);
                    break;
                case LAST_SIX_MONTH:
                    complaints = getComplaintDao().listComplaintsByTimePeriod(TimePeriod.SIX_MONTHS);
                    break;
                case LAST_YEAR:
                    complaints = getComplaintDao().listComplaintsByTimePeriod(TimePeriod.ONE_YEAR);
                    break;
                default:
                    complaints = getComplaintDao().listComplaintsByTimePeriod(TimePeriod.THIRTY_DAYS);
                    break;
            }

            for ( ComplaintListView clv : complaints ) {
                getEventPublisher().publishComplaintSearchResultEvent(clv, authentication, ipAddress);
            }
            return complaints;
        }
        catch (PersistenceException e) {
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
