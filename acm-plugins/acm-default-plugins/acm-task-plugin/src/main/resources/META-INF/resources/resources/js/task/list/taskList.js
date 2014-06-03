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
                var c = this._taskList[i];
                //if (taskId == c.taskId) {
                if (taskId == c.complaintId) {
                    found = c;
                    break;
                }
            }//end for
        }
        return found;
    }

    //datetime format: "2014-04-30T16:51:33.914+0000"
    ,getDateFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = dt.substr(0, 10);
        }
        return d;
    }
};

