package com.armedia.acm.proxy.http;

/*-
 * #%L
 * Tool Integrations: HTTP Proxy
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProxyServletTest
{

    @Test
    public void encodeUriQuery() throws Exception
    {
        String in = "The Grateful Dead";
        String expected = "The%20Grateful%20Dead";

        StringBuilder actual = (StringBuilder) ProxyServlet.encodeUriQuery(in);

        assertEquals(expected, actual.toString());
    }
    
}
