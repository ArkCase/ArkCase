'use strict';

angular.module('time-tracking').controller('TimeTrackingController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'ObjectService', 'Helper.ObjectTreeService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, TimeTrackingInfoService, ObjectService, HelperObjectTreeService) {

		var promiseGetModuleConfig = ConfigService.getModuleConfig("time-tracking").then(function (config) {
			$scope.config = config;
            $scope.componentLinks = HelperObjectTreeService.createComponentLinks(config, ObjectService.ObjectTypes.TIMESHEET);
            $scope.activeLinkId = "main";
			return config;
		});
		$scope.$on('req-component-config', function (e, componentId) {
			promiseGetModuleConfig.then(function (config) {
				var componentConfig = _.find(config.components, {id: componentId});
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		});

        $scope.$on('req-select-timesheet', function (e, selectedTimesheet) {
            var components = Util.goodArray(selectedTimesheet.components);
            $scope.activeLinkId = (1 == components.length) ? components[0] : "main";
        });

        $scope.getActive = function (linkId) {
            return ($scope.activeLinkId == linkId) ? "active" : ""
        };

        $scope.onClickComponentLink = function (linkId) {
            $scope.activeLinkId = linkId;
            $state.go('time-tracking.' + linkId, {
                id: $stateParams.id
            });
        };

		$scope.linksShown = false;
		$scope.toggleShowLinks = function () {
			$scope.linksShown = !$scope.linksShown;
		};

		$scope.progressMsg = $translate.instant("timeTracking.progressNoTimesheet");
		$scope.$on('req-select-timesheet', function (e, selectedTimesheet) {
			$scope.$broadcast('timesheet-selected', selectedTimesheet);

			var id = Util.goodMapValue(selectedTimesheet, "nodeId", null);
			loadTimesheet(id);
		});

		var loadTimesheet = function (id) {
            if (Util.goodPositive(id)) {
				if ($scope.timesheetInfo && $scope.timesheetInfo.id != id) {
					$scope.timesheetInfo = null;
				}
				$scope.progressMsg = $translate.instant("timeTracking.progressLoading") + " " + id + "...";

				TimeTrackingInfoService.getTimeTrackingInfo(id).then(
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