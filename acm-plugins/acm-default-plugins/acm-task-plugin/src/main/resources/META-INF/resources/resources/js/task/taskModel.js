/**
 * Task.Model
 *
 * @author jwu
 */
Task.Model = Task.Model || {
    create : function() {
        if (Task.Service.create)       {Task.Service.create();}
    }
    ,onInitialized: function() {
        if (Task.Service.onInitialized)       {Task.Service.onInitialized();}
    }

    ,_objectType: "TASK"
    ,getObjectType: function() {
        return this._objectType;
    }


    ,getObjectId: function() {
        return TaskList.getTaskId();
    }
};

