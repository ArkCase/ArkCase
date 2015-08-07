/**
 * CaseFileSplit.Model
 *
 * @author jwu
 */
CaseFileSplit.Model = CaseFileSplit.Model || {
    create : function() {
        if (CaseFileSplit.Model.MicroData.create)         {CaseFileSplit.Model.MicroData.create();}
        if (CaseFileSplit.Model.Lookup.create)         {CaseFileSplit.Model.Lookup.create();}
        if (CaseFileSplit.Model.Detail.create)         {CaseFileSplit.Model.Detail.create();}
        if (CaseFileSplit.Model.People.create)         {CaseFileSplit.Model.People.create();}
        if (CaseFileSplit.Model.Notes.create)          {CaseFileSplit.Model.Notes.create();}
        if (CaseFileSplit.Model.Summary.create)        {CaseFileSplit.Model.Summary.create();}

        if (CaseFileSplit.Service.create)              {CaseFileSplit.Service.create();}
    }
    ,onInitialized: function() {
        if (CaseFileSplit.Model.MicroData.onInitialized)         {CaseFileSplit.Model.MicroData.onInitialized();}
        if (CaseFileSplit.Model.Lookup.onInitialized)         {CaseFileSplit.Model.Lookup.onInitialized();}
        if (CaseFileSplit.Model.People.onInitialized)         {CaseFileSplit.Model.People.onInitialized();}
        if (CaseFileSplit.Model.Detail.onInitialized)         {CaseFileSplit.Model.Detail.onInitialized();}
        if (CaseFileSplit.Model.Notes.onInitialized)          {CaseFileSplit.Model.Notes.onInitialized();}
        if (CaseFileSplit.Model.Summary.onInitialized)          {CaseFileSplit.Model.Summary.onInitialized();}

        if (CaseFileSplit.Service.onInitialized)              {CaseFileSplit.Service.onInitialized();}
    }


    ,DOC_TYPE_CASE_FILE  : "CASE_FILE"
    ,DOC_TYPE_FILE       : "FILE"
    ,DOC_CATEGORY_DOCUMENT_SM : "Document"
    ,DOC_TYPE_FILE_SM       : "file"
    ,DOC_CATEGORY_FILE_SM   : "Document"



    ,MicroData: {
        create : function() {
            this.parentCaseFileId      = Acm.Object.MicroData.get("parentCasefileId");
        }
        ,onInitialized: function() {
        }
    }

    ,Detail: {
        create : function() {
            this.cacheParentCaseFile = new Acm.Model.CacheFifo();
            this.cacheSplittedCaseFile = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(CaseFileSplit.Controller.VIEW_CHANGED_DETAIL              , this.onViewChangedDetail);
        }
        ,onInitialized: function() {
            CaseFileSplit.Service.Detail.retrieveParentCaseFile(CaseFileSplit.Model.MicroData.parentCaseFileId);
        }

        ,onViewChangedDetail: function(caseFileId, details) {
            CaseFileSplit.Service.Detail.saveDetail(caseFileId, details);
        }

        ,getAssignee: function(caseFile) {
            var assignee = null;
            if (CaseFileSplit.Model.Detail.validateCaseFile(caseFile)) {
                if (Acm.isArray(caseFile.participants)) {
                    for (var i = 0; i < caseFile.participants.length; i++) {
                        var participant =  caseFile.participants[i];
                        if ("assignee" == participant.participantType) {
                            assignee = participant.participantLdapId;
                            break;
                        }
                    }
                }
            }
            return assignee;
        }
        
        ,getGroup: function(caseFile) {
            var group = null;
            if (CaseFileSplit.Model.Detail.validateCaseFile(caseFile)) {
                if (Acm.isArray(caseFile.participants)) {
                    for (var i = 0; i < caseFile.participants.length; i++) {
                        var participant =  caseFile.participants[i];
                        if ("owning group" == participant.participantType) {
                            group = participant.participantLdapId;
                            break;
                        }
                    }
                }
            }
            return group;
        }


        ,validateCaseFile: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Acm.isArray(data.childObjects)) {
                return false;
            }
            if (!Acm.isArray(data.milestones)) {
                return false;
            }
            if (!Acm.isArray(data.participants)) {
                return false;
            }
            if (!Acm.isArray(data.personAssociations)) {
                return false;
            }
            if (!Acm.isArray(data.references)) {
                return false;
            }
            return true;
        }
    }

    ,People: {
        create : function() {
        }
        ,onInitialized: function() {
        }
        ,validateDeletedPersonAssociation: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedPersonAssociationId)) {
                return false;
            }
            return true;
        }
        ,validatePersonAssociation: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.person)) {
                return false;
            }
            if (!Acm.isArray(data.person.contactMethods)) {
                return false;
            }
            if (!Acm.isArray(data.person.addresses)) {
                return false;
            }
            if (!Acm.isArray(data.person.securityTags)) {
                return false;
            }
            if (!Acm.isArray(data.person.personAliases)) {
                return false;
            }
            if (!Acm.isArray(data.person.organizations)) {
                return false;
            }
            return true;
        }
    }

    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
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
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            return true;
        }
    }


    ,Lookup: {
        create: function() {
            this._assignees    =  new Acm.Model.CacheFifo();
            this._groups    =  new Acm.Model.CacheFifo();
            this._users    = new Acm.Model.CacheFifo();
            Acm.Dispatcher.addEventListener(CaseFileSplit.Controller.MODEL_RETRIEVED_PARENT_CASE_FILE             ,this.onModelRetrievedParentCaseFile);
        }
        ,onInitialized: function() {
            var users = CaseFileSplit.Model.Lookup.getUsers();
            if (Acm.isEmpty(users)) {
                CaseFileSplit.Service.Lookup.retrieveUsers();
            } else {
                CaseFileSplit.Controller.modelRetrievedUsers(users);
            }
        }
        ,getAssignees: function(caseFileId) {
            return this._assignees.get(caseFileId);
        }
        ,setAssignees: function(caseFileId, assignees) {
            this._assignees.put(caseFileId, assignees);
        }
        ,getGroups: function(caseFileId) {
            return this._groups.get(caseFileId);
        }
        ,setGroups: function(caseFileId, groups) {
            this._groups.put(caseFileId, groups);
        }
        ,getUsers: function(caseFileId) {
            return this._users.get(caseFileId);
        }
        ,setUsers: function(caseFileId,users) {
            this._users.put(caseFileId, users);
        }

        ,onModelRetrievedParentCaseFile: function(objData) {
            CaseFileSplit.Model.Lookup.refreshAssigneesAndGroups();
        }

        ,refreshAssigneesAndGroups: function() {
            var caseFileId = CaseFileSplit.Model.MicroData.parentCaseFileId;

            var assignees = CaseFileSplit.Model.Lookup.getAssignees(caseFileId);
            if (Acm.isEmpty(assignees)) {
                CaseFileSplit.Service.Lookup.retrieveAssignees(caseFileId);
            } else {
                CaseFileSplit.Controller.modelFoundAssignees(assignees);
            }

            var groups = CaseFileSplit.Model.Lookup.getGroups(caseFileId);
            if (Acm.isEmpty(groups)) {
                CaseFileSplit.Service.Lookup.retrieveGroups();
            } else {
                CaseFileSplit.Controller.modelRetrievedGroups(groups);
            }
        }


        ,_personTypes : ['Complaintant','Subject','Witness','Wrongdoer','Other', 'Initiator', 'Victim', 'Defendant', 'Investigating Officer', 'Police Witness']
        ,getPersonTypes : function() {
            return this._personTypes;
        }

        ,_personTitles : ['Mr','mr', 'Mrs','mrs', 'Ms','ms', 'Miss','miss']
        ,getPersonTitles : function() {
            return this._personTitles;
        }

        ,_participantTypes : {'assignee': 'Assignee', 'co-owner': 'Co-Owner', 'supervisor': 'Supervisor', 'owning group': 'Owning Group', 'approver': 'Approver', 'collaborator': 'Collaborator', 'follower': 'Follower', 'reader': 'Reader', 'No Access': 'No Access'}
        ,getParticipantTypes : function() {
            return this._participantTypes;
        }

        ,validateUsers: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }

        ,validateGroups: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }

        ,validateAssignees: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }


        ,createGroupGetParameter: function(caseFile){
            var groupGetParameter = '';
            var groupName = CaseFileSplit.Model.Detail.getGroup(caseFile);
            if (groupName && groupName.length > 0) {
                groupGetParameter = '/' + groupName;
            }

            return groupGetParameter;
        }
        ,createCurrentAssigneeGetParameter: function(caseFile){
            var currentAssigneeGetParameter = '';
            var currentAssignee = CaseFileSplit.Model.Detail.getAssignee(caseFile);
            if (currentAssignee && currentAssignee.length > 0) {
                currentAssigneeGetParameter = '/' + currentAssignee;
            }

            return currentAssigneeGetParameter;
        }
    }

    ,Summary: {
        create: function() {
            Acm.Dispatcher.addEventListener(CaseFileSplit.Controller.VIEW_RETRIEVED_SUMMARY             ,this.onViewRetrievedSummary);
        }
        ,onInitialized: function() {

        }
        ,onViewRetrievedSummary: function(summary){
            CaseFileSplit.Service.Summary.splitCaseFile(summary);
        }
        ,validateSummary: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.caseFileId)) {
                return false;
            }
            if (!Acm.isArray(data.attachments)) {
                return false;
            }
            if (Acm.isEmpty(data.preserveFolderStructure)) {
                return false;
            }
            return true;
        }
    }

};

