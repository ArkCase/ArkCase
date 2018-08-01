package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface CloseComplaintService
{
    @Transactional
    void save(CloseComplaintRequest form, Authentication auth, String mode) throws Exception;
}
