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
angular.module('cost-tracking').controller('CostTrackingController', ['$scope', '$stateParams', '$translate', 'ConfigService', 'CallCostTrackingService', 'UtilService',
	function($scope, $stateParams, $translate, ConfigService, CallCostTrackingService, Util) {
		var promiseGetModuleConfig = ConfigService.getModuleConfig("cost-tracking").then(function (config) {
			$scope.config = config;
			return config;
		});
		$scope.$on('req-component-config', function (e, componentId) {
			promiseGetModuleConfig.then(function (config) {
				var componentConfig = _.find(config.components, {id: componentId});
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		});

		$scope.progressMsg = $translate.instant("costTracking.progressNoCostsheet");
		$scope.$on('req-select-costsheet', function (e, selectedCostsheet) {
			$scope.$broadcast('costsheet-selected', selectedCostsheet);

			var id = Util.goodMapValue(selectedCostsheet, "nodeId", null);
			loadCostsheet(id);
		});

		var loadCostsheet = function (id) {
			if (id) {
				if ($scope.costsheetInfo && $scope.costsheetInfo.id != id) {
					$scope.costsheetInfo = null;
				}
				$scope.progressMsg = $translate.instant("costTracking.progressLoading") + " " + id + "...";



				CallCostTrackingService.getCostTrackingInfo(id).then(
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