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
    }
    ,onInitialized: function() {
        if (CaseFileSplit.Service.Detail.onInitialized) {CaseFileSplit.Service.Detail.onInitialized();}
        if (CaseFileSplit.Service.Lookup.onInitialized) {CaseFileSplit.Service.Lookup.onInitialized();}

        if (CaseFileSplit.Service.People.onInitialized) {CaseFileSplit.Service.People.onInitialized();}
        if (CaseFileSplit.Service.Notes.onInitialized) {CaseFileSplit.Service.Notes.onInitialized();}
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

        ,saveObject : function(splittedCaseFileId, splittedCaseFile) {
            var url = App.getContextPath() + CaseFileSplit.Model.interface.apiSaveObject(objType, objId);
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelSavedSplittedCaseFile(response);
                    } else {
                        if (CaseFileSplit.Model.Detail.validateCaseFile(response)) {
                            CaseFileSplit.Model.Detail.cacheSplittedCaseFile.put(splittedCaseFileId,response);
                        }
                        else{
                                CaseFileSplit.Controller.modelSavedSplittedCaseFile(response);
                            }
                        }
                    }
                ,url
                ,JSON.stringify(splittedCaseFile)
            )
        }
        ,_saveCaseFile: function(caseFileId, caseFile, handler) {
            CaseFileSplit.Service.Detail.saveObject(CaseFileSplit.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile, handler);
        }
        ,saveDetail: function(caseFileId, details) {
            var caseFile = CaseFileSplit.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFileSplit.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.details = details;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFileSplit.Controller.modelSavedDetail(caseFileId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
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
        ,API_DELETE_PERSON_ASSOCIATION_    : "/api/latest/plugin/personAssociation/delete/"


        ,_saveCaseFile: function(caseFileId, caseFile, handler) {
            CaseFileSplit.Service.Detail.saveObject(CaseFileSplit.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile, handler);
        }
        ,deleteParticipant: function(caseFileId, participantId) {
            var caseFile = CaseFileSplit.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFileSplit.Model.Detail.validateCaseFile(caseFile)) {
                var toDelete = -1;
                for (var i = 0; i < caseFile.participants.length; i++) {
                    if (Acm.compare(caseFile.participants[i].id, participantId)) {
                        toDelete = i;
                        break;
                    }
                }

                if (0 <= toDelete) {
                    caseFile.participants.splice(toDelete, 1);
                    this._saveCaseFile(caseFileId, caseFile
                        ,function(data) {
                            if (CaseFileSplit.Model.Detail.validateCaseFile(data)) {
                                CaseFileSplit.Controller.modelDeletedParticipant(caseFileId, Acm.Service.responseWrapper(data, participantId));
                            }
                        }
                    );
                }
            }
        }

        ,deletePersonAssociation : function(caseFileId, personAssociationId) {
            var url = App.getContextPath() + this.API_DELETE_PERSON_ASSOCIATION_ + personAssociationId;
            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelDeletedPersonAssociation(response);

                    } else {
                        if (CaseFileSplit.Model.People.validateDeletedPersonAssociation(response)) {
                            if (response.deletedPersonAssociationId == personAssociationId) {
                                var caseFile = CaseFileSplit.Model.Detail.getCacheCaseFile(caseFileId);
                                if (CaseFileSplit.Model.Detail.validateCaseFile(caseFile)) {
                                    for (var i = 0; i < caseFile.personAssociations.length; i++) {
                                        var pa = caseFile.personAssociations[i];
                                        if (CaseFileSplit.Model.People.validatePersonAssociation(pa)) {
                                            if (pa.id == response.deletedPersonAssociationId) {
                                                caseFile.personAssociations.splice(i, 1);
                                                CaseFileSplit.Controller.modelDeletedPersonAssociation(Acm.Service.responseWrapper(response, personAssociationId));
                                                break;
                                            }
                                        }
                                    } //end for
                                }
                            }
                        }
                    } //end else
                }
                ,url
            )
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


        ,deleteNote : function(noteId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        CaseFileSplit.Controller.modelDeletedNote(response);

                    } else {
                        if (CaseFileSplit.Model.Notes.validateDeletedNote(response)) {
                            var caseFileId = CaseFileSplit.Model.getCaseFileId();
                            if (response.deletedNoteId == noteId) {
                                var noteList = CaseFileSplit.Model.Notes.cacheNoteList.get(caseFileId);
                                for (var i = 0; i < noteList.length; i++) {
                                    if (noteId == noteList[i].id) {
                                        noteList.splice(i, 1);
                                        CaseFileSplit.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
                                        return;
                                    }
                                } //end for
                            }
                        }
                    } //end else
                }
                ,url
            )
        }
    }

};

