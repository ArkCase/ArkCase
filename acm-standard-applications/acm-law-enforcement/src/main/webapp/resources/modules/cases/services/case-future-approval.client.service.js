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
            url: 'api/latest/plugin/task/getBuckslipFutureTasks/businessProcessId/' + businessProcessId
        });
    };

    var _getBuckslipPastTasks = function (businessProcessId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/getBuckslipPastTasks/businessProcessId/' + businessProcessId
        });
    };

    var _getBuckslipProcessesForChildren = function (objectType, objectId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/getBuckslipProcessesForChildren/objectType/' + objectType + '/objectId/' + objectId
        });
    };

    var _isWorkflowInitiable = function (businessProcessId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/isWorkflowInitiable/businessProcessId/' + businessProcessId
        });
    };

    var _isWorkflowWithdrawable = function (businessProcessId) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/task/isWorkflowWithdrawable/businessProcessId/' + businessProcessId
        });
    };

    var _initiateRoutingWorkflow = function (businessProcessId, receiveTaskId) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/initiateRoutingWorkflow/businessProcessId/' + businessProcessId + '/receiveTaskId/' + receiveTaskId
        });
    };

    var _withdrawRoutingWorkflow = function (taskId, messageName) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/withdrawRoutingWorkflow/taskId/' + taskId + '/messageName/' + messageName
        });
    };

    var _updateBuckslipProcess = function (buckslipProcess) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/updateBuckslipProcess',
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
