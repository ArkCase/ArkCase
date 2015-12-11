'use strict';

/**
 * @ngdoc controller
 * @name cost-tracking.controller:CostTrackingController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/controllers/cost-tracking.client.controller.js modules/cost-tracking/controllers/cost-tracking.client.controller.js}
 *
 * The Cost Tracking module main controller
 */
angular.module('cost-tracking').controller('CostTrackingController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'ObjectService', 'Helper.ObjectTreeService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, CostTrackingInfoService, ObjectService, HelperObjectTreeService) {

		var promiseGetModuleConfig = ConfigService.getModuleConfig("cost-tracking").then(function (config) {
			$scope.config = config;
            $scope.componentLinks = HelperObjectTreeService.createComponentLinks(config, ObjectService.ObjectTypes.COSTSHEET);
            $scope.activeLinkId = "main";
			return config;
		});
		$scope.$on('req-component-config', function (e, componentId) {
			promiseGetModuleConfig.then(function (config) {
				var componentConfig = _.find(config.components, {id: componentId});
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		});

        $scope.$on('req-select-costsheet', function (e, selectedCostsheet) {
            var components = Util.goodArray(selectedCostsheet.components);
            $scope.activeLinkId = (1 == components.length) ? components[0] : "main";
        });

        $scope.getActive = function (linkId) {
            return ($scope.activeLinkId == linkId) ? "active" : ""
        };

        $scope.onClickComponentLink = function (linkId) {
            $scope.activeLinkId = linkId;
            $state.go('cost-tracking.' + linkId, {
                id: $stateParams.id
            });
        };

		$scope.progressMsg = $translate.instant("costTracking.progressNoCostsheet");
		$scope.$on('req-select-costsheet', function (e, selectedCostsheet) {
			$scope.$broadcast('costsheet-selected', selectedCostsheet);

			var id = Util.goodMapValue(selectedCostsheet, "nodeId", null);
			loadCostsheet(id);
		});

		var loadCostsheet = function (id) {
            if (Util.goodPositive(id)) {
				if ($scope.costsheetInfo && $scope.costsheetInfo.id != id) {
					$scope.costsheetInfo = null;
				}
				$scope.progressMsg = $translate.instant("costTracking.progressLoading") + " " + id + "...";

				CostTrackingInfoService.getCostTrackingInfo(id).then(
					function (costsheetInfo) {
						$scope.progressMsg = null;
						$scope.costsheetInfo = costsheetInfo;
						$scope.$broadcast('costsheet-updated', costsheetInfo);
						return costsheetInfo;
					}
					, function (errorData) {
						$scope.costsheetInfo = null;
						$scope.progressMsg = $translate.instant("costTracking.progressError") + " " + id;
						return errorData;
					}
				);
			}
		};
		loadCostsheet($stateParams.id);
	}
]);