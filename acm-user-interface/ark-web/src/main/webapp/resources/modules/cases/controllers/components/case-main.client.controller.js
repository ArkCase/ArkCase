'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', 'UtilService', 'ConfigService', 'Case.InfoService'
    , function ($scope, Util, ConfigService, CaseInfoService) {
        //$scope.$emit('req-component-config', 'main');
        //$scope.$on('component-config', function (e, componentId, config) {
        //	if (componentId == 'main') {
        //		$scope.config = config;
        //	}
        //});

        var promiseConfig = ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
			$scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            $scope.widgets = Util.goodMapValue($scope.config, "widgets", []);
			return moduleConfig;
		});

        ConfigService.getComponentConfig("cases", "main").then(function (componentConfig) {
            var a1 = componentConfig;
            var a2 = $scope.config;

            return componentConfig;
        });


        $scope.$on('case-updated', function (e, data) {
            if (!CaseInfoService.validateCaseInfo(data)) {
                return;
            }
            $scope.caseInfo = data;

            //promiseConfig.then(function(moduleConfig) {
            $scope["details"] = "11111";
            $scope["peopleNames"] = "22222";
            $scope["documentCount"] = "33333";
            $scope["participants"] = "44444";
            $scope["noteCount"] = "55555";
            $scope["taskCount"] = "66666";
            $scope["referenceCount"] = "rrrrr";
            $scope["historyCount"] = "hhhhh";
            $scope["correspondenceCount"] = "cccccc";
            $scope["timesheetCount"] = "timetttt";
            $scope["costsheetCount"] = "costcccc";
            $scope["calendarEventCount"] = "calllll";
            //    return moduleConfig;
            //});
        });
	}
]);

