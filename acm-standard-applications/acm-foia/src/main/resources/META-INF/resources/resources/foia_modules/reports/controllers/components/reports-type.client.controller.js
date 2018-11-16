'use strict';
/**
 * @ngdoc controller
 * @name reports.controller:Reports.TypeController
 *
 *
 * The Reports module's report type controller
 */
angular.module('reports').controller('Reports.TypeController', [ '$scope', function($scope) {
    $scope.$on('component-config', applyConfig);
    $scope.$emit('req-component-config', 'foiaTypes');
    $scope.config = null;

    function applyConfig(e, componentId, config) {
        if (componentId == 'foiaTypes') {
            $scope.config = config;

            $scope.$watchCollection('data.reportSelected', function(newValue, oldValue) {
                if (newValue) {
                    $scope.data.reportSelected = newValue;
                    if ($scope.config.resetTypeValues.indexOf($scope.data.reportSelected) > -1) {
                        $scope.data.typeSelected = '';
                    }
                }
            });
        }
    }
} ]);