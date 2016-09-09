package com.armedia.acm.plugins.casefile.web.api;

import java.util.List;

public class CaseFileNextPossibleQueuesResponse
{
    private final String defaultNextQueue;

    private final String defaultReturnQueue;

    private final List<String> nextPossibleQueues;

    public CaseFileNextPossibleQueuesResponse(String defaultNextQueue, String defaultReturnQueue, List<String> nextPossibleQueues)
    {
        this.defaultNextQueue = defaultNextQueue;
        this.defaultReturnQueue = defaultReturnQueue;
        this.nextPossibleQueues = nextPossibleQueues;
    }

    public String getDefaultNextQueue()
    {
        return defaultNextQueue;
    }

    public String getDefaultReturnQueue()
    {
        return defaultReturnQueue;
    }

    public List<String> getNextPossibleQueues()
    {
        return nextPossibleQueues;
    }

}
