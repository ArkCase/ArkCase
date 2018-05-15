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
 * Filter that clears the {@link Authentication} object from the {@link SecurityContext} and returns 401 Unauthorized
 * error. Only used in
 * the Single Sign-On scenario. This filter is used for REST calls and does not cause a redirect to /samllogin page, but
 * lets
 * {@link org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler} return 401 error.
 * <p>
 * Created by Bojan Milenkoski on 12.4.2016
 */
public class AcmSamlRestAuthenticationCheckFilter extends AcmSamlAuthenticationCheckFilterBase
{

    @Override
    public boolean shouldRedirectToLoginPage()
    {
        // REST services should return 401, not redirect
        return false;
    }
}
