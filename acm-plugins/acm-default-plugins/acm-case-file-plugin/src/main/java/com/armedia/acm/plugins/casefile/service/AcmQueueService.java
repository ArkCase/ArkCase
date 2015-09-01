package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.AcmQueue;

import java.util.List;

/**
 * Created by nebojsha on 31.08.2015.
 */
public interface AcmQueueService
{
    List<AcmQueue> listAllQueues();
}
