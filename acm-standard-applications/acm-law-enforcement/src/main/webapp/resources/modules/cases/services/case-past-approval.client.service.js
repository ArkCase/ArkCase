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

    var _getBuckslipHistoryForCase = function (caseId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/case/'+ caseId
        });
    };

    return {
        getBuckslipHistoryForCase: _getBuckslipHistoryForCase
    }
  }
]);
