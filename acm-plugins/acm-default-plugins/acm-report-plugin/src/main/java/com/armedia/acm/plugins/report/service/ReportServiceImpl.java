package com.armedia.acm.plugins.report.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.pentaho.config.PentahoReportUrl;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.model.Reports;

public class ReportServiceImpl implements ReportService{

	private final Logger LOG = LoggerFactory.getLogger(getClass());

    private String reportsPropertiesFileLocation;
    private String reportToGroupsMapPropertiesFileLocation;
    private Map<String, String> reportToGroupsMapProperties;
    private Map<String, String> reportPluginProperties;
	private MuleClient muleClient;
	private PropertyFileManager propertyFileManager;
	private PentahoReportUrl reportUrl;
	
	private final String PENTAHO_REPORT_URL_TEMPLATE = "PENTAHO_REPORT_URL_TEMPLATE";
	private final String PENTAHO_REPORT_URL_TEMPLATE_DEFAULT = "/pentaho/api/repos/{path}/viewer";
	private final String PENTAHO_SERVER_USER = "PENTAHO_SERVER_USER";
	private final String PENTAHO_SERVER_USER_DEFAULT = "admin";
	private final String PENTAHO_SERVER_PASSWORD = "PENTAHO_SERVER_PASSWORD";
	private final String PENTAHO_SERVER_PASSWORD_DEFAULT = "password";
	
	@Override
	public List<Report> getPentahoReports() throws Exception, MuleException
	{
		Reports reports = null;
		
		MuleMessage received = getMuleClient().send("vm://getPentahoReports.in", getReportUrl().getReportsUrl().replace("http://", "").replace("https://", ""), null);
		String xml = received.getPayload(String.class);
		
		MuleException e = received.getInboundProperty("getPantehoReportsException");
        if ( e != null )
        {
            throw e;
        }
		
		if (xml != null)
		{
			String utf8XML = new String(xml.getBytes(), StandardCharsets.UTF_8);
			reports = (Reports) convertFromXMLToObject(utf8XML, Reports.class);
			
			if (reports == null)
			{
				throw new RuntimeException(xml);
			}
		}
		else
		{
			throw new RuntimeException("Taking Pentaho reports failed.");
		}
		
		return reports.getValue();
	}

	@Override
	public List<Report> getAcmReports() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public boolean saveReportToGroupsMap(Map<String, List<String>> reportsToGroupsMap, Authentication auth)
    {
        boolean success;
        try
        {
            getPropertyFileManager().storeMultiple(prepareReportToGroupsMapForSaving(reportsToGroupsMap), getReportToGroupsMapPropertiesFileLocation(), true);
            success = true;
        }
        catch (Exception e)
        {
            LOG.error("Cannot save report to groups map", e);
            success = false;
        }
        return success;
    }

    @Override
    public Map<String, List<String>> getReportToGroupsMap()
    {
        Map<String, String> reportsToGroupsMap = getReportToGroupsMapProperties();
        return prepareReportToGroupsMapForRetrieving(reportsToGroupsMap);
    }

	@Override
	public boolean saveReports(List<Report> reports) {
		
		if (reports != null && reports.size() > 0)
		{
			Map<String, String> propertiesToUpdate = new HashMap<String, String>();
			List<String> propertiesToDelete = new ArrayList<String>();
			for (Report report : reports)
			{
				String key = report.getPropertyName();
				
				if (report.isInjected())
				{
					String value = createPentahoReportUri(report.getPropertyPath());
					propertiesToUpdate.put(key, value);
				}
				else
				{
					propertiesToDelete.add(key);
				}
			}
			
			getPropertyFileManager().storeMultiple(propertiesToUpdate, getReportsPropertiesFileLocation(), false);
			getPropertyFileManager().removeMultiple(propertiesToDelete, getReportsPropertiesFileLocation());
		}
		return true;
	}
	
