package com.armedia.acm.configuration.core;

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class LabelsRefreshedEvent extends AcmEvent {

    public LabelsRefreshedEvent(Object source) {
        super(source);
        setEventDate(new Date());
    }
}
