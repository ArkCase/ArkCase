package com.armedia.acm.plugins.report.web.api;

import com.armedia.acm.plugins.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/report", "/api/latest/plugin/report" })
public class SaveReportToGroupsMapAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private ReportService reportService;

	@RequestMapping(value="/reporttogroupsmap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean saveReportToGroupsMap(@RequestBody Map<String, List<String>> reportToGroupsMap, Authentication auth)
    {
		LOG.debug("Saving reports to groups map ...");
		
		boolean retval = getReportService().saveReportToGroupsMap(reportToGroupsMap, auth);
        LOG.debug("Successfuly saved ? " + retval);

		return retval;
    }

	public ReportService getReportService()
	{
		return reportService;
	}

	public void setReportService(ReportService reportService)
	{
		this.reportService = reportService;
	}
}
