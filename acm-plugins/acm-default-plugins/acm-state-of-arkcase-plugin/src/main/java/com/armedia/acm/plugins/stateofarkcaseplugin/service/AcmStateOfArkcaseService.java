package com.armedia.acm.plugins.stateofarkcaseplugin.service;

import java.io.File;
import java.time.LocalDate;

public interface AcmStateOfArkcaseService
{
    /**
     * Generates state of Arkcase report as zip file
     */
    File generateReportForDay(LocalDate day);
}
