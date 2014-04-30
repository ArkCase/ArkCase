package com.armedia.acm.event;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.spring.SpringContextHolder;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Listen for ACM events, and trigger any registered event responses.
 */
public class AcmEventResponseLauncher implements ApplicationListener<AcmEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private SpringContextHolder contextHolder;

    private MuleContextManager muleContextManager;

    @Override
    public void onApplicationEvent(AcmEvent acmEvent)
    {
        String eventName = acmEvent.getEventType();

        Map<String, EventResponse> eventResponses = getContextHolder().getAllBeansOfType(EventResponse.class);

        for ( EventResponse response : eventResponses.values() )
        {
            if ( response.getEventName().equals(eventName) && response.isEnabled() )
            {
                log.info("Launching event response '" + response.getAction().getActionName() + "'...");

                Map<String, Object> messageProperties = new HashMap<>();
                messageProperties.put("ACM_USER", acmEvent.getUserId());
                messageProperties.put(acmEvent.getObjectType(), acmEvent.getObjectId());
                messageProperties.put("OBJECT_TYPE", acmEvent.getObjectType());
                messageProperties.put("OBJECT_ID", acmEvent.getObjectId());
                messageProperties.put("EVENT_TYPE", acmEvent.getEventType());
                messageProperties.put("EVENT_DATE", acmEvent.getEventDate());
                messageProperties.put("IP_ADDRESS", acmEvent.getIpAddress());
                messageProperties.put("EVENT_SUCCEEDED", acmEvent.isSucceeded());
                messageProperties.putAll(response.getParameters());
                try
                {
                    getMuleContextManager().getMuleClient().dispatch(response.getAction().getTargetMuleEndpoint(), acmEvent, messageProperties);
                } catch (MuleException e)
                {
                    log.error("Could not dispatch Mule event: " + e.getMessage(), e);
                }


            }
        }
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }
}
