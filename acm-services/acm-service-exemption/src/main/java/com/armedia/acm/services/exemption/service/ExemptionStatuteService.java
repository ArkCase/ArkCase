package com.armedia.acm.services.exemption.service;

import com.armedia.acm.services.exemption.exception.DeleteExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.GetExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.SaveExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExemptionStatuteService
{


    ExemptionStatute saveExemptionStatutes(ExemptionStatute exemptionStatute, String user) throws SaveExemptionStatuteException;

    @Transactional
    List<ExemptionStatute> getExemptionStatutesOnDocument(Long caseId, Long fileId) throws GetExemptionStatuteException;

    @Transactional
    void saveExemptionStatutesOnDocument(Long fileId, List<String> exemptionStatutes, String user)
            throws SaveExemptionStatuteException;

    void deleteExemptionStatute(Long statuteId) throws DeleteExemptionStatuteException;
}
