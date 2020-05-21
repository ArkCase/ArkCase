package com.armedia.acm.plugins.consultation.model;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ConsultationEnqueueResponse
{

    private final boolean success;
    private final ErrorReason reason;
    private final List<String> errors;
    private final String requestedQueue;

    private Consultation consultation;

    public ConsultationEnqueueResponse(ErrorReason reason, String requestedQueue, Consultation consultation)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        errors = new ArrayList<>();
        this.requestedQueue = requestedQueue;
        this.consultation = consultation;
    }

    public ConsultationEnqueueResponse(ErrorReason reason, List<String> errors, String requestedQueue, Consultation consultation)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        this.errors = errors;
        this.requestedQueue = requestedQueue;
        this.consultation = consultation;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public ErrorReason getReason()
    {
        return reason;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public String getRequestedQueue()
    {
        return requestedQueue;
    }

    public Consultation getConsultation()
    {
        return consultation;
    }

    public void setConsultation(Consultation consultation)
    {
        this.consultation = consultation;
    }

    public enum ErrorReason
    {
        NO_ERROR, LEAVE, NEXT_POSSIBLE, ENTER, ON_LEAVE, ON_ENTER
    }
}
