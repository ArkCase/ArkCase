package com.armedia.acm.auth;

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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Filter that clears the {@link Authentication} object from the {@link SecurityContext} and causes authentication check
 * of the user against
 * ADFS server. Only used in the Single Sign-On scenario. This filter is used for non REST calls and cause redirect to
 * the /samllogin page
 * where the 'redirectURL' is set to the Angular state.
 * <p>
 * Created by Bojan Milenkoski on 14.3.2016
 */
public class AcmSamlAuthenticationCheckFilter extends AcmSamlAuthenticationCheckFilterBase
{
    @Override
    public boolean shouldRedirectToLoginPage()
    {
        // non REST resources redirect to the /samllogin page
        return true;
    }
}
