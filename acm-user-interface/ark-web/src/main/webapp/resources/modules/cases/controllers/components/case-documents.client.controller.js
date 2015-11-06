'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal', 'UtilService', 'ValidationService', 'StoreService', 'HelperService', 'LookupService',
    function ($scope, $stateParams, $modal, Util, Validator, Store, Helper, LookupService) {
		$scope.$emit('req-component-config', 'documents');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('documents' == componentId) {
                $scope.config = config;
            }
        });

        var cacheFileTypes = new Store.SessionData(Helper.SessionCacheNames.FILE_TYPES);
        var fileTypes = cacheFileTypes.get();
        var promiseFileTypes = Util.serviceCall({
            service: LookupService.getFileTypes
            , result: fileTypes
            , onSuccess: function (data) {
                if (Validator.validateFileTypes(data)) {
                    fileTypes = data;
                    cacheFileTypes.set(fileTypes);
                    return fileTypes;
                }
            }
        }).then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );

        var cacheFormTypes = new Store.SessionData(Helper.SessionCacheNames.FORM_TYPES);
        var formTypes = cacheFormTypes.get();
        var promiseFormTypes = Util.serviceCall({
            service: LookupService.getPlainforms
            , param: {objType: Helper.ObjectTypes.CASE_FILE}
            , result: formTypes
            , onSuccess: function (data) {
                if (Validator.validatePlainForms(data)) {
                    var plainForms = data;
                    formTypes = [];
                    _.each(plainForms, function (plainForm) {
                        var formType = {};
                        formType.type = plainForm.key;
                        formType.label = Util.goodValue(plainForm.name);
                        formType.url = Util.goodValue(plainForm.url);
                        formType.urlParameters = Util.goodArray(plainForm.urlParameters);
                        formType.form = true;
                        formTypes.unshift(formType);
                    });
                    cacheFormTypes.set(formTypes);
                    return formTypes;
                }
            }
        }).then(
            function (formTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = formTypes.concat(Util.goodArray($scope.fileTypes));
                return formTypes;
            }
        );

        $scope.treeArgs = {};

        $scope.objectType = Helper.ObjectTypes.CASE_FILE;
        $scope.objectId = $stateParams.id;
        $scope.containerId = 0;
        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;
                //$scope.objectType = Helper.ObjectTypes.CASE_FILE;
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