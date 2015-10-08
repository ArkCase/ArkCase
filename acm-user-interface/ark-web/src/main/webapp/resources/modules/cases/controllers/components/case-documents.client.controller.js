'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', 'UtilService', 'ValidationService', 'StoreService', 'LookupService',
    function ($scope, $stateParams, Util, Validator, Store, LookupService) {
		$scope.$emit('req-component-config', 'documents');

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
		function applyConfig(e, componentId, config) {
			if (componentId == 'documents') {
				$scope.config = config;
			}
		}

        //
        //var promiseFileTypes = Util.servicePromise({
        //    service: LookupService.getConfig
        //    , param: {name: "caseFile"}
        //    , callback: function (data) {
        //        $scope.fileTypes = Util.goodJsonObj(data.fileTypes, []);
        //        return $scope.fileTypes;
        //    }
        //});
        //var promiseFileTypes = Util.servicePromise({
        //    service: LookupService.getFileTypes
        //    , callback: function (data) {
        //        $scope.fileTypes = Util.goodArray(data);
        //        return $scope.fileTypes;
        //    }
        //});

        $scope.objectType = Util.Constant.OBJTYPE_CASE_FILE;
        $scope.objectId = $stateParams.id;
        $scope.containerId = 0;
        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;
                //$scope.objectType = Util.Constant.OBJTYPE_CASE_FILE;
                //$scope.objectId = Util.goodValue(data.id, 0);
            }
        });

        $scope.getArgs = function () {
            return {b: "big", d: {dc: "dc123"}};
        };
        $scope.loadData = function (folderId) {
            folderId = Util.goodValue(folderId, 0);
            if (0 >= folderId) {
                var z = 1;
                return [];
            }

            $scope.fileTypes = ["abc", "123"];
            var z = 1;
            return [{some: "value"}];
        };

	}
]);