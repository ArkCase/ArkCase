package com.armedia.acm.services.billing;

/*-
 * #%L
 * ACM Service: Billing
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.touchnet.secureLink.service.TPGSecureLink_BindingStub;
import com.touchnet.secureLink.service.TPGSecureLink_ServiceLocator;
import com.touchnet.secureLink.types.GenerateSecureLinkTicketRequest;
import com.touchnet.secureLink.types.GenerateSecureLinkTicketResponse;
import com.touchnet.secureLink.types.NameValuePair;
import org.junit.Test;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;


public class TouchNetTest
{


    @Test
    public void testGenerateSecureLinkTicket() throws ServiceException, RemoteException
    {

        GenerateSecureLinkTicketRequest req = new GenerateSecureLinkTicketRequest();
        req.setTicketName("Test");
        NameValuePair[] pairs = new NameValuePair[1];
        pairs[0] = new NameValuePair();
        pairs[0].setName("AMT");
        pairs[0].setValue("10.00");
        req.setNameValuePairs(pairs);

        TPGSecureLink_BindingStub binding;

        TPGSecureLink_ServiceLocator locator = new TPGSecureLink_ServiceLocator();
        locator.setTPGSecureLinkEndpointAddress("https://test.secure.touchnet.net:8703/C30002test_tlink/services/TPGSecureLink");
        binding = (TPGSecureLink_BindingStub)locator.getTPGSecureLink();

        binding.setUsername("Armedia");
        binding.setPassword("WwOEzo932M");


        GenerateSecureLinkTicketResponse value = binding.generateSecureLinkTicket(req);

        System.out.println("TicketID: " + value.getTicket());


    }
}
