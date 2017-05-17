'use strict';

angular.module('admin').controller('Admin.WorkflowsConfigDiagramController', ['$scope', '$modalInstance', 'Admin.WorkflowsConfigService', 'deploymentId', 'key', 'version' , 'showLoader', 'showError',
        function ($scope, $modalInstance, AdminWorkflowsConfigService, deploymentId, key, version, showLoader, showError) {
            $scope.showLoader = showLoader;
            $scope.showError = showError;
            AdminWorkflowsConfigService.diagram(deploymentId, key, version).then(function(response){
                $scope.showLoader = false;
                $scope.showError = false;
                $scope.diagramData = 'data:image/png;base64,' + response.data.data;
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
