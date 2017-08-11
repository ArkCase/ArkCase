/**
 *
 */
package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author riste.tutureski
 */
@Controller
@RequestMapping({"/api/v1/users", "/api/latest/users"})
public class SaveGroupAPIController
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private AcmGroupDao groupDao;


    private GroupService groupService;

    @RequestMapping(value = "/group/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveGroup(@RequestBody AcmGroup group, Authentication auth) throws AcmCreateObjectFailedException
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Saving ad-hoc group " + group);
        }

        try
        {
            return groupService.checkAndSaveAdHocGroup(group);
        } catch (Exception e)
        {
            throw new AcmCreateObjectFailedException("Group", e.getMessage(), e);
        }

    }

    @RequestMapping(value = "/group/save/{parentId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveSubGroup(@RequestBody AcmGroup subGroup,
                                 @PathVariable("parentId") String parentId,
                                 Authentication auth) throws AcmCreateObjectFailedException
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Saving ad-hoc subgroup " + subGroup);
        }

        try
        {
            AcmGroup parent = getGroupDao().findByName(parentId);

            // If supervisor for the subgroup is empty, get from the parent group
            if (subGroup.getSupervisor() == null)
            {
                subGroup.setSupervisor(parent.getSupervisor());
            }

            subGroup.setParentGroup(parent);

            return groupService.checkAndSaveAdHocGroup(subGroup);

        } catch (Exception e)
        {
            throw new AcmCreateObjectFailedException("Group", e.getMessage(), e);
        }

    }



    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }
}
