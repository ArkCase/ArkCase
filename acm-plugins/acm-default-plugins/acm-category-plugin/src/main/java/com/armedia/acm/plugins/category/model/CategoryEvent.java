package com.armedia.acm.plugins.category.model;

import com.armedia.acm.core.model.AcmEvent;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 16, 2017
 *
 */
public class CategoryEvent extends AcmEvent
{

    private static final long serialVersionUID = 3652276020071035651L;

    public enum CategoryEventType
    {

        CREATE("com.armedia.acm.plugins.category.model.action.create"),
        EDIT("com.armedia.acm.plugins.category.model.action.edit"),
        DELETE("com.armedia.acm.plugins.category.model.action.delete"),
        ACTIVATE("com.armedia.acm.plugins.category.model.action.activate"),
        DEACTIVATE("com.armedia.acm.plugins.category.model.action.deactivate");

        private String actionType;

        private CategoryEventType(String actionType)
        {
            this.actionType = actionType;
        }

        public String type()
        {
            return actionType;
        }
    }

    public CategoryEvent(Category source, CategoryEventType eventType, String description)
    {

        this(source, eventType, description, true);

    }

    public CategoryEvent(Category source, CategoryEventType eventType, String description, boolean userActionSucceeded)
    {

        super(source);

        setSucceeded(userActionSucceeded);
        setObjectId(source.getId());
        setEventDate(source.getModified());
        setUserId(source.getModifier());
        setObjectType(source.getObjectType());
        setEventType(eventType.type());
        setEventDescription(description);

    }

}
