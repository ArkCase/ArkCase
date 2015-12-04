'use strict';
/**
 * @ngdoc controller
 * @name reports.controller:Reports.DatepickersController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/controllers/components/reports-datepickers.client.controller.js modules/reports/controllers/components/reports-datepickers.client.controller.js}
 *
 * The Reports module's date pickers controller
 */
angular.module('reports').controller('Reports.DatepickersController', ['$scope',
    function ($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'datepickers');

        $scope.config = null;
        function applyConfig(e, componentId, config) {
            if (componentId == 'datepickers') {
                $scope.config = config;
            }
        }

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
    }
]);