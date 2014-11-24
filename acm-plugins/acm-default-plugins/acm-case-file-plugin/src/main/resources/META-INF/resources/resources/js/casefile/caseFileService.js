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

    ,List: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_CASE_FILE_LIST         : "/api/latest/plugin/search/CASE_FILE"

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
                        if (Acm.Validator.validateSolrData(response)) {
                            var treeInfo = CaseFile.Model.Tree.Config.getTreeInfo();
                            treeInfo.total = response.response.numFound;

                            var caseFiles = response.response.docs;
                            var pageId = CaseFile.Model.Tree.Config.getPageId();
                            CaseFile.Model.List.cachePage.put(pageId, caseFiles);

                            var key = treeInfo.initKey;

                            var caseFileId;
                            if (null == key) {
                                if (0 < caseFiles.length) {
                                    caseFileId = parseInt(caseFiles[0].object_id_s);
                                    if (0 < caseFileId) {
                                        key = start + "." + caseFileId;
                                    }
                                }
                            } else {
                                caseFileId = CaseFile.Model.Tree.Key.getCaseFileIdByKey(key);
                                treeInfo.initKey = null;
                            }

                            CaseFile.Model.setCaseFileId(caseFileId);
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

        ,API_RETRIEVE_CASE_FILE_           : "/api/latest/plugin/casefile/byId/"
        ,API_SAVE_CASE_FILE                : "/api/latest/plugin/casefile/"
        ,API_SAVE_PERSON_ASSOCIATION       : "/api/latest/plugin/personAssociation"
        ,API_DELETE_PERSON_ASSOCIATION_    : "/api/latest/plugin/personAssociation/delete/"


        ,retrieveCaseFile : function(caseFileId) {
            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_ + caseFileId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFile(response);

                    } else {
                        if (CaseFile.Model.Detail.validateData(response)) {
                            var caseFile = response;
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            if (caseFileId != caseFile.id) {
                                return;         //user clicks another caseFile before callback, do nothing
                            }
                            CaseFile.Model.Detail.cacheCaseFile.put(caseFileId, caseFile);

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
                                CaseFile.Model.List.cachePage.put(pageId, caseFiles);

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
                        if (CaseFile.Model.Detail.validateData(response)) {
                            var caseFile = response;
                            CaseFile.Model.Detail.cacheCaseFile.put(caseFile.id, caseFile);
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
                        if (CaseFile.Model.Detail.validateData(response)) {
                            var caseFile = response;
                            CaseFile.Model.Detail.cacheCaseFile.put(caseFile.id, caseFile);
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
        ,saveCaseTitle: function(caseFileId, title) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.title = title;
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedCaseTitle(caseFileId, Acm.Service.responseWrapper(data, data.title));
                    }
                );
            }
        }
        ,saveIncidentDate: function(caseFileId, incidentDate) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.incidentDate = incidentDate;
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedIncidentDate(caseFileId, Acm.Service.responseWrapper(data, data.incidentDate));
                    }
                );
            }
        }
        ,saveAssignee: function(caseFileId, assignee) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                CaseFile.Model.Detail.setAssignee(caseFile, assignee);
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedAssignee(caseFileId, Acm.Service.responseWrapper(data, assignee));
                    }
                );
            }
        }
        ,saveSubjectType: function(caseFileId, caseType) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.caseType = caseType;
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedSubjectType(caseFileId, Acm.Service.responseWrapper(data, data.caseType));
                    }
                );
            }
        }
        ,savePriority: function(caseFileId, priority) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.priority = priority;
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedPriority(caseFileId, Acm.Service.responseWrapper(data, data.priority));
                    }
                );
            }
        }
        ,saveDueDate: function(caseFileId, dueDate) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.dueDate = dueDate;
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedDueDate(caseFileId, Acm.Service.responseWrapper(data, data.dueDate));
                    }
                );
            }
        }
        ,saveDetail: function(caseFileId, details) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.details = details;
                this.saveCaseFile(caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedDetail(caseFileId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }
        ,saveChildObject: function(caseFileId, childObject) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                for (var i = 0; i < caseFile.childObjects.length; i++) {
                    if (Acm.compare(caseFile.childObjects[i].targetId, childObject.targetid)) {
                        caseFile.childObjects[i].title  = childObject.title;
                        caseFile.childObjects[i].status = childObject.status;
                        this.saveCaseFile(caseFile
                            ,function(data) {
                                var savedChildObject = null;
                                if (CaseFile.Model.Detail.validateData(data)) {
                                    for (var i = 0; i < data.childObjects.length; i++) {
                                        if (Acm.compare(data.childObjects[i].targetId, childObject.targetid)) {
                                            savedChildObject = data.childObjects[i];
                                            break;
                                        }
                                    }
                                }
                                CaseFile.Controller.modelSavedChildObject(caseFileId, Acm.Service.responseWrapper(data, savedChildObject));
                            }
                        );
                    }
                }
            }
        }
        ,addParticipant: function(caseFileId, participant) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                caseFile.participants.push(participant);
                this.saveCaseFile(caseFile
                    ,function(data) {
                        var addedParticipant = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            for (var i = 0; i < data.participants.length; i++) {
                                if (Acm.compare(data.participants[i].participantLdapId, participant.participantLdapId)
                                    && Acm.compare(data.participants[i].participantType, participant.participantType)) {
                                    addedParticipant = data.participants[i];
                                    break;
                                }
                            }
                        }
                        if (addedParticipant) {
                            CaseFile.Controller.modelAddedParticipant(caseFileId, Acm.Service.responseWrapper(data, addedParticipant));
                        }
                    }
                );
            }
        }
        ,updateParticipant: function(caseFileId, participant) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                for (var i = 0; i < caseFile.participants.length; i++) {
                    if (Acm.compare(caseFile.participants[i].id, participant.id)) {
                        caseFile.participants[i].participantLdapId  = participant.participantLdapId;
                        caseFile.participants[i].participantType = participant.participantType;
                        break;
                    }
                } //end for

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var savedParticipant = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            for (var i = 0; i < data.participants.length; i++) {
                                if (Acm.compare(data.participants[i].id, participant.id)) {
                                    savedParticipant = data.participants[i];
                                    break;
                                }
                            }
                        }
                        if (savedParticipant) {
                            CaseFile.Controller.modelUpdatedParticipant(caseFileId, Acm.Service.responseWrapper(data, savedParticipant));
                        }
                    }
                );
            }
        }
        ,deleteParticipant: function(caseFileId, participantId) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var toDelete = -1;
                for (var i = 0; i < caseFile.participants.length; i++) {
                    if (Acm.compare(caseFile.participants[i].id, participantId)) {
                        toDelete = i;
                        break;
                    }
                }

                if (0 <= toDelete) {
                    caseFile.participants.splice(toDelete, 1);
                    this.saveCaseFile(caseFile
                        ,function(data) {
                            if (CaseFile.Model.Detail.validateData(data)) {
                                CaseFile.Controller.modelDeletedParticipant(caseFileId, Acm.Service.responseWrapper(data, participantId));
                            }
                        }
                    );
                }
            }
        }

        ,addPersonAssociation : function(caseFileId, personAssociation) {
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelAddedPersonAssociation(caseFileId, response);

                    } else {
                        if (CaseFile.Model.Detail.validatePersonAssociation(response)) {
                            //check caseFileId == personAssociation.parentId;
                            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
                            if (CaseFile.Model.Detail.validateData(caseFile)) {
                                //check response.parentId == caseFileId
                                //check response.id not null, > 0
                                //check response.id not already in caseFile.personAssociations array
                                var addedPersonAssociation = response;
                                caseFile.personAssociations.push(addedPersonAssociation);
                                CaseFile.Model.Detail.cacheCaseFile.put(caseFileId, caseFile);
                                CaseFile.Controller.modelAddedPersonAssociation(caseFileId, addedPersonAssociation);
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_PERSON_ASSOCIATION
                ,JSON.stringify(personAssociation)
            )
        }
        ,updatePersonAssociation: function(caseFileId, personAssociation) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                for (var i = 0; i < caseFile.personAssociations.length; i++) {
                    if (Acm.compare(caseFile.personAssociations[i].id, personAssociation.id)) {
                        caseFile.personAssociations[i].person.title  =  personAssociation.person.title;
                        caseFile.personAssociations[i].person.givenName = personAssociation.person.givenName;
                        caseFile.personAssociations[i].person.familyName = personAssociation.person.familyName;
                        caseFile.personAssociations[i].personType = pa.personType;
                        break;
                    }
                } //end for

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var savedPersonAssociation = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            for (var i = 0; i < data.personAssociations.length; i++) {
                                if (Acm.compare(data.personAssociations[i].id, pa.id)) {
                                    savedPersonAssociation = data.personAssociations[i];
                                    break;
                                }
                            }
                        }
                        if (savedPersonAssociation) {
                            CaseFile.Controller.modelUpdatedPersonAssociation(caseFileId, Acm.Service.responseWrapper(data, savedPersonAssociation));
                        }
                    }
                );
            }
        }

        ,_validateDeletedPersonAssociation: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedPersonAssociationId)) {
                return false;
            }
            return true;
        }
        ,deletePersonAssociation : function(caseFileId, personAssociationId) {
            var url = App.getContextPath() + this.API_DELETE_PERSON_ASSOCIATION_ + personAssociationId;
            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelDeletedPersonAssociation(response);

                    } else {
                        if (CaseFile.Service.Detail._validateDeletedPersonAssociation(response)) {
                            if (response.deletedPersonAssociationId == personAssociationId) {
                                var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateData(caseFile)) {
                                    for (var i = 0; i < caseFile.personAssociations.length; i++) {
                                        var pa = caseFile.personAssociations[i];
                                        if (CaseFile.Model.Detail.validatePersonAssociation(pa)) {
                                            if (pa.id == response.deletedPersonAssociationId) {
                                                caseFile.personAssociations.splice(i, 1);
                                                CaseFile.Model.Detail.cacheCaseFile.put(caaeFileId, caseFile);
                                                CaseFile.Controller.modelDeletedPersonAssociation(Acm.Service.responseWrapper(response, personAssociationId));
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

        ,addContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    //ensure contactMethod.id undefined?
                    contactMethods.push(contactMethod);
                }

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var addedContactMethod = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                var contactMethods = personAssociation.person.contactMethods;
                                for (var i = 0; i < contactMethods.length; i++) {
                                    if (Acm.compare(contactMethods[i].type, contactMethod.type)
                                        && Acm.compare(contactMethods[i].value, contactMethod.value)) {
                                        addedContactMethod = contactMethods[i];
                                        break;
                                    }
                                }
                            }
                        }
                        if (addedContactMethod) {
                            CaseFile.Controller.modelAddedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedContactMethod));
                        }
                    }
                );
            }
        }
        ,updateContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    for (var i = 0; i < contactMethods.length; i++) {
                        if (Acm.compare(contactMethods[i].id, contactMethod.id)) {
                            contactMethods[i].type = contactMethod.type;
                            contactMethods[i].value = contactMethod.value;
                            break;
                        }
                    }

                    this.saveCaseFile(caseFile
                        ,function(data) {
                            var savedContactMethod = null;
                            if (CaseFile.Model.Detail.validateData(data)) {
                                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                    var contactMethods = personAssociation.person.contactMethods;
                                    for (var i = 0; i < contactMethods.length; i++) {
                                        if (Acm.compare(contactMethods[i].id, contactMethod.id)) {
                                            savedContactMethod = contactMethods[i];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (savedContactMethod) {
                                CaseFile.Controller.modelUpdatedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedContactMethod));
                            }
                        }
                    );
                }
            }
        }
        ,deleteContactMethod: function(caseFileId, personAssociationId, contactMethodId) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var toDelete = -1;
                    var contactMethods = personAssociation.person.contactMethods;
                    for (var i = 0; i < contactMethods.length; i++) {
                        if (Acm.compare(contactMethods[i].id, contactMethodId)) {
                            toDelete = i;
                            break;
                        }
                    }

                    if (0 <= toDelete) {
                        contactMethods.splice(toDelete, 1);
                        this.saveCaseFile(caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateData(data)) {
                                    CaseFile.Controller.modelDeletedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, contactMethodId));
                                }
                            }
                        );
                    }
                }
            }
        }



