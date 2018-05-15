'use strict';

angular.module('admin').controller('AdminController', [ '$scope', 'ConfigService', function($scope, ConfigService) {
    $scope.config = ConfigService.getModule({
        moduleId: 'admin'
    });
    $scope.$on('req-component-config', onConfigRequest);
    $scope.$on('created-report-schedule', onCreatedReportSchedule);

    function onCreatedReportSchedule(e, data) {
        $scope.$broadcast('new-report-schedule', data);
    }

    function onConfigRequest(e, componentId) {
        $scope.config.$promise.then(function(config) {
            var componentConfig = _.find(config.components, {
                id: componentId
            });
            $scope.$broadcast('component-config', componentId, componentConfig);
        });
    }
} ]);