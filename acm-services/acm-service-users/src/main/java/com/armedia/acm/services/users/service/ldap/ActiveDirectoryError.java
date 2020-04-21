/*
 * #%L
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package com.armedia.acm.services.users.service.ldap;

public enum ActiveDirectoryError
{
    USERNAME_NOT_FOUND(0x525),
    INVALID_PASSWORD(0x52e),
    ACCOUNT_RESTRICTED(0x52f),
    INVALID_LOGON_HOURS(0x530),
    INVALID_WORKSTATION(0x531),
    PASSWORD_EXPIRED(0x532),
    ACCOUNT_DISABLED(0x533),
    TOO_MANY_CONTEXT_IDS(0x568),
    ACCOUNT_EXPIRED(0x701),
    PASSWORD_NEEDS_RESET(0x773),
    ACCOUNT_LOCKED(0x775),
    UNKNOWN(-1),;

    private int code;

    ActiveDirectoryError(int code)
    {
        this.code = code;
    }

    public static ActiveDirectoryError toActiveDirectoryError(int code)
    {
        switch (code)
        {
        case 0x525:
            return USERNAME_NOT_FOUND;
        case 0x52e:
            return INVALID_PASSWORD;
        case 0x52f:
            return ACCOUNT_RESTRICTED;
        case 0x530:
            return INVALID_LOGON_HOURS;
        case 0x531:
            return INVALID_WORKSTATION;
        case 0x532:
            return PASSWORD_EXPIRED;
        case 0x533:
            return ACCOUNT_DISABLED;
        case 0x568:
            return TOO_MANY_CONTEXT_IDS;
        case 0x701:
            return ACCOUNT_EXPIRED;
        case 0x773:
            return PASSWORD_NEEDS_RESET;
        case 0x775:
            return ACCOUNT_LOCKED;
        default:
            return UNKNOWN;
        }
    }

    public int getCode()
    {
        return code;
    }
}
