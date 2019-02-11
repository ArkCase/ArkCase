package com.armedia.acm.services.mediaengine.model;

/*-
 * #%L
 * ACM Service: Media engine
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public interface MediaEngineConstants
{
    String OBJECT_TYPE = "MEDIA_SERVICES";
    String SYSTEM_USER = "MEDIA_SERVICE";

    String LOCK_TYPE_WRITE = "WRITE";

    String SYSTEM_IP_ADDRESS = "127.0.0.1";

    String CREATED_EVENT = "com.armedia.acm.services.[SERVICE].created";
    String UPDATED_EVENT = "com.armedia.acm.services.[SERVICE].updated";
    String QUEUED_EVENT = "com.armedia.acm.services.[SERVICE].queued";
    String PROCESSING_EVENT = "com.armedia.acm.services.[SERVICE].processing";
    String COMPLETED_EVENT = "com.armedia.acm.services.[SERVICE].completed";
    String FAILED_EVENT = "com.armedia.acm.services.[SERVICE].failed";
    String CANCELLED_EVENT = "com.armedia.acm.services.[SERVICE].cancelled";
    String COMPILED_EVENT = "com.armedia.acm.services.[SERVICE].compiled";
    String ROLLBACK_EVENT = "com.armedia.acm.services.[SERVICE].rollback";
    String PROVIDER_FAILED_EVENT = "com.armedia.acm.services.[SERVICE].provider.failed";
}
