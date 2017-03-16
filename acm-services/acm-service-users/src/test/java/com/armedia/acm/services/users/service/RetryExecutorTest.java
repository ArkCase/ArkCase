package com.armedia.acm.services.users.service;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RetryExecutorTest
{

    int attempts;

    @Before
    public void setUp()
    {
        attempts = 2;
    }

    @Test
    public void test() throws Exception
    {
        RetryExecutor retryExecutor = new RetryExecutor(1000, 3);
        retryExecutor.retry(() -> System.out.println("Test"));
    }

    @Test(expected = Exception.class)
    public void testRetryWithSecondTrySuccess() throws Exception
    {
        RetryExecutor retryExecutor = new RetryExecutor(1000, 3);
        retryExecutor.retry(() -> success());
        System.out.println("Successful attempt");
    }

    @Test(expected = Exception.class)
    public void testRetry() throws Exception
    {
        RetryExecutor retryExecutor = new RetryExecutor(1000, 3);
        retryExecutor.retry(() -> failure());
        Assert.fail("The executor should throw exception");
    }

    @Test(expected = Exception.class)
    public void testRetryResult() throws Exception
    {
        RetryExecutor<String> retryExecutor = new RetryExecutor<>(1000, 3);
        String result = retryExecutor.retryResult(() -> noResult());
        Assert.fail("The executor should throw exception");
    }

    @Test
    public void testRetryResultSuccess() throws Exception
    {
        RetryExecutor<String> retryExecutor = new RetryExecutor<>();
        String result = retryExecutor.retryResult(this::successfulResult);
        Assert.assertEquals("Successful attempt", result);
    }

    private String successfulResult()
    {
        success();
        return "Successful attempt";
    }

    private String noResult()
    {
        throw new RuntimeException("failure");
    }

    private void success()
    {
        if (attempts > 0)
        {
            System.out.println("Attempt: " + attempts);
            attempts--;
            throw new RuntimeException("failure");
        }
    }

    private void failure()
    {
        throw new RuntimeException("failure");
    }
}
