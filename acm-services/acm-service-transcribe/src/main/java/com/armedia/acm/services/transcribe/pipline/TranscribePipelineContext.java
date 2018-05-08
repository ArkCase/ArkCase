package com.armedia.acm.services.transcribe.pipline;

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.transcribe.model.TranscribeType;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribePipelineContext extends AbstractPipelineContext
{
    private EcmFileVersion ecmFileVersion;
    private TranscribeType type;
    private String processId;

    public EcmFileVersion getEcmFileVersion()
    {
        return ecmFileVersion;
    }

    public void setEcmFileVersion(EcmFileVersion ecmFileVersion)
    {
        this.ecmFileVersion = ecmFileVersion;
    }

    public TranscribeType getType()
    {
        return type;
    }

    public void setType(TranscribeType type)
    {
        this.type = type;
    }

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
    }
}
