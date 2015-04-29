/**
 * CaseFile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
CaseFile.Service = {
    create : function() {
        if (CaseFile.Service.Lookup.create) {CaseFile.Service.Lookup.create();}
        if (CaseFile.Service.Detail.create) {CaseFile.Service.Detail.create();}
        if (CaseFile.Service.People.create) {CaseFile.Service.People.create();}
        //if (CaseFile.Service.Documents.create) {CaseFile.Service.Documents.create();}
        if (CaseFile.Service.Notes.create) {CaseFile.Service.Notes.create();}
        if (CaseFile.Service.Tasks.create) {CaseFile.Service.Tasks.create();}
        if (CaseFile.Service.Correspondence.create) {CaseFile.Service.Correspondence.create();}
        if (CaseFile.Service.History.create) {CaseFile.Service.History.create();}
        if (CaseFile.Service.OutlookCalendar.create)   {CaseFile.Service.OutlookCalendar.create();}
        if (CaseFile.Service.Time.create)   {CaseFile.Service.Time.create();}
        if (CaseFile.Service.Cost.create)   {CaseFile.Service.Cost.create();}
    }
    ,onInitialized: function() {
        if (CaseFile.Service.Lookup.onInitialized) {CaseFile.Service.Lookup.onInitialized();}
        if (CaseFile.Service.Detail.onInitialized) {CaseFile.Service.Detail.onInitialized();}
        if (CaseFile.Service.People.onInitialized) {CaseFile.Service.People.onInitialized();}
        //if (CaseFile.Service.Documents.onInitialized) {CaseFile.Service.Documents.onInitialized();}
        if (CaseFile.Service.Notes.onInitialized) {CaseFile.Service.Notes.onInitialized();}
        if (CaseFile.Service.Tasks.onInitialized) {CaseFile.Service.Tasks.onInitialized();}
        if (CaseFile.Service.Correspondence.onInitialized) {CaseFile.Service.Correspondence.onInitialized();}
        if (CaseFile.Service.History.onInitialized)        {CaseFile.Service.History.onInitialized();}
        if (CaseFile.Service.OutlookCalendar.onInitialized)        {CaseFile.Service.OutlookCalendar.onInitialized();}
        if (CaseFile.Service.Time.onInitialized)        {CaseFile.Service.Time.onInitialized();}
        if (CaseFile.Service.Cost.onInitialized)        {CaseFile.Service.Cost.onInitialized();}
    }

    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_GET_ASSIGNEES             : "/api/latest/service/functionalaccess/users/acm-case-approve"
        ,API_GET_SUBJECT_TYPES         : "/api/latest/plugin/casefile/caseTypes"
        ,API_GET_PRIORITIES            : "/api/latest/plugin/complaint/priorities"
        ,API_GET_GROUPS				   : "/api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
        ,API_GET_USERS				   : "/api/latest/plugin/search/USER?n=1000&s=name asc"

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
        	var caseFile = CaseFile.View.getActiveCaseFile();
        	if (caseFile == null) {
        		return null;
        	}
        	
        	var groupGetParameter = CaseFile.Service.Lookup._createGroupGetParameter(caseFile);
        	var currentAssigneeGetParameter = CaseFile.Service.Lookup._createCurrentAssigneeGetParameter(caseFile);
        	
        	if (currentAssigneeGetParameter !== '' && groupGetParameter === '') {
        		// only if current assignee is not empty but the group is empty, then add /* for group, to be able to
        		// rich the required request method and return all users
        		groupGetParameter = '/*'
        	}
        	
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelFoundAssignees(response);

                    } else {
                        if (CaseFile.Service.Lookup._validateAssignees(response)) {
                            var assignees = response;
                            CaseFile.Model.Lookup.setAssignees(CaseFile.View.getActiveCaseFileId(), assignees);
                            CaseFile.Controller.modelFoundAssignees(assignees);
                        }
                        return assignees;
                    }
                }
                ,App.getContextPath() + this.API_GET_ASSIGNEES + groupGetParameter + currentAssigneeGetParameter
            )
        }
        
        ,_createGroupGetParameter: function(caseFile){
        	var groupGetParameter = '';
        	var groupName = CaseFile.Model.Detail.getGroup(caseFile);
        	if (groupName && groupName.length > 0) {
        		groupGetParameter = '/' + groupName;
        	}
        	
        	return groupGetParameter;
        }
        ,_createCurrentAssigneeGetParameter: function(caseFile){
        	var currentAssigneeGetParameter = '';
        	var currentAssignee = CaseFile.Model.Detail.getAssignee(caseFile);
        	if (currentAssignee && currentAssignee.length > 0) {
        		currentAssigneeGetParameter = '/' + currentAssignee;
        	}
        	
        	return currentAssigneeGetParameter;
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
                        CaseFile.Controller.modelFoundSubjectTypes(response);

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
                        CaseFile.Controller.modelFoundPriorities(response);

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
        
        ,retrieveGroups : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedGroups(response);
                    } else {
                        if (response.response && response.response.docs && CaseFile.Service.Lookup._validateGroups(response.response.docs)) {
                            var groups = response.response.docs;
                            CaseFile.Model.Lookup.setGroups(CaseFile.View.getActiveCaseFileId(), groups);
                            CaseFile.Controller.modelRetrievedGroups(groups);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_GROUPS
            )
        }
        
        ,_validateGroups: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        
        ,retrieveUsers : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedUsers(response);
                    } else {
                        if (response.response && response.response.docs && CaseFile.Service.Lookup._validateUsers(response.response.docs)) {
                            var users = response.response.docs;
                            CaseFile.Model.Lookup.setUsers(users);
                            CaseFile.Controller.modelRetrievedUsers(users);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_USERS
            )
        }
        
        ,_validateUsers: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
    }

    ,Detail: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,_saveCaseFile: function(caseFileId, caseFile, handler) {
            ObjNav.Service.Detail.saveObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile, handler);
        }
        ,saveCaseTitle: function(caseFileId, title) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.title = title;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedCaseTitle(caseFileId, Acm.Service.responseWrapper(data, data.title));
                    }
                );
            }
        }
        ,saveIncidentDate: function(caseFileId, incidentDate) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.incidentDate = incidentDate;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedIncidentDate(caseFileId, Acm.Service.responseWrapper(data, data.incidentDate));
                    }
                );
            }
        }
        ,saveAssignee: function(caseFileId, assignee) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                CaseFile.Model.Detail.setAssignee(caseFile, assignee);
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedAssignee(caseFileId, Acm.Service.responseWrapper(data, assignee));
                    }
                );
            }
        }
        ,saveGroup: function(caseFileId, group) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                CaseFile.Model.Detail.setGroup(caseFile, group);
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedGroup(caseFileId, Acm.Service.responseWrapper(data, group));
                    }
                );
            }
        }
        ,saveSubjectType: function(caseFileId, caseType) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.caseType = caseType;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedSubjectType(caseFileId, Acm.Service.responseWrapper(data, data.caseType));
                    }
                );
            }
        }
        ,savePriority: function(caseFileId, priority) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.priority = priority;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedPriority(caseFileId, Acm.Service.responseWrapper(data, data.priority));
                    }
                );
            }
        }
        ,saveDueDate: function(caseFileId, dueDate) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.dueDate = dueDate;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedDueDate(caseFileId, Acm.Service.responseWrapper(data, data.dueDate));
                    }
                );
            }
        }
        ,saveDetail: function(caseFileId, details) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.details = details;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedDetail(caseFileId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }

        ,updateCaseRestriction: function(caseFileId, restriction) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.restricted = restriction;
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        CaseFile.Controller.modelSavedRestriction(caseFileId, Acm.Service.responseWrapper(data, data.restricted));
                    }
                );
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

    ,People: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_SAVE_PERSON_ASSOCIATION       : "/api/latest/plugin/personAssociation"
        ,API_DELETE_PERSON_ASSOCIATION_    : "/api/latest/plugin/personAssociation/delete/"


        //-------------------------

        ,_saveCaseFile: function(caseFileId, caseFile, handler) {
            ObjNav.Service.Detail.saveObject(CaseFile.Model.DOC_TYPE_CASE_FILE, caseFileId, caseFile, handler);
        }
        ,saveChildObject: function(caseFileId, childObject) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                for (var i = 0; i < caseFile.childObjects.length; i++) {
                    if (Acm.compare(caseFile.childObjects[i].targetId, childObject.targetid)) {
                        caseFile.childObjects[i].title  = childObject.title;
                        caseFile.childObjects[i].status = childObject.status;
                        this._saveCaseFile(caseFileId, caseFile
                            ,function(data) {
                                var savedChildObject = null;
                                if (CaseFile.Model.Detail.validateCaseFile(data)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                caseFile.participants.push(participant);
                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var addedParticipant = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                for (var i = 0; i < caseFile.participants.length; i++) {
                    if (Acm.compare(caseFile.participants[i].id, participant.id)) {
                        caseFile.participants[i].participantLdapId  = participant.participantLdapId;
                        caseFile.participants[i].participantType = participant.participantType;
                        break;
                    }
                } //end for

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var savedParticipant = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
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
                            if (CaseFile.Model.Detail.validateCaseFile(data)) {
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
                        if (CaseFile.Model.People.validatePersonAssociation(response)) {
                            //check caseFileId == personAssociation.parentId;
                            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
                            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                                //check response.parentId == caseFileId
                                //check response.id not null, > 0
                                //check response.id not already in caseFile.personAssociations array
                                var addedPersonAssociation = response;
                                caseFile.personAssociations.push(addedPersonAssociation);
                                //CaseFile.Model.Detail.cacheCaseFile.put(caseFileId, caseFile);
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                for (var i = 0; i < caseFile.personAssociations.length; i++) {
                    if (Acm.compare(caseFile.personAssociations[i].id, personAssociation.id)) {
                        caseFile.personAssociations[i].person.title  =  personAssociation.person.title;
                        caseFile.personAssociations[i].person.givenName = personAssociation.person.givenName;
                        caseFile.personAssociations[i].person.familyName = personAssociation.person.familyName;
                        caseFile.personAssociations[i].personType = personAssociation.personType;
                        break;
                    }
                } //end for

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var savedPersonAssociation = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
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
                        if (CaseFile.Service.People._validateDeletedPersonAssociation(response)) {
                            if (response.deletedPersonAssociationId == personAssociationId) {
                                var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
                                if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                                    for (var i = 0; i < caseFile.personAssociations.length; i++) {
                                        var pa = caseFile.personAssociations[i];
                                        if (CaseFile.Model.People.validatePersonAssociation(pa)) {
                                            if (pa.id == response.deletedPersonAssociationId) {
                                                caseFile.personAssociations.splice(i, 1);
                                                //CaseFile.Model.Detail.cacheCaseFile.put(caseFileId, caseFile);
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    //ensure contactMethod.id undefined?
                    contactMethods.push(contactMethod);
                }

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var addedContactMethod = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    for (var i = 0; i < contactMethods.length; i++) {
                        if (Acm.compare(contactMethods[i].id, contactMethod.id)) {
                            contactMethods[i].type = contactMethod.type;
                            contactMethods[i].value = contactMethod.value;
                            break;
                        }
                    }

                    this._saveCaseFile(caseFileId, caseFile
                        ,function(data) {
                            var savedContactMethod = null;
                            if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveCaseFile(caseFileId, caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                    CaseFile.Controller.modelDeletedContactMethod(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, contactMethodId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addSecurityTag: function(caseFileId, personAssociationId, securityTag) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    //ensure securityTag.id undefined?
                    securityTags.push(securityTag);
                }

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var addedSecurityTag = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    for (var i = 0; i < securityTags.length; i++) {
                        if (Acm.compare(securityTags[i].id, securityTag.id)) {
                            securityTags[i].type = securityTags.type;
                            securityTags[i].value = securityTag.value;
                            break;
                        }
                    }

                    this._saveCaseFile(caseFileId, caseFile
                        ,function(data) {
                            var savedSecurityTag = null;
                            if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveCaseFile(caseFileId, caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                    CaseFile.Controller.modelDeletedSecurityTag(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, securityTagId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addPersonAlias: function(caseFileId, personAssociationId, personAlias) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    //ensure personAlias.id undefined?
                    personAliases.push(personAlias);
                }

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var addedPersonAlias = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    for (var i = 0; i < personAliases.length; i++) {
                        if (Acm.compare(personAliases[i].id, personAlias.id)) {
                            personAliases[i].aliasType = personAlias.aliasType;
                            personAliases[i].aliasValue = personAlias.aliasValue;
                            break;
                        }
                    }

                    this._saveCaseFile(caseFileId, caseFile
                        ,function(data) {
                            var savedPersonAlias = null;
                            if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveCaseFile(caseFileId, caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                    CaseFile.Controller.modelDeletedPersonAlias(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, personAliasId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addAddress: function(caseFileId, personAssociationId, address) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var addresses = personAssociation.person.addresses;
                    //ensure address.id undefined?
                    addresses.push(address);
                }

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var addedAddress = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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

                    this._saveCaseFile(caseFileId, caseFile
                        ,function(data) {
                            var savedAddress = null;
                            if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveCaseFile(caseFileId, caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                    CaseFile.Controller.modelDeletedAddress(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, addressId));
                                }
                            }
                        );
                    }
                }
            }
        }

        ,addOrganization: function(caseFileId, personAssociationId, organization) {
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    //ensure organization.id undefined?
                    organizations.push(organization);
                }

                this._saveCaseFile(caseFileId, caseFile
                    ,function(data) {
                        var addedOrganization = null;
                        if (CaseFile.Model.Detail.validateCaseFile(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    for (var i = 0; i < organizations.length; i++) {
                        if (Acm.compare(organizations[i].organizationId, organization.organizationId)) {
                            organizations[i].organizationType = organization.organizationType;
                            organizations[i].organizationValue = organization.organizationValue;
                            break;
                        }
                    }

                    this._saveCaseFile(caseFileId, caseFile
                        ,function(data) {
                            var savedOrganization = null;
                            if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
            var caseFile = CaseFile.Model.Detail.getCacheCaseFile(caseFileId);
            if (CaseFile.Model.Detail.validateCaseFile(caseFile)) {
                var personAssociations = caseFile.personAssociations;
                var personAssociation = CaseFile.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (CaseFile.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveCaseFile(caseFileId, caseFile
                            ,function(data) {
                                if (CaseFile.Model.Detail.validateCaseFile(data)) {
                                    CaseFile.Controller.modelDeletedOrganization(caseFileId, personAssociationId, Acm.Service.responseWrapper(data, organizationId));
                                }
                            }
                        );
                    }
                }
            }
        }
    }

    ,Documents_JTable_Retire: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_DOWNLOAD_DOCUMENT_      : "/api/latest/plugin/ecm/download/byId/"
        ,API_UPLOAD_DOCUMENT         : "/api/latest/service/ecm/upload"
        ,API_RETRIEVE_DOCUMENT_      : "/api/latest/service/ecm/folder/"

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
        ,retrieveDocumentsDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFile.Service.Documents.API_RETRIEVE_DOCUMENT_ + CaseFile.Model.DOC_TYPE_CASE_FILE;
                    url += "/" + caseFileId + "?category=" + CaseFile.Model.DOC_CATEGORY_DOCUMENT_SM;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (CaseFile.Model.Documents.validateDocuments(data)) {
                        var documents = data;
                        CaseFile.Model.Documents.cacheDocuments.put(caseFileId + "." +jtParams.jtStartIndex, documents);
                        jtData = callbackSuccess(documents);
                    }
                    return jtData;
                }
            );
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
                        // the upload returns an array of documents, for when the user uploads multiple files.
                        if (CaseFile.Model.Documents.validateNewDocument(response)) {
                            //add to the top of the cache
                            var caseId = CaseFile.View.getActiveCaseFileId();
                            var documentsCache = CaseFile.Model.Documents.cacheDocuments.get(caseId + "." + 0);
                            if(CaseFile.Model.Documents.validateDocuments(documentsCache)) {
                                var documents = documentsCache.children;
                                for ( var a = 0; a < response.length; a++ )
                                {
                                    var f = response[a];
                                    var doc = {};
                                    doc.objectId = Acm.goodValue(f.fileId);
                                    doc.name = Acm.goodValue(f.fileName);
                                    doc.creator = Acm.goodValue(f.creator);
                                    doc.created = Acm.goodValue(f.created);
                                    doc.objectType = CaseFile.Model.DOC_TYPE_FILE_SM;
                                    doc.category = CaseFile.Model.DOC_CATEGORY_FILE_SM;
                                    documentsCache.children.unshift(doc);
                                    documents.totalChildren++;
                                }
                            }
                            CaseFile.Controller.modelAddedDocument(response);
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
                    url =  App.getContextPath() + CaseFile.Service.Notes.API_LIST_NOTES_ + CaseFile.Model.DOC_TYPE_CASE_FILE + "/";
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
                        //if (CaseFile.Model.Detail.validateCaseFile(response)) {
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
                        //if (CaseFile.Model.Detail.validateCaseFile(response)) {
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
                        //if (CaseFile.Model.Detail.validateCaseFile(response)) {
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

        ,API_RETRIEVE_CORRESPONDENCE_      : "/api/latest/service/ecm/folder/"
        ,API_CREATE_CORRESPONDENCE_      : "/api/latest/service/correspondence"

        ,retrieveCorrespondenceDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFile.Service.Correspondence.API_RETRIEVE_CORRESPONDENCE_ + CaseFile.Model.DOC_TYPE_CASE_FILE;
                    url += "/" + caseFileId + "?category=" + CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (CaseFile.Model.Correspondence.validateCorrespondences(data)) {
                        var correspondences = data;
                        CaseFile.Model.Correspondence.cacheCorrespondences.put(caseFileId + "." +jtParams.jtStartIndex, correspondences);
                        jtData = callbackSuccess(correspondences);
                    }
                    return jtData;
                }
            );
        }


        ,createCorrespondence : function(caseFile, templateName) {
            var url = App.getContextPath() + this.API_CREATE_CORRESPONDENCE_
                + "?templateName=" + templateName
                + "&parentObjectType=" + CaseFile.Model.DOC_TYPE_CASE_FILE
                + "&parentObjectId=" + caseFile.id
                + "&targetCmisFolderId=" + caseFile.container.folder.cmisFolderId;

            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelCreatedCorrespondence(response);

                    } else {
                        if (CaseFile.Model.Correspondence.validateNewCorrespondence(response)) {
                            //add to the top of the cache
                            var correspondencesCache = CaseFile.Model.Correspondence.cacheCorrespondences.get(caseFile.id + "." + 0);
                            if(CaseFile.Model.Correspondence.validateCorrespondences(correspondencesCache)) {
                                var correspondences = correspondencesCache.children;
                                var correspondence = {};
                                correspondence.objectId = Acm.goodValue(response.fileId);
                                correspondence.name = Acm.goodValue(response.fileName);
                                correspondence.creator = Acm.goodValue(response.creator);
                                correspondence.created = Acm.goodValue(response.created);
                                correspondence.objectType = CaseFile.Model.DOC_TYPE_FILE_SM;
                                correspondence.category = CaseFile.Model.DOC_CATEGORY_CORRESPONDENCE_SM;
                                correspondencesCache.children.unshift(correspondence);
                                correspondencesCache.totalChildren++;
                            }
                            CaseFile.Controller.modelCreatedCorrespondence(response);
                        }
                    }
                }
                ,url
            )
        }
    }

    ,History: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_CASE_FILE_HISTORY : "/api/latest/plugin/audit"

        ,retrieveHistoryDeferred : function(caseFileId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + CaseFile.Service.History.API_CASE_FILE_HISTORY;
                    url += '/' + CaseFile.Model.DOC_TYPE_CASE_FILE + '/'
                    url += caseFileId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (CaseFile.Model.History.validateHistory(data)) {
                        var history = data;
                        CaseFile.Model.History.cacheHistory.put(caseFileId + "." +jtParams.jtStartIndex, history);
                        jtData = callbackSuccess(history);
                    }
                    return jtData;
                }
            );
        }
    }

    ,OutlookCalendar: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        , API_RETRIEVE_CALENDAR_ITEMS: "/api/v1/plugin/outlook/calendar"


        ,retrieveOutlookOutlookCalendarItems : function(caseFileId) {
            var url = App.getContextPath() + this.API_RETRIEVE_CALENDAR_ITEMS;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedOutlookCalendarItems(response);

                    } else {
                        if (CaseFile.Model.OutlookCalendar.validateOutlookCalendarItems(response)) {
                            var outlookCalendarItems = response;
                            CaseFile.Model.OutlookCalendar.cacheOutlookCalendarItems.put(caseFileId, outlookCalendarItems);
                            CaseFile.Controller.modelRetrievedOutlookCalendarItems(outlookCalendarItems);
                        }
                    }
                }
                ,url
            )
        }
    }

    ,Time: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        , API_RETRIEVE_TIMESHEETS: "/api/v1/service/timesheet/"


        ,retrieveTimesheets : function(caseFileId) {
            var url = App.getContextPath() + this.API_RETRIEVE_TIMESHEETS;
            url += "objectId/" + caseFileId + "/";
            url += "objectType/" + CaseFile.Model.DOC_TYPE_CASE_FILE;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedTimesheets(response);

                    } else {
                        if (CaseFile.Model.Time.validateTimesheets(response)) {
                            var timesheets = response;
                            CaseFile.Model.Time.cacheTimesheets.put(caseFileId, timesheets);
                            CaseFile.Controller.modelRetrievedTimesheets(timesheets);
                        }
                    }
                }
                ,url
            )
        }
    }

    ,Cost: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        , API_RETRIEVE_COSTSHEETS: "/api/v1/service/costsheet/"


        ,retrieveCostsheets : function(caseFileId) {
            var url = App.getContextPath() + this.API_RETRIEVE_COSTSHEETS;
            url += "objectId/" + caseFileId + "/";
            url += "objectType/" + CaseFile.Model.DOC_TYPE_CASE_FILE;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        CaseFile.Controller.modelRetrievedCostsheets(response);

                    } else {
                        if (CaseFile.Model.Cost.validateCostsheets(response)) {
                            var costsheets = response;
                            CaseFile.Model.Cost.cacheCostsheets.put(caseFileId, costsheets);
                            CaseFile.Controller.modelRetrievedCostsheets(costsheets);
                        }
                    }
                }
                ,url
            )
        }
    }

};

