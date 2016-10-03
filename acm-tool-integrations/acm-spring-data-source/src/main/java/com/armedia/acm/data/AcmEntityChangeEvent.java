package com.armedia.acm.data;


import com.google.common.base.MoreObjects;
import org.springframework.context.ApplicationEvent;

public class AcmEntityChangeEvent extends ApplicationEvent
{
    private AcmEntityChangesHolder acmEntityChangesHolder;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public AcmEntityChangeEvent(AcmEntityChangesHolder source)
    {
        super(source);
        this.acmEntityChangesHolder = source;
    }

    public AcmEntityChangesHolder getAcmEntityChangesHolder()
    {
        return acmEntityChangesHolder;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("changes", acmEntityChangesHolder)
                .toString();
    }

    public enum ACTION
    {
        INSERT,
        UPDATE,
        DELETE
    }
}
