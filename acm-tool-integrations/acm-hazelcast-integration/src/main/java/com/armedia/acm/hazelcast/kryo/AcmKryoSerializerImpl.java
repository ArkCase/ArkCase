package com.armedia.acm.hazelcast.kryo;

import info.jerrinot.subzero.internal.PropertyUserSerializer;
import info.jerrinot.subzero.internal.strategy.GlobalKryoStrategy;
import info.jerrinot.subzero.internal.strategy.TypedKryoStrategy;

public class AcmKryoSerializerImpl<T> extends AbstractAcmKryoSerializer<T>
{

    AcmKryoSerializerImpl()
    {
        super(new GlobalKryoStrategy(PropertyUserSerializer.INSTANCE));
    }

    public AcmKryoSerializerImpl(Class<T> clazz)
    {
        super(new TypedKryoStrategy(clazz, PropertyUserSerializer.INSTANCE));
    }

    @Override
    public int getTypeId()
    {
        return 10000;
    }
}