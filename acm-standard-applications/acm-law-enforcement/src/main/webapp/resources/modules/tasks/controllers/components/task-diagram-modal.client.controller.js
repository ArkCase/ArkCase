'use strict';

angular.module('tasks').controller('Tasks.DiagramModalController', ['$scope', '$modalInstance', 'Task.WorkflowService', 'taskId' , 'showLoader', 'showError',
        function ($scope, $modalInstance, TaskWorkflowService, taskId, showLoader, showError) {
            $scope.showLoader = showLoader;
            $scope.showError = showError;
            TaskWorkflowService.diagram(taskId).then(function(response){
                $scope.showLoader = false;
                $scope.showError = false;
                $scope.diagramData = 'data:image/png;base64,' + response.data;
            }, function(error){
                $scope.showLoader = false;
                $scope.showError = true;
            });
            $scope.onClickClose = function () {
                $modalInstance.dismiss('Close');
            };
        }
    ]
);
