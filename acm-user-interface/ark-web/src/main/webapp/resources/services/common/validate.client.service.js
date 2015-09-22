'use strict';

angular.module('services').factory('ValidationService', ["UtilService",
    function (Util) {return {

        validateUsers: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        }
        ,validateUser: function(data){
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.name)) {
                return false;
            }
            if (Util.isEmpty(data.last_name_lcs)) {
                return false;
            }
            if (Util.isEmpty(data.first_name_lcs)) {
                return false;
            }
            if (Util.isEmpty(data.object_id_s)) {
                return false;
            }
            if (Util.isEmpty(data.object_type_s)) {
                return false;
            }
            return true;
        }
        ,validateCaseFile: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id) || Util.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Util.isArray(data.childObjects)) {
                return false;
            }
            if (!Util.isArray(data.milestones)) {
                return false;
            }
            if (!Util.isArray(data.participants)) {
                return false;
            }
            if (!Util.isArray(data.personAssociations)) {
                return false;
            }
            if (!Util.isArray(data.references)) {
                return false;
            }
            return true;
        }
        ,validatePersonAssociations: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validatePersonAssociation(data[i])) {
                    return false;
                }
            }
            return true;
        }
        ,validatePersonAssociation: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.person)) {
                return false;
            }
            if (!Util.isArray(data.person.contactMethods)) {
                return false;
            }
            if (!Util.isArray(data.person.addresses)) {
                return false;
            }
            if (!Util.isArray(data.person.securityTags)) {
                return false;
            }
            if (!Util.isArray(data.person.personAliases)) {
                return false;
            }
            if (!Util.isArray(data.person.organizations)) {
                return false;
            }
            return true;
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
        
    }}
]);