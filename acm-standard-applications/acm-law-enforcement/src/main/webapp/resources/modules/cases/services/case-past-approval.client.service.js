'use strict';

/**
 * @ngdoc service
 * @name services:Case.PastApprovalService
 *
 * @description
 *
 *
 * Case.PastApprovalService provides functions for Case File Approval Routing
 */
angular.module('services').factory('Case.PastApprovalService', ['$http', function ($http) {

    var _getCompletedBuckslipProcessIdForObject = function (objectType, objectId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/objectType/'+objectType+'/objectId/'+objectId+'/completedBuckslipProcessIdForObject'
        });
    };

    return {
        getCompletedBuckslipProcessIdForObject: _getCompletedBuckslipProcessIdForObject
    }
  }
]);
