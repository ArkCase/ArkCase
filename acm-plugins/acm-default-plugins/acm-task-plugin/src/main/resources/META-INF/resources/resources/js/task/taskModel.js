/**
 * Task.Model
 *
 * @author jwu
 */
Task.Model = Task.Model || {
    create : function() {
        if (Task.Service.create)                                    {Task.Service.create();}
        if (Task.Model.Tree.create)                                 {Task.Model.Tree.create();}
        if (Task.Model.Detail.create)                               {Task.Model.Detail.create();}
        if (Task.Model.Notes.create)                                {Task.Model.Notes.create();}
        if (Task.Model.History.create)                              {Task.Model.History.create();}
        if (Task.Model.WorkflowOverview.create)                     {Task.Model.WorkflowOverview.create();}
        if (Task.Model.Attachments.create)                          {Task.Model.Attachments.create();}
        if (Task.Model.DocumentUnderReview.create)                  {Task.Model.DocumentUnderReview.create();}
        if (Task.Model.RejectComments.create)                       {Task.Model.RejectComments.create();}
    }
    ,onInitialized: function() {
        if (Task.Service.onInitialized)                             {Task.Service.onInitialized();}
        if (Task.Model.Tree.onInitialized)                          {Task.Model.Tree.onInitialized();}
        if (Task.Model.Detail.onInitialized)                        {Task.Model.Detail.onInitialized();}
        if (Task.Model.Notes.onInitialized)                         {Task.Model.Notes.onInitialized();}
        if (Task.Model.History.onInitialized)                       {Task.Model.History.onInitialized();}
        if (Task.Model.WorkflowOverview.onInitialized)              {Task.Model.WorkflowOverview.onInitialized();}
        if (Task.Model.Attachments.onInitialized)                   {Task.Model.Attachments.onInitialized();}
        if (Task.Model.DocumentUnderReview.onInitialized)           {Task.Model.DocumentUnderReview.onInitialized();}
        if (Task.Model.RejectComments.onInitialized)                {Task.Model.RejectComments.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            return "/api/latest/plugin/search/TASK";
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            return "/api/latest/plugin/task/byId/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            return "/api/latest/plugin/task/save/" + objId;
        }
        ,nodeId: function(obj) {
            return obj.object_id_s;
            //return parseInt(obj.object_id_s);
        }
        ,nodeType: function(obj) {
            return (obj.adhocTask_b)? Task.Model.DOC_TYPE_ADHOC_TASK : Task.Model.DOC_TYPE_TASK;
        }
        ,nodeTitle: function(obj) {
            var title;
            if(Acm.isNotEmpty(obj.name) && Acm.isNotEmpty(obj.priority_s) && Acm.isNotEmpty(obj.due_tdt)){
                title = Acm.getDateFromDatetime(obj.due_tdt) + ", " + obj.priority_s +", "+ obj.name;
            }
            else if(Acm.isNotEmpty(obj.name) && Acm.isNotEmpty(obj.priority_s)){
                title = obj.priority_s +", "+ obj.name;
            }
            else if(Acm.isNotEmpty(obj.name)){
                title = obj.name;
            }
            else{
                title = "(No title)";
            }
            return title;
        }
        ,nodeToolTip: function(obj) {
            return Acm.goodValue(obj.name);
        }
        ,objToSolr: function(objData) {
            var solr = {};
            solr.due_tdt = task.dueDate;
            solr.title_parseable = task.title;
            solr.priority_s = task.priority;
            solr.object_id_s = task.taskId;
            solr.object_type_s = Task.Model.DOC_TYPE_TASK;
            solr.parent_object_id_i = task.attachedToObjectId;
            solr.parent_object_type_s = task.attachedToObjectType;
            solr.adhocTask_b = task.adhocTask;
            return solr;
        }
        ,validateObjData: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.taskId)) {
                return false;
            }
//            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
//             return false;
//             }
//             if (!Acm.isArray(data.childObjects)) {
//             return false;
//             }
//             if (!Acm.isArray(data.participants)) {
//             return false;
//             }
//             if (!Acm.isArray(data.personAssociations)) {
//             return false;
//             }
            return true;
        }
        ,nodeTypeMap: function() {
            return Task.Model.Tree.Key.nodeTypeMap;
        }
    }



    ,DOC_TYPE_TASK        : "TASK"
    ,DOC_TYPE_ADHOC_TASK  : "ADHOC"

