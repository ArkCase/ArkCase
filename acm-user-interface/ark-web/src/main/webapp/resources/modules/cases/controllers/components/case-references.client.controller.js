'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$window', 'UtilService', 'ValidationService', 'LookupService',
    function ($scope, $window, Util, Validator, LookupService) {
		$scope.$emit('req-component-config', 'references');


        var promiseObjectTypes = Util.servicePromise({
            service: LookupService.getObjectTypes
            , callback: function (data) {
                $scope.objectTypes = [];
                _.forEach(data, function (item) {
                    $scope.objectTypes.push(item);
                });
                return $scope.objectTypes;
            }
        });


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
			}
		}


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = Util.goodValue(data, {references: []});
                $scope.gridOptions.data = $scope.caseInfo.references;
            }
        });


        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            $q.all([promiseObjectTypes]).then(function (data) {
                var type = Util.goodMapValue([rowEntity, "targetType"]);
                var find = _.where($scope.objectTypes, {type: type});
                if (0 < find.length) {
                    var url = Util.goodValue(find[0].url);
                    var id = Util.goodMapValue([rowEntity, "targetId"]);
                    url = url.replace(":id", id);
                    $window.location.href = url;
                }
            });
        }

	}
]);