package com.armedia.acm.activiti;

import org.activiti.bpmn.model.ActivitiListener;

/**
 * This class is needed so we can define ActivitiListeners in Spring XML files.  Spring keeps trying to
 * resolve what it thinks is a property placeholder (since Activiti uses "${...}" to indicate an Activiti
 * expression).
 *
 * So, just put the expression part (the part between ${ and }) in the Spring config file, and this
 * wrapper class adds the ${ and the }, so Activiti keeps working.
 */
public class AcmActivitiListener extends ActivitiListener
{
    @Override
    public String getImplementation()
    {
        return "${" + super.getImplementation() + "}";
    }
}
