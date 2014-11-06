/**
 * Task is namespace component for Task plugin
 *
 * @author jwu
 */
var Task = Task || {
    create: function() {
    }

    ,_task : {}
    ,getObjectType : function() {
    	return "TASK";
    }
    ,getTask : function() {
        return this._task;
    }
    ,setTask : function(task) {
        this._task = task;
    }
    ,getTaskId : function() {
        return this._task.taskId;
    }
    ,setTaskId : function(id) {
        this._task.taskId = id;
    }


};

