'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Task.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "attachments"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (taskInfo) {
                $scope.taskInfo = taskInfo;
            }
        });


        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.TASK).then(
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

        $scope.objectType = ObjectService.ObjectTypes.TASK;
        $scope.objectId = $stateParams.id;
        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TaskInfoService.getTaskInfo(currentObjectId).then(function (taskInfo) {
                $scope.taskInfo = taskInfo;
                $scope.objectId = taskInfo.taskId;
                return taskInfo;
            });
        }

        var silentReplace = function (value, replace, replacement) {
            if (!Util.isEmpty(value) && value.replace) {
                value = value.replace(replace, replacement);
            }
            return value;
        };
        $scope.uploadForm = function (type, folderId, onCloseForm) {
            if ($scope.taskInfo) {
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
                            var _value = _.get($scope.taskInfo, urlParameters[i].keyValue)
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