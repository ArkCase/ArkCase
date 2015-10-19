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

        var promiseFileTypes = Util.serviceCall({
            service: LookupService.getFileTypes
            , onSuccess: function (data) {
                if (Validator.validateFileTypes(data)) {
                    $scope.fileTypes = $scope.fileTypes || [];
                    $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(data));
                    return $scope.fileTypes;
                }
            }
        });
        var promiseFormTypes = Util.serviceCall({
            service: LookupService.getPlainforms
            , param: {objType: Util.Constant.OBJTYPE_CASE_FILE}
            , onSuccess: function (data) {
                if (Validator.validatePlainForms(data)) {
                    var plainForms = data;
                    $scope.fileTypes = $scope.fileTypes || [];
                    for (var i = 0; i < plainForms.length; i++) {
                        var fileType = {};
                        fileType.type = plainForms[i].key;
                        fileType.label = Util.goodValue(plainForms[i].name);
                        fileType.url = Util.goodValue(plainForms[i].url);
                        fileType.urlParameters = Util.goodArray(plainForms[i].urlParameters);
                        fileType.form = true;
                        $scope.fileTypes.unshift(fileType);
                    }
                    return $scope.fileTypes;
                }
            }
        });

        $scope.treeArgs = {};

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

        var silentReplace = function (value, replace, replacement) {
            if (!Util.isEmpty(value) && value.replace) {
                value = value.replace(replace, replacement);
            }
            return value;
        };
        $scope.uploadForm = function (type, folderId, onCloseForm) {
            if ($scope.caseInfo) {
                //CaseFile.View.Documents.getFileTypeByType(type);
                var fileType = _.find($scope.fileTypes, {type: type});
                if (Validator.validatePlainForm(fileType)) {
                    var data = "_data=(";

                    var url = fileType.url;
                    var urlParameters = fileType.urlParameters;
                    var parametersAsString = '';
                    for (var i = 0; i < urlParameters.length; i++) {
                        var key = urlParameters[i].name;
                        var value = '';
                        if (!Util.isEmpty(urlParameters[i].defaultValue)) {
                            value = silentReplace(urlParameters[i].defaultValue, "'", "_0027_");
                        } else if (!Util.isEmpty(urlParameters[i].keyValue)) {
                            if (!Util.isEmpty($scope.caseInfo[urlParameters[i].keyValue])) {
                                value = silentReplace($scope.caseInfo[urlParameters[i].keyValue], "'", "_0027_");
                            }
                        }
                        value = encodeURIComponent(value);
                        parametersAsString += key + ":'" + Util.goodValue(value) + "',";
                    }
                    parametersAsString += "folderId:'" + folderId + "',";
                    data += parametersAsString;

                    url = url.replace("_data=(", data);
                    return url;
                }
            }
        }
	}
]);