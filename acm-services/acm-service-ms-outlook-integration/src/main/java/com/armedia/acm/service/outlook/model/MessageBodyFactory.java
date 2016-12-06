package com.armedia.acm.service.outlook.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Template factory for email notifications
 * 
 * @author dame.gjorgjievski
 *
 */
public class MessageBodyFactory
{

    private static final String DEFAULT_TEMPLATE = "${model.header} \n\n ${model.body} \n\n\n ${model.footer}";

    private String template;

    public MessageBodyFactory()
    {
    }

    public MessageBodyFactory(String template)
    {
        this.template = template;
    }

    public String getTemplate()
    {
        return template != null ? template : DEFAULT_TEMPLATE;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    /**
     * Builds message body string with bound model content from template
     * 
     * @param model
     * @return
     */
    public String buildMessageBodyFromTemplate(String body, String header, String footer)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("header", header);
        model.put("body", body);
        model.put("footer", footer);
        return buildMessageBodyFromTemplate(model);
    }

    /**
     * Builds message body string with bound model content from template
     * 
     * @param model
     * @return
     */
    public String buildMessageBodyFromTemplate(Map<String, Object> model)
    {

        if (!model.containsKey("${model.header}"))
        {
            model.put("${model.header}", "");
        }
        if (!model.containsKey("${model.body}"))
        {
            model.put("${model.body}", "");
        }
        if (!model.containsKey("${model.footer}"))
        {
            model.put("${model.footer}", "");
        }

        String template = new String(getTemplate());
        for (Map.Entry<String, Object> entry : model.entrySet())
        {
            template = template.replace("${model." + entry.getKey() + "}", entry.getValue().toString());
        }
        return template;
    }

}
