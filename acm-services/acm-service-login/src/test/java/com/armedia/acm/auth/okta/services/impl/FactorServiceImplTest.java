package com.armedia.acm.auth.okta.services.impl;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.ProviderType;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorProfile;
import com.armedia.acm.auth.okta.model.factor.FactorStatus;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.factor.SecurityQuestion;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by joseph.mcgrady on 11/13/2017.
 */
public class FactorServiceImplTest extends EasyMockSupport
{
    private FactorServiceImpl unit;
    private Factor expectedFactor;
    private FactorProfile expectedProfile;
    private OktaUser expectedUser;
    private OktaRestService mockOktaRestService;

    @Before
    public void setup()
    {
        expectedFactor = new Factor();
        expectedFactor.setId("2q89738dbnsdjvu83");
        expectedFactor.setFactorType(FactorType.EMAIL);
        expectedFactor.setProvider(ProviderType.OKTA);
        expectedProfile = new FactorProfile();
        expectedProfile.setEmail("test@armedia.com");
        expectedFactor.setProfile(expectedProfile);
        expectedFactor.setStatus(FactorStatus.NOT_SETUP);

        expectedUser = new OktaUser();
        expectedUser.setId("uy298hf238");

        mockOktaRestService = createMock(OktaRestService.class);

        unit = new FactorServiceImpl();
        unit.setOktaRestService(mockOktaRestService);
    }

    @Test
    public void getFactorByIdTest() throws OktaException
    {
        ResponseEntity<Factor> responseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors/" + expectedFactor.getId(),
                HttpMethod.GET, Factor.class, "parameters")).andReturn(responseEntity);

        replayAll();
        Factor factor = unit.getFactor(expectedFactor.getId(), expectedUser);
        verifyAll();

        // Tests factor data
        assertNotNull(factor);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void getFactorByTypeTest() throws OktaException
    {
        Factor[] expectedFactors = {expectedFactor};
        ResponseEntity<Factor[]> responseEntity = new ResponseEntity<>(expectedFactors, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors",
                HttpMethod.GET, Factor[].class, "parameters")).andReturn(responseEntity).atLeastOnce();

        replayAll();
        Factor factor = unit.getFactor(expectedFactor.getFactorType(), expectedUser);
        verifyAll();

        // Tests factor data
        assertNotNull(factor);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void listEnrolledFactorsTest() throws OktaException
    {
        Factor[] expectedFactors = {expectedFactor};
        ResponseEntity<Factor[]> responseEntity = new ResponseEntity<>(expectedFactors, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors",
                HttpMethod.GET, Factor[].class, "parameters")).andReturn(responseEntity).atLeastOnce();

        replayAll();
        List<Factor> factors = unit.listEnrolledFactors(expectedUser);
        verifyAll();

        // Tests factor data
        assertNotNull(factors);
        assertEquals(1, factors.size());
        Factor factor = factors.get(0);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void listAvailableFactorsTest() throws OktaException
    {
        Factor[] expectedFactors = {expectedFactor};
        ResponseEntity<Factor[]> responseEntity = new ResponseEntity<>(expectedFactors, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors/catalog",
                HttpMethod.GET, Factor[].class, "parameters")).andReturn(responseEntity).atLeastOnce();

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors",
                HttpMethod.GET, Factor[].class, "parameters")).andReturn(new ResponseEntity<>(new Factor[]{new Factor()}, HttpStatus.OK)).times(1);

        replayAll();
        List<Factor> factors = unit.listAvailableFactors(expectedUser);
        verifyAll();

        // Tests factor data
        assertNotNull(factors);
        assertEquals(1, factors.size());
        Factor factor = factors.get(0);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void listSecurityQuestionsTest() throws OktaException
    {
        SecurityQuestion expectedQuestion = new SecurityQuestion();
        expectedQuestion.setQuestion("first_job");
        expectedQuestion.setQuestionText("programmer");
        SecurityQuestion[] expectedQuestions = {expectedQuestion};
        ResponseEntity<SecurityQuestion[]> responseEntity = new ResponseEntity<>(expectedQuestions, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors/questions",
                HttpMethod.GET, SecurityQuestion[].class, "parameters")).andReturn(responseEntity).atLeastOnce();

        replayAll();
        List<SecurityQuestion> questions = unit.listSecurityQuestions(expectedUser);
        verifyAll();

        // Tests factor data
        assertNotNull(questions);
        assertEquals(1, questions.size());
        SecurityQuestion question = questions.get(0);
        assertEquals(expectedQuestion.getQuestion(), question.getQuestion());
        assertEquals(expectedQuestion.getQuestionText(), question.getQuestionText());
    }

    @Test
    public void deleteFactorByIdTest() throws OktaException
    {
        ResponseEntity<Factor> deleteResponseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors/" + expectedFactor.getId(),
                HttpMethod.DELETE, Factor.class, "parameters")).andReturn(deleteResponseEntity);

        replayAll();
        unit.deleteFactor(expectedFactor.getId(), expectedUser);
        verifyAll();
    }

    @Test
    public void deleteFactorByTypeTest() throws OktaException
    {
        Factor[] expectedFactors = {expectedFactor};
        ResponseEntity<Factor[]> responseEntity = new ResponseEntity<>(expectedFactors, HttpStatus.OK);
        ResponseEntity<Factor> deleteResponseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors",
                HttpMethod.GET, Factor[].class, "parameters")).andReturn(responseEntity).atLeastOnce();
        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors/" + expectedFactor.getId(),
                HttpMethod.DELETE, Factor.class, "parameters")).andReturn(deleteResponseEntity);

        replayAll();
        unit.deleteFactor(expectedFactor.getFactorType(), expectedUser);
        verifyAll();
    }
}