	private String createPentahoReportUri(String path)
	{
		String url = getPropertyFileManager().load(getReportsPropertiesFileLocation(), PENTAHO_REPORT_URL_TEMPLATE, PENTAHO_REPORT_URL_TEMPLATE_DEFAULT);
		String user = getPropertyFileManager().load(getReportsPropertiesFileLocation(), PENTAHO_SERVER_USER, PENTAHO_SERVER_USER_DEFAULT);
		String password = getPropertyFileManager().load(getReportsPropertiesFileLocation(), PENTAHO_SERVER_PASSWORD, PENTAHO_SERVER_PASSWORD_DEFAULT);
		
		if (url != null)
		{
			url = url.replace("{path}", path);
			url = url + "?userid=" + user + "&password=" + password;
		}
		
		return url;
	}
	
	private Object convertFromXMLToObject(String xml, Class<?> c)
	{
		Object obj = null;
		try
		{
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
	        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = documentBuilder.parse(inputStream);
	        Element element = document.getDocumentElement();
	        JAXBContext context = JAXBContext.newInstance(c);
	        Unmarshaller unmarshaller = context.createUnmarshaller();
	        JAXBElement<?> jaxbElement = unmarshaller.unmarshal(element, c);
	        obj = jaxbElement.getValue();
		}
        catch(Exception e) 
		{
        	LOG.error("Error while creating Object from XML. ", e);
        }
		
		return obj;
	}

    private Map<String, String> prepareReportToGroupsMapForSaving(Map<String, List<String>> reportsToGroupsMap)
    {
        Map<String, String> retval = new HashMap<String, String>();

        if (reportsToGroupsMap != null && reportsToGroupsMap.size() > 0)
        {
            for (Map.Entry<String, List<String>> entry : reportsToGroupsMap.entrySet())
            {
                retval.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
            }
        }

        return retval;
    }

    private Map<String, List<String>> prepareReportToGroupsMapForRetrieving(Map<String, String> reportsToGroupsMap)
    {
        Map<String, List<String>> retval = new HashMap<String, List<String>>();

        if (reportsToGroupsMap != null && reportsToGroupsMap.size() > 0)
        {
            for (Map.Entry<String, String> entry : reportsToGroupsMap.entrySet())
            {
                if(!("".equals(entry.getValue())) && entry.getValue() != null){
                    retval.put(entry.getKey(), Arrays.asList(entry.getValue().split(",")));
                }
            }
        }

        return retval;
    }


	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}

	public PropertyFileManager getPropertyFileManager() {
		return propertyFileManager;
	}

	public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
		this.propertyFileManager = propertyFileManager;
	}

	public PentahoReportUrl getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(PentahoReportUrl reportUrl) {
		this.reportUrl = reportUrl;
	}

    public String getReportsPropertiesFileLocation() {
        return reportsPropertiesFileLocation;
    }

    public void setReportsPropertiesFileLocation(String reportsPropertiesFileLocation) {
        this.reportsPropertiesFileLocation = reportsPropertiesFileLocation;
    }

    public String getReportToGroupsMapPropertiesFileLocation() {
        return reportToGroupsMapPropertiesFileLocation;
    }

    public void setReportToGroupsMapPropertiesFileLocation(String reportToGroupsMapPropertiesFileLocation) {
        this.reportToGroupsMapPropertiesFileLocation = reportToGroupsMapPropertiesFileLocation;
    }

    public Map<String, String> getReportToGroupsMapProperties() {
        return reportToGroupsMapProperties;
    }

    public void setReportToGroupsMapProperties(Map<String, String> reportToGroupsMapProperties) {
        this.reportToGroupsMapProperties = reportToGroupsMapProperties;
    }

    public Map<String, String> getReportPluginProperties() {
        return reportPluginProperties;
    }

    public void setReportPluginProperties(Map<String, String> reportPluginProperties) {
        this.reportPluginProperties = reportPluginProperties;
    }

}
