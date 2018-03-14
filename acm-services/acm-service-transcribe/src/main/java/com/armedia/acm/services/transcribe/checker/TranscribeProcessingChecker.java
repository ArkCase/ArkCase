package com.armedia.acm.services.transcribe.checker;

import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.TranscribeServiceProviderNotFoundException;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeStatusType;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/08/2018
 */
public class TranscribeProcessingChecker implements JavaDelegate
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseTranscribeService arkCaseTranscribeService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception
    {
        List<Long> ids = (List<Long>) delegateExecution.getVariable(TranscribeBusinessProcessVariableKey.IDS.toString());

        if (ids != null && ids.size() > 0)
        {
            // Because all IDs are follow the same business process (Transcribe objects for these IDS have the same information, just different ids),
            // we need just to take one of them (if there are many), check if we need to proceed, and proceed if yes.
            // After that, the business process will update all IDs

            try
            {
                Transcribe transcribe = getArkCaseTranscribeService().get(ids.get(0));
                if (TranscribeStatusType.QUEUED.toString().equals(transcribe.getStatus()))
                {
                    TranscribeConfiguration configuration = getArkCaseTranscribeService().getConfiguration();
                    List<Transcribe> processingTranscribeObjects = getArkCaseTranscribeService().getAllByStatus(TranscribeStatusType.PROCESSING.toString());
                    List<Transcribe> processingTranscribeObjectsDistinctByProcessId = processingTranscribeObjects.stream().filter(distinctByKey(Transcribe::getProcessId)).collect(Collectors.toList());

                    if (configuration.getNumberOfFilesForProcessing() > processingTranscribeObjectsDistinctByProcessId.size())
                    {
                        try
                        {
                            // Create Transcribe Job on provider side and set the Status and Action to PROCESSING
                            getArkCaseTranscribeService().getTranscribeServiceFactory().getService(configuration.getProvider()).create(transcribe);
                            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.STATUS.toString(), TranscribeStatusType.PROCESSING);
                            delegateExecution.setVariable(TranscribeBusinessProcessVariableKey.ACTION.toString(), TranscribeStatusType.PROCESSING);
                        }
                        catch (TranscribeServiceProviderNotFoundException | CreateTranscribeException e)
                        {
                            LOG.error("Error while calling PROVIDER=[{}] to transcribe the media. REASON=[{}]", configuration.getProvider().toString(), e.getMessage(), e);
                        }
                    }
                }

            }
            catch (GetTranscribeException | GetConfigurationException e)
            {
                LOG.warn("Could not check if Transcribe should be processed. REASON=[{}]", e.getMessage());
            }
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor)
    {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public ArkCaseTranscribeService getArkCaseTranscribeService()
    {
        return arkCaseTranscribeService;
    }

    public void setArkCaseTranscribeService(ArkCaseTranscribeService arkCaseTranscribeService)
    {
        this.arkCaseTranscribeService = arkCaseTranscribeService;
    }
}
