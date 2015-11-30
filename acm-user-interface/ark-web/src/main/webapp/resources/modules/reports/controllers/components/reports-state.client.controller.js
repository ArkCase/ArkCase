'use strict';
/**
 * @ngdoc controller
 * @name reports.controller:Reports.SelectionController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/controllers/components/reports-state.client.controller.js modules/reports/controllers/components/reports-state.client.controller.js}
 *
 * The Reports module's report selection controller
 */
angular.module('reports').controller('Reports.StateController', ['$scope',
    function ($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'caseStates');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'caseStates') {
                $scope.config = config;


                $scope.$watchCollection('data.reportSelected', function (newValue, oldValue) {
                    if (newValue) {
                        if ($scope.data.reportSelected == $scope.config.resetCaseStateValue) {
                            $scope.data.caseStateSelected = '';
                        }
                    }
                });
            }
        }
    }
]);