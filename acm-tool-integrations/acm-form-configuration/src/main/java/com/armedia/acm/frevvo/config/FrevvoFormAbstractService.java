
/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.file.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

/**
 * @author riste.tutureski
 *
 */
public abstract class FrevvoFormAbstractService implements FrevvoFormService{

	private Logger LOG = LoggerFactory.getLogger(FrevvoFormAbstractService.class);
	
	private Map<String, Object> properties;
	private HttpServletRequest request;
	private Authentication authentication;
	private AuthenticationTokenService authenticationTokenService;
	private UserDao userDao;
	private UserActionDao userActionDao;
	private EcmFileService ecmFileService;
    private String servletContextPath;
    private String userIpAddress;
    private EcmFileDao ecmFileDao;
    private AcmUserActionExecutor userActionExecutor;
    private MuleClient muleClient;

    @Override
	public Object init() {
		Object result = "";
		
		String mode = getRequest().getParameter("mode");
		String xmlId = getRequest().getParameter("xmlId");
		
		if ("edit".equals(mode) && null != xmlId && !"".equals(xmlId))
		{
			try{
				Long id = Long.parseLong(xmlId);
				EcmFile file = getEcmFileDao().find(id);
				
				MuleMessage message = getMuleClient().send("vm://downloadFileFlow.in", file.getEcmFileId(), null);
				
				if (null != message && message.getPayload() instanceof ContentStream)
				{
					result = getContent((ContentStream) message.getPayload());
				}
				
			}
			catch(Exception e)
			{
				LOG.warn("EcmFile with id=" + xmlId + " is not found while edit mode. Empty Frevvo form will be shown.");
			}
		}
		
		return result;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Authentication getAuthentication() {
		return authentication;
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	@Override
	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;		
	}

	@Override
	public UserDao getUserDao() {
		return userDao;
	}

	@Override
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Override
	public UserActionDao getUserActionDao() {
		return userActionDao;
	}

	@Override
	public void setUserActionDao(UserActionDao userActionDao) {
		this.userActionDao = userActionDao;
	}
	
	@Override
	public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }
	
	@Override
	public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
	
	@Override
	public String getServletContextPath()
    {
        return servletContextPath;
    }

	@Override
    public void setServletContextPath(String servletContextPath)
    {
        this.servletContextPath = servletContextPath;
    }
	
	public Object convertFromXMLToObject(String xml, Class<?> c) {
		Object obj = null;
		try{
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
	        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = documentBuilder.parse(inputStream);
	        Element element = document.getDocumentElement();
	        JAXBContext context = JAXBContext.newInstance(c);
	        Unmarshaller unmarshaller = context.createUnmarshaller();
	        JAXBElement<?> jaxbElement = unmarshaller.unmarshal(element, c);
	        obj = jaxbElement.getValue();
		}
        catch(Exception e) {
        	LOG.error("Error while creating Object from XML. " + e);
        }
		
		return obj;
	}
	
