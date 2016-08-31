package com.armedia.acm.plugins.casefile.web.api;

import java.util.List;

public class CaseFileNextPossibleQueuesResponse
{
    private final String defaultNextQueue;

    private final List<String> nextPossibleQueues;

    public CaseFileNextPossibleQueuesResponse(String defaultNextQueue, List<String> nextPossibleQueues)
    {
        this.defaultNextQueue = defaultNextQueue;
        this.nextPossibleQueues = nextPossibleQueues;
    }

    public String getDefaultNextQueue()
    {
        return defaultNextQueue;
    }

    public List<String> getNextPossibleQueues()
    {
        return nextPossibleQueues;
    }

}
