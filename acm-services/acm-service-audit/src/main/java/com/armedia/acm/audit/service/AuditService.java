/**
 *
 */
package com.armedia.acm.audit.service;

import com.armedia.acm.audit.model.AuditEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author riste.tutureski
 */
public interface AuditService {

    public void purgeBatchRun();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async("auditorExecutor")
    void audit(AuditEvent auditEvent);
}