//    ,getTaskId: function() {
//        return ObjNav.Model.getObjectId();
//    }
//    ,setTaskId: function(taskId) {
//        ObjNav.Model.setObjectId(taskId);
//    }
    ,getTask: function() {
        var nodeId = ObjNav.Model.getObjectId();
        var nodeType = ObjNav.Model.getObjectType();
        return ObjNav.Model.Detail.getCacheObject(nodeType, nodeId);
    }
    ,findTask: function(nodeType, nodeId) {
        return ObjNav.Model.Detail.getCacheObject(nodeType, nodeId);
    }

    ,Tree: {
        create: function() {
            if (Task.Model.Tree.Key.create)        {Task.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (Task.Model.Tree.Key.onInitialized)        {Task.Model.Tree.Key.onInitialized();}
        }

        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,NODE_TYPE_PART_DETAILS      : "det"
            ,NODE_TYPE_PART_NOTES        : "note"
            ,NODE_TYPE_PART_HISTORY      : "his"
            ,NODE_TYPE_PART_WORKFLOW     : "wkfl"
            ,NODE_TYPE_PART_ATTACHMENTS  : "att"
            ,NODE_TYPE_PART_DOCUMENTS    : "doc"
            ,NODE_TYPE_PART_REWORK       : "rewk"
            ,NODE_TYPE_PART_REJECT       : "rej"

            ,nodeTypeMap: [
                {nodeType: "prevPage"    ,icon: "i i-arrow-up"     ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i i-arrow-down"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""                 ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/TASK"     ,icon: "i i-checkmark"    ,tabIds:
                    ["tabDetails"
                        ,"tabDocuments"
                        ,"tabNotes"
                        ,"tabHistory"
                        ,"tabReworkInstructions"
                        ,"tabWorkflowOverview"
                        ,"tabAttachments"
                    ]}
                ,{nodeType: "p/ADHOC"     ,icon: "i i-checkmark"    ,tabIds:
                    ["tabDetails"
                        ,"tabNotes"
                        ,"tabHistory"
                        ,"tabRejectComments"
                        ,"tabWorkflowOverview"
                        ,"tabAttachments"
                    ]}
                ,{nodeType: "p/TASK/det"      ,icon: "",tabIds: ["tabDetails"]}
                ,{nodeType: "p/TASK/note"     ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/TASK/his"      ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "p/TASK/wkfl"     ,icon: "",tabIds: ["tabWorkflowOverview"]}
                ,{nodeType: "p/TASK/att"      ,icon: "",tabIds: ["tabAttachments"]}
                ,{nodeType: "p/TASK/doc"      ,icon: "",tabIds: ["tabDocuments"]}
                ,{nodeType: "p/TASK/rewk"     ,icon: "",tabIds: ["tabReworkInstructions"]}
                ,{nodeType: "p/ADHOC/det"     ,icon: "",tabIds: ["tabDetails"]}
                ,{nodeType: "p/ADHOC/note"    ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/ADHOC/his"     ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "p/ADHOC/wkfl"    ,icon: "",tabIds: ["tabWorkflowOverview"]}
                ,{nodeType: "p/ADHOC/att"     ,icon: "",tabIds: ["tabAttachments"]}
                ,{nodeType: "p/ADHOC/rej"     ,icon: "",tabIds: ["tabRejectComments"]}
            ]
        }
    }

    ,Detail: {
        create : function() {
            Acm.Dispatcher.addEventListener(Task.Controller.VIEW_CHANGED_DETAIL  , this.onViewChangedDetail);
        }
        ,onInitialized: function() {
        }

        ,onViewChangedDetail: function(nodeType, taskId, details){
            Task.Service.Detail.saveDetail(nodeType, taskId, details);
        }
    }

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(Task.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(Task.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(Task.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
        }

        ,onViewAddedNote: function(note) {
            Task.Service.Notes.addNote(note);
        }
        ,onViewUpdatedNote: function(note) {
            Task.Service.Notes.updateNote(note);
        }
        ,onViewDeletedNote: function(noteId) {
            Task.Service.Notes.deleteNote(noteId);
        }

        ,validateNotes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }

        ,validateNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            /*if (Acm.isEmpty(data.id)) {
             return false;
             }*/
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            if (Acm.isEmpty(data.note)) {
                return false;
            }
            return true;
        }

        ,validateDeletedNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
    }

    ,History: {
        create : function() {
            this.cacheHistory = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,validateHistory: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.resultPage)) {
                return false;
            }
            if (Acm.isNotArray(data.resultPage)) {
                return false;
            }
            if (Acm.isEmpty(data.totalCount)) {
                return false;
            }
            return true;
        }
        ,validateEvent: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.eventDate)) {
                return false;
            }
            if (Acm.isEmpty(data.eventType)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.objectType)) {
                return false;
            }
            if (Acm.isEmpty(data.userId)) {
                return false;
            }
            return true;
        }
    }

    ,WorkflowOverview: {
        create : function() {
            this.cacheWorkflowOverview = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT    ,this.onModelRetrievedObject);
        }
        ,onInitialized: function() {
        }
        ,onModelRetrievedObject: function(task) {
            //var taskId = Task.Model.getTaskId();
            //var task = Task.Model.getObject();
            //var adhoc_b = false;
            if (Task.Model.interface.validateObjData(task)) {
                var taskId = task.taskId;
                if (Acm.isNotEmpty(task.businessProcessId)){
                    Task.Service.WorkflowOverview.retrieveWorkflowOverview(task.businessProcessId,taskId, false);
                //}else if (Acm.isNotEmpty(task.taskId)){
                } else {
                    //adhoc_b = true;
                    Task.Service.WorkflowOverview.retrieveWorkflowOverview(taskId,taskId, true);
                }

            }
        }
        ,validateWorkflowOverview: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }
        ,validateWorkflowOverviewRecord: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.participant)) {
                return false;
            }
            /*if (Acm.isEmpty(data.role)) {
             return false;
             }*/
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            if (Acm.isEmpty(data.startDate)) {
                return false;
            }
            /*if (Acm.isEmpty(data.endDate)) {
             return false;
             }*/
            return true;
        }
    }

    ,Attachments: {
        create : function() {
            this.cacheAttachments = new Acm.Model.CacheFifo(2);      //todo: remove this cache, use task cache instead
        }
        ,onInitialized: function() {
        }
        ,validateUploadedAttachments: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }
        ,validateExistingAttachments: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.childObjects)) {
                return false;
            }
            if (Acm.isNotArray(data.childObjects)) {
                return false;
            }
            return true;
        }
        ,validateAttachmentRecord: function(data) {
            if (Acm.isEmpty(data.targetId)) {
                return false;
            }
            if (Acm.isEmpty(data.targetName)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.status)) {
                return false;
            }
            return true;
        }
    }

    ,DocumentUnderReview: {
        create : function() {
            this.cacheTask = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
        }
        ,validateDocumentUnderReviewRecord: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.fileId)) {
                return false;
            }
            if (Acm.isEmpty(data.fileName)) {
                return false;
            }
            if (Acm.isEmpty(data.created)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.parentObjects)) {
                return false;
            }
            if (Acm.isNotArray(data.parentObjects)) {
                return false;
            }
            if (Acm.isEmpty(data.parentObjects[0].status)) {
                return false;
            }
            return true;
        }
        ,validateDocumentUnderReview: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.documentUnderReview)) {
                return false;
            }
            return true;
        }
    }

    ,RejectComments: {
        create : function() {
            this.cacheRejectComments = new Acm.Model.CacheFifo();
            Acm.Dispatcher.addEventListener(Task.Controller.MODEL_RETRIEVED_TASK, this.onModelRetrievedTask);
        }
        ,onInitialized: function() {
        }

        ,_rejectNoteType: 'REJECT_COMMENT'

        ,onModelRetrievedTask: function(task) {
            if (task.hasError) {
                //empty table?
            } else {
                var taskId = ObjNav.Model.getObjectId();
                Task.Service.RejectComments.retrieveRejectComments(Task.Model.RejectComments._rejectNoteType,taskId,Task.Model.DOC_TYPE_TASK);
            }
        }

        ,validateRejectCommentRecord: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            /*if (Acm.isEmpty(data.id)) {
             return false;
             }*/
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            if (Acm.isEmpty(data.note)) {
                return false;
            }
            return true;
        }

        ,validateRejectComments: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            /*if (Acm.isEmpty(data.parentId)) {
             return false;
             }
             if (Acm.isEmpty(data.note)) {
             return false;
             }*/
            return true;
        }

    }

};

