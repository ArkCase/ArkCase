package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
        RetryExecutor retryExecutor = new RetryExecutor(attempts, 3, new ArrayList<>());
        retryExecutor.retry(() -> System.out.println("Test"));
    }

    @Test(expected = Exception.class)
    public void testRetryWithSecondTrySuccess() throws Exception
    {
        RetryExecutor retryExecutor = new RetryExecutor(attempts, 3, new ArrayList<>());
        retryExecutor.retry(this::success);
        System.out.println("Successful attempt");
    }

    @Test(expected = Exception.class)
    public void testRetry() throws Exception
    {
        RetryExecutor retryExecutor = new RetryExecutor(attempts, 3, Arrays.asList(RuntimeException.class));
        retryExecutor.retry(this::failure);
        Assert.fail("The executor should throw exception");
        Assert.assertTrue(attempts == 0);
    }

    @Test(expected = Exception.class)
    public void testNoRetryAttempts() throws Exception
    {
        RetryExecutor retryExecutor = new RetryExecutor(attempts, 3, Arrays.asList(Exception.class));
        retryExecutor.retry(this::failure);
        Assert.fail("The executor should throw exception");
        Assert.assertTrue(attempts == 2);
    }

    @Test(expected = Exception.class)
    public void testRetryResult() throws Exception
    {
        RetryExecutor<String> retryExecutor = new RetryExecutor<>(attempts, 3, new ArrayList<>());
        String result = retryExecutor.retryResult(this::noResult);
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
