'use strict';

angular.module('tasks').controller('Tasks.DiagramByProcessIdModalController', [ '$scope', '$modalInstance', 'Task.WorkflowService', 'processId', 'showLoader', 'showError', function($scope, $modalInstance, TaskWorkflowService, processId, showLoader, showError) {
    $scope.showLoader = showLoader;
    $scope.showError = showError;
    TaskWorkflowService.diagramByProcessId(processId).then(function(response) {
        $scope.showLoader = false;
        $scope.showError = false;
        $scope.diagramData = 'data:image/png;base64,' + response.data;
    }, function(error) {
        $scope.showLoader = false;
        $scope.showError = true;
    });
    $scope.onClickClose = function() {
        $modalInstance.dismiss('Close');
    };
} ]);
