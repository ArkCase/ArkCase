package com.armedia.acm.data;

/**
 * Support traceability from legacy systems by storing the ID from the legacy system in ArkCase.
 * The legacy system ID is set during data migration. This allows for traceability back to the
 * source system during data migrations.
 *
 * Created by riste.tutureski on 4/22/2016.
 */
public interface AcmLegacySystemEntity
{
    String getLegacySystemId();

    void setLegacySystemId(String legacySystemId);
}
