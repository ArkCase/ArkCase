package com.armedia.acm.services.transcribe.factory;

import com.armedia.acm.services.transcribe.exception.TranscribeServiceProviderNotFoundException;
import com.armedia.acm.services.transcribe.model.TranscribeServiceProvider;
import com.armedia.acm.services.transcribe.provider.aws.service.AWSTranscribeService;
import com.armedia.acm.services.transcribe.service.TranscribeService;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class TranscribeServiceFactory
{
    private AWSTranscribeService awsTranscribeService;

    public TranscribeService getService(TranscribeServiceProvider provider) throws TranscribeServiceProviderNotFoundException
    {
        switch (provider)
        {
            case AWS:
                return awsTranscribeService;
        }

        throw new TranscribeServiceProviderNotFoundException(String.format("Provider [%s] not found.", provider != null ? provider.toString() : null));
    }

    public void setAwsTranscribeService(AWSTranscribeService awsTranscribeService)
    {
        this.awsTranscribeService = awsTranscribeService;
    }
}