//        ,closeCaseFile : function(data) {
//            var caseFileId = CaseFile.getCaseFileId();
//            Acm.Ajax.asyncPost(App.getContextPath() + this.API_CLOSE_CASE_FILE_ + caseFileId
//                ,JSON.stringify(data)
//                ,CaseFile.Callback.EVENT_CASEFILE_CLOSED
//            );
//        }
//        ,consolidateCase: function(data) {
//        }
    }

    ,Documents: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_DOWNLOAD_DOCUMENT_      : "/api/latest/plugin/ecm/download/byId/"
    }

    ,Notes: {
        create: function() {
        }
        ,initialize: function() {
        }
        ,API_SAVE_NOTE               : "/api/latest/plugin/note"
        ,API_DELETE_NOTE_            : "/api/latest/plugin/note/"
        ,API_LIST_NOTES_             : "/api/latest/plugin/note/"

        ,retrieveNoteListDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFile.Service.Notes.API_LIST_NOTES_ + CaseFile.Model.getObjectType() + "/";
                    url += caseFileId;
                    return url;
                }
                ,function(data) {
                    var jtData = null
                    if (CaseFile.Model.Notes.validateNotes(data)) {
                        var noteList = data;


                        CaseFile.Model.Notes.cacheNoteList.put(caseFileId, noteList);
                        jtData = callbackSuccess(noteList);
                    }
                    return jtData;
                }
            );
        }


        ,saveNote : function(data, handler) {
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            CaseFile.Controller.modelSavedNote(response);
                        }

                    } else {
                        if (CaseFile.Model.Notes.validateNote(response)) {
                            var note = response;
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            if (caseFileId == note.parentId) {
                                var noteList = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                                var found = -1;
                                for (var i = 0; i < noteList.length; i++) {
                                    if (note.id == noteList[i].id) {
                                        found = i;
                                        break;
                                    }
                                }
                                if (0 > found) {                //add new note
                                    noteList.push(note);
                                } else {                        // update existing note
                                    noteList[found] = note;
                                }

                                if (handler) {
                                    handler(note);
                                } else {
                                    CaseFile.Controller.modelSavedNote(note);
                                }
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_NOTE
                ,JSON.stringify(data)
            )
        }
        ,addNote: function(note) {
            if (CaseFile.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        CaseFile.Controller.modelAddedNote(data);
                    }
                );
            }
        }
        ,updateNote: function(note) {
            if (CaseFile.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        CaseFile.Controller.modelUpdatedNote(data);
                    }
                );
            }
        }

        ,_validateDeletedNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
        ,deleteNote : function(noteId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelDeletedNote(response);

                    } else {
                        if (CaseFile.Service.Notes._validateDeletedNote(response)) {
                            var caseFileId = CaseFile.Model.getCaseFileId();
                            if (response.deletedNoteId == noteId) {
                                var noteList = CaseFile.Model.Notes.cacheNoteList.get(caseFileId);
                                for (var i = 0; i < noteList.length; i++) {
                                    if (noteId == noteList[i].id) {
                                        noteList.splice(i, 1);
                                        CaseFile.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
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

    ,Tasks: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_TASKS_         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="

        ,retrieveTaskListDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFile.Service.Tasks.API_RETRIEVE_TASKS_;
                    url += caseFileId;

                    //for test
                    //url = App.getContextPath() + "/api/latest/plugin/search/CASE_FILE";

                    return url;
                }
                ,function(data) {
                    var jtData = null
                    if (Acm.Validator.validateSolrData(data)) {
                        var responseHeader = data.responseHeader;
                        if (0 == responseHeader.status) {
                            //response.start should match to jtParams.jtStartIndex
                            //response.docs.length should be <= jtParams.jtPageSize

                            var response = data.response;
                            var taskList = [];
                            for (var i = 0; i < response.docs.length; i++) {
                                var doc = response.docs[i];
                                var task = {};
                                task.id = doc.object_id_s;
                                task.title = Acm.goodValue(response.docs[i].name); //title_t ?
                                task.created = Acm.getDateFromDatetime(doc.create_dt);
                                task.priority = Acm.goodValue(doc.priority_i);
                                task.dueDate = Acm.getDateFromDatetime(doc.due_dt);
                                task.status = Acm.goodValue(doc.status_s);
                                task.assignee = Acm.goodValue(doc.assignee_s);
                                taskList.push(task);
                            }
                            CaseFile.Model.Tasks.cacheTaskList.put(caseFileId, taskList);

                            jtData = callbackSuccess(taskList);

                        } else {
                            if (Acm.isNotEmpty(data.error)) {
                                //todo: report error to controller. data.error.msg + "(" + data.error.code + ")";
                            }
                        }
                    }

                    return jtData;
                }
            );
        }
    }



//    ,API_LIST_CASE_FILE         : "/api/latest/plugin/search/CASE_FILE"
//    ,API_RETRIEVE_PERSON_       : "/api/latest/plugin/person/find?assocId="
//    ,API_RETRIEVE_DETAIL        : "/api/latest/plugin/casefile/byId/"
//    ,API_SAVE_CASE_FILE         : "/api/latest/plugin/casefile/"
//    ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
//    ,API_UPLOAD_CASE_FILE_FILE  : "/api/latest/plugin/casefile/file"
//    ,API_RETRIEVE_TASKS         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="
//    ,API_CLOSE_CASE_FILE_       : "/api/latest/plugin/casefile/closeCase/"




};

