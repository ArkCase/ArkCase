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

        ,saveCaseFile : function(data) {
            var caseFile = data;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelSavedCaseFile(response);

                    } else {
                        if (CaseFile.Service.Detail._validateCaseFile(response)) {
                            var caseFile = response;
                            CaseFile.Model.cacheCaseFile.put(caseFile.id, caseFile);
                            CaseFile.Controller.modelSavedCaseFile(caseFile);
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_CASE_FILE
                ,JSON.stringify(caseFile)
            )
        }

        ,closeCaseFile : function(data) {
            var caseFileId = CaseFile.getCaseFileId();
//            Acm.Ajax.asyncPost(App.getContextPath() + this.API_CLOSE_CASE_FILE_ + caseFileId
//                ,JSON.stringify(data)
//                ,CaseFile.Callback.EVENT_CASEFILE_CLOSED
//            );
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

