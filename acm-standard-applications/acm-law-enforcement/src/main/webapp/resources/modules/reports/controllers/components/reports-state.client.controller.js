'use strict';

angular.module('reports').controller('Reports.StateController', [ '$scope', 'ConfigService', 'Object.LookupService', function($scope, ConfigService, ObjectLookupService) {

    // This configuration can be moved to ~/.arkcase/acm/acm-reports-parameters.json
    ConfigService.getComponentConfig("reports", "caseStates").then(function(config) {
        $scope.config = config;
        $scope.$watchCollection('data.reportSelected', function(newValue, oldValue) {
            if (newValue) {
                $scope.data.reportSelected = newValue;
                if ($scope.config.resetCaseStateValues.indexOf($scope.data.reportSelected) > -1) {
                    $scope.data.caseStateSelected = '';
                }
            }
        });
        return config;
    });

    ObjectLookupService.getLookupByLookupName("reportStates").then(function(reportStates) {
        $scope.reportStates = reportStates;
        return reportStates;
    });

} ]);