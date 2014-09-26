package com.armedia.acm.services.signature.web.api;

import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.model.ApplicationSignatureEvent;
import com.armedia.acm.services.signature.model.Signature;
import com.armedia.acm.services.signature.service.SignatureEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		 "classpath:/spring/spring-web-acm-web.xml",
		"classpath:/spring/spring-library-electronic-signature-test.xml" })
public class SignatureAPIControllerTest extends EasyMockSupport {

	private MockMvc mockMvc;
	private MockHttpSession mockHttpSession;
	private Authentication mockAuthentication;

	private SignatureAPIController unit;

	private SignatureDao mockSignatureDao;
	private SignatureEventPublisher mockSignatureEventPublisher;
	private LdapAuthenticateManager mockLdapAuthenticateManager;
	

	@Autowired
	private ExceptionHandlerExceptionResolver exceptionResolver;

	private Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() throws Exception {
		mockSignatureDao = createMock(SignatureDao.class);
		mockSignatureEventPublisher = createMock(SignatureEventPublisher.class);
		mockLdapAuthenticateManager = createMock(LdapAuthenticateManager.class);
		mockHttpSession = new MockHttpSession();
		mockAuthentication = createMock(Authentication.class);

		unit = new SignatureAPIController();

		unit.setSignatureDao(mockSignatureDao);
		unit.setSignatureEventPublisher(mockSignatureEventPublisher);
		unit.setLdapAuthenticateManager(mockLdapAuthenticateManager);

		mockMvc = MockMvcBuilders.standaloneSetup(unit)
				.setHandlerExceptionResolvers(exceptionResolver).build();
	}

	@Test
	public void signObject_Task_authenticated() throws Exception {
		Long objectId = 500L;
		String objectType = "TASK";
		String ipAddress = "ipAddress";
		String password = "password";
		String userName = "userName";
		
		Signature foundSignature = new Signature();
		foundSignature.setObjectId(objectId);
		foundSignature.setObjectType(objectType);

		Capture<Signature> signatureToSave = new Capture<>();
		Capture<ApplicationSignatureEvent> capturedEvent = new Capture<>();

		mockHttpSession.setAttribute("acm_ip_address", ipAddress);

		expect(mockLdapAuthenticateManager.authenticate(userName, password)).andReturn(true);
		expect(mockSignatureDao.save(capture(signatureToSave))).andReturn(foundSignature);
		mockSignatureEventPublisher.publishSignatureEvent(capture(capturedEvent));
		// MVC test classes must call getName() somehow
		expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

		replayAll();

		// To see details on the HTTP calls, change .andReturn() to .andDo(print())
		MvcResult result = mockMvc
				.perform(
						post("/api/v1/plugin/signature/confirm/{objectType}/{objectId}", objectType, objectId)
								.param("confirmPassword", password)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockHttpSession)
								.principal(mockAuthentication)).andReturn();

		verifyAll();

		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertTrue(result.getResponse().getContentType()
				.startsWith(MediaType.APPLICATION_JSON_VALUE));

		String returned = result.getResponse().getContentAsString();

		log.info("results: " + returned);

		ObjectMapper objectMapper = new ObjectMapper();

		Signature signedSignature = objectMapper.readValue(returned, Signature.class);

		assertNotNull(signedSignature);
		assertEquals(signedSignature.getObjectId(), objectId);

		ApplicationSignatureEvent event = capturedEvent.getValue();
		assertEquals(objectId, event.getObjectId());
		assertEquals(objectType, event.getObjectType());
		assertTrue(event.isSucceeded());
	}
	
	@Test
	public void signObject_Task_notauthenticated() throws Exception {
		Long objectId = 500L;
		String objectType = "TASK";
		String ipAddress = "ipAddress";
		String password = "password";
		String userName = "userName";
		
		Signature foundSignature = new Signature();
		foundSignature.setObjectId(objectId);

		Capture<ApplicationSignatureEvent> capturedEvent = new Capture<>();

		mockHttpSession.setAttribute("acm_ip_address", ipAddress);

		expect(mockLdapAuthenticateManager.authenticate(userName, password)).andReturn(false);
		mockSignatureEventPublisher.publishSignatureEvent(capture(capturedEvent));
		// MVC test classes must call getName() somehow
		expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

		replayAll();

		// To see details on the HTTP calls, change .andReturn() to .andDo(print())
		MvcResult result = mockMvc
				.perform(
						post("/api/v1/plugin/signature/confirm/{objectType}/{objectId}", objectType, objectId)
								.param("confirmPassword", password)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockHttpSession)
								.principal(mockAuthentication)).andReturn();

		verifyAll();

		ApplicationSignatureEvent event = capturedEvent.getValue();
		assertEquals(objectId, event.getObjectId());
		assertEquals(objectType, event.getObjectType());
		assertFalse(event.isSucceeded());
	}

	@Test
	 public void signTask_exception() throws Exception
	 {
		Long objectId = 500L;
		String objectType = "TASK";
		String ipAddress = "ipAddress";
		String password = "password";
		String userName = "userName";

		Signature foundSignature = new Signature();
		foundSignature.setObjectId(objectId);

		Capture<Signature> signatureToSave = new Capture<>();
		Capture<ApplicationSignatureEvent> capturedEvent = new Capture<>();

		mockHttpSession.setAttribute("acm_ip_address", ipAddress);
		
		expect(mockLdapAuthenticateManager.authenticate(userName, password)).andReturn(true);
		expect(mockSignatureDao.save(capture(signatureToSave))).andThrow(new RuntimeException("testException"));
		mockSignatureEventPublisher.publishSignatureEvent(capture(capturedEvent));
		// MVC test classes must call getName() somehow
		expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

		replayAll();

		// To see details on the HTTP calls, change .andReturn() to .andDo(print())
		MvcResult result = mockMvc
				.perform(
						post("/api/v1/plugin/signature/confirm/{objectType}/{objectId}", objectType, objectId)
								.param("confirmPassword", password)
								.contentType(
										MediaType.APPLICATION_FORM_URLENCODED)
								.session(mockHttpSession)
								.principal(mockAuthentication)).andReturn();

		verifyAll();

		ApplicationSignatureEvent event = capturedEvent.getValue();
		assertEquals(objectId, event.getObjectId());
		assertEquals(objectType, event.getObjectType());
		assertFalse(event.isSucceeded());

	 }

}
