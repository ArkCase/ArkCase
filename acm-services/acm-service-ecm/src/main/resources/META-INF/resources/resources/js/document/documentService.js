/**
 * Document.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Document.Service = {
    create : function() {
        if (Document.Service.Lookup.create) {Document.Service.Lookup.create();}
        if (Document.Service.Detail.create) {Document.Service.Detail.create();}
        if (Document.Service.People.create) {Document.Service.People.create();}
        if (Document.Service.Documents.create) {Document.Service.Documents.create();}
        if (Document.Service.Notes.create) {Document.Service.Notes.create();}
        if (Document.Service.Tasks.create) {Document.Service.Tasks.create();}
        if (Document.Service.Correspondence.create) {Document.Service.Correspondence.create();}
    }
    ,onInitialized: function() {
        if (Document.Service.Lookup.onInitialized) {Document.Service.Lookup.onInitialized();}
        if (Document.Service.Detail.onInitialized) {Document.Service.Detail.onInitialized();}
        if (Document.Service.People.onInitialized) {Document.Service.People.onInitialized();}
        if (Document.Service.Documents.onInitialized) {Document.Service.Documents.onInitialized();}
        if (Document.Service.Notes.onInitialized) {Document.Service.Notes.onInitialized();}
        if (Document.Service.Tasks.onInitialized) {Document.Service.Tasks.onInitialized();}
        if (Document.Service.Correspondence.onInitialized) {Document.Service.Correspondence.onInitialized();}
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
                        Document.Controller.modelFoundAssignees(response);

                    } else {
                        if (Document.Service.Lookup._validateAssignees(response)) {
                            var assignees = response;
                            Document.Model.Lookup.setAssignees(assignees);
                            Document.Controller.modelFoundAssignees(assignees);
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
                        Document.Controller.modelFoundSubjectTypes(response);

                    } else {
                        if (Document.Service.Lookup._validateSubjectTypes(response)) {
                            var subjectTypes = response;
                            Document.Model.Lookup.setSubjectTypes(subjectTypes);
                            Document.Controller.modelFoundSubjectTypes(subjectTypes);
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
                        Document.Controller.modelFoundPriorities(response);

                    } else {
                        if (Document.Service.Lookup._validatePriorities(response)) {
                            var priorities = response;
                            Document.Model.Lookup.setPriorities(priorities);
                            Document.Controller.modelFoundPriorities(priorities);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_PRIORITIES
            )
        }
    }

    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,_saveDocument: function(caseFileId, caseFile, handler) {
            ObjNav.Service.Detail.saveObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile, handler);
        }
        ,saveCaseTitle: function(caseFileId, title) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.title = title;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedCaseTitle(caseFileId, Acm.Service.responseWrapper(data, data.title));
                    }
                );
            }
        }
        ,saveIncidentDate: function(caseFileId, incidentDate) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.incidentDate = incidentDate;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedIncidentDate(caseFileId, Acm.Service.responseWrapper(data, data.incidentDate));
                    }
                );
            }
        }
        ,saveAssignee: function(caseFileId, assignee) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                Document.Model.Detail.setAssignee(caseFile, assignee);
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedAssignee(caseFileId, Acm.Service.responseWrapper(data, assignee));
                    }
                );
            }
        }
        ,saveSubjectType: function(caseFileId, caseType) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.caseType = caseType;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedSubjectType(caseFileId, Acm.Service.responseWrapper(data, data.caseType));
                    }
                );
            }
        }
        ,savePriority: function(caseFileId, priority) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.priority = priority;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedPriority(caseFileId, Acm.Service.responseWrapper(data, data.priority));
                    }
                );
            }
        }
        ,saveDueDate: function(caseFileId, dueDate) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.dueDate = dueDate;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedDueDate(caseFileId, Acm.Service.responseWrapper(data, data.dueDate));
                    }
                );
            }
        }
        ,saveDetail: function(caseFileId, details) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.details = details;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedDetail(caseFileId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }

        ,updateCaseRestriction: function(caseFileId, restriction) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.restricted = restriction;
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        Document.Controller.modelSavedRestriction(caseFileId, Acm.Service.responseWrapper(data, data.restricted));
                    }
                );
            }
        }


//        ,closeDocument : function(data) {
//            var caseFileId = Document.getDocumentId();
//            Acm.Ajax.asyncPost(App.getContextPath() + this.API_CLOSE_CASE_FILE_ + caseFileId
//                ,JSON.stringify(data)
//                ,Document.Callback.EVENT_CASEFILE_CLOSED
//            );
//        }
//        ,consolidateCase: function(data) {
//        }
    }

    ,People: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_SAVE_PERSON_ASSOCIATION       : "/api/latest/plugin/personAssociation"
        ,API_DELETE_PERSON_ASSOCIATION_    : "/api/latest/plugin/personAssociation/delete/"


        //-------------------------

        ,_saveDocument: function(caseFileId, caseFile, handler) {
            ObjNav.Service.Detail.saveObject(Document.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile, handler);
        }
        ,saveChildObject: function(caseFileId, childObject) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                for (var i = 0; i < caseFile.childObjects.length; i++) {
                    if (Acm.compare(caseFile.childObjects[i].targetId, childObject.targetid)) {
                        caseFile.childObjects[i].title  = childObject.title;
                        caseFile.childObjects[i].status = childObject.status;
                        this._saveDocument(caseFileId, caseFile
                            ,function(data) {
                                var savedChildObject = null;
                                if (Document.Model.Detail.validateDocument(data)) {
                                    for (var i = 0; i < data.childObjects.length; i++) {
                                        if (Acm.compare(data.childObjects[i].targetId, childObject.targetid)) {
                                            savedChildObject = data.childObjects[i];
                                            break;
                                        }
                                    }
                                }
                                Document.Controller.modelSavedChildObject(caseFileId, Acm.Service.responseWrapper(data, savedChildObject));
                            }
                        );
                    }
                }
            }
        }
        ,addParticipant: function(caseFileId, participant) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                caseFile.participants.push(participant);
                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var addedParticipant = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            for (var i = 0; i < data.participants.length; i++) {
                                if (Acm.compare(data.participants[i].participantLdapId, participant.participantLdapId)
                                    && Acm.compare(data.participants[i].participantType, participant.participantType)) {
                                    addedParticipant = data.participants[i];
                                    break;
                                }
                            }
                        }
                        if (addedParticipant) {
                            Document.Controller.modelAddedParticipant(caseFileId, Acm.Service.responseWrapper(data, addedParticipant));
                        }
                    }
                );
            }
        }
        ,updateParticipant: function(caseFileId, participant) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                for (var i = 0; i < caseFile.participants.length; i++) {
                    if (Acm.compare(caseFile.participants[i].id, participant.id)) {
                        caseFile.participants[i].participantLdapId  = participant.participantLdapId;
                        caseFile.participants[i].participantType = participant.participantType;
                        break;
                    }
                } //end for

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var savedParticipant = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            for (var i = 0; i < data.participants.length; i++) {
                                if (Acm.compare(data.participants[i].id, participant.id)) {
                                    savedParticipant = data.participants[i];
                                    break;
                                }
                            }
                        }
                        if (savedParticipant) {
                            Document.Controller.modelUpdatedParticipant(caseFileId, Acm.Service.responseWrapper(data, savedParticipant));
                        }
                    }
                );
            }
        }
        ,deleteParticipant: function(caseFileId, participantId) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var toDelete = -1;
                for (var i = 0; i < caseFile.participants.length; i++) {
                    if (Acm.compare(caseFile.participants[i].id, participantId)) {
                        toDelete = i;
                        break;
                    }
                }

                if (0 <= toDelete) {
                    caseFile.participants.splice(toDelete, 1);
                    this._saveDocument(caseFileId, caseFile
                        ,function(data) {
                            if (Document.Model.Detail.validateDocument(data)) {
                                Document.Controller.modelDeletedParticipant(caseFileId, Acm.Service.responseWrapper(data, participantId));
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
                        Document.Controller.modelAddedPersonAssociation(caseFileId, response);

                    } else {
                        if (Document.Model.People.validatePersonAssociation(response)) {
                            //check caseFileId == personAssociation.parentId;
                            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
                            if (Document.Model.Detail.validateDocument(caseFile)) {
                                //check response.parentId == caseFileId
                                //check response.id not null, > 0
                                //check response.id not already in caseFile.personAssociations array
                                var addedPersonAssociation = response;
                                caseFile.personAssociations.push(addedPersonAssociation);
                                //Document.Model.Detail.cacheDocument.put(caseFileId, caseFile);
                                Document.Controller.modelAddedPersonAssociation(caseFileId, addedPersonAssociation);
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_PERSON_ASSOCIATION
                ,JSON.stringify(personAssociation)
            )
        }
        ,updatePersonAssociation: function(caseFileId, personAssociation) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                for (var i = 0; i < caseFile.personAssociations.length; i++) {
                    if (Acm.compare(caseFile.personAssociations[i].id, personAssociation.id)) {
                        caseFile.personAssociations[i].person.title  =  personAssociation.person.title;
                        caseFile.personAssociations[i].person.givenName = personAssociation.person.givenName;
                        caseFile.personAssociations[i].person.familyName = personAssociation.person.familyName;
                        caseFile.personAssociations[i].personType = personAssociation.personType;
                        break;
                    }
                } //end for

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var savedPersonAssociation = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            for (var i = 0; i < data.personAssociations.length; i++) {
                                if (Acm.compare(data.personAssociations[i].id, personAssociation.id)) {
                                    savedPersonAssociation = data.personAssociations[i];
                                    break;
                                }
                            }
                        }
                        if (savedPersonAssociation) {
                            Document.Controller.modelUpdatedPersonAssociation(caseFileId, Acm.Service.responseWrapper(data, savedPersonAssociation));
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
                        Document.Controller.modelDeletedPersonAssociation(response);

                    } else {
                        if (Document.Service.People._validateDeletedPersonAssociation(response)) {
                            if (response.deletedPersonAssociationId == personAssociationId) {
                                var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
                                if (Document.Model.Detail.validateDocument(caseFile)) {
                                    for (var i = 0; i < caseFile.personAssociations.length; i++) {
                                        var pa = caseFile.personAssociations[i];
                                        if (Document.Model.People.validatePersonAssociation(pa)) {
                                            if (pa.id == response.deletedPersonAssociationId) {
                                                caseFile.personAssociations.splice(i, 1);
                                                //Document.Model.Detail.cacheDocument.put(caseFileId, caseFile);
                                                Document.Controller.modelDeletedPersonAssociation(Acm.Service.responseWrapper(response, personAssociationId));
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
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    //ensure contactMethod.id undefined?
                    contactMethods.push(contactMethod);
                }

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var addedContactMethod = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Document.Controller.modelAddedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedContactMethod));
                        }
                    }
                );
            }
        }
        ,updateContactMethod: function(caseFileId, personAssociationId, contactMethod) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    for (var i = 0; i < contactMethods.length; i++) {
                        if (Acm.compare(contactMethods[i].id, contactMethod.id)) {
                            contactMethods[i].type = contactMethod.type;
                            contactMethods[i].value = contactMethod.value;
                            break;
                        }
                    }

                    this._saveDocument(caseFileId, caseFile
                        ,function(data) {
                            var savedContactMethod = null;
                            if (Document.Model.Detail.validateDocument(data)) {
                                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Document.Controller.modelUpdatedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedContactMethod));
                            }
                        }
                    );
                }
            }
        }
        ,deleteContactMethod: function(caseFileId, personAssociationId, contactMethodId) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveDocument(caseFileId, caseFile
                            ,function(data) {
                                if (Document.Model.Detail.validateDocument(data)) {
                                    Document.Controller.modelDeletedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, contactMethodId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    //ensure securityTag.id undefined?
                    securityTags.push(securityTag);
                }

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var addedSecurityTag = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Document.Controller.modelAddedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedSecurityTag));
                        }
                    }
                );
            }
        }
        ,updateSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    for (var i = 0; i < securityTags.length; i++) {
                        if (Acm.compare(securityTags[i].id, securityTag.id)) {
                            securityTags[i].type = securityTags.type;
                            securityTags[i].value = securityTag.value;
                            break;
                        }
                    }

                    this._saveDocument(caseFileId, caseFile
                        ,function(data) {
                            var savedSecurityTag = null;
                            if (Document.Model.Detail.validateDocument(data)) {
                                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Document.Controller.modelUpdatedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedSecurityTag));
                            }
                        }
                    );
                }
            }
        }
        ,deleteSecurityTag: function(caseFileId, personAssociationId, securityTagId) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveDocument(caseFileId, caseFile
                            ,function(data) {
                                if (Document.Model.Detail.validateDocument(data)) {
                                    Document.Controller.modelDeletedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, securityTagId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    //ensure personAlias.id undefined?
                    personAliases.push(personAlias);
                }

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var addedPersonAlias = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Document.Controller.modelAddedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedPersonAlias));
                        }
                    }
                );
            }
        }
        ,updatePersonAlias: function(caseFileId, personAssociationId, personAlias) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    for (var i = 0; i < personAliases.length; i++) {
                        if (Acm.compare(personAliases[i].id, personAlias.id)) {
                            personAliases[i].aliasType = personAlias.aliasType;
                            personAliases[i].aliasValue = personAlias.aliasValue;
                            break;
                        }
                    }

                    this._saveDocument(caseFileId, caseFile
                        ,function(data) {
                            var savedPersonAlias = null;
                            if (Document.Model.Detail.validateDocument(data)) {
                                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Document.Controller.modelUpdatedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedPersonAlias));
                            }
                        }
                    );
                }
            }
        }
        ,deletePersonAlias: function(caseFileId, personAssociationId, personAliasId) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveDocument(caseFileId, caseFile
                            ,function(data) {
                                if (Document.Model.Detail.validateDocument(data)) {
                                    Document.Controller.modelDeletedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, personAliasId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addAddress: function(caseFileId, personAssociationId, address) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var addresses = personAssociation.person.addresses;
                    //ensure address.id undefined?
                    addresses.push(address);
                }

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var addedAddress = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Document.Controller.modelAddedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedAddress));
                        }
                    }
                );
            }
        }
        ,updateAddress: function(caseFileId, personAssociationId, address) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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

                    this._saveDocument(caseFileId, caseFile
                        ,function(data) {
                            var savedAddress = null;
                            if (Document.Model.Detail.validateDocument(data)) {
                                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Document.Controller.modelUpdatedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedAddress));
                            }
                        }
                    );
                }
            }
        }
        ,deleteAddress: function(caseFileId, personAssociationId, addressId) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveDocument(caseFileId, caseFile
                            ,function(data) {
                                if (Document.Model.Detail.validateDocument(data)) {
                                    Document.Controller.modelDeletedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addressId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addOrganization: function(caseFileId, personAssociationId, organization) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    //ensure organization.id undefined?
                    organizations.push(organization);
                }

                this._saveDocument(caseFileId, caseFile
                    ,function(data) {
                        var addedOrganization = null;
                        if (Document.Model.Detail.validateDocument(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Document.Controller.modelAddedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addedOrganization));
                        }
                    }
                );
            }
        }
        ,updateOrganization: function(caseFileId, personAssociationId, organization) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    for (var i = 0; i < organizations.length; i++) {
                        if (Acm.compare(organizations[i].organizationId, organization.organizationId)) {
                            organizations[i].organizationType = organization.organizationType;
                            organizations[i].organizationValue = organization.organizationValue;
                            break;
                        }
                    }

                    this._saveDocument(caseFileId, caseFile
                        ,function(data) {
                            var savedOrganization = null;
                            if (Document.Model.Detail.validateDocument(data)) {
                                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Document.Controller.modelUpdatedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, savedOrganization));
                            }
                        }
                    );
                }
            }
        }
        ,deleteOrganization: function(caseFileId, personAssociationId, organizationId) {
            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
            if (Document.Model.Detail.validateDocument(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = Document.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Document.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveDocument(caseFileId, caseFile
                            ,function(data) {
                                if (Document.Model.Detail.validateDocument(data)) {
                                    Document.Controller.modelDeletedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, organizationId));
                                }
                            }
                        );
                    }
                }
            }
        }
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
            if (Acm.isNotArray(data)) {
                return false;
            }
            if (0 >= data.length) {
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
                        Document.Controller.modelAddedDocument(response);
                    } else {
                        if (Document.Service.Documents._validateUploadInfo(response)) {
                            if(response!= null){
                                var uploadInfo = response;
                                //var caseFileId = Document.Model.getDocumentId();
                                /*var prevAttachmentsList = Document.Model.Documents.cacheDocuments.get(caseFileId);
                                for(var i = 0; i < response.files.length; i++){
                                    var attachment = {};
                                    attachment.id = response.files[i].id;
                                    attachment.name = response.files[i].name;
                                    attachment.status = response.files[i].status;
                                    attachment.creator = response.files[i].creator;
                                    attachment.created = response.files[i].created;
                                    attachment.targetSubtype = response.files[i].uploadFileType;
                                    attachment.targetType = Document.Model.DOCUMENT_TARGET_TYPE_FILE;
                                    prevAttachmentsList.push(attachment);
                                    //attachment.category = response.files[i].category;
                                }
                                Document.Model.Documents.cacheDocuments.put(caseFileId, prevAttachmentsList);*/
                                Document.Controller.modelAddedDocument(uploadInfo);
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
                    url =  App.getContextPath() + Document.Service.Notes.API_LIST_NOTES_ + Document.Model.DOC_TYPE_CASE_FILE + "/";
                    url += caseFileId;
                    return url;
                }
                ,function(data) {
                    var jtData = null
                    if (Document.Model.Notes.validateNotes(data)) {
                        var noteList = data;


                        Document.Model.Notes.cacheNoteList.put(caseFileId, noteList);
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
                            Document.Controller.modelSavedNote(response);
                        }

                    } else {
                        if (Document.Model.Notes.validateNote(response)) {
                            var note = response;
                            var caseFileId = Document.Model.getDocumentId();
                            if (caseFileId == note.parentId) {
                                var noteList = Document.Model.Notes.cacheNoteList.get(caseFileId);
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
                                    Document.Controller.modelSavedNote(note);
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
            if (Document.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        Document.Controller.modelAddedNote(data);
                    }
                );
            }
        }
        ,updateNote: function(note) {
            if (Document.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        Document.Controller.modelUpdatedNote(data);
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
                        Document.Controller.modelDeletedNote(response);

                    } else {
                        if (Document.Service.Notes._validateDeletedNote(response)) {
                            var caseFileId = Document.Model.getDocumentId();
                            if (response.deletedNoteId == noteId) {
                                var noteList = Document.Model.Notes.cacheNoteList.get(caseFileId);
                                for (var i = 0; i < noteList.length; i++) {
                                    if (noteId == noteList[i].id) {
                                        noteList.splice(i, 1);
                                        Document.Controller.modelDeletedNote(Acm.Service.responseWrapper(response, noteId));
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
                        Document.Controller.modelRetrievedTasks(response);

                    } else {
                        //if (Document.Model.Detail.validateDocument(response)) {
                            var tasks = response;
                            Document.Model.Tasks.cacheTasks.put(0, tasks);
                            Document.Controller.modelRetrievedTasks(response);
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
                        Document.Controller.modelCompletedTask(response);

                    } else {
                        //if (Document.Model.Detail.validateDocument(response)) {
                        var task = response;
                        var caseFileId = Document.Model.getDocumentId();
                        var tasks = Document.Model.Tasks.cacheTasks.get(0);
                        var taskList = Document.Model.Tasks.cacheTaskSolr.get(caseFileId);
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
                        Document.Model.Tasks.cacheTasks.put(0,tasks);
                        Document.Model.Tasks.cacheTaskSolr.put(caseFileId,taskList);

                        // Document.Model.Tasks.cacheTaskSolr.reset();
                        Document.Controller.modelCompletedTask(response);
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
                        Document.Controller.modelCompletedTask(response);

                    } else {
                        //if (Document.Model.Detail.validateDocument(response)) {
                        var task = response;
                        var caseFileId = Document.Model.getDocumentId();
                        var tasks = Document.Model.Tasks.cacheTasks.get(0);
                        var taskList = Document.Model.Tasks.cacheTaskSolr.get(caseFileId);
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
                        Document.Model.Tasks.cacheTasks.put(0,tasks);
                        Document.Model.Tasks.cacheTaskSolr.put(caseFileId,taskList);
                        Document.Controller.modelCompletedTask(response);
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
                    url =  App.getContextPath() + Document.Service.Tasks.API_RETRIEVE_TASKS_SOLR;
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
                            Document.Model.Tasks.cacheTaskSolr.put(caseFileId, taskList);

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
                + "&parentObjectType=" + Document.Model.DOC_TYPE_CASE_FILE
                + "&parentObjectId=" + caseFileIn.id
                + "&targetCmisFolderId=" + caseFileIn.ecmFolderId
                ;

            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Document.Controller.modelCreatedCorrespondence(response);

                    } else {
                        if (Document.Service.Correspondence._validateEcmFile(response)) {
                            var ecmFile = response;
                            var caseFileId = caseFileIn.id;

                            var caseFile = Document.Model.Detail.getCacheDocument(caseFileId);
                            if(Document.Model.Detail.validateDocument(caseFile)){
                                var childObject = {};
                                childObject.targetId = ecmFile.fileId;
                                childObject.targetName = ecmFile.fileName;
                                childObject.created = ecmFile.created;
                                childObject.creator = ecmFile.creator;
                                childObject.modified = ecmFile.modified;
                                childObject.modifier = ecmFile.modifier;
                                childObject.status = ecmFile.status;
                                childObject.targetType = Document.Model.DOC_TYPE_FILE;
                                childObject.category = Document.Model.DOC_CATEGORY_CORRESPONDENCE;

                                caseFile.childObjects.push(childObject);
                                //Document.Model.Detail.cacheDocument.put(caseFileId, caseFile);
                            }

                            Document.Controller.modelCreatedCorrespondence(caseFileId);
                        }
                    }
                }
                ,url
            )
        }
    }

};

