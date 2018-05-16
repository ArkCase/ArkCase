package com.armedia.acm.auth.okta.services.impl;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.ProviderType;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorProfile;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.services.FactorService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph.mcgrady on 11/13/2017.
 */
public class FactorLifecycleServiceImplTest extends EasyMockSupport
{
    private FactorLifecycleServiceImpl unit;
    private Factor expectedFactor;
    private FactorProfile expectedProfile;
    private OktaUser expectedUser;
    private OktaRestService mockOktaRestService;
    private FactorService mockFactorService;

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

        expectedUser = new OktaUser();
        expectedUser.setId("uy298hf238");

        mockOktaRestService = createMock(OktaRestService.class);
        mockFactorService = createMock(FactorServiceImpl.class);

        unit = new FactorLifecycleServiceImpl();
        unit.setOktaRestService(mockOktaRestService);
        unit.setFactorService(mockFactorService);
    }

    @Test
    public void enrollTest() throws OktaException
    {
        ResponseEntity<Factor> responseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);
        String expectedRequestBody = "{\"provider\":\"OKTA\",\"profile\":{\"email\":\"test@armedia.com\"},\"factorType\":\"email\"}";

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/factors?activate=true",
                HttpMethod.POST, Factor.class, expectedRequestBody)).andReturn(responseEntity);

        replayAll();
        Factor factor = unit.enroll(expectedFactor.getFactorType(), expectedFactor.getProvider(), expectedProfile, expectedUser);
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
    public void activateByIdTest() throws OktaException
    {
        ResponseEntity<Factor> responseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);
        String expectedRequestBody = "{\"passCode\":\"276825\"}";

        expect(mockOktaRestService.doRestCall(
                "/api/v1/users/" + expectedUser.getId() + "/factors/" + expectedFactor.getId() + "/lifecycle/activate",
                HttpMethod.POST, Factor.class, expectedRequestBody)).andReturn(responseEntity);

        replayAll();
        Factor factor = unit.activate(expectedFactor.getId(), "276825", expectedUser);
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
    public void activateByTypeTest() throws OktaException
    {
        List<Factor> expectedList = new ArrayList<>();
        expectedList.add(expectedFactor);
        ResponseEntity<Factor> responseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);
        String expectedRequestBody = "{\"passCode\":\"276825\"}";

        expect(mockFactorService.listEnrolledFactors(expectedUser)).andReturn(expectedList);
        expect(mockOktaRestService.doRestCall(
                "/api/v1/users/" + expectedUser.getId() + "/factors/" + expectedFactor.getId() + "/lifecycle/activate",
                HttpMethod.POST, Factor.class, expectedRequestBody)).andReturn(responseEntity);

        replayAll();
        Factor factor = unit.activate(expectedFactor.getFactorType(), "276825", expectedUser);
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
    public void resetFactorsTest() throws OktaException
    {
        ResponseEntity<Factor> responseEntity = new ResponseEntity<>(expectedFactor, HttpStatus.OK);
        String expectedRequestBody = "{}";

        expect(mockOktaRestService.doRestCall("/api/v1/users/" + expectedUser.getId() + "/lifecycle/reset_factors",
                HttpMethod.POST, Factor.class, expectedRequestBody)).andReturn(responseEntity);

        replayAll();
        unit.resetFactors(expectedUser);
        verifyAll();
    }
}
