package com.armedia.acm.plugins.onlyoffice.model;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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
 * 0 - the force saving request is performed to the command service
 * 1 - the force saving request is performed each time the saving is done (e.g. the Save button is clicked), which
 * is only available when the forcesave option is set to true.
 * 2 - the force saving request is performed by timer with the settings from the server config.
 */
public enum ForceSaveType
{
    BY_COMMAND_SERVICE(0),
    BY_SAVING_IS_DONE(1),
    BY_TIMER(2);
    private final int value;

    ForceSaveType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
