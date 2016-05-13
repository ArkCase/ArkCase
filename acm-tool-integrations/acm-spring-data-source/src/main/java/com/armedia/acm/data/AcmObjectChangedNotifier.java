package com.armedia.acm.data;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nebojsha on 06.05.2016.
 */
public class AcmObjectChangedNotifier
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private MessageChannel objectEventChannel;
    private Set<String> includeObjectTypes = new HashSet<>();
    private Set<String> includeParentObjectTypes = new HashSet<>();
    private Set<String> includeClassNames = new HashSet<>();

    public void notifyChange(AcmObjectEvent acmObject)
    {
        if (includeClassNames.contains(acmObject.getClassName())
                || includeObjectTypes.contains(acmObject.getObjectType())
                || includeParentObjectTypes.contains(acmObject.getParentObjectType()))
        {
            createAndSendMessage(acmObject);
        } else
        {
            log.debug("Object is not eligible for notifying, didn't pass the filters. {}", acmObject);
        }
    }

    private void createAndSendMessage(AcmObjectEvent acmObject)
    {
        Message<AcmObjectEvent> insertMessage = MessageBuilder.withPayload(acmObject).build();
        objectEventChannel.send(insertMessage);
    }

    public void setObjectEventChannel(MessageChannel ftpChannel)
    {
        this.objectEventChannel = ftpChannel;
    }

    public void setIncludeObjectTypes(String includeObjectTypes)
    {
        this.includeObjectTypes = parseCSV(includeObjectTypes);
    }


    public void setIncludeParentObjectTypes(String includeParentObjectTypes)
    {
        this.includeParentObjectTypes = parseCSV(includeParentObjectTypes);
    }

    public void setIncludeClassNames(String includeClassNames)
    {
        this.includeClassNames = parseCSV(includeClassNames);
    }


    /**
     * parses comma separated values from properties
     *
     * @param includeObjectTypes
     * @return returns Set of parsed values, empty Set if nothing found
     */
    private Set<String> parseCSV(String includeObjectTypes)
    {
        Set<String> parsedCSV = new HashSet<>();
        if (includeObjectTypes == null)
        {
            return parsedCSV;
        }
        for (String s : includeObjectTypes.split(",[\\s]*"))
        {
            if (StringUtils.isNotEmpty(s.trim()))
            {
                parsedCSV.add(s);
            }
        }
        return parsedCSV;
    }
}
