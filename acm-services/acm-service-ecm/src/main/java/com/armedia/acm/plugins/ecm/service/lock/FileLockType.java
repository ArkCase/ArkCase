package com.armedia.acm.plugins.ecm.service.lock;

/**
 * If new locking types are added in this enumeration, then the {@link FileLockingProvider} must be updated to
 * handle these types.
 * 
 * Created by bojan.milenkoski on 03/05/2018.
 */
public enum FileLockType
{
    READ, WRITE, DELETE, SHARED_WRITE
}
