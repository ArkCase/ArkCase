package com.armedia.acm.plugins.report.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.pentaho.config.PentahoReportUrl;
import com.armedia.acm.plugins.report.model.Report;
import com.armedia.acm.plugins.report.model.Reports;

public class ReportServiceImpl implements ReportService{

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private String reportsPropertyFileLocation;
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
			
			getPropertyFileManager().storeMultiple(propertiesToUpdate, getReportsPropertyFileLocation());
			getPropertyFileManager().removeMultiple(propertiesToDelete, getReportsPropertyFileLocation());
		}
		
		return true;
	}
	
	private String createPentahoReportUri(String path)
	{
		String url = getPropertyFileManager().load(getReportsPropertyFileLocation(), PENTAHO_REPORT_URL_TEMPLATE, PENTAHO_REPORT_URL_TEMPLATE_DEFAULT);
		String user = getPropertyFileManager().load(getReportsPropertyFileLocation(), PENTAHO_SERVER_USER, PENTAHO_SERVER_USER_DEFAULT);
		String password = getPropertyFileManager().load(getReportsPropertyFileLocation(), PENTAHO_SERVER_PASSWORD, PENTAHO_SERVER_PASSWORD_DEFAULT);
		
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

	public String getReportsPropertyFileLocation() {
		return reportsPropertyFileLocation;
	}

	public void setReportsPropertyFileLocation(String reportsPropertyFileLocation) {
		this.reportsPropertyFileLocation = reportsPropertyFileLocation;
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

}
