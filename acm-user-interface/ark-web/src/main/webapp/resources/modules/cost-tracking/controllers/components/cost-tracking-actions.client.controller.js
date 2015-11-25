'use strict';

/**
 * @ngdoc controller
 * @name cost-tracking.controller:CostTracking.ActionsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/controllers/components/cost-tracking-actions.client.controller.js modules/cost-tracking/controllers/components/cost-tracking-actions.client.controller.js}
 *
 * The Cost Tracking actions controller
 */
angular.module('cost-tracking').controller('CostTracking.ActionsController', ['$scope', '$state',
    function ($scope, $state) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.costsheetInfo = null;

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
        });

        /**
         * @ngdoc method
         * @name loadNewCostsheetFrevvoForm
         * @methodOf cost-tracking.controller:CostTracking.ActionsController
         *
         * @description
         * Displays the create new costsheet Frevvo form for the user
         */
        $scope.loadNewCostsheetFrevvoForm = function () {
            $state.go('newCostsheet');
        };

        /**
         * @ngdoc method
         * @name loadExistingCostsheetFrevvoForm
         * @methodOf cost-tracking.controller:CostTracking.ActionsController
         *
         * @description
         * Displays the existing costsheet Frevvo form for the user
         */
        $scope.loadExistingCostsheetFrevvoForm = function () {
            $state.go('editCostsheet', { parentId : $scope.costsheetInfo.parentId, parentType : $scope.costsheetInfo.parentType});
        };

    }
]);