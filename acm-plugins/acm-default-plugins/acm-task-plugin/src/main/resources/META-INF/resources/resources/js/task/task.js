/**
 * Task is namespace component for Task plugin
 *
 * @author jwu
 */
var Task = Task || {
    create: function() {
        if (Task.Model.create)      {Task.Model.create();}
        if (Task.View.create)       {Task.View.create();}
        if (Task.Controller.create) {Task.Controller.create();}

        if (SubscriptionOp.create) {
            SubscriptionOp.create({
                getSubscriptionInfo: function() {
                    return {userId: App.getUserName()
                        ,objectType: Task.Model.getObjectType()
                        ,objectId: Task.Model.getObjectId()
                    };
                }
            });
        }
    }
    ,onInitialized: function() {
        if (Task.Model.onInitialized)      {Task.Model.onInitialized();}
        if (Task.View.onInitialized)       {Task.View.onInitialized();}
        if (Task.Controller.onInitialized) {Task.Controller.onInitialized();}
        if (SubscriptionOp.onInitialized)  {SubscriptionOp.onInitialized();}
    }
};

