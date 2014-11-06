/**
 * CaseFile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
CaseFile.Service = {
    create : function() {
    }
    ,initialize: function() {
    }

    ,List: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_CASE_FILE_LIST         : "/api/latest/plugin/search/CASE_FILE"

        ,_validateList: function(data) {
            if (Acm.isEmpty(data.responseHeader) || Acm.isEmpty(data.response)) {
                return false;
            }
            if (Acm.isEmpty(data.response.numFound) || Acm.isEmpty(data.response.start) || Acm.isEmpty(data.response.docs)) {
                return false;
            }
            return true;
        }
        ,retrieveCaseFileList: function(treeInfo){
            var caseFileId = treeInfo.caseFileId;
            var initKey = treeInfo.initKey;
            var start = treeInfo.start;
            var n = treeInfo.n;
            var s = treeInfo.s;
            var q = treeInfo.q;

            s = s ? s : "name desc";

            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_LIST;
            url += "?start=" + treeInfo.start;
            url += "&n=" + treeInfo.n;
            url += "&s=" + s;

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFileList(response);  //response has two properties: response.hasError, response.errorMsg

                    } else {
                        if (CaseFile.Service.List._validateList(response)) {
                            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
                            treeInfo.total = response.response.numFound;

                            var caseFiles = response.response.docs;
                            var pageId = CaseFile.Model.Tree.Config.getPageId();
                            CaseFile.Model.cachePage.put(pageId, caseFiles);

                            var key = treeInfo.initKey;

                            if (null == key) {
                                if (0 < caseFiles.length) {
                                    var caseFileId = parseInt(caseFiles[0].object_id_s);
                                    if (0 < caseFileId) {
                                        key = start + "." + caseFileId;
                                    }
                                }
                            } else {
                                treeInfo.initKey = null;
                            }

                            CaseFile.Controller.modelRetrievedCaseFileList(key);
                        }
                    }
                }
                ,url
            )
        }
    }

    ,Detail: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_CASE_FILE_    : "/api/latest/plugin/casefile/byId/"
        ,API_SAVE_CASE_FILE         : "/api/latest/plugin/casefile/"

        ,_validateCaseFile: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
                return false;
            }
            return true;
        }
        ,retrieveCaseFile : function(caseFileId) {
            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_ + caseFileId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFile(response);

                    } else {
                        if (CaseFile.Service.Detail._validateCaseFile(response)) {
                            var caseFile = response;
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            if (caseFileId != caseFile.id) {
                                return;         //user clicks another caseFile before callback, do nothing
                            }
                            CaseFile.Model.cacheCaseFile.put(caseFileId, caseFile);

                            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
                            if (0 < treeInfo.caseFileId) {      //handle single caseFil situation
                                treeInfo.total = 1;

                                var pageId = treeInfo.start;
                                var caseFilSolr = {};
                                caseFilSolr.author = caseFile.creator;
                                caseFilSolr.author_s = caseFile.creator;
                                caseFilSolr.create_dt = caseFile.created;
                                caseFilSolr.last_modified = caseFile.modified;
                                caseFilSolr.modifier_s = caseFile.modifier;
                                caseFilSolr.name = caseFile.caseNumber;
                                caseFilSolr.object_id_s = caseFile.id;
                                caseFilSolr.object_type_s = CaseFile.Model.getObjectType();
                                caseFilSolr.owner_s = caseFile.creator;
                                caseFilSolr.status_s = caseFile.status;
                                caseFilSolr.title_t = caseFile.title;

                                var caseFiles = [caseFilSolr];
                                CaseFile.Model.cachePage.put(pageId, caseFiles);

                                var key = pageId + "." + treeInfo.caseFileId.toString();
                                CaseFile.Controller.modelRetrievedCaseFileList(key);

                            } else {
                                CaseFile.Controller.modelRetrievedCaseFile(caseFile);
                            }
                        }
                    }
                }
                ,url
            )
        }

        ,retrieveCaseFileDeferred : function(caseFileId, callbackSuccess) {
            return Acm.Service.deferredGet(
                function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Service.Detail._validateCaseFile(response)) {
                            var caseFile = response;
                            CaseFile.Model.cacheCaseFile.put(caseFile.id, caseFile);
                            return callbackSuccess(response);
                        }
                    }
                }
                ,App.getContextPath() + CaseFile.Service.Detail.API_RETRIEVE_CASE_FILE_ + caseFileId
            );
        }

        ,saveCaseFile : function(data, handler) {
            var caseFile = data;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            CaseFile.Controller.modelSavedCaseFile(response);
                        }

                    } else {
                        if (CaseFile.Service.Detail._validateCaseFile(response)) {
                            var caseFile = response;
                            CaseFile.Model.cacheCaseFile.put(caseFile.id, caseFile);
                            if (handler) {
                                handler(caseFile);
                            } else {
                                CaseFile.Controller.modelSavedCaseFile(caseFile);
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_CASE_FILE
                ,JSON.stringify(caseFile)
            )
        }
        ,_dataWrapper: function(data, value) {
            if (data.hasError) {
                return data;
            } else {
                return value;
            }
        }
        ,saveCaseTitle: function(caseFileId, title) {
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            caseFile.title = title;
            this.saveCaseFile(caseFile
                ,function(data) {
                    CaseFile.Controller.modelSavedCaseTitle(caseFileId, CaseFile.Service.Detail._dataWrapper(data, data.title));
                }
            );
        }
        ,saveIncidentDate: function(caseFileId, created) {
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            caseFile.created = created;
            this.saveCaseFile(caseFile
                ,function(data) {
                    CaseFile.Controller.modelSavedIncidentDate(caseFileId, CaseFile.Service.Detail._dataWrapper(data, data.created));
                }
            );
        }
        ,saveAssignee: function(caseFileId, assignee) {
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            caseFile.assignee = assignee;
            this.saveCaseFile(caseFile
                ,function(data) {
                    CaseFile.Controller.modelSavedIncidentDate(caseFileId, CaseFile.Service.Detail._dataWrapper(data, data.assignee));
                }
            );
        }
        ,saveSubjectType: function(caseFileId, caseType) {
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            caseFile.caseType = caseType;
            this.saveCaseFile(caseFile
                ,function(data) {
                    CaseFile.Controller.modelSavedIncidentDate(caseFileId, CaseFile.Service.Detail._dataWrapper(data, data.caseType));
                }
            );
        }
        ,savePriority: function(caseFileId, priority) {
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            caseFile.priority = priority;
            this.saveCaseFile(caseFile
                ,function(data) {
                    CaseFile.Controller.modelSavedPriority(caseFileId, CaseFile.Service.Detail._dataWrapper(data, data.priority));
                }
            );
        }
        ,saveDueDate: function(caseFileId, dueDate) {
            var caseFile = CaseFile.Model.getCaseFile(caseFileId);
            caseFile.created = dueDate;
            this.saveCaseFile(caseFile
                ,function(data) {
                    CaseFile.Controller.modelSavedDueDate(caseFileId, CaseFile.Service.Detail._dataWrapper(data, data.created));
                }
            );
        }

        ,closeCaseFile : function(data) {
            var caseFileId = CaseFile.getCaseFileId();
//            Acm.Ajax.asyncPost(App.getContextPath() + this.API_CLOSE_CASE_FILE_ + caseFileId
//                ,JSON.stringify(data)
//                ,CaseFile.Callback.EVENT_CASEFILE_CLOSED
//            );
        }
    }

    ,Lookup: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_GET_ASSIGNEES             : "/api/latest/users/withPrivilege/acm-complaint-approve"
        ,API_GET_SUBJECT_TYPES         : "/api/latest/plugin/complaint/types"
        ,API_GET_PRIORITIES            : "/api/latest/plugin/complaint/priorities"

        ,_validateAssignees: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        ,retrieveAssignees : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFile(response);

                    } else {
                        if (CaseFile.Service.Lookup._validateAssignees(response)) {
                            var assignees = response;
                            CaseFile.Model.Lookup.setAssignees(assignees);
                            CaseFile.Controller.modelFoundAssignees(assignees);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_ASSIGNEES
            )
        }

        ,_validateSubjectTypes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        ,retrieveSubjectTypes : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFile(response);

                    } else {
                        if (CaseFile.Service.Lookup._validateSubjectTypes(response)) {
                            var subjectTypes = response;
                            CaseFile.Model.Lookup.setSubjectTypes(subjectTypes);
                            CaseFile.Controller.modelFoundSubjectTypes(subjectTypes);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_SUBJECT_TYPES
            )
        }

        ,_validatePriorities: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        ,retrievePriorities : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFile(response);

                    } else {
                        if (CaseFile.Service.Lookup._validatePriorities(response)) {
                            var priorities = response;
                            CaseFile.Model.Lookup.setPriorities(priorities);
                            CaseFile.Controller.modelFoundPriorities(priorities);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_PRIORITIES
            )
        }
    }
    ,Document: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"

        ,_validate: function(data) {
            if (Acm.isEmpty(data.responseHeader) || Acm.isEmpty(data.response)) {
                return false;
            }
            if (Acm.isEmpty(data.response.numFound) || Acm.isEmpty(data.response.start) || Acm.isEmpty(data.response.docs)) {
                return false;
            }
            return true;
        }

        ,downloadDocument : function(caseFileId, callbackSuccess) {
            return Acm.Service.deferredGet(
                function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Service.Detail._validate(response)) {
                            var caseFile = response;
                            CaseFile.Model.cacheCaseFile.put(caseFile.id, caseFile);
                            return callbackSuccess(response);
                        }
                    }
                }
                ,App.getContextPath() + CaseFile.Service.TaskList.API_RETRIEVE_TASKS_ + caseFileId
            );
        }
    }

    ,TaskList: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_TASKS_         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="

        ,_validate: function(data) {
            if (Acm.isEmpty(data.responseHeader) || Acm.isEmpty(data.response)) {
                return false;
            }
            if (Acm.isEmpty(data.response.numFound) || Acm.isEmpty(data.response.start) || Acm.isEmpty(data.response.docs)) {
                return false;
            }
            return true;
        }

        ,retrieveTaskListDeferred : function(caseFileId, callbackSuccess) {
            return Acm.Service.deferredGet(
                function(response) {
                    if (!response.hasError) {
                        if (CaseFile.Service.Detail._validate(response)) {
                            var caseFile = response;
                            CaseFile.Model.cacheCaseFile.put(caseFile.id, caseFile);
                            return callbackSuccess(response);
                        }
                    }
                }
                ,App.getContextPath() + CaseFile.Service.TaskList.API_RETRIEVE_TASKS_ + caseFileId
            );
        }
    }



    ,API_LIST_CASE_FILE         : "/api/latest/plugin/search/CASE_FILE"
    ,API_RETRIEVE_PERSON_       : "/api/latest/plugin/person/find?assocId="
    ,API_RETRIEVE_DETAIL        : "/api/latest/plugin/casefile/byId/"
    ,API_SAVE_CASE_FILE         : "/api/latest/plugin/casefile/"
    ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
    ,API_UPLOAD_CASE_FILE_FILE  : "/api/latest/plugin/casefile/file"
    ,API_RETRIEVE_TASKS         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="
    ,API_CLOSE_CASE_FILE_       : "/api/latest/plugin/casefile/closeCase/"


    ,listCaseFile : function(treeInfo) {
        var caseFileId = treeInfo.caseFileId;
        var initKey = treeInfo.initKey;
        var start = treeInfo.start;
        var n = treeInfo.n;
        var s = treeInfo.s;
        var q = treeInfo.q;

        s = s ? s : "name desc";

        var url = App.getContextPath() + this.API_LIST_CASE_FILE;
        url += "?start=" + treeInfo.start;
        url += "&n=" + treeInfo.n;
        url += "&s=" + s;
        Acm.Ajax.asyncGet(url
            ,CaseFile.Callback.EVENT_LIST_RETRIEVED
        );
    }
    ,retrieveDetail : function(caseFileId) {
        Acm.Ajax.asyncGet(App.getContextPath() + this.API_RETRIEVE_DETAIL + caseFileId
            ,CaseFile.Callback.EVENT_DETAIL_RETRIEVED
        );
    }
    ,saveCaseFile : function(data) {
        var updatedCaseFile = data;
        var key = "objectType";

        Acm.Ajax.asyncPost(App.getContextPath() + this.API_SAVE_CASE_FILE
            ,JSON.stringify(updatedCaseFile)
            ,CaseFile.Callback.EVENT_CASEFILE_SAVED
        );
    }

    ,closeCaseFile : function(data) {
        var caseFileId = CaseFile.getCaseFileId();
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_CLOSE_CASE_FILE_ + caseFileId
            ,JSON.stringify(data)
            ,CaseFile.Callback.EVENT_CASEFILE_CLOSED
        );
    }



};

