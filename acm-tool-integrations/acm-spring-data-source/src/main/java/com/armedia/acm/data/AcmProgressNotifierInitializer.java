package com.armedia.acm.data;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 22, 2017
 *
 */
public class AcmProgressNotifierInitializer implements ApplicationListener<ContextRefreshedEvent>
{

    private AcmProgressNotifier progressNotifier;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, AcmProgressNotifierMessageBuilder> progressNotifierMessageBuilders = applicationContext
                .getBeansOfType(AcmProgressNotifierMessageBuilder.class);
        Map<String, AcmProgressNotifierMessageBuilder> mappedKeys = progressNotifierMessageBuilders.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue().getObjectType(), e -> e.getValue()));
        if (!mappedKeys.isEmpty())
        {
            progressNotifier.setMessageBuilders(mappedKeys);
        }
    }

    /**
     * @param progressNotifier
     *            the progressNotifier to set
     */
    public void setProgressNotifier(AcmProgressNotifier progressNotifier)
    {
        this.progressNotifier = progressNotifier;
    }

}
