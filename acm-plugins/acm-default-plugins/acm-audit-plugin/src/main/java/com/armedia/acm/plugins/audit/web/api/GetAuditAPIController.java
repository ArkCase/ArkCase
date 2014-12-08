/**
 * 
 */
package com.armedia.acm.plugins.audit.web.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/audit", "/api/latest/plugin/audit" })
public class GetAuditAPIController {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private AuditDao auditDao;
	
	@RequestMapping(value = "/page", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public QueryResultPageWithTotalCount<AuditEvent> auditPage(@RequestParam(value="start", required = false, defaultValue = "0") int start,
									 @RequestParam(value="n", required = false, defaultValue = "10") int n,
									 @RequestParam(value="s", required = false, defaultValue = "eventDate DESC") String s)
	{
		QueryResultPageWithTotalCount<AuditEvent> page = new QueryResultPageWithTotalCount<AuditEvent>();
		List<AuditEvent> result = new ArrayList<AuditEvent>();
		
		String sortBy = "eventDate";
		String sort = "DESC";
		
		String[] sArray = s.split(" ");
		if (sArray != null) {
			if (sArray.length == 1) {
				sortBy = sArray[0];
			} else if (sArray.length > 1) {
				sortBy = sArray[0];
				sort = sArray[1];
			}
		}
		
		LOG.info("Taking audit records: start=" + start + ", n=" + n);
		result = getAuditDao().findPage(start, n, sortBy, sort);
		
		LOG.info("Taking total records ...");
		
		int total = getAuditDao().count();
		
		LOG.info("Total records: " + total);
		
		page.setStartRow(start);
		page.setMaxRows(n);
		page.setResultPage(result);
		page.setTotalCount(total);
		
		return page;
	}

	public AuditDao getAuditDao() 
	{
		return auditDao;
	}

	public void setAuditDao(AuditDao auditDao) 
	{
		this.auditDao = auditDao;
	}
	
}
