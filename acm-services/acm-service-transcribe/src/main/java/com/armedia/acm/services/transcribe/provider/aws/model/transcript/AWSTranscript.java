package com.armedia.acm.services.transcribe.provider.aws.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/17/2018
 */
public class AWSTranscript
{
    @JsonProperty("jobName")
    private String jobName;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("results")
    private AWSTranscriptResult result;

    @JsonProperty("status")
    private String status;

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public AWSTranscriptResult getResult()
    {
        return result;
    }

    public void setResult(AWSTranscriptResult result)
    {
        this.result = result;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
