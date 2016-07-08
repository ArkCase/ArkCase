/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.armedia.acm.frevvo.model.FrevvoFormConstants;
import com.frevvo.forms.client.ApplicationEntry;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.FormTypeFeed;
import com.frevvo.forms.client.FormsService;
import com.frevvo.forms.client.SchemaEntry;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoServiceImpl implements FrevvoService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private FrevvoFormUrl formUrl;
	
	@Override
	public void login()
	{
		try 
		{
			LOG.debug("Logging to Frevvo server.");
			FormsService service = getFormsService();
			
			if (service != null)
			{
				logout();
			}
			
			String protocol = getFormUrl().getInternalProtocol();
			String host = getFormUrl().getInternalHost();
			
			// Frevvo API service need port (at least default). If not provided in the properties file let's try to default port 80.
			int port = 80;
			if (getFormUrl().getInternalPortAsInteger() != null && getFormUrl().getInternalPortAsInteger() > 0)
			{
				port = getFormUrl().getInternalPortAsInteger();
			}
			
			service = new FormsService(protocol, host, port, null);
			
			String designer = getFormUrl().getDesignerUser();
			String admin = getFormUrl().getAdminUser() + "@" + getFormUrl().getTenant();
			String adminPass = getFormUrl().getAdminPassword();
			
			service.loginAs(designer, admin, adminPass);
			getRequest().getSession().setAttribute (FrevvoFormConstants.FREVVO_FORMS_SERVICE, service);
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
			FormsService service = (FormsService) getRequest().getSession().getAttribute(FrevvoFormConstants.FREVVO_FORMS_SERVICE);
			if (service != null) 
			{
				service.logout();
				getRequest().getSession().removeAttribute(FrevvoFormConstants.FREVVO_FORMS_SERVICE);
	   		}
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
			
			FormsService service = getFormsService();
			URL appEntryUrl = service.getEntryURL(ApplicationEntry.class, id);
		    ApplicationEntry application = service.getEntry(fixFrevvoUrl(appEntryUrl), ApplicationEntry.class);
		    
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
			
			FormsService service = getFormsService();
			URL formEntryUrl = service.getEntryURL(FormTypeEntry.class, id);
		    FormTypeEntry form = service.getEntry(fixFrevvoUrl(formEntryUrl), FormTypeEntry.class);

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
			FormsService service = getFormsService();
			URL formTypeUrl = new URL(application.getFormTypeFeedLink().getHref());
			FormTypeFeed formFeed = service.getFeed(fixFrevvoUrl(formTypeUrl), FormTypeFeed.class);
			
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
		List<FormTypeEntry> plainForms = new ArrayList<FormTypeEntry>();
		
		if (allForms != null)
		{
			for (FormTypeEntry formEntity: allForms)
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
		try
		{
			if (schema != null && schema.getXmlBlob() != null && schema.getXmlBlob().getBlob() != null && !schema.getXmlBlob().getBlob().isEmpty())
			{
				String blob = schema.getXmlBlob().getBlob();
				
				// Blob is string in XML format but it not contains root element. For that reason it cannot be parsed.
				// Here I am adding root element just to be able to parse below
				blob = FrevvoFormConstants.ROOT_START + blob + FrevvoFormConstants.ROOT_END;
				
				InputStream stream = new ByteArrayInputStream(blob.getBytes(Charsets.UTF_8));
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				
				Document document =  builder.parse(stream);
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
		}
		catch (Exception e)
		{
			LOG.error("Cannot take Form id.", e);
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
			
			FormsService service = getFormsService();
			URL schemaEntryUrl = service.getEntryURL(FormTypeEntry.class, id);
		    SchemaEntry schema = service.getEntry(fixFrevvoUrl(schemaEntryUrl), SchemaEntry.class);

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
		LOG.debug("Getting Forms Service.");
		
		FormsService service = null;
		try
		{
			service = (FormsService) getRequest().getSession().getAttribute(FrevvoFormConstants.FREVVO_FORMS_SERVICE);
		}
		catch(Exception e)
		{
			LOG.error("Cannot take Forms Service.", e);
		}
		
		return service;
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
	public FrevvoFormUrl getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(FrevvoFormUrl formUrl) {
		this.formUrl = formUrl;
	}	
	
	public HttpServletRequest getRequest() {
		return getFormUrl().getCurrentRequest();
	}
}
