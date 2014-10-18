/**
 * TaskList is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var TaskList = TaskList || {
    create: function() {
        TaskList.Object.create();
        TaskList.Event.create();
        TaskList.Page.create();
        TaskList.Rule.create();
        TaskList.Service.create();
        TaskList.Callback.create();

        Acm.deferred(TaskList.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

    ,_taskList: []
    ,getTaskList: function() {
        return this._taskList;
    }
    ,setTaskList: function(list) {
        return this._taskList = list;
    }
    ,findTask: function(taskId) {
        var found = null;
        if (Acm.isNotEmpty(this._taskList)) {
            var len = this._taskList.length;
            for (var i = 0; i < len; i++) {
                var t = this._taskList[i];
                if (taskId == t.object_id_s) {
                    found = t;
                    break;
                }
            }//end for
        }
        return found;
    }

    ,_singleObject: false
    ,isSingleObject: function() {
        return this._singleObject;
    }
    ,setSingleObject: function(single) {
        this._singleObject = single;
    }

};

