/**
 * Task is namespace component for Task plugin
 *
 * @author jwu
 */
var Task = Task || {
    initialize: function() {
    }

    ,_task : {}
    ,getObjectType : function() {
    	return "TASK";
    }
    ,getTask : function() {
        return this._task;
    }
    ,setTask : function(c) {
        this._task = c;
    }
    ,getTaskId : function() {
        return this._task.taskId;
    }
    ,setTaskId : function(id) {
        this._task.taskId = id;
    }


};

