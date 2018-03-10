package com.armedia.acm.services.transcribe.checker;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/08/2018
 */
public class TranscribeProcessingChecker implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        // TODO: Implement functionality for checking if we need to process some Transcribe objects in the queue
    }
}
