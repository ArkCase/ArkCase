package com.armedia.acm.service.objectlock.model;

/*-
 * #%L
 * ACM Service: Object lock
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
 * Created by dragan.simonovski on 04/25/2016.
 */
public interface AcmObjectLockConstants
{
    String WORD_EDIT_LOCK = "WORD_EDIT_LOCK";
    String CHECKOUT_LOCK = "CHECKOUT_LOCK";
    String CHECKIN_LOCK = "CHECKIN_LOCK";
    String CANCEL_LOCK = "CANCEL_LOCK";
    String OBJECT_LOCK = "OBJECT_LOCK";
    /**
     * this should be used when objects needs to be locked for long running tasks.
     */
    String LOCK_FOR_PROCESSING = "OBJECT_PROCESSING_LOCK";

    String EXCLUSIVE_TREE_LOCK = "EXCLUSIVE_TREE_LOCK";

    String SHARED_LOCK = "SHARED_LOCK";
}
