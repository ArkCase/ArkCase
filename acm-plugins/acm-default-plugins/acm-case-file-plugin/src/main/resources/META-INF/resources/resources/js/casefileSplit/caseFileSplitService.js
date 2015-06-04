/**
 * CaseFileSplit.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
CaseFileSplit.Service = {
    create : function() {
        if (CaseFileSplit.Service.Detail.create) {CaseFileSplit.Service.Detail.create();}
        if (CaseFileSplit.Service.Lookup.create) {CaseFileSplit.Service.Lookup.create();}

        if (CaseFileSplit.Service.People.create) {CaseFileSplit.Service.People.create();}
        if (CaseFileSplit.Service.Notes.create) {CaseFileSplit.Service.Notes.create();}
        if (CaseFileSplit.Service.Summary.create) {CaseFileSplit.Service.Summary.create();}

    }
    ,onInitialized: function() {
        if (CaseFileSplit.Service.Detail.onInitialized) {CaseFileSplit.Service.Detail.onInitialized();}
        if (CaseFileSplit.Service.Lookup.onInitialized) {CaseFileSplit.Service.Lookup.onInitialized();}

        if (CaseFileSplit.Service.People.onInitialized) {CaseFileSplit.Service.People.onInitialized();}
        if (CaseFileSplit.Service.Notes.onInitialized) {CaseFileSplit.Service.Notes.onInitialized();}
        if (CaseFileSplit.Service.Summary.onInitialized) {CaseFileSplit.Service.Summary.onInitialized();}

    }

    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_RETRIEVE_CASE_FILE_DETAILS         : "/api/v1/plugin/casefile/byId/"
        ,retrieveParentCaseFile : function(parentCaseFileId) {
            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_DETAILS + parentCaseFileId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelRetrievedObjectError(response);

                    } else {
                        if (CaseFileSplit.Model.Detail.validateCaseFile(response)) {
                            CaseFileSplit.Model.Detail.cacheParentCaseFile.put(parentCaseFileId,response);
                                CaseFileSplit.Controller.modelRetrievedParentCaseFile(response);

                            } else {
                                CaseFileSplit.Controller.modelRetrievedParentCaseFile(response);
                            }
                        }
                    }
                ,url
            )
        }
    }
    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_GET_GROUPS				   : "/api/latest/service/functionalaccess/groups/acm-case-approve?n=1000&s=name asc"
        ,API_GET_USERS				   : "/api/latest/plugin/search/USER?n=1000&s=name asc"
        ,API_GET_ASSIGNEES             : "/api/latest/service/functionalaccess/users/acm-case-approve"


        ,retrieveAssignees : function(parentCaseFileId) {
            var caseFile = CaseFileSplit.Model.Detail.cacheParentCaseFile.get(parentCaseFileId);
            if (caseFile == null) {
                return null;
            }

            var groupGetParameter = CaseFileSplit.Model.Lookup.createGroupGetParameter(caseFile);
            var currentAssigneeGetParameter = CaseFileSplit.Model.Lookup.createCurrentAssigneeGetParameter(caseFile);

            if (currentAssigneeGetParameter !== '' && groupGetParameter === '') {
                // only if current assignee is not empty but the group is empty, then add /* for group, to be able to
                // rich the required request method and return all users
                groupGetParameter = '/*'
            }

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelFoundAssignees(response);

                    } else {
                        if (CaseFileSplit.Model.Lookup.validateAssignees(response)) {
                            var assignees = response;
                            CaseFileSplit.Model.Lookup.setAssignees(parentCaseFileId, assignees);
                            CaseFileSplit.Controller.modelFoundAssignees(assignees);
                        }
                        return assignees;
                    }
                }
                ,App.getContextPath() + this.API_GET_ASSIGNEES + groupGetParameter + currentAssigneeGetParameter
            )
        }


        ,retrieveGroups : function(parentCaseFileId) {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelRetrievedGroups(response);
                    } else {
                        if (response.response && response.response.docs && CaseFileSplit.Model.Lookup.validateGroups(response.response.docs)) {
                            var groups = response.response.docs;
                            CaseFileSplit.Model.Lookup.setGroups(parentCaseFileId, groups);
                            CaseFileSplit.Controller.modelRetrievedGroups(groups);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_GROUPS
            )
        }



        ,retrieveUsers : function(parentCaseFileId) {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelRetrievedUsers(response);
                    } else {
                        if (response.response && response.response.docs && CaseFileSplit.Model.Lookup.validateUsers(response.response.docs)) {
                            var users = response.response.docs;
                            CaseFileSplit.Model.Lookup.setUsers(parentCaseFileId,users);
                            CaseFileSplit.Controller.modelRetrievedUsers(users);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_USERS
            )
        }


    }


    ,People: {
        create: function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Notes: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_SAVE_NOTE               : "/api/latest/plugin/note"
        ,API_DELETE_NOTE_            : "/api/latest/plugin/note/"
        ,API_LIST_NOTES_             : "/api/latest/plugin/note/"

        ,retrieveNoteListDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFileSplit.Service.Notes.API_LIST_NOTES_ + CaseFileSplit.Model.DOC_TYPE_CASE_FILE + "/";
                    url += caseFileId;
                    return url;
                }
                ,function(data) {
                    var jtData = null
                    if (CaseFileSplit.Model.Notes.validateNotes(data)) {
                        var noteList = data;
                        CaseFileSplit.Model.Notes.cacheNoteList.put(caseFileId, noteList);
                        jtData = callbackSuccess(noteList);
                    }
                    return jtData;
                }
            );
        }
    }
    ,Summary: {
        create: function () {
        }
        , onInitialized: function () {
        }
        ,API_SPLIT_CASE_FILE : "/api/latest/plugin/copyCaseFile"

        ,splitCaseFile: function(summary){
            var url = this.API_SPLIT_CASE_FILE;
            return Acm.Service.call({type: "POST"
                ,url: url
                ,data: JSON.stringify(summary)
                ,callback: function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelSplitCaseFile(response);
                    } else {
                        if (CaseFile.Model.Detail.validateCaseFile(response)) {
                            var splitCaseFile = response;
                            CaseFileSplit.Controller.modelSplitCaseFile(splitCaseFile);
                            return true;
                        }
                    } //end else
                }
            });
        }
    }
};

