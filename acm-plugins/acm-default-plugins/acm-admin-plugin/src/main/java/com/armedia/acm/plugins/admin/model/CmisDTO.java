package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @author aleksandar.bujaroski
 */
public class CmisDTO {

    @SerializedName("cmis.id")
    private String id;

    @SerializedName("cmis.baseUrl")
    private String baseUrl;

    @SerializedName("cmis.username")
    private String username;

    @SerializedName("cmis.password")
    private String password;

    @SerializedName("cmis.maxIdle")
    private int maxIdle;

    @SerializedName("cmis.maxActive")
    private int maxActive;

    @SerializedName("cmis.maxWait")
    private int maxWait;

    @SerializedName("cmis.minEvictionMillis")
    private int minEvictionMillis;

    @SerializedName("cmis.evictionCheckIntervalMillis")
    private int evictionCheckIntervalMillis;

    @SerializedName("cmis.recconectCount")
    private int recconectCount;

    @SerializedName("cmis.recconnectFrequency")
    private int recconnectFrequency;

    @SerializedName("cmis.cmisVersioningState")
    private String cmisVersioningState;

    @SerializedName("cmis.useAlfrescoExtension")
    private boolean useAlfrescoExtension;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getMinEvictionMillis() {
        return minEvictionMillis;
    }

    public void setMinEvictionMillis(int minEvictionMillis) {
        this.minEvictionMillis = minEvictionMillis;
    }

    public int getEvictionCheckIntervalMillis() {
        return evictionCheckIntervalMillis;
    }

    public void setEvictionCheckIntervalMillis(int evictionCheckIntervalMillis) {
        this.evictionCheckIntervalMillis = evictionCheckIntervalMillis;
    }

    public int getRecconectCount() {
        return recconectCount;
    }

    public void setRecconectCount(int recconectCount) {
        this.recconectCount = recconectCount;
    }

    public int getRecconnectFrequency() {
        return recconnectFrequency;
    }

    public void setRecconnectFrequency(int recconnectFrequency) {
        this.recconnectFrequency = recconnectFrequency;
    }

    public String getCmisVersioningState() {
        return cmisVersioningState;
    }

    public void setCmisVersioningState(String cmisVersioningState) {
        this.cmisVersioningState = cmisVersioningState;
    }

    public boolean isUseAlfrescoExtension() {
        return useAlfrescoExtension;
    }

    public void setUseAlfrescoExtension(boolean useAlfrescoExtension) {
        this.useAlfrescoExtension = useAlfrescoExtension;
    }
}
