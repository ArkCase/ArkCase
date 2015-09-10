'use strict';

angular.module('services').factory('ValidatorService', ["UtilService",
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
        }
    }
]);