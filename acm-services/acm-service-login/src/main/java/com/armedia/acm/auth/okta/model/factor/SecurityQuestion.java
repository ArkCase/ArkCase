package com.armedia.acm.auth.okta.model.factor;

public class SecurityQuestion
{
    private String question;
    private String questionText;

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
}