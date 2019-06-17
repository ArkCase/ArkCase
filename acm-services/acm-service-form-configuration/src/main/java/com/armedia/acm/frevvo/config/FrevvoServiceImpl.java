/**
 * 
 */
package com.armedia.acm.frevvo.config;

/*-
 * #%L
 * ACM Service: Form Configuration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.frevvo.model.FrevvoFormConstants;
import com.frevvo.forms.client.ApplicationEntry;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.FormTypeFeed;
import com.frevvo.forms.client.FormsService;
import com.frevvo.forms.client.SchemaEntry;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoServiceImpl implements FrevvoService
{

    private Logger LOG = LogManager.getLogger(getClass());
    private FrevvoFormUrl formUrl;
    private ThreadLocal<FormsService> service = new ThreadLocal<>();

    @Override
    public void login()
    {
        try
        {
            LOG.debug("Logging to Frevvo server.");
            if (service != null)
            {
                logout();
            }

            String protocol = getFormUrl().getInternalProtocol();
            String host = getFormUrl().getInternalHost();

            // Frevvo API service need port (at least default). If not provided in the properties file let's try to
            // default port 80.
            int port = 80;
            if (getFormUrl().getInternalPortAsInteger() != null && getFormUrl().getInternalPortAsInteger() > 0)
            {
                port = getFormUrl().getInternalPortAsInteger();
            }

            service.set(new FormsService(protocol, host, port, null));

            String designer = getFormUrl().getDesignerUser();
            String admin = getFormUrl().getAdminUser() + "@" + getFormUrl().getTenant();
            String adminPass = getFormUrl().getAdminPassword();

            service.get().loginAs(designer, admin, adminPass);
        }
        catch (Exception e)
        {
            LOG.error("Cannot login to Frevvo server.", e);
        }
    }

    @Override
    public void logout()
    {
        try
        {
            LOG.debug("Logout from Frevvo server.");
            service.get().logout();
            service.remove();
        }
        catch (Exception e)
        {
            LOG.error("Cannot logout from Frevvo.", e);
        }
    }

    @Override
    public ApplicationEntry getApplication(String id)
    {
        try
        {
            LOG.debug("Taking Frevvo Application with id=" + id);

            URL appEntryUrl = service.get().getEntryURL(ApplicationEntry.class, id);
            ApplicationEntry application = service.get().getEntry(fixFrevvoUrl(appEntryUrl), ApplicationEntry.class);

            return application;
        }
        catch (Exception e)
        {
            LOG.error("Cannot get Frevvo Application with id=" + id, e);
        }

        return null;
    }

    @Override
    public FormTypeEntry getForm(String id)
    {
        try
        {
            LOG.debug("Taking Form with id=" + id);

            URL formEntryUrl = service.get().getEntryURL(FormTypeEntry.class, id);
            FormTypeEntry form = service.get().getEntry(fixFrevvoUrl(formEntryUrl), FormTypeEntry.class);

            return form;
        }
        catch (Exception e)
        {
            LOG.error("Cannot get Form with id=" + id, e);
        }
        return null;
    }

    @Override
    public List<FormTypeEntry> getForms(ApplicationEntry application)
    {
        try
        {
            URL formTypeUrl = new URL(application.getFormTypeFeedLink().getHref());
            FormTypeFeed formFeed = service.get().getFeed(fixFrevvoUrl(formTypeUrl), FormTypeFeed.class);

            return formFeed.getEntries();
        }
        catch (Exception e)
        {
            LOG.error("Cannot take Forms for given application.", e);
        }

        return null;
    }

    @Override
    public List<FormTypeEntry> getPlainForms(ApplicationEntry application)
    {
        List<FormTypeEntry> allForms = getForms(application);
        List<FormTypeEntry> plainForms = new ArrayList<>();

        if (allForms != null)
        {
            for (FormTypeEntry formEntity : allForms)
            {
                if (formEntity.getSummary() != null && formEntity.getSummary().getPlainText() != null &&
                        FrevvoFormConstants.PLAIN.equalsIgnoreCase(formEntity.getSummary().getPlainText().trim()))
                {
                    plainForms.add(formEntity);
                }
            }

            return plainForms;
        }

        return null;
    }

    @Override
    public String getFormKey(SchemaEntry schema)
    {
        if (schema != null && schema.getXmlBlob() != null && schema.getXmlBlob().getBlob() != null
                && !schema.getXmlBlob().getBlob().isEmpty())
        {
            String blob = schema.getXmlBlob().getBlob();

            // Blob is string in XML format but it not contains root element. For that reason it cannot be parsed.
            // Here I am adding root element just to be able to parse below
            blob = FrevvoFormConstants.ROOT_START + blob + FrevvoFormConstants.ROOT_END;

            try (InputStream stream = new ByteArrayInputStream(blob.getBytes(Charsets.UTF_8)))
            {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document document = builder.parse(stream);
                NodeList list = document.getElementsByTagName(FrevvoFormConstants.ELEMENT_KEY_NODE_NAME);

                if (list != null && list.getLength() == 1)
                {
                    Node node = list.item(0);
                    String extIdValue = node.getAttributes().getNamedItem(FrevvoFormConstants.ELEMENT_KEY_ATTRIBUTE_NAME).getNodeValue();

                    if (extIdValue != null)
                    {
                        return extIdValue.replace(FrevvoFormConstants.ELEMENT_KEY_PREFIX, "");
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("Cannot take Form id.", e);
            }
        }

        return null;
    }

    @Override
    public String getFormType(FormTypeEntry form)
    {
        try
        {
            if (form != null && form.getId() != null && !form.getId().isEmpty())
            {
                // Form ID is complex id constructed with pattern: <FORM_TYPE>!<APPLICATION_ID>!<USERNAME>
                // We need only <FORM_TYPE>
                return form.getId().split("!")[0];
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot take Form id.", e);
        }
        return null;
    }

    @Override
    public String getFormApplicationId(FormTypeEntry form)
    {
        try
        {
            if (form != null && form.getId() != null && !form.getId().isEmpty())
            {
                // Form ID is complex id constructed with pattern: <FORM_TYPE>!<APPLICATION_ID>!<USERNAME>
                // We need only <APPLICATION_ID>
                return form.getId().split("!")[1];
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot take Application id.", e);
        }

        return null;
    }

    @Override
    public SchemaEntry getSchema(String id)
    {
        try
        {
            LOG.debug("Taking Schema for id=" + id);

            URL schemaEntryUrl = service.get().getEntryURL(FormTypeEntry.class, id);
            SchemaEntry schema = service.get().getEntry(fixFrevvoUrl(schemaEntryUrl), SchemaEntry.class);

            return schema;
        }
        catch (Exception e)
        {
            LOG.error("Cannot get Schema for id=" + id, e);
        }
        return null;
    }

    @Override
    public FormsService getFormsService()
    {
        return service.get();
    }

    private URL fixFrevvoUrl(URL url)
    {
        try
        {
            String urlAsString = url.toString();
            LOG.debug("Original URL: " + urlAsString);

            if (urlAsString != null)
            {
                if (getFormUrl().getProtocol() != null && !getFormUrl().getProtocol().isEmpty())
                {
                    urlAsString = urlAsString.replace(getFormUrl().getProtocol(), getFormUrl().getInternalProtocol());
                }

                if (getFormUrl().getHost() != null && !getFormUrl().getHost().isEmpty())
                {
                    urlAsString = urlAsString.replace(getFormUrl().getHost(), getFormUrl().getInternalHost());
                }

                // If internal port is null or empty, we should remove the original ":8082" and replace with ""
                String separator = "";
                String internalPort = getFormUrl().getInternalPort();
                if (internalPort == null || internalPort.isEmpty())
                {
                    separator = ":";
                    internalPort = "";
                }

                if (getFormUrl().getPort() != null && !getFormUrl().getPort().isEmpty())
                {
                    urlAsString = urlAsString.replace(separator + getFormUrl().getPort(), internalPort);
                }

                LOG.debug("Changed URL: " + urlAsString);

                return new URL(urlAsString);
            }
        }
        catch (MalformedURLException e)
        {
            LOG.error("Cannot create URL from string.");
        }
        return null;
    }

    @Override
    public FrevvoFormUrl getFormUrl()
    {
        return formUrl;
    }

    public void setFormUrl(FrevvoFormUrl formUrl)
    {
        this.formUrl = formUrl;
    }

    public HttpServletRequest getRequest()
    {
        return getFormUrl().getCurrentRequest();
    }
}
