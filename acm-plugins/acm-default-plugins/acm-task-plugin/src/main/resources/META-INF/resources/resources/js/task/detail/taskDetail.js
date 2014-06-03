/**
 * TaskDetail is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var TaskDetail = TaskDetail || {
    initialize: function() {
        TaskDetail.Object.initialize();
        TaskDetail.Event.initialize();
        TaskDetail.Page.initialize();
        TaskDetail.Rule.initialize();
        TaskDetail.Service.initialize();
        TaskDetail.Callback.initialize();

        Acm.deferred(TaskDetail.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}

//    ,_taskList: []
//    ,getTaskDetail: function() {
//        return this._taskList;
//    }
//    ,setTaskDetail: function(list) {
//        return this._taskList = list;
//    }
//    ,findTask: function(taskId) {
//        var found = null;
//        if (Acm.isNotEmpty(this._taskList)) {
//            var len = this._taskList.length;
//            for (var i = 0; i < len; i++) {
//                var c = this._taskList[i];
//                if (taskId == c.taskId) {
//                    found = c;
//                    break;
//                }
//            }//end for
//        }
//        return found;
//    }

    //datetime format: "2014-04-30T16:51:33.914+0000"
    ,getDateFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = dt.substr(0, 10);
        }
        return d;
    }
};

