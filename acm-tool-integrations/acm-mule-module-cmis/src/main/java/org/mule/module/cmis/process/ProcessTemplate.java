
package org.mule.module.cmis.process;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import javax.annotation.Generated;

/**
 * ProcessTemplate provides a processing context for message processing.
 * <p/>
 * Examples of processing context can be to provide error handling, transaction state verification,
 * transactional demarcation, connection management, security, etc.
 * 
 * @param <T>
 *            type of the return value of the processing execution
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface ProcessTemplate<T, O>
{

    public T execute(ProcessCallback<T, O> callback, org.mule.api.processor.MessageProcessor messageProcessor, org.mule.api.MuleEvent event)
            throws Exception;

    public T execute(ProcessCallback<T, O> callback, org.mule.api.routing.filter.Filter filter, org.mule.api.MuleMessage message)
            throws Exception;
}
