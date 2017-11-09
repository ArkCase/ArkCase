package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmUser;

import java.util.List;

/**
 * Ldap User POST JSON request
 */
@PasswordValidation(message = "Password pattern rule validations")
public class UserDTO
{
    private String userId;
    private String firstName;
    private String lastName;
    private String mail;
    private String password;
    private String currentPassword;
    private List<String> groupNames;

    public AcmUser toAcmUser(String userId, String defaultLang)
    {
        AcmUser acmUser = new AcmUser();
        acmUser.setUserId(userId.toLowerCase());
        acmUser.setLang(defaultLang);
        updateAcmUser(acmUser);
        return acmUser;
    }

    public AcmUser updateAcmUser(AcmUser acmUser)
    {
        acmUser.setFullName(String.format("%s %s", firstName, lastName));
        acmUser.setFirstName(firstName);
        acmUser.setLastName(lastName);
        acmUser.setMail(mail);
        return acmUser;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public List<String> getGroupNames()
    {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames)
    {
        this.groupNames = groupNames;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
