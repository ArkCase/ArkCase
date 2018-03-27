package com.armedia.acm.plugins.ecm.state;

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmFilesState extends StateOfModule
{
    private Long numberOfDocuments;
    private Long sizeOfRepository;

    public Long getNumberOfDocuments()
    {
        return numberOfDocuments;
    }

    public void setNumberOfDocuments(Long numberOfDocuments)
    {
        this.numberOfDocuments = numberOfDocuments;
    }

    public Long getSizeOfRepository()
    {
        return sizeOfRepository;
    }

    public void setSizeOfRepository(Long sizeOfRepository)
    {
        this.sizeOfRepository = sizeOfRepository;
    }
}
