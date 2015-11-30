'use strict';
/**
 * @ngdoc controller
 * @name reports.controller:Reports.SelectionController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/controllers/components/reports-selection.client.controller.js modules/reports/controllers/components/reports-selection.client.controller.js}
 *
 * The Reports module's report selection controller
 */
angular.module('reports').controller('Reports.SelectionController', ['$scope',
    function ($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'reportselection');

        $scope.config = null;
        function applyConfig(e, componentId, config) {
            if (componentId == 'reportselection') {
                $scope.config = config;
                // Watch parent reports variable
                $scope.$watchCollection('data.reports', updateAvailableReports);
            }
        }

        function updateAvailableReports() {
            $scope.availableReports = [];
            _.forEach($scope.data.reports, function (value, key) {
                $scope.availableReports.push({"name": key.split('_').join(' '), "id": key});
            })
        }
    }
]);