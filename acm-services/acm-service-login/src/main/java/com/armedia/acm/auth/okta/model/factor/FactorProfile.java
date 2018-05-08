package com.armedia.acm.auth.okta.model.factor;

import com.fasterxml.jackson.annotation.JsonRootName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonRootName("profile")
public class FactorProfile
{
    private String question;
    private String questionText;
    private String phoneNumber;
    private String phoneExtension;
    private String credentialId;
    private String email;

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getQuestionText()
    {
        return questionText;
    }

    public void setQuestionText(String questionText)
    {
        this.questionText = questionText;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneExtension()
    {
        return phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension)
    {
        this.phoneExtension = phoneExtension;
    }

    public String getCredentialId()
    {
        return credentialId;
    }

    public void setCredentialId(String credentialId)
    {
        this.credentialId = credentialId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("question", question)
                .append("questionText", questionText)
                .append("phoneNumber", phoneNumber)
                .append("phoneExtension", phoneExtension)
                .append("credentialId", credentialId)
                .append("email", email)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        FactorProfile that = (FactorProfile) o;

        return new EqualsBuilder()
                .append(question, that.question)
                .append(questionText, that.questionText)
                .append(phoneNumber, that.phoneNumber)
                .append(phoneExtension, that.phoneExtension)
                .append(credentialId, that.credentialId)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(question)
                .append(questionText)
                .append(phoneNumber)
                .append(phoneExtension)
                .append(credentialId)
                .append(email)
                .toHashCode();
    }
}
