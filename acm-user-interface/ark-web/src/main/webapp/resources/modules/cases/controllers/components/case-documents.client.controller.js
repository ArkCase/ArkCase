'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService'
    , function ($scope, $stateParams, $modal, Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService) {

        //$scope.$emit('req-component-config', 'documents');
        //$scope.$on('component-config', function (e, componentId, config) {
        //    if ('documents' == componentId) {
        //        $scope.config = config;
        //    }
        //});
        ConfigService.getComponentConfig("cases", "documents").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        ObjectLookupService.getFormTypes().then(
            function (formTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(formTypes));
                return formTypes;
            }
        );
        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = $stateParams.id;

        //$scope.$on('case-updated', function (e, data) {
        //    if (CaseInfoService.validateCaseInfo(data)) {
        //        $scope.caseInfo = data;
        //    }
        //});
        CaseInfoService.getCaseInfo($stateParams.id).then(function (caseInfo) {
            $scope.caseInfo = caseInfo;
            return caseInfo;
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
                if (ObjectLookupService.validatePlainForm(fileType)) {
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
                            var _value = _.get($scope.caseInfo, urlParameters[i].keyValue)
                            if (!Util.isEmpty(_value)) {
                                value = silentReplace(_value, "'", "_0027_");
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