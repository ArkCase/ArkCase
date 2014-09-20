/**
 * TaskList is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var TaskList = TaskList || {
    initialize: function() {
        TaskList.Object.initialize();
        TaskList.Event.initialize();
        TaskList.Page.initialize();
        TaskList.Rule.initialize();
        TaskList.Service.initialize();
        TaskList.Callback.initialize();

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

