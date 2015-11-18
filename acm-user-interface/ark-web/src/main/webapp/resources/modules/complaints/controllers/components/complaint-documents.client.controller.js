'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal', 'UtilService', 'ConstantService', 'Object.LookupService',
    function ($scope, $stateParams, $modal, Util, Constant, ObjectLookupService) {

        $scope.$emit('req-component-config', 'documents');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('documents' == componentId) {
                $scope.config = config;
            }
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

        var z = 1;
        return;

        $scope.objectType = Constant.ObjectTypes.COMPLAINT;
        $scope.objectId = $stateParams.id;

        $scope.$on('complaint-updated', function (e, data) {
            $scope.complaintInfo = data;
        });

        var silentReplace = function (value, replace, replacement) {
            if (!Util.isEmpty(value) && value.replace) {
                value = value.replace(replace, replacement);
            }
            return value;
        };
        $scope.uploadForm = function (type, folderId, onCloseForm) {
            if ($scope.complaintInfo) {
                //Complaint.View.Documents.getFileTypeByType(type);
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
                            if (!Util.isEmpty($scope.complaintInfo[urlParameters[i].keyValue])) {
                                value = silentReplace($scope.complaintInfo[urlParameters[i].keyValue], "'", "_0027_");
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