	public FrevvoUploadedFiles saveAttachments(
            MultiValueMap<String, MultipartFile> attachments,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName) throws AcmCreateObjectFailedException
	{
        FrevvoUploadedFiles retval = new FrevvoUploadedFiles();

		if ( attachments != null )
		{
			for ( Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet() )
			{
				String mode = getRequest().getParameter("mode");
				String xmlId = getRequest().getParameter("xmlId");
				String pdfId = getRequest().getParameter("pdfId");
				
                if ( entry.getKey().startsWith("form_"))
                {
                    // form xml...
                    List<MultipartFile> xml = entry.getValue();
                    if ( xml != null && xml.size() == 1 )
                    {
                        MultipartFile xmlAttachment = xml.get(0);
                        
                        EcmFile formXml = null;
                        
                        // Update XML form if the mode is "edit", otherwise create new
                        if ("edit".equals(mode) && null != xmlId && !"".equals(xmlId))
                        {
                        	EcmFile file = null;
                        	try{
                        		Long id = Long.parseLong(xmlId);
                        		file = getEcmFileDao().find(id);
                        	}
                        	catch(Exception e)
                        	{
                        		LOG.warn("The file with id=" + xmlId + " is not found. The update will not proceed.");
                        	}
                        	
                        	formXml = getEcmFileService().update(
                        			file,
                        			xmlAttachment,
                        			getAuthentication());
                        }
                        else
                        {
                        	formXml = uploadFile(
                                getFormName() + "_xml",
                                targetCmisFolderId,
                                parentObjectType,
                                parentObjectId,
                                parentObjectName,
                                xmlAttachment);
                        }
                        retval.setFormXml(formXml);
                    }
                }
                else if ( !entry.getKey().equals("UploadFiles" ) )
                {
                    // form pdf
                    List<MultipartFile> pdf = entry.getValue();
                    if ( pdf != null && pdf.size() == 1 )
                    {
                        MultipartFile pdfAttachment = pdf.get(0);
                        EcmFile pdfRendition = null;
                        
                        // Update PDF form if the mode is "edit", otherwise create new
                        if ("edit".equals(mode) && null != pdfId && !"".equals(pdfId))
                        {
                        	EcmFile file = null;
                        	try{
                        		Long id = Long.parseLong(pdfId);
                        		file = getEcmFileDao().find(id);
                        	}
                        	catch(Exception e)
                        	{
                        		LOG.warn("The file with id=" + pdfId + " is not found. The update will not proceed.");
                        	}
                        	pdfRendition = getEcmFileService().update(
                        			file,
                        			pdfAttachment,
                        			getAuthentication());
                        }
                        else
                        {
                        	pdfRendition = uploadFile(
	                                getFormName(),
	                                targetCmisFolderId,
	                                parentObjectType,
	                                parentObjectId,
	                                parentObjectName,
	                                pdfAttachment);
                        }
                        retval.setPdfRendition(pdfRendition);
                    }
                }
                else
                {
                    // this must be the other uploaded files
                    final List<MultipartFile> attachmentsList = entry.getValue();

                    if (attachmentsList != null && !attachmentsList.isEmpty() )
                    {
                        for (final MultipartFile attachment : attachmentsList)
                        {
                            EcmFile uploaded = uploadFile(
                                    "attachment",
                                    targetCmisFolderId,
                                    parentObjectType,
                                    parentObjectId,
                                    parentObjectName,
                                    attachment);
                            retval.getUploadedFiles().add(uploaded);
                        }
                    }
                }

			}
		}

        return retval;
	}

    private EcmFile uploadFile(String fileType,
                            String targetCmisFolderId,
                            String parentObjectType,
                            Long parentObjectId,
                            String parentObjectName,
                            MultipartFile attachment)
            throws AcmCreateObjectFailedException
    {
        try
        {
            AcmMultipartFile file = new AcmMultipartFile(
                attachment.getName(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.isEmpty(),
                attachment.getSize(),
                attachment.getBytes(),
                attachment.getInputStream(),
                true);

            EcmFile uploaded = getEcmFileService().upload(
                fileType,
                file,
                getAuthentication(),
                targetCmisFolderId,
                parentObjectType,
                parentObjectId,
                parentObjectName);

            return uploaded;
        }
        catch (IOException e)
        {
            LOG.error("Could not upload file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException("file", e.getMessage(), e);
        }
    }

    public String cleanXML(String xml)
	{
		if (xml != null){
			String changedXML = xml.replaceAll("(?s)<rta_label.*?<\\/rta_label>", "");
			changedXML = changedXML.replaceAll("<[^<>]*?\\/>", "");
			changedXML = changedXML.replaceAll("(?s)<[^\\/<>]*?>[\n\r\t ]*?<\\/[^<>]*?>", "");
			
			if (!xml.equals(changedXML)){
				return cleanXML(changedXML);
			}
		}
		return xml;
	}
	
	public List<String> convertToList(String source, String delimiter){
		if (source != null && !"".equals(source)) {
			String[] sourceArray = source.split(delimiter, -1);
			return new LinkedList<String>(Arrays.asList(sourceArray)); 
		}
		
		return null;
	}
	
	private String getContent(ContentStream contentStream)
	{
		String content = "";
		InputStream inputStream = null;
		
		try
        {
			inputStream = contentStream.getStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			content = writer.toString();
        } 
		catch (IOException e) 
		{
        	LOG.error("Could not copy input stream to the writer: " + e.getMessage(), e);
		}
		finally
        {
            if ( inputStream != null )
            {
                try
                {
                	inputStream.close();
                }
                catch (IOException e)
                {
                    LOG.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }
		
		return content;
	}

    @Override
    public String getUserIpAddress()
    {
        return userIpAddress;
    }

    @Override
    public void setUserIpAddress(String userIpAddress)
    {
        this.userIpAddress = userIpAddress;
    }

	/**
	 * @return the ecmFileDao
	 */
	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	/**
	 * @param ecmFileDao the ecmFileDao to set
	 */
	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	/**
	 * @return the userActionExecutor
	 */
	public AcmUserActionExecutor getUserActionExecutor() {
		return userActionExecutor;
	}

	/**
	 * @param userActionExecutor the userActionExecutor to set
	 */
	public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor) {
		this.userActionExecutor = userActionExecutor;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}	
	
}
