package com.armedia.acm.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.Expression;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by dmiller on 1/17/14.
 */
public class ActivitiMuleListener implements ExecutionListener
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Expression payloadExpression;
    private Expression endpointUrl;
    private Expression resultVariable;
    private Expression muleClient;
    private MuleClient realMuleClient;

    public void sendMessage(String url, Object payload, DelegateExecution execution) throws MuleException
    {
        sendMessage(getRealMuleClient(), url, payload, null, execution);
    }

    public void sendMessage(String url, Object payload, String resultVariable, DelegateExecution execution) throws MuleException
    {
        sendMessage(getRealMuleClient(), url, payload, resultVariable, execution);
    }

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception
    {
        String message = String.format("Mule Listener executing for process ID " +
                "[%s], event [%s]", delegateExecution.getProcessInstanceId(),
                delegateExecution.getEventName());
        log.debug(message);

        String url = (String) getEndpointUrl().getValue(delegateExecution);
        Object payload = getPayloadExpression().getValue(delegateExecution);
        String resultVariable = (String) getResultVariable().getValue(delegateExecution);

        MuleClient mc = (MuleClient) getMuleClient().getValue(delegateExecution);

        sendMessage(mc, url, payload, resultVariable, delegateExecution);
    }

    private void sendMessage(MuleClient mc, String url, Object payload, String resultVariable, DelegateExecution de)
            throws MuleException
    {

        Map<String, Object> messageProps = new HashMap<String, Object>();
        setPropertyUnlessNull(messageProps, "activityId", de.getCurrentActivityId());
        setPropertyUnlessNull(messageProps, "activityName", de.getCurrentActivityName());
        setPropertyUnlessNull(messageProps, "eventName", de.getEventName());
        setPropertyUnlessNull(messageProps, "id", de.getId());
        setPropertyUnlessNull(messageProps, "parentId", de.getParentId());
        setPropertyUnlessNull(messageProps, "processBusinessKey", de.getProcessBusinessKey());
        setPropertyUnlessNull(messageProps, "processDefinitionId", de.getProcessDefinitionId());
        setPropertyUnlessNull(messageProps, "processVariablesMap", de.getVariables());
        setPropertyUnlessNull(messageProps, "processInstanceId", de.getProcessInstanceId());


        MuleMessage mm = mc.send(url, payload, messageProps);

        log.debug("Mule sent back: " + mm.getPayload());

        if ( resultVariable != null )
        {
            de.setVariable(resultVariable, mm.getPayload());
        }


    }

    private void setPropertyUnlessNull(Map<String, Object> map, String key, Object value)
    {
        if ( value != null )
        {
            map.put(key, value);
        }
    }

    public Expression getPayloadExpression() {
        return payloadExpression;
    }

    public void setPayloadExpression(Expression payloadExpression) {
        this.payloadExpression = payloadExpression;
    }

    public Expression getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(Expression endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public Expression getResultVariable() {
        return resultVariable;
    }

    public void setResultVariable(Expression resultVariable) {
        this.resultVariable = resultVariable;
    }

    public Expression getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(Expression muleClient) {
        this.muleClient = muleClient;
    }

    public void setRealMuleClient(MuleClient realMuleClient) {
        this.realMuleClient = realMuleClient;
    }

    public MuleClient getRealMuleClient() {
        return realMuleClient;
    }
}
