package com.armedia.acm.services.email.model;

/*-
 * #%L
 * ACM Service: Email
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

/**
 * Builds the email body text.
 *
 * @param <T>
 *            contains data needed to send an email in arbitrary format, may be a POJO.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 14, 2016
 */
@FunctionalInterface
public interface EmailBodyBuilder<T>
{

    /**
     * @param emailData
     *            contains data needed to send an email in arbitrary format, may be a POJO.
     * @return the email body text.
     */
    String buildEmailBody(T emailData);

}
