package com.armedia.acm.services.mediaengine.model;

/*-
 * #%L
 * ACM Service: Media engine
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public interface MediaEngineConfiguration
{
    String getTempPath();

    void setTempPath(String tempPath);

    String getProvider();

    void setProvider(String provider);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isAutomaticEnabled();

    void setAutomaticEnabled(boolean automaticEnabled);

    boolean isNewMediaEngineForNewVersion();

    void setNewMediaEngineForNewVersion(boolean newMediaEngineForNewVersion);

    boolean isCopyMediaEngineForNewVersion();

    void setCopyMediaEngineForNewVersion(boolean copyMediaEngineForNewVersion);

    BigDecimal getCost();

    void setCost(BigDecimal cost);

    int getConfidence();

    void setConfidence(int confidence);

    int getNumberOfFilesForProcessing();

    void setNumberOfFilesForProcessing(int numberOfFilesForProcessing);

    MediaEngineServices getService();

    void setService(MediaEngineServices service);

    List<String> getProviders();

    void setProviders(List<String> providers);

    int getProviderPurgeAttempts();

    void setProviderPurgeAttempts(int providerPurgeAttempts);

    String getExcludedFileTypes();

    void setExcludedFileTypes(String excludedFileTypes);
}
