/**
 * Complaint.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Complaint.Service = {
    create : function() {
        if (Complaint.Service.Lookup.create) {Complaint.Service.Lookup.create();}
        if (Complaint.Service.Detail.create) {Complaint.Service.Detail.create();}
        if (Complaint.Service.People.create) {Complaint.Service.People.create();}
        //if (Complaint.Service.Documents.create) {Complaint.Service.Documents.create();}
        if (Complaint.Service.Notes.create) {Complaint.Service.Notes.create();}
        if (Complaint.Service.Tasks.create) {Complaint.Service.Tasks.create();}
        if (Complaint.Service.History.create) {Complaint.Service.History.create();}
        if (Complaint.Service.Time.create) {Complaint.Service.Time.create();}
        if (Complaint.Service.Cost.create) {Complaint.Service.Cost.create();}
    }
    ,onInitialized: function() {
        if (Complaint.Service.Lookup.onInitialized) {Complaint.Service.Lookup.onInitialized();}
        if (Complaint.Service.Detail.onInitialized) {Complaint.Service.Detail.onInitialized();}
        if (Complaint.Service.People.onInitialized) {Complaint.Service.People.onInitialized();}
        //if (Complaint.Service.Documents.onInitialized) {Complaint.Service.Documents.onInitialized();}
        if (Complaint.Service.Notes.onInitialized) {Complaint.Service.Notes.onInitialized();}
        if (Complaint.Service.Tasks.onInitialized) {Complaint.Service.Tasks.onInitialized();}
        if (Complaint.Service.History.onInitialized) {Complaint.Service.History.onInitialized();}
        if (Complaint.Service.Time.onInitialized) {Complaint.Service.Time.onInitialized();}
        if (Complaint.Service.Cost.onInitialized) {Complaint.Service.Cost.onInitialized();}
    }

    ,Lookup: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_GET_APPROVERS             : "/api/latest/service/functionalaccess/users/acm-complaint-approve"
        ,API_GET_COMPLAINT_TYPES       : "/api/latest/plugin/complaint/types"
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
        	var complaint = Complaint.View.getActiveComplaint();
        	if (complaint == null) {
        		return null;
        	}
        	var groupGetParameter = '';
        	var groupName = Complaint.Model.Detail.getGroup(complaint);
        	if (groupName && groupName.length > 0) {
        		groupGetParameter = '/' + groupName;
        	}
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Complaint.Controller.modelFoundAssignees(response);

                    } else {
                        if (Complaint.Service.Lookup._validateAssignees(response)) {
                            var assignees = response;
                            Complaint.Model.Lookup.setAssignees(Complaint.View.getActiveComplaintId(), assignees);
                            Complaint.Controller.modelFoundAssignees(assignees);
                        }
                        return assignees;
                    }
                }
                ,App.getContextPath() + this.API_GET_APPROVERS + groupGetParameter
            )
        }

        ,_validateComplaintTypes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        ,retrieveComplaintTypes : function() {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Complaint.Controller.modelFoundComplaintTypes(response);

                    } else {
                        if (Complaint.Service.Lookup._validateComplaintTypes(response)) {
                            var complaintTypes = response;
                            Complaint.Model.Lookup.setComplaintTypes(complaintTypes);
                            Complaint.Controller.modelFoundComplaintTypes(complaintTypes);
                        }
                    }
                }
                ,App.getContextPath() + this.API_GET_COMPLAINT_TYPES
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
                        Complaint.Controller.modelFoundPriorities(response);

                    } else {
                        if (Complaint.Service.Lookup._validatePriorities(response)) {
                            var priorities = response;
                            Complaint.Model.Lookup.setPriorities(priorities);
                            Complaint.Controller.modelFoundPriorities(priorities);
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
                    	Complaint.Controller.modelRetrievedGroups(response);
                    } else {
                        if (response.response && response.response.docs && Complaint.Service.Lookup._validateGroups(response.response.docs)) {
                            var groups = response.response.docs;
                            Complaint.Model.Lookup.setGroups(Complaint.View.getActiveComplaintId(), groups);
                            Complaint.Controller.modelRetrievedGroups(groups);
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
                    	Complaint.Controller.modelRetrievedUsers(response);
                    } else {
                        if (response.response && response.response.docs && Complaint.Service.Lookup._validateUsers(response.response.docs)) {
                            var users = response.response.docs;
                            Complaint.Model.Lookup.setUsers(users);
                            Complaint.Controller.modelRetrievedUsers(users);
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
        create: function () {
        }
        , onInitialized: function () {
        }

        ,_saveComplaint: function(complaintId, complaint, handler) {
            ObjNav.Service.Detail.saveObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId, complaint, handler);
        }
        ,saveComplaintTitle: function(complaintId, title) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.complaintTitle = title;
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedComplaintTitle(complaintId, Acm.Service.responseWrapper(data, data.complaintTitle));
                    }
                );
            }
        }
        ,saveIncidentDate: function(complaintId, incidentDate) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.incidentDate = incidentDate;
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedIncidentDate(complaintId, Acm.Service.responseWrapper(data, data.incidentDate));
                    }
                );
            }
        }
        ,saveAssignee: function(complaintId, assignee) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                Complaint.Model.Detail.setAssignee(complaint, assignee);
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedAssignee(complaintId, Acm.Service.responseWrapper(data, assignee));
                    }
                );
            }
        }
        ,saveGroup: function(complaintId, group) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                Complaint.Model.Detail.setGroup(complaint, group);
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedGroup(complaintId, Acm.Service.responseWrapper(data, group));
                    }
                );
            }
        }
        ,saveComplaintType: function(complaintId, complaintType) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.complaintType = complaintType;
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedComplaintType(complaintId, Acm.Service.responseWrapper(data, data.complaintType));
                    }
                );
            }
        }
        ,savePriority: function(complaintId, priority) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.priority = priority;
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedPriority(complaintId, Acm.Service.responseWrapper(data, data.priority));
                    }
                );
            }
        }
        ,saveDetail: function(complaintId, details) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.details = details;
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedDetail(complaintId, Acm.Service.responseWrapper(data, data.details));
                    }
                );
            }
        }
        ,updateComplaintRestriction: function(complaintId, restriction) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.restricted = restriction;
                this._saveComplaint(complaintId, complaint
                    ,function(data) {
                        Complaint.Controller.modelSavedRestriction(complaintId, Acm.Service.responseWrapper(data, data.restricted));
                    }
                );
            }
        }
    }

    ,People: {
        create: function () {
        }
        , onInitialized: function () {
        }

//        , API_SAVE_COMPLAINT: "/api/latest/plugin/complaint/"
        , API_SAVE_PERSON_ASSOCIATION: "/api/latest/plugin/personAssociation"
        , API_DELETE_PERSON_ASSOCIATION_: "/api/latest/plugin/personAssociation/delete/"

        ,_saveComplaint: function(complaintId, complaint, handler) {
            ObjNav.Service.Detail.saveObject(Complaint.Model.DOC_TYPE_COMPLAINT, complaintId, complaint, handler);
        }
//        , saveComplaint: function (data, handler) {
//            var complaint = data;
//            Acm.Service.asyncPost(
//                function (response) {
//                    if (response.hasError) {
//                        if (handler) {
//                            handler(response);
//                        } else {
//                            Complaint.Controller.modelSavedComplaint(response);
//                        }
//
//                    } else {
//                        if (Complaint.Model.Detail.validateData(response)) {
//                            var complaint = response;
//                            Complaint.Model.Detail.putCacheComplaint(complaint.complaintId, complaint);
//                            if (handler) {
//                                handler(complaint);
//                            } else {
//                                Complaint.Controller.modelSavedComplaint(complaint);
//                            }
//                        }
//                    }
//                }
//                , App.getContextPath() + this.API_SAVE_COMPLAINT
//                , JSON.stringify(complaint)
//            )
//        }
        , addParticipant: function (complaintId, participant) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                complaint.participants.push(participant);
                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var addedParticipant = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            for (var i = 0; i < data.participants.length; i++) {
                                if (Acm.compare(data.participants[i].participantLdapId, participant.participantLdapId)
                                    && Acm.compare(data.participants[i].participantType, participant.participantType)) {
                                    addedParticipant = data.participants[i];
                                    break;
                                }
                            }
                        }
                        if (addedParticipant) {
                            Complaint.Controller.modelAddedParticipant(complaintId, Acm.Service.responseWrapper(data, addedParticipant));
                        }
                    }
                );
            }
        }
        , updateParticipant: function (complaintId, participant) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                for (var i = 0; i < complaint.participants.length; i++) {
                    if (Acm.compare(complaint.participants[i].id, participant.id)) {
                        complaint.participants[i].participantLdapId = participant.participantLdapId;
                        complaint.participants[i].participantType = participant.participantType;
                        break;
                    }
                } //end for

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var savedParticipant = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            for (var i = 0; i < data.participants.length; i++) {
                                if (Acm.compare(data.participants[i].id, participant.id)) {
                                    savedParticipant = data.participants[i];
                                    break;
                                }
                            }
                        }
                        if (savedParticipant) {
                            Complaint.Controller.modelUpdatedParticipant(complaintId, Acm.Service.responseWrapper(data, savedParticipant));
                        }
                    }
                );
            }
        }
        , deleteParticipant: function (complaintId, participantId) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var toDelete = -1;
                for (var i = 0; i < complaint.participants.length; i++) {
                    if (Acm.compare(complaint.participants[i].id, participantId)) {
                        toDelete = i;
                        break;
                    }
                }

                if (0 <= toDelete) {
                    complaint.participants.splice(toDelete, 1);
                    this._saveComplaint(complaintId, complaint
                        , function (data) {
                            if (Complaint.Model.Detail.validateComplaint(data)) {
                                Complaint.Controller.modelDeletedParticipant(complaintId, Acm.Service.responseWrapper(data, participantId));
                            }
                        }
                    );
                }
            }
        }
        , addLocation: function (complaint) {
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                this._saveComplaint(complaint.complaintId, complaint
                    , function (data) {
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            Complaint.Controller.modelAddedLocation(complaint.complaintId, Acm.Service.responseWrapper(data, complaint.complaintId));
                        }
                    }
                );
            }
        }
        , updateLocation: function (complaint) {
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                this._saveComplaint(complaint.complaintId, complaint
                    , function (data) {
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            Complaint.Controller.modelUpdatedLocation(complaint.complaintId, Acm.Service.responseWrapper(data, complaint.complaintId));
                        }
                    }
                );
            }
        }
        , deleteLocation: function (complaint) {
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                this._saveComplaint(complaint.complaintId, complaint
                    , function (data) {
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            Complaint.Controller.modelDeletedLocation(complaint.complaintId, Acm.Service.responseWrapper(data, complaint.complaintId));
                        }
                    }
                );
            }
        }
        , addPersonAssociation: function (complaintId, personAssociation) {
            Acm.Service.asyncPost(
                function (response) {
                    if (response.hasError) {
                        Complaint.Controller.modelAddedPersonAssociation(complaintId, response);

                    } else {
                        if (Complaint.Model.People.validatePersonAssociation(response)) {
                            //check complaintId == personAssociation.parentId;
                            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
                            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                                //check response.parentId == complaintId
                                //check response.id not null, > 0
                                //check response.id not already in complaint.personAssociations array
                                var addedPersonAssociation = response;
                                complaint.personAssociations.push(addedPersonAssociation);
                                Complaint.Model.Detail.putCacheComplaint(complaintId, complaint);
                                Complaint.Controller.modelAddedPersonAssociation(complaintId, addedPersonAssociation);
                            }
                        }
                    }
                }
                , App.getContextPath() + this.API_SAVE_PERSON_ASSOCIATION
                , JSON.stringify(personAssociation)
            )
        }
        , updatePersonAssociation: function (complaintId, personAssociation) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                for (var i = 0; i < complaint.personAssociations.length; i++) {
                    if (Acm.compare(complaint.personAssociations[i].id, personAssociation.id)) {
                        complaint.personAssociations[i].person.title = personAssociation.person.title;
                        complaint.personAssociations[i].person.givenName = personAssociation.person.givenName;
                        complaint.personAssociations[i].person.familyName = personAssociation.person.familyName;
                        complaint.personAssociations[i].personType = personAssociation.personType;
                        break;
                    }
                } //end for

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var savedPersonAssociation = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            for (var i = 0; i < data.personAssociations.length; i++) {
                                if (Acm.compare(data.personAssociations[i].id, personAssociation.id)) {
                                    savedPersonAssociation = data.personAssociations[i];
                                    break;
                                }
                            }
                        }
                        if (savedPersonAssociation) {
                            Complaint.Controller.modelUpdatedPersonAssociation(complaintId, Acm.Service.responseWrapper(data, savedPersonAssociation));
                        }
                    }
                );
            }
        }

        , _validateDeletedPersonAssociation: function (data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedPersonAssociationId)) {
                return false;
            }
            return true;
        }
        , deletePersonAssociation: function (complaintId, personAssociationId) {
            var url = App.getContextPath() + this.API_DELETE_PERSON_ASSOCIATION_ + personAssociationId;
            Acm.Service.asyncDelete(
                function (response) {
                    if (response.hasError) {
                        Complaint.Controller.modelDeletedPersonAssociation(response);

                    } else {
                        if (Complaint.Service.People._validateDeletedPersonAssociation(response)) {
                            if (response.deletedPersonAssociationId == personAssociationId) {
                                var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
                                if (Complaint.Model.Detail.validateComplaint(complaint)) {
                                    for (var i = 0; i < complaint.personAssociations.length; i++) {
                                        var pa = complaint.personAssociations[i];
                                        if (Complaint.Model.People.validatePersonAssociation(pa)) {
                                            if (pa.id == response.deletedPersonAssociationId) {
                                                complaint.personAssociations.splice(i, 1);
                                                Complaint.Model.Detail.putCacheComplaint(complaintId, complaint);
                                                Complaint.Controller.modelDeletedPersonAssociation(Acm.Service.responseWrapper(response, personAssociationId));
                                                break;
                                            }
                                        }
                                    } //end for
                                }
                            }
                        }
                    } //end else
                }
                , url
            )
        }

        , addContactMethod: function (complaintId, personAssociationId, contactMethod) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    //ensure contactMethod.id undefined?
                    contactMethods.push(contactMethod);
                }

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var addedContactMethod = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Complaint.Controller.modelAddedContactMethod(complaintId, personAssociationId, Acm.Service.responseWrapper(data, addedContactMethod));
                        }
                    }
                );
            }
        }
        , updateContactMethod: function (complaintId, personAssociationId, contactMethod) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var contactMethods = personAssociation.person.contactMethods;
                    for (var i = 0; i < contactMethods.length; i++) {
                        if (Acm.compare(contactMethods[i].id, contactMethod.id)) {
                            contactMethods[i].type = contactMethod.type;
                            contactMethods[i].value = contactMethod.value;
                            break;
                        }
                    }

                    this._saveComplaint(complaintId, complaint
                        , function (data) {
                            var savedContactMethod = null;
                            if (Complaint.Model.Detail.validateComplaint(data)) {
                                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Complaint.Controller.modelUpdatedContactMethod(complaintId, personAssociationId, Acm.Service.responseWrapper(data, savedContactMethod));
                            }
                        }
                    );
                }
            }
        }
        , deleteContactMethod: function (complaintId, personAssociationId, contactMethodId) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveComplaint(complaintId, complaint
                            , function (data) {
                                if (Complaint.Model.Detail.validateComplaint(data)) {
                                    Complaint.Controller.modelDeletedContactMethod(complaintId, personAssociationId, Acm.Service.responseWrapper(data, contactMethodId));
                                }
                            }
                        );
                    }
                }
            }
        }

        , addSecurityTag: function (complaintId, personAssociationId, securityTag) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    //ensure securityTag.id undefined?
                    securityTags.push(securityTag);
                }

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var addedSecurityTag = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Complaint.Controller.modelAddedSecurityTag(complaintId, personAssociationId, Acm.Service.responseWrapper(data, addedSecurityTag));
                        }
                    }
                );
            }
        }
        , updateSecurityTag: function (complaintId, personAssociationId, securityTag) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var securityTags = personAssociation.person.securityTags;
                    for (var i = 0; i < securityTags.length; i++) {
                        if (Acm.compare(securityTags[i].id, securityTag.id)) {
                            securityTags[i].type = securityTags.type;
                            securityTags[i].value = securityTag.value;
                            break;
                        }
                    }

                    this._saveComplaint(complaintId, complaint
                        , function (data) {
                            var savedSecurityTag = null;
                            if (Complaint.Model.Detail.validateComplaint(data)) {
                                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Complaint.Controller.modelUpdatedSecurityTag(complaintId, personAssociationId, Acm.Service.responseWrapper(data, savedSecurityTag));
                            }
                        }
                    );
                }
            }
        }
        , deleteSecurityTag: function (complaintId, personAssociationId, securityTagId) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveComplaint(complaintId, complaint
                            , function (data) {
                                if (Complaint.Model.Detail.validateComplaint(data)) {
                                    Complaint.Controller.modelDeletedSecurityTag(complaintId, personAssociationId, Acm.Service.responseWrapper(data, securityTagId));
                                }
                            }
                        );
                    }
                }
            }
        }

        , addPersonAlias: function (complaintId, personAssociationId, personAlias) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    //ensure personAlias.id undefined?
                    personAliases.push(personAlias);
                }

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var addedPersonAlias = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Complaint.Controller.modelAddedPersonAlias(complaintId, personAssociationId, Acm.Service.responseWrapper(data, addedPersonAlias));
                        }
                    }
                );
            }
        }
        , updatePersonAlias: function (complaintId, personAssociationId, personAlias) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var personAliases = personAssociation.person.personAliases;
                    for (var i = 0; i < personAliases.length; i++) {
                        if (Acm.compare(personAliases[i].id, personAlias.id)) {
                            personAliases[i].aliasType = personAlias.aliasType;
                            personAliases[i].aliasValue = personAlias.aliasValue;
                            break;
                        }
                    }

                    this._saveComplaint(complaintId, complaint
                        , function (data) {
                            var savedPersonAlias = null;
                            if (Complaint.Model.Detail.validateComplaint(data)) {
                                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Complaint.Controller.modelUpdatedPersonAlias(complaintId, personAssociationId, Acm.Service.responseWrapper(data, savedPersonAlias));
                            }
                        }
                    );
                }
            }
        }
        , deletePersonAlias: function (complaintId, personAssociationId, personAliasId) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveComplaint(complaintId, complaint
                            , function (data) {
                                if (Complaint.Model.Detail.validateComplaint(data)) {
                                    Complaint.Controller.modelDeletedPersonAlias(complaintId, personAssociationId, Acm.Service.responseWrapper(data, personAliasId));
                                }
                            }
                        );
                    }
                }
            }
        }

        , addAddress: function (complaintId, personAssociationId, address) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var addresses = personAssociation.person.addresses;
                    //ensure address.id undefined?
                    addresses.push(address);
                }

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var addedAddress = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                                var addresses = personAssociation.person.addresses;
                                for (var i = 0; i < addresses.length; i++) {
                                    if (Acm.compare(addresses[i].type, address.type)
                                        && Acm.compare(addresses[i].streetAddress, address.streetAddress)
                                        && Acm.compare(addresses[i].city, address.city)
                                        && Acm.compare(addresses[i].state, address.state)
                                        && Acm.compare(addresses[i].zip, address.zip)
                                        && Acm.compare(addresses[i].country, address.country)
                                        ) {
                                        addedAddress = addresses[i];
                                        break;
                                    }
                                }
                            }
                        }
                        if (addedAddress) {
                            Complaint.Controller.modelAddedAddress(complaintId, personAssociationId, Acm.Service.responseWrapper(data, addedAddress));
                        }
                    }
                );
            }
        }
        , updateAddress: function (complaintId, personAssociationId, address) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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

                    this._saveComplaint(complaintId, complaint
                        , function (data) {
                            var savedAddress = null;
                            if (Complaint.Model.Detail.validateComplaint(data)) {
                                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Complaint.Controller.modelUpdatedAddress(complaintId, personAssociationId, Acm.Service.responseWrapper(data, savedAddress));
                            }
                        }
                    );
                }
            }
        }
        , deleteAddress: function (complaintId, personAssociationId, addressId) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveComplaint(complaintId, complaint
                            , function (data) {
                                if (Complaint.Model.Detail.validateComplaint(data)) {
                                    Complaint.Controller.modelDeletedAddress(complaintId, personAssociationId, Acm.Service.responseWrapper(data, addressId));
                                }
                            }
                        );
                    }
                }
            }
        }

        , addOrganization: function (complaintId, personAssociationId, organization) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    //ensure organization.id undefined?
                    organizations.push(organization);
                }

                this._saveComplaint(complaintId, complaint
                    , function (data) {
                        var addedOrganization = null;
                        if (Complaint.Model.Detail.validateComplaint(data)) {
                            var personAssociations = data.personAssociations;
                            var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                            if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                            Complaint.Controller.modelAddedOrganization(complaintId, personAssociationId, Acm.Service.responseWrapper(data, addedOrganization));
                        }
                    }
                );
            }
        }
        , updateOrganization: function (complaintId, personAssociationId, organization) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
                    var organizations = personAssociation.person.organizations;
                    for (var i = 0; i < organizations.length; i++) {
                        if (Acm.compare(organizations[i].organizationId, organization.organizationId)) {
                            organizations[i].organizationType = organization.organizationType;
                            organizations[i].organizationValue = organization.organizationValue;
                            break;
                        }
                    }

                    this._saveComplaint(complaintId, complaint
                        , function (data) {
                            var savedOrganization = null;
                            if (Complaint.Model.Detail.validateComplaint(data)) {
                                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, data.personAssociations);
                                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                                Complaint.Controller.modelUpdatedOrganization(complaintId, personAssociationId, Acm.Service.responseWrapper(data, savedOrganization));
                            }
                        }
                    );
                }
            }
        }
        , deleteOrganization: function (complaintId, personAssociationId, organizationId) {
            var complaint = Complaint.Model.Detail.getCacheComplaint(complaintId);
            if (Complaint.Model.Detail.validateComplaint(complaint)) {
                var personAssociations = complaint.personAssociations;
                var personAssociation = Complaint.Model.People.findPersonAssociation(personAssociationId, personAssociations);
                if (Complaint.Model.People.validatePersonAssociation(personAssociation)) {
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
                        this._saveComplaint(complaintId, complaint
                            , function (data) {
                                if (Complaint.Model.Detail.validateComplaint(data)) {
                                    Complaint.Controller.modelDeletedOrganization(complaintId, personAssociationId, Acm.Service.responseWrapper(data, organizationId));
                                }
                            }
                        );
                    }
                }
            }
        }
    }

    ,Documents_JTable_To_Retire: {
        create: function(){
        }
        ,onInitialized: function(){
        }

        ,API_UPLOAD_FILE            : "/api/latest/service/ecm/upload"
        ,API_DOWNLOAD_DOCUMENT      : "/api/v1/plugin/ecm/download/byId/"
        ,API_RETRIEVE_DOCUMENT_      : "/api/latest/service/ecm/folder/"

        ,retrieveDocumentsDeferred : function(complaintId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Complaint.Service.Documents.API_RETRIEVE_DOCUMENT_ + Complaint.Model.DOC_TYPE_COMPLAINT;
                    url += "/" + complaintId + "?category=" + Complaint.Model.DOC_CATEGORY_FILE_SM;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Complaint.Model.Documents.validateDocuments(data)) {
                        var documents = data;
                        Complaint.Model.Documents.cacheDocuments.put(complaintId + "." +jtParams.jtStartIndex, documents);
                        jtData = callbackSuccess(documents);
                    }
                    return jtData;
                }
            );
        }
        ,uploadDocuments: function(formData) {
            var url = App.getContextPath() + this.API_UPLOAD_FILE;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        Complaint.Controller.modelAddedDocument(response);
                    } else {
                        // the upload returns an array of documents, for when the user uploads multiple files.
                        if (Complaint.Model.Documents.validateNewDocument(response)) {
                            //add to the top of the cache
                            var complaintId = Complaint.View.getActiveComplaintId();
                            var documentsCache = Complaint.Model.Documents.cacheDocuments.get(complaintId + "." + 0);
                            if(Complaint.Model.Documents.validateDocuments(documentsCache)) {
                                var documents = documentsCache.children;
                                for ( var a = 0; a < response.length; a++ )
                                {
                                    var f = response[a];
                                    var doc = {};
                                    doc.objectId = Acm.goodValue(f.fileId);
                                    doc.name = Acm.goodValue(f.fileName);
                                    doc.creator = Acm.goodValue(f.creator);
                                    doc.created = Acm.goodValue(f.created);
                                    doc.objectType = Complaint.Model.DOC_TYPE_FILE_SM;
                                    doc.category = Complaint.Model.DOC_CATEGORY_FILE_SM;
                                    documentsCache.children.unshift(doc);
                                    documents.totalChildren++;
                                }
                            }
                            Complaint.Controller.modelAddedDocument(response);
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

        ,retrieveNotesDeferred : function(complaintId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Complaint.Service.Notes.API_LIST_NOTES_ + Complaint.Model.DOC_TYPE_COMPLAINT + "/";
                    url += complaintId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Complaint.Model.Notes.validateNotes(data)) {
                        var noteList = data;
                        Complaint.Model.Notes.cacheNoteList.put(complaintId, noteList);
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
                            Complaint.Controller.modelSavedNote(response);
                        }

                    } else {
                        if (Complaint.Model.Notes.validateNote(response)) {
                            var note = response;
                            var complaintId = Complaint.Model.getComplaintId();

                            if (complaintId == note.parentId) {
                                var noteList = Complaint.Model.Notes.cacheNoteList.get(complaintId);
                                if(Acm.isNotEmpty(noteList)){
                                    var found = -1;
                                    for (var i = 0; i < noteList.length; i++) {
                                        if (note.id == noteList[i].id) {
                                            found = i;
                                            break;
                                        }
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
                                    Complaint.Controller.modelSavedNote(note);
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
            if (Complaint.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        Complaint.Controller.modelAddedNote(data);
                    }
                );
            }
        }
        ,updateNote: function(note) {
            if (Complaint.Model.Notes.validateNote(note)) {
                this.saveNote(note
                    ,function(data) {
                        Complaint.Controller.modelUpdatedNote(data);
                    }
                );
            }
        }

        ,deleteNote : function(noteId) {
            var url = App.getContextPath() + this.API_DELETE_NOTE_ + noteId;

            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        Complaint.Controller.modelDeletedNote(response);

                    } else {
                        if (Complaint.Model.Notes.validateDeletedNote(response)) {
                            var complaintId = Complaint.Model.getComplaintId();
                            var deletedNote = response;
                            if (deletedNote.deletedNoteId == noteId) {
                                var notes = Complaint.Model.Notes.cacheNoteList.get(complaintId);
                                if (Complaint.Model.Notes.validateNotes(notes)) {
                                    for (var i = 0; i < notes.length; i++) {
                                        if (noteId == notes[i].id) {
                                            notes.splice(i, 1);
                                            Complaint.Controller.modelDeletedNote(Acm.Service.responseWrapper(deletedNote, noteId));
                                            return;
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

    ,Tasks: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_TASKS_SOLR         : "/api/latest/plugin/search/children?parentType=COMPLAINT&childType=TASK&parentId="

        ,retrieveTaskListDeferred : function(complaintId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Complaint.Service.Tasks.API_RETRIEVE_TASKS_SOLR;
                    url += complaintId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Acm.Validator.validateSolrData(data)) {
                        var responseHeader = data.responseHeader;
                        if (0 == responseHeader.status) {
                            //response.start should match to jtParams.jtStartIndex
                            //response.docs.length should be <= jtParams.jtPageSize
                            var response = data.response;
                            var tasks = [];
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
                                tasks.push(task);
                            }
                            Complaint.Model.Tasks.cacheTaskSolr.put(complaintId, tasks);

                            jtData = callbackSuccess(tasks);

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

    ,History: {
        create: function() {
        }
        ,onInitialized: function() {
        }
        ,API_COMPLAINT_HISTORY : "/api/latest/plugin/audit"

        ,retrieveHistoryDeferred : function(complaintId, postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Complaint.Service.History.API_COMPLAINT_HISTORY;
                    url += '/' + Complaint.Model.DOC_TYPE_COMPLAINT + '/'
                    url += complaintId;
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.jTableGetEmptyRecord();
                    if (Complaint.Model.History.validateHistory(data)) {
                        var history = data;
                        Complaint.Model.History.cacheHistory.put(complaintId + "." +jtParams.jtStartIndex, history);
                        jtData = callbackSuccess(history);
                    }
                    return jtData;
                }
            );
        }
    }

    ,Time: {
        create : function() {
        }
        ,onInitialized: function() {
        }

        , API_RETRIEVE_TIMESHEETS: "/api/v1/service/timesheet/"


        ,retrieveTimesheets : function(complaintId) {
            var url = App.getContextPath() + this.API_RETRIEVE_TIMESHEETS;
            url += "objectId/" + complaintId + "/";
            url += "objectType/" + Complaint.Model.DOC_TYPE_COMPLAINT;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Complaint.Controller.modelRetrievedTimesheets(response);

                    } else {
                        if (Complaint.Model.Time.validateTimesheets(response)) {
                            var timesheets = response;
                            Complaint.Model.Time.cacheTimesheets.put(complaintId, timesheets);
                            Complaint.Controller.modelRetrievedTimesheets(timesheets);
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


        ,retrieveCostsheets : function(complaintId) {
            var url = App.getContextPath() + this.API_RETRIEVE_COSTSHEETS;
            url += "objectId/" + complaintId + "/";
            url += "objectType/" + Complaint.Model.DOC_TYPE_COMPLAINT;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Complaint.Controller.modelRetrievedCostsheets(response);

                    } else {
                        if (Complaint.Model.Cost.validateCostsheets(response)) {
                            var costsheets = response;
                            Complaint.Model.Cost.cacheCostsheets.put(complaintId, costsheets);
                            Complaint.Controller.modelRetrievedCostsheets(costsheets);
                        }
                    }
                }
                ,url
            )
        }
    }
};

