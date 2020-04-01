package com.armedia.acm.services.exemption.service;

/*-
 * #%L
 * ACM Service: Exemption
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

import java.util.List;

import com.armedia.acm.services.exemption.exception.DeleteExemptionCodeException;
import com.armedia.acm.services.exemption.exception.SaveExemptionCodeException;
import com.armedia.acm.services.exemption.exception.UpdateExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionCode;

public interface ExemptionService
{

    List<ExemptionCode> saveExemptionCodes(ExemptionCode exemptionCodes, String user) throws SaveExemptionCodeException;

    void deleteExemptionCode(Long tagId) throws DeleteExemptionCodeException;

    void updateExemptionStatute(ExemptionCode exemptionData) throws UpdateExemptionStatuteException;
}
