package com.armedia.acm.services.signature;

/*-
 * #%L
 * ACM Service: Electronic Signature
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

public class BaseTestCase
{

    @Rule
    public TestName name = new TestName();

    @Before
    public void printBeforeTestRun() throws Exception
    {
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Starting Test: " + name.getMethodName());
        System.out.println("-------------------------------------------------------------------------------------");
    }

    @After
    public void printAfterTestRun() throws Exception
    {
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Finished Test: " + name.getMethodName());
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println();
    }
}
