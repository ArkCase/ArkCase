package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.plugins.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class GetReportToGroupsMapAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());

    private ReportService reportService;
	
	@RequestMapping(value="/reporttogroupsmap", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<String>> getReportToGroupsMap()
    {
		LOG.debug("Getting report to groups map ...");
		Map<String, List<String>> retval = getReportService().getReportToGroupsMap();
        if(null == retval){
            LOG.warn("Properties not available..");
        }
        LOG.debug("Reports to groups map : " + retval.toString());
		return retval;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

}
