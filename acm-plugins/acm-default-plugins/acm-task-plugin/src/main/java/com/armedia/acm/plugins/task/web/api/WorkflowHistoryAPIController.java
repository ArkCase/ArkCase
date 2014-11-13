/**
 * 
 */
package com.armedia.acm.plugins.task.web.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.plugins.task.model.WorkflowHistoryInstance;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;

/**
 * @author riste.tutureski
 *
 */
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class WorkflowHistoryAPIController {
	
	private TaskDao taskDao;

	@RequestMapping(value = "/history/{id}/{adhoc}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<WorkflowHistoryInstance> getWorkflowHistory(@PathVariable("id") String id, @PathVariable("adhoc") boolean adhoc, Authentication authentication)
	{
		List<WorkflowHistoryInstance> retval = new ArrayList<WorkflowHistoryInstance>();
		
		if (null != id)
		{
			retval = getTaskDao().getWorkflowHistory(id, adhoc);
		}
		
		return retval;
	}

	public TaskDao getTaskDao() {
		return taskDao;
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
	
}
