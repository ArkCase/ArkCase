'use strict';

///**
// * @ngdoc controller
// * @name time-tracking.controller:TimeTrackingController
// *
// * @description
// * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/time-tracking/controllers/time-tracking.client.controller.js modules/time-tracking/controllers/time-tracking.client.controller.js}
// *
// * The Time Tracking module main controller
// */
angular.module('time-tracking').controller('TimeTrackingController', ['$scope', '$stateParams', '$translate', 'ConfigService', 'TimeTrackingService', 'CallTimeTrackingService', 'UtilService',
	function ($scope, $stateParams, $translate, ConfigService, TimeTrackingService, CallTimeTrackingService, Util) {
		var promiseGetModuleConfig = ConfigService.getModuleConfig("time-tracking").then(function (config) {
			$scope.config = config;
			return config;
		});
		$scope.$on('req-component-config', function (e, componentId) {
			promiseGetModuleConfig.then(function (config) {
				var componentConfig = _.find(config.components, {id: componentId});
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		});

		$scope.progressMsg = $translate.instant("timeTracking.progressNoTimesheet");
		$scope.$on('req-select-timesheet', function (e, selectedTimesheet) {
			$scope.$broadcast('timesheet-selected', selectedTimesheet);

			var id = Util.goodMapValue(selectedTimesheet, "nodeId", null);
			loadTimesheet(id);
		});

		var loadTimesheet = function (id) {
			if (id) {
				if ($scope.timesheetInfo && $scope.timesheetInfo.id != id) {
					$scope.timesheetInfo = null;
				}
				$scope.progressMsg = $translate.instant("timeTracking.progressLoading") + " " + id + "...";


				CallTimeTrackingService.getTimeTrackingInfo(id).then(
					function (timesheetInfo) {
						$scope.progressMsg = null;
						$scope.timesheetInfo = timesheetInfo;
						$scope.$broadcast('timesheet-updated', timesheetInfo);
						return timesheetInfo;
					}
					, function (errorData) {
						$scope.timesheetInfo = null;
						$scope.progressMsg = $translate.instant("timeTracking.progressError") + " " + id;
						return errorData;
					}
				);
			}
		};
		loadTimesheet($stateParams.id);
	}
]);