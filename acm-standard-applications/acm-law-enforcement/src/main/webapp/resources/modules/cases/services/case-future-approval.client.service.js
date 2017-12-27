'use strict';

/**
 * @ngdoc service
 * @name services:Case.FutureApprovalService
 *
 * @description
 *
 *
 * Case.FutureApprovalService provides functions for Case File Approval Routing
 */
angular.module('services').factory('Case.FutureApprovalService', ['$http', function ($http) {

    var _getBuckslipFutureTasks = function (businessProcessId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/businessProcess/'+ businessProcessId + '/futureTasks'
        });
    };

    var _getBuckslipPastTasks = function (businessProcessId, readFromHistory) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/businessProcess/' + businessProcessId + '/pastTasks',
            params: {
                readFromHistory: readFromHistory
            }
        });
    };

    var _getBuckslipProcessesForChildren = function (objectType, objectId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/objectType/' + objectType + '/objectId/' + objectId + '/buckslipProcessesForChildren'
        });
    };

    var _isWorkflowInitiable = function (businessProcessId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/businessProcess/' + businessProcessId + '/initiatable'
        });
    };

    var _isWorkflowWithdrawable = function (businessProcessId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/businessProcess/' + businessProcessId + '/withdrawable'
        });
    };

    var _initiateRoutingWorkflow = function (businessProcessId) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/businessProcess/' + businessProcessId + '/initiate'
        });
    };

    var _withdrawRoutingWorkflow = function (taskId) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/' + taskId + '/withdraw'
        });
    };

    var _updateBuckslipProcess = function (buckslipProcess) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/buckslipProcesses',
            data: buckslipProcess,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    return {
        getBuckslipFutureTasks: _getBuckslipFutureTasks,
        getBuckslipPastTasks: _getBuckslipPastTasks,
        getBuckslipProcessesForChildren: _getBuckslipProcessesForChildren,
        isWorkflowInitiable: _isWorkflowInitiable,
        isWorkflowWithdrawable: _isWorkflowWithdrawable,
        initiateRoutingWorkflow: _initiateRoutingWorkflow,
        withdrawRoutingWorkflow: _withdrawRoutingWorkflow,
        updateBuckslipProcess: _updateBuckslipProcess
    }

    }
]);
