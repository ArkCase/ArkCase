package com.armedia.acm.services.signature.web.api;

import java.util.ArrayList;
import java.util.List;

import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.model.Signature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class FindSignaturesByTypeByIdAPIControllerTest extends EasyMockSupport {

	private MockMvc mockMvc;
	private MockHttpSession mockHttpSession;
	private Authentication mockAuthentication;

	private FindSignaturesByTypeByIdAPIController unit;

	private SignatureDao mockSignatureDao;

	@Autowired
	private ExceptionHandlerExceptionResolver exceptionResolver;

	private Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() throws Exception {
		mockSignatureDao = createMock(SignatureDao.class);
		mockHttpSession = new MockHttpSession();
		mockAuthentication = createMock(Authentication.class);

		unit = new FindSignaturesByTypeByIdAPIController();

		unit.setSignatureDao(mockSignatureDao);

		mockMvc = MockMvcBuilders.standaloneSetup(unit)
				.setHandlerExceptionResolvers(exceptionResolver).build();
	}

	@Test
	public void findSignatures() throws Exception {
		Long objectId = 500L;
		String objectType = "TASK";
		String ipAddress = "ipAddress";
		String userName = "userName";
		
		Signature foundSignature = new Signature();
		foundSignature.setObjectId(objectId);
		foundSignature.setObjectType(objectType);
		
		List<Signature> signatureList = new ArrayList<Signature>();
		signatureList.add(foundSignature);

		mockHttpSession.setAttribute("acm_ip_address", ipAddress);

		expect(mockSignatureDao.findByObjectIdObjectType(objectId, objectType)).andReturn(signatureList);
		// MVC test classes must call getName() somehow
		expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

		replayAll();

		// To see details on the HTTP calls, change .andReturn() to .andDo(print())	
//		ResultActions resultAction = mockMvc
//		.perform(
//				get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
//						.session(mockHttpSession)
//						.principal(mockAuthentication)).andDo(print());

		MvcResult result = mockMvc
		.perform(
				get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
						.session(mockHttpSession)
						.principal(mockAuthentication)).andReturn();
		
		verifyAll();

		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		assertTrue(result.getResponse().getContentType()
				.startsWith(MediaType.APPLICATION_JSON_VALUE));

		String returned = result.getResponse().getContentAsString();

		log.info("results: " + returned);

		ObjectMapper objectMapper = new ObjectMapper();

		List<Signature> returnedSignatureList = objectMapper.readValue(
				returned,
				objectMapper.getTypeFactory().constructParametricType(List.class, Signature.class));

		assertNotNull(returnedSignatureList);
		assertEquals(returnedSignatureList.size(), 1);
		assertEquals(returnedSignatureList.get(0).getObjectId(), objectId);
		assertEquals(returnedSignatureList.get(0).getObjectType(), objectType);
	}
	
	@Test
	public void findSignatures_exception() throws Exception {
		Long objectId = 500L;
		String objectType = "TASK";
		String ipAddress = "ipAddress";
		String userName = "userName";
		
		Signature foundSignature = new Signature();
		foundSignature.setObjectId(objectId);
		foundSignature.setObjectType(objectType);
		
		List<Signature> signatureList = new ArrayList<Signature>();
		signatureList.add(foundSignature);

		mockHttpSession.setAttribute("acm_ip_address", ipAddress);

		expect(mockSignatureDao.findByObjectIdObjectType(objectId, objectType)).andThrow(new RuntimeException("testException"));
		// MVC test classes must call getName() somehow
		expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

		replayAll();

		// To see details on the HTTP calls, change .andReturn() to .andDo(print())	
//		ResultActions resultAction = mockMvc
//		.perform(
//				get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
//						.session(mockHttpSession)
//						.principal(mockAuthentication)).andDo(print());

		MvcResult result = mockMvc
		.perform(
				get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
						.session(mockHttpSession)
						.principal(mockAuthentication)).andReturn();
		
		verifyAll();

		assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

	}
}

