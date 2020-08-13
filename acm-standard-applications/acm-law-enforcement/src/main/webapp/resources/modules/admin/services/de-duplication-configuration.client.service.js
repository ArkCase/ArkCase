'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.DeDuplicationConfigurationService
 *
 * @description
 * Contains REST calls for Admin De Duplication Configuration
 */

angular.module('admin').factory('Admin.DeDuplicationConfigurationService', [ '$http', function($http) {
    return {

        getDeDuplicationConfiguration: function() {
            return $http({
                method: 'GET',
                url: 'api/latest/service/ecm/deDuplication/getConfig'
            });
        },

        saveDeDuplicationConfiguration: function(deDuplication) {
            return $http({
                method: 'PUT',
                url: 'api/latest/service/ecm/deDuplication/updateConfig',
                data: deDuplication,
                headers: {
                    "Content-Type": "application/json"
                }
            });
        }
    }
} ]);
