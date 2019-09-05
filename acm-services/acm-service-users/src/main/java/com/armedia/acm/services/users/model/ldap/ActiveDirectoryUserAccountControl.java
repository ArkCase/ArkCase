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

package com.armedia.acm.services.users.model.ldap;

import static com.armedia.acm.services.users.model.ldap.ActiveDirectoryUserAccountControl.UAC_BIT.*;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Source:
 * https://support.microsoft.com/en-us/help/305144/how-to-use-useraccountcontrol-to-manipulate-user-account-properties
 */
public class ActiveDirectoryUserAccountControl
{
    private final int uacValue;

    private ActiveDirectoryUserAccountControl(String uacValue)
    {
        this.uacValue = NumberUtils.toInt(uacValue, -1);
    }

    public static ActiveDirectoryUserAccountControl from(String uacValue)
    {
        return new ActiveDirectoryUserAccountControl(uacValue);
    }

    public boolean isAccountLockout()
    {
        return isBit(LOCKOUT);
    }

    public boolean isAccountActive()
    {
        return isBit(NORMAL_ACCOUNT);
    }

    public boolean isPasswordCantChange()
    {
        return isBit(PASSWD_CANT_CHANGE);
    }

    public boolean isPasswordExpired()
    {
        return isBit(UAC_BIT.PASSWORD_EXPIRED);
    }

    public boolean isPasswordNeverExpires()
    {
        return isBit(DONT_EXPIRE_PASSWORD);
    }

    public boolean isAccountDisabled()
    {
        return uacValue == INVALID_ACCOUNT_CONTROL.bitValue() || isBit(ACCOUNTDISABLE);
    }

    public boolean isBit(final UAC_BIT uacBit)
    {
        return (uacValue & uacBit.bitValue()) == uacBit.bitValue();
    }

    public UAC_BIT getUserAccountControl()
    {
        switch (uacValue)
        {
        case 0x0001:
            return SCRIPT;
        case 0x0002:
            return ACCOUNTDISABLE;
        case 0x0008:
            return HOMEDIR_REQUIRED;
        case 0x0010:
            return LOCKOUT;
        case 0x0020:
            return PASSWD_NOTREQD;
        case 0x0040:
            return PASSWD_CANT_CHANGE;
        case 0x0080:
            return ENCRYPTED_TEXT_PWD_ALLOWED;
        case 0x0100:
            return TEMP_DUPLICATE_ACCOUNT;
        case 0x0200:
            return NORMAL_ACCOUNT;
        case 0x0800:
            return INTERDOMAIN_TRUST_ACCOUNT;
        case 0x1000:
            return WORKSTATION_TRUST_ACCOUNT;
        case 0x2000:
            return SERVER_TRUST_ACCOUNT;
        case 0x10000:
            return DONT_EXPIRE_PASSWORD;
        case 0x20000:
            return MNS_LOGON_ACCOUNT;
        case 0x40000:
            return SMARTCARD_REQUIRED;
        case 0x80000:
            return TRUSTED_FOR_DELEGATION;
        case 0x100000:
            return NOT_DELEGATED;
        case 0x200000:
            return USE_DES_KEY_ONLY;
        case 0x400000:
            return DONT_REQ_PREAUTH;
        case 0x800000:
            return PASSWORD_EXPIRED;
        case 0x1000000:
            return TRUSTED_TO_AUTH_FOR_DELEGATION;
        case 0x04000000:
            return PARTIAL_SECRETS_ACCOUNT;
        default:
            return INVALID_ACCOUNT_CONTROL;
        }

    }

    public enum UAC_BIT
    {
        // SCRIPT - The logon script will be run.
        SCRIPT(0x0001),
        // ACCOUNTDISABLE - The user account is disabled.
        ACCOUNTDISABLE(0x0002),
        // HOMEDIR_REQUIRED - The home folder is required.
        HOMEDIR_REQUIRED(0x0008),
        // LOCKOUT - Account is locked out
        LOCKOUT(0x0010),
        // PASSWD_NOTREQD - No password is required.
        PASSWD_NOTREQD(0x0020),
        // PASSWD_CANT_CHANGE - The user cannot change the password.
        PASSWD_CANT_CHANGE(0x0040),
        // ENCRYPTED_TEXT_PASSWORD_ALLOWED - The user can send an encrypted password.
        ENCRYPTED_TEXT_PWD_ALLOWED(0x0080),
        // TEMP_DUPLICATE_ACCOUNT - This is an account for users whose primary account is in another domain.
        TEMP_DUPLICATE_ACCOUNT(0x0100),
        // NORMAL_ACCOUNT - This is a default account type that represents a typical user.
        NORMAL_ACCOUNT(0x0200),
        // INTERDOMAIN_TRUST_ACCOUNT - This is a permit to trust an account for a system domain that trusts other
        // domains.
        INTERDOMAIN_TRUST_ACCOUNT(0x0800),
        // WORKSTATION_TRUST_ACCOUNT - This is a computer account for a computer that is running Microsoft Windows NT
        // 4.0
        // Workstation, Microsoft Windows NT 4.0 Server, Microsoft Windows 2000 Professional, or Windows 2000 Server
        // and is a member of this domain.
        WORKSTATION_TRUST_ACCOUNT(0x1000),
        // SERVER_TRUST_ACCOUNT - This is a computer account for a domain controller that is a member of this domain.
        SERVER_TRUST_ACCOUNT(0x2000),
        // DONT_EXPIRE_PASSWD - Represents the password, which should never expire on the account.
        DONT_EXPIRE_PASSWORD(0x10000),
        // MNS_LOGON_ACCOUNT - This is an MNS logon account.
        MNS_LOGON_ACCOUNT(0x20000),
        // SMARTCARD_REQUIRED - When this flag is set, it forces the user to log on by using a smart card.
        SMARTCARD_REQUIRED(0x40000),
        // TRUSTED_FOR_DELEGATION - When this flag is set, the service account (the user or computer account) under
        // which a
        // service runs is trusted for Kerberos delegation. Any such service can impersonate a client requesting the
        // service. To enable a service for Kerberos delegation, you must set this flag on the userAccountControl
        // property
        // of the service account.
        TRUSTED_FOR_DELEGATION(0x80000),
        // NOT_DELEGATED - When this flag is set, the security context of the user is not delegated to a service even
        // if the service account is set as trusted for Kerberos delegation.
        NOT_DELEGATED(0x100000),
        // USE_DES_KEY_ONLY - (Windows 2000/Windows Server 2003) Restrict this principal to use only Data Encryption
        // Standard (DES) encryption types for keys.
        USE_DES_KEY_ONLY(0x200000),
        // DONT_REQUIRE_PREAUTH - (Windows 2000/Windows Server 2003)
        // This account does not require Kerberos pre-authentication for logging on.
        DONT_REQ_PREAUTH(0x400000),
        // PASSWORD_EXPIRED - (Windows 2000/Windows Server 2003) The user's password has expired.
        PASSWORD_EXPIRED(0x800000),
        // TRUSTED_TO_AUTH_FOR_DELEGATION - (Windows 2000/Windows Server 2003) The account is enabled
        // for delegation. This is a security-sensitive setting. Accounts that have this option enabled should
        // be tightly controlled. This setting lets a service that runs under the account assume a client's identity
        // and authenticate as that user to other remote servers on the network.
        TRUSTED_TO_AUTH_FOR_DELEGATION(0x1000000),
        // PARTIAL_SECRETS_ACCOUNT - (Windows Server 2008/Windows Server 2008 R2)
        // The account is a read-only domain controller (RODC). This is a security-sensitive setting.
        // Removing this setting from an RODC compromises security on that server.
        PARTIAL_SECRETS_ACCOUNT(0x04000000),
        // INVALID_ACCOUNT_CONTROL - Invalid userAccountControl attribute
        INVALID_ACCOUNT_CONTROL(-0x1),;

        private final int bitValue;

        UAC_BIT(int bitValue)
        {
            this.bitValue = bitValue;
        }

        public int bitValue()
        {
            return bitValue;
        }

    }
}
