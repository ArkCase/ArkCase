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
    ,onInitialized: function() {
    }

    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_GET_ASSIGNEES             : "/api/latest/users/withPrivilege/acm-complaint-approve"
        ,API_GET_SUBJECT_TYPES         : "/api/latest/plugin/casefile/caseTypes"
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
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_CASE_FILE_LIST         : "/api/latest/plugin/search/CASE_FILE"

        ,retrieveCaseFileList: function(treeInfo){
            var start  = treeInfo.start;
            var n      = treeInfo.n;
            var sort   = treeInfo.sort;
            var filter = treeInfo.filter;
            var q      = treeInfo.q;

            var url = App.getContextPath() + this.API_RETRIEVE_CASE_FILE_LIST;
            if (0 <= treeInfo.start) {
                url += "?start=" + treeInfo.start;
            }
            if (0 < treeInfo.n) {
                url += "&n=" + treeInfo.n;
            }
            if (Acm.isNotEmpty(treeInfo.sort)) {
                url += "&s=" + treeInfo.sort;
//            } else {
//                url += "&s=name desc";
            }
            if (Acm.isNotEmpty(treeInfo.filter)) {
                url += "&filters=" + treeInfo.filter;
            }

            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCaseFileList(response);  //response has two properties: response.hasError, response.errorMsg

                    } else {
                        if (Acm.Validator.validateSolrData(response)) {
                            var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
                            treeInfo.total = response.response.numFound;

                            var caseFiles = response.response.docs;
                            var pageId = AcmEx.Model.Tree.Config.getPageId();
                            CaseFile.Model.List.cachePage.put(pageId, caseFiles);

                            var subKey = treeInfo.key;

                            var objId = 0;
                            var key;
                            if (null == subKey) {
                                if (0 < caseFiles.length) {
                                    objId = parseInt(caseFiles[0].object_id_s);
                                    if (0 < objId) {
                                        key = CaseFile.Model.Tree.Key.getKeyByObjWithPage(start, objId);
                                    }
                                }
                            } else {
                                key = CaseFile.Model.Tree.Key.getKeyBySubWithPage(pageId, subKey);
                                objId = AcmEx.Model.Tree.Key.getObjIdByKey(key);
                                treeInfo.key = null;
                            }

                            CaseFile.Model.setCaseFileId(objId);
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
        ,onInitialized: function() {
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

                            // save list of childObjects also in the cache for easier handling
                            if(caseFile.childObjects){
                                var documents = [];
                                for (var i = 0; i < caseFile.childObjects.length; i++) {
                                    var childObject = caseFile.childObjects[i];
                                    var document = {};
                                    document.id = childObject.targetId;
                                    document.name = childObject.targetName;
                                    document.created = childObject.created;
                                    document.creator = childObject.creator;
                                    document.status = childObject.status;
                                    document.targetType = childObject.targetType;
                                    document.targetSubtype = childObject.targetSubtype;
                                    document.category = childObject.category;
                                    documents.push(document);
                                }
                                CaseFile.Model.Documents.cacheDocuments.put(caseFileId, documents);
                            }


                            var treeInfo = AcmEx.Model.Tree.Config.getTreeInfo();
                            if (0 < treeInfo.objId) {      //handle single caseFile situation
                                treeInfo.total = 1;

                                var pageId = treeInfo.start;
                                var caseFilSolr = {};
                                caseFilSolr.author = caseFile.creator;
                                caseFilSolr.author_s = caseFile.creator;
                                caseFilSolr.create_tdt = caseFile.created;
                                caseFilSolr.last_modified_tdt = caseFile.modified;
                                caseFilSolr.modifier_s = caseFile.modifier;
                                caseFilSolr.name = caseFile.caseNumber;
                                caseFilSolr.object_id_s = caseFile.id;
                                caseFilSolr.object_type_s = CaseFile.Model.getObjectType();
                                caseFilSolr.owner_s = caseFile.creator;
                                caseFilSolr.status_s = caseFile.status;
                                caseFilSolr.title_parseable = caseFile.title;

                                var caseFiles = [caseFilSolr];
                                CaseFile.Model.List.cachePage.put(pageId, caseFiles);

                                var key = CaseFile.Model.Tree.Key.getKeyByObjWithPage(pageId, treeInfo.objId);
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
                        caseFile.personAssociations[i].personType = personAssociation.personType;
                        break;
                    }
                } //end for

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var savedPersonAssociation = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            for (var i = 0; i < data.personAssociations.length; i++) {
                                if (Acm.compare(data.personAssociations[i].id, personAssociation.id)) {
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
                                                CaseFile.Model.Detail.cacheCaseFile.put(caseFileId, caseFile);
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

        ,addSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    //ensure securityTag.id undefined?
                    securityTags.push(securityTag);
                }

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var addedSecurityTag = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                var securityTags = personAssociation.person.securityTags;
                                for (var i = 0; i < securityTags.length; i++) {
                                    if (Acm.compare(securityTags[i].type, securityTags.type)
                                        && Acm.compare(securityTags[i].value, securityTag.value)) {
                                        addedSecurityTag = securityTags[i];
                                        break;
                                    }
                                }
                            }
                        }
                        if (addedSecurityTag) {
                            CaseFile.Controller.modelAddedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedSecurityTag));
                        }
                    }
                );
            }
        }
        ,updateSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    for (var i = 0; i < securityTags.length; i++) {
                        if (Acm.compare(securityTags[i].id, securityTag.id)) {
                            securityTags[i].type = securityTags.type;
                            securityTags[i].value = securityTag.value;
                            break;
                        }
                    }

                    this.saveCaseFile(caseFile
                        ,function(data) {
                            var savedSecurityTag = null;
                            if (CaseFile.Model.Detail.validateData(data)) {
                                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                    var securityTags = personAssociation.person.securityTags;
                                    for (var i = 0; i < securityTags.length; i++) {
                                        if (Acm.compare(securityTags[i].id, securityTag.id)) {
                                            savedSecurityTag = securityTags[i];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (savedSecurityTag) {
                                CaseFile.Controller.modelUpdatedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedSecurityTag));
                            }
                        }
                    );
                }
            }
        }
        ,deleteSecurityTag: function(caseFileId, personAssociationId, securityTagId) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var toDelete = -1;
                    var securityTags = personAssociation.person.securityTags;
                    for (var i = 0; i < securityTags.length; i++) {
                        if (Acm.compare(securityTags[i].id, securityTagId)) {
                            toDelete = i;
                            break;
                        }
                    }

                    if (0 <= toDelete) {
                        securityTags.splice(toDelete, 1);
                        this.saveCaseFile(caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateData(data)) {
                                    CaseFile.Controller.modelDeletedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, securityTagId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    //ensure personAlias.id undefined?
                    personAliases.push(personAlias);
                }

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var addedPersonAlias = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                var personAliases = personAssociation.person.personAliases;
                                for (var i = 0; i < personAliases.length; i++) {
                                    if (Acm.compare(personAliases[i].aliasType, personAlias.aliasType)
                                        && Acm.compare(personAliases[i].aliasValue, personAlias.aliasValue)) {
                                        addedPersonAlias = personAliases[i];
                                        break;
                                    }
                                }
                            }
                        }
                        if (addedPersonAlias) {
                            CaseFile.Controller.modelAddedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedPersonAlias));
                        }
                    }
                );
            }
        }
        ,updatePersonAlias: function(caseFileId, personAssociationId, personAlias) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    for (var i = 0; i < personAliases.length; i++) {
                        if (Acm.compare(personAliases[i].id, personAlias.id)) {
                            personAliases[i].aliasType = personAlias.aliasType;
                            personAliases[i].aliasValue = personAlias.aliasValue;
                            break;
                        }
                    }

                    this.saveCaseFile(caseFile
                        ,function(data) {
                            var savedPersonAlias = null;
                            if (CaseFile.Model.Detail.validateData(data)) {
                                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                    var personAliases = personAssociation.person.personAliases;
                                    for (var i = 0; i < personAliases.length; i++) {
                                        if (Acm.compare(personAliases[i].id, personAlias.id)) {
                                            savedPersonAlias = personAliases[i];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (savedPersonAlias) {
                                CaseFile.Controller.modelUpdatedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedPersonAlias));
                            }
                        }
                    );
                }
            }
        }
        ,deletePersonAlias: function(caseFileId, personAssociationId, personAliasId) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var toDelete = -1;
                    var personAliases = personAssociation.person.personAliases;
                    for (var i = 0; i < personAliases.length; i++) {
                        if (Acm.compare(personAliases[i].id, personAliasId)) {
                            toDelete = i;
                            break;
                        }
                    }

                    if (0 <= toDelete) {
                        personAliases.splice(toDelete, 1);
                        this.saveCaseFile(caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateData(data)) {
                                    CaseFile.Controller.modelDeletedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, personAliasId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addAddress: function(caseFileId, personAssociationId, address) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var addresses = personAssociation.person.addresses;
                    //ensure address.id undefined?
                    addresses.push(address);
                }

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var addedAddress = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                var addresses = personAssociation.person.addresses;
                                for (var i = 0; i < addresses.length; i++) {
                                    if (Acm.compare(addresses[i].type, address.type)
                                        && Acm.compare(addresses[i].streetAddress, address.streetAddress)
                                        && Acm.compare(addresses[i].city         , address.city)
                                        && Acm.compare(addresses[i].state        , address.state)
                                        && Acm.compare(addresses[i].zip          , address.zip)
                                        && Acm.compare(addresses[i].country      , address.country)
                                        ) {
                                        addedAddress = addresses[i];
                                        break;
                                    }
                                }
                            }
                        }
                        if (addedAddress) {
                            CaseFile.Controller.modelAddedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedAddress));
                        }
                    }
                );
            }
        }
        ,updateAddress: function(caseFileId, personAssociationId, address) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var addresses = personAssociation.person.addresses;
                    for (var i = 0; i < addresses.length; i++) {
                        if (Acm.compare(addresses[i].id, address.id)) {
                            addresses[i].type = address.type;
                            addresses[i].streetAddress = address.streetAddress;
                            addresses[i].city = address.city;
                            addresses[i].state = address.state;
                            addresses[i].zip = address.zip;
                            addresses[i].country = address.country;
                            break;
                        }
                    }

                    this.saveCaseFile(caseFile
                        ,function(data) {
                            var savedAddress = null;
                            if (CaseFile.Model.Detail.validateData(data)) {
                                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                    var addresses = personAssociation.person.addresses;
                                    for (var i = 0; i < addresses.length; i++) {
                                        if (Acm.compare(addresses[i].id, address.id)) {
                                            savedAddress = addresses[i];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (savedAddress) {
                                CaseFile.Controller.modelUpdatedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedAddress));
                            }
                        }
                    );
                }
            }
        }
        ,deleteAddress: function(caseFileId, personAssociationId, addressId) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var toDelete = -1;
                    var addresses = personAssociation.person.addresses;
                    for (var i = 0; i < addresses.length; i++) {
                        if (Acm.compare(addresses[i].id, addressId)) {
                            toDelete = i;
                            break;
                        }
                    }

                    if (0 <= toDelete) {
                        addresses.splice(toDelete, 1);
                        this.saveCaseFile(caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateData(data)) {
                                    CaseFile.Controller.modelDeletedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addressId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addOrganization: function(caseFileId, personAssociationId, organization) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    //ensure organization.id undefined?
                    organizations.push(organization);
                }

                this.saveCaseFile(caseFile
                    ,function(data) {
                        var addedOrganization = null;
                        if (CaseFile.Model.Detail.validateData(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                var organizations = personAssociation.person.organizations;
                                for (var i = 0; i < organizations.length; i++) {
                                    if (Acm.compare(organizations[i].organizationType, organization.organizationType)
                                        && Acm.compare(organizations[i].organizationValue, organization.organizationValue)) {
                                        addedOrganization = organizations[i];
                                        break;
                                    }
                                }
                            }
                        }
                        if (addedOrganization) {
                            CaseFile.Controller.modelAddedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedOrganization));
                        }
                    }
                );
            }
        }
        ,updateOrganization: function(caseFileId, personAssociationId, organization) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    for (var i = 0; i < organizations.length; i++) {
                        if (Acm.compare(organizations[i].organizationId, organization.organizationId)) {
                            organizations[i].organizationType = organization.organizationType;
                            organizations[i].organizationValue = organization.organizationValue;
                            break;
                        }
                    }

                    this.saveCaseFile(caseFile
                        ,function(data) {
                            var savedOrganization = null;
                            if (CaseFile.Model.Detail.validateData(data)) {
                                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                                    var organizations = personAssociation.person.organizations;
                                    for (var i = 0; i < organizations.length; i++) {
                                        if (Acm.compare(organizations[i].organizationId, organization.organizationId)) {
                                            savedOrganization = organizations[i];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (savedOrganization) {
                                CaseFile.Controller.modelUpdatedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedOrganization));
                            }
                        }
                    );
                }
            }
        }
        ,deleteOrganization: function(caseFileId, personAssociationId, organizationId) {
            var caseFile = CaseFile.Model.Detail.getCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateData(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.Detail.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.Detail.validatePersonAssociation(personAssociation)) {
                    var toDelete = -1;
                    var organizations = personAssociation.person.organizations;
                    for (var i = 0; i < organizations.length; i++) {
                        if (Acm.compare(organizations[i].organizationId, organizationId)) {
                            toDelete = i;
                            break;
                        }
                    }

                    if (0 <= toDelete) {
                        organizations.splice(toDelete, 1);
                        this.saveCaseFile(caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateData(data)) {
                                    CaseFile.Controller.modelDeletedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, organizationId));
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
        ,onInitialized: function() {
        }

        ,API_DOWNLOAD_DOCUMENT_      : "/api/latest/plugin/ecm/download/byId/"
        ,API_UPLOAD_DOCUMENT: "/api/latest/plugin/casefile/file"

        ,_validateUploadInfo: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.files)) {
                return false;
            }
            if (!Acm.isArray(data.files)) {
                return false;
            }
            if (0 >= data.files.length) {
                return false;
            }
            return true;
        }
        ,uploadDocument: function(formData) {
            var url = App.getContextPath() + this.API_UPLOAD_DOCUMENT;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        CaseFile.Controller.modelAddedDocument(response);
                    } else {
                        if (CaseFile.Service.Documents._validateUploadInfo(response)) {
                            if(response!= null){
                                var uploadInfo = response;
                                var caseFileId = CaseFile.Model.getCaseFileId();
                                var prevAttachmentsList = CaseFile.Model.Documents.cacheDocuments.get(caseFileId);
                                for(var i = 0; i < response.files.length; i++){
                                    var attachment = {};
                                    attachment.id = response.files[i].id;
                                    attachment.name = response.files[i].name;
                                    attachment.status = response.files[i].status;
                                    attachment.creator = response.files[i].creator;
                                    attachment.created = response.files[i].created;
                                    attachment.targetSubtype = response.files[i].uploadFileType;
                                    attachment.targetType = CaseFile.Model.DOCUMENT_TARGET_TYPE_FILE;
                                    prevAttachmentsList.push(attachment);
                                    //attachment.category = response.files[i].category;
                                }
                                CaseFile.Model.Documents.cacheDocuments.put(caseFileId, prevAttachmentsList);
                                CaseFile.Controller.modelAddedDocument(uploadInfo);
                            }
                        }
                    }
                }
            });
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
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_TASKS_SOLR         : "/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId="
        ,API_RETRIEVE_TASKS    : "/api/latest/plugin/task/forUser/"
        ,API_COMPLETE_TASK         : "/api/latest/plugin/task/completeTask/"
        ,API_COMPLETE_TASK_WITH_OUTCOME         : "/api/latest/plugin/task/completeTask"

        ,retrieveTask : function() {
            var url = App.getContextPath() + this.API_RETRIEVE_TASKS + App.getUserName();
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedTasks(response);

                    } else {
                        //if (CaseFile.Model.Detail.validateData(response)) {
                            var tasks = response;
                            CaseFile.Model.Tasks.cacheTasks.put(0, tasks);
                            CaseFile.Controller.modelRetrievedTasks(response);
                        //}
                    }
                }
                ,url
            )
        }
        ,completeTask : function(taskId) {
            var url = App.getContextPath() + this.API_COMPLETE_TASK + taskId;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelCompletedTask(response);

                    } else {
                        //if (CaseFile.Model.Detail.validateData(response)) {
                        var task = response;
                        var caseFileId = CaseFile.Model.getCaseFileId();
                        var tasks = CaseFile.Model.Tasks.cacheTasks.get(0);
                        var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(caseFileId);
                        for(var i = 0; i < tasks.length; i++){
                            if(task.taskId ==  tasks[i].taskId){
                                tasks[i] = task;
                            }
                        }
                        for(var i = 0; i < taskList.length; i++){
                            if(task.taskId ==  taskList[i].id){
                                taskList[i].status = 'COMPLETE';
                            }
                        }
                        CaseFile.Model.Tasks.cacheTasks.put(0,tasks);
                        CaseFile.Model.Tasks.cacheTaskSolr.put(caseFileId,taskList);

                        // CaseFile.Model.Tasks.cacheTaskSolr.reset();
                        CaseFile.Controller.modelCompletedTask(response);
                        //}
                    }
                }
                ,url
                ,"{}"
            )
        }
        ,completeTaskWithOutcome : function(task) {
            var url = App.getContextPath() + this.API_COMPLETE_TASK_WITH_OUTCOME;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelCompletedTask(response);

                    } else {
                        //if (CaseFile.Model.Detail.validateData(response)) {
                        var task = response;
                        var caseFileId = CaseFile.Model.getCaseFileId();
                        var tasks = CaseFile.Model.Tasks.cacheTasks.get(0);
                        var taskList = CaseFile.Model.Tasks.cacheTaskSolr.get(caseFileId);
                        for(var i = 0; i < tasks.length; i++){
                            if(task.taskId ==  tasks[i].taskId){
                                tasks[i] = task;
                            }
                        }
                        for(var i = 0; i < taskList.length; i++){
                            if(task.taskId ==  taskList[i].id){
                                taskList[i].status = 'COMPLETE';
                            }
                        }
                        CaseFile.Model.Tasks.cacheTasks.put(0,tasks);
                        CaseFile.Model.Tasks.cacheTaskSolr.put(caseFileId,taskList);
                        CaseFile.Controller.modelCompletedTask(response);
                        //}
                    }
                }
                ,url
                ,JSON.stringify(task)
            )
        }
        ,retrieveTaskListDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFile.Service.Tasks.API_RETRIEVE_TASKS_SOLR;
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
                                task.title = Acm.goodValue(response.docs[i].name); //title_parseable ?? //title_t ?
                                task.created = Acm.getDateFromDatetime(doc.create_tdt);
                                task.priority = Acm.goodValue(doc.priority_s);
                                task.dueDate = Acm.getDateFromDatetime(doc.due_tdt); // from date_td to date_tdt
                                task.status = Acm.goodValue(doc.status_s);
                                task.assignee = Acm.goodValue(doc.assignee_s);
                                taskList.push(task);
                            }
                            CaseFile.Model.Tasks.cacheTaskSolr.put(caseFileId, taskList);

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



    ,Correspondence: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_CREATE_CORRESPONDENCE_      : "/api/latest/service/correspondence"

        ,_validateEcmFile: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.fileId)) {
                return false;
            }
            if (Acm.isEmpty(data.ecmFileId)) {
                return false;
            }
            if (!Acm.isArray(data.parentObjects)) {
                return false;
            }
            return true;
        }
        ,createCorrespondence : function(data, templateName) {
            var caseFileIn = data;
            var url = App.getContextPath() + this.API_CREATE_CORRESPONDENCE_
                + "?templateName=" + templateName
                + "&parentObjectType=" + CaseFile.Model.getObjectType()
                + "&parentObjectId=" + caseFileIn.id
                + "&parentObjectName=" + caseFileIn.caseNumber
                + "&targetCmisFolderId=" + caseFileIn.ecmFolderId
                ;

            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelCreatedCorrespondence(response);

                    } else {
                        if (CaseFile.Service.Correspondence._validateEcmFile(response)) {
                            var ecmFile = response;
                            var caseFileId = caseFileIn.id;

                            var caseFile = CaseFile.Model.Detail.cacheCaseFile.get(caseFileId);
                            if(CaseFile.Model.Detail.validateData(caseFile)){
                                var childObject = {};
                                childObject.targetId = ecmFile.fileId;
                                childObject.targetName = ecmFile.fileName;
                                childObject.created = ecmFile.creator;
                                childObject.creator = ecmFile.created;
                                childObject.modified = ecmFile.modified;
                                childObject.modifier = ecmFile.modifier;
                                childObject.status = ecmFile.status;
                                childObject.targetType = CaseFile.Model.DOCUMENT_TARGET_TYPE_FILE;
                                childObject.category = CaseFile.Model.DOCUMENT_CATEGORY_CORRESPONDENCE;

                                caseFile.childObjects.push(childObject);
                                CaseFile.Model.Detail.cacheCaseFile.put(caseFileId, caseFile);
                            }

                            var documents = CaseFile.Model.Documents.cacheDocuments.get(caseFileId);
                            var document = {};
                            document.id = ecmFile.fileId;
                            document.name = ecmFile.fileName;
                            document.status = ecmFile.status;
                            document.creator = ecmFile.creator;
                            document.created = ecmFile.created;
                            document.targetType = CaseFile.Model.DOCUMENT_TARGET_TYPE_FILE;
                            document.category = CaseFile.Model.DOCUMENT_CATEGORY_CORRESPONDENCE;
                            documents.push(document);
                            CaseFile.Model.Documents.cacheDocuments.put(caseFileId, documents);


                            CaseFile.Controller.modelCreatedCorrespondence(caseFileId);
                        }
                    }
                }
                ,url
            )
        }
    }

};

