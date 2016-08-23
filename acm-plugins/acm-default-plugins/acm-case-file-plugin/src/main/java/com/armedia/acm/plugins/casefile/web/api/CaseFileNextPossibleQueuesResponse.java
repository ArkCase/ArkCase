package com.armedia.acm.plugins.casefile.web.api;

import java.util.List;

public class CaseFileNextPossibleQueuesResponse
{
    private List<String> nextPossibleQueues;

    public CaseFileNextPossibleQueuesResponse(String defaultNextQueue, List<String> nextPossibleQueues)
    {
        this.nextPossibleQueues = nextPossibleQueues;
    }

    public List<String> getNextPossibleQueues()
    {
        return nextPossibleQueues;
    }

    public void setNextPossibleQueues(List<String> nextPossibleQueues)
    {
        this.nextPossibleQueues = nextPossibleQueues;
    }

}
