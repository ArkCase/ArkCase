'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$window', 'UtilService', 'ValidationService', 'LookupService',
    function ($scope, $window, Util, Validator, LookupService) {
		$scope.$emit('req-component-config', 'references');

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
		function applyConfig(e, componentId, config) {
			if (componentId == 'references') {
				$scope.config = config;

                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    enableFiltering: config.enableFiltering,
                    columnDefs: config.columnDefs
                };

                var promiseTypes = Util.servicePromise({
                    service: LookupService.getObjectTypes
                    , callback: function (data) {
                        $scope.objectTypes = [];
                        _.forEach(data, function (item) {
                            $scope.objectTypes.push(item);
                        });
                        return $scope.objectTypes;
                    }
                });
			}
		}


        $scope.$on('case-retrieved', function (e, data) {
            $scope.caseInfo = Util.goodValue(data, {references: []});
            $scope.gridOptions.data = $scope.caseInfo.references;

            //var test = [{
            //    associationId: 105
            //    , associationType: "REFERENCE"
            //    , category: null
            //    , created: "2015-08-03T11:23:32.567-0400"
            //    , creator: "ann-acm"
            //    , modified: "2015-08-03T11:23:32.567-0400"
            //    , modifier: "ann-acm"
            //    , parentId: 104
            //    , parentName: "20150803_104"
            //    , parentType: "CASE_FILE"
            //    , status: "ACTIVE"
            //    , targetId: 106
            //    , targetName: "20150803_106"
            //    , targetSubtype: null
            //    , targetTitle: "20150803_106"
            //    , targetType: "COMPLAINT"
            //}];
            //$scope.gridOptions.data = test;
        });


        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            var type = Util.goodMapValue([rowEntity, "targetType"]);
            var find = _.where($scope.objectTypes, {type: type});
            if (0 < find.length) {
                var url = Util.goodValue(find[0].url);
                var id = Util.goodMapValue([rowEntity, "targetId"]);
                url = url.replace(":id", id);
                $window.location.href = url;
            }
        }

	}
]);