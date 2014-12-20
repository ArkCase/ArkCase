/**
 * TaskList is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var TaskList = TaskList || {
    create: function() {
        TaskList.cachePage = new Acm.Model.CacheFifo(2);
        TaskList.cacheTask = new Acm.Model.CacheFifo(3);
        TaskList.cacheParentObject = new Acm.Model.CacheFifo(3);
        TaskList.cacheNoteList = new Acm.Model.CacheFifo(3);
        TaskList.cacheWorkflowHistory = new Acm.Model.CacheFifo(3);
        TaskList.cacheTaskEvents = new Acm.Model.CacheFifo(3);
        TaskList.cacheAttachments = new Acm.Model.CacheFifo(3);
        TaskList.cacheRejectComments = new Acm.Model.CacheFifo(3);





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

    ,cacheNoteList: null
    ,cachePage: null
    ,cacheTask: null
    ,cacheParentObject: null
    ,cacheWorkflowHistory: null
    ,cacheTaskEvents: null
    ,cacheAttachments: null
    ,cacheRejectComments: null

    ,DLG_REJECT_TASK_START: 0
    ,DLG_REJECT_TASK_N: 10
    ,DLG_REJECT_TASK_SORT_DIRECTION: 'ASC'
    	
    ,REJECT_COMMENT: 'REJECT_COMMENT'

    ,_parentObjId: 0
    ,getParentObjId: function(){
        return this._parentObjId;
    }
    ,setParentObjId : function(id) {
        this._parentObjId = id;
    }
    ,getParentObj: function() {
        if (0 >= this._parentObjId) {
            return null;
        }
        return this.cacheParentObject.get(this._parentObjId);
    }
    ,_taskId: 0
    ,getTaskId : function() {
        return this._taskId;
    }
    ,setTaskId : function(id) {
        this._taskId = id;
    }
    ,getTask: function() {
        if (0 >= this._taskId) {
            return null;
        }
        return this.cacheTask.get(this._taskId);
    }
    ,getWorkflowHistory: function() {
    if (0 >= this._taskId) {
        return null;
    }
    return this.cacheWorkflowHistory.get(this._taskId);
    }
    ,getAttachmentsList: function() {
        if (0 >= this._taskId) {
            return null;
        }
        return this.cacheAttachments.get(this._taskId);
    }
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

