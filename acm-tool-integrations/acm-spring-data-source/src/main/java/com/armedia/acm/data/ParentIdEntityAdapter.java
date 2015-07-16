package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PrePersist;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Update child entities with ID of the parent
 *
 */
public class ParentIdEntityAdapter extends DescriptorEventAdapter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ThreadLocal<String> userId = new ThreadLocal<>();

    @Override
    public void postInsert(DescriptorEvent event) {
        super.postInsert(event);
        Class c = event.getSource().getClass();
        for (Method method : c.getDeclaredMethods()) {
            if (method.getAnnotation(PrePersist.class) != null) {
                try {
                    method.setAccessible(true);
                    method.invoke(event.getSource());
                } catch (IllegalAccessException e) {
                    log.error("unable to invoke the method. Possible problems with PERSISTING the object.", e);
                } catch (InvocationTargetException e) {
                    log.error("unable to invoke the method. Possible problems with PERSISTING the object.", e);
                }
            }
        }
    }

    public String getUserId()
    {
        return userId.get();
    }

    public void setUserId(String userId)
    {
        this.userId.set(userId);
    }
}
