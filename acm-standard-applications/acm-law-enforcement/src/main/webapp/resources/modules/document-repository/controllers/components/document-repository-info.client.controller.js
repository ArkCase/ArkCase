'use strict';

angular.module('document-repository').controller('DocumentRepository.InfoController', ['$scope', '$stateParams'
    , '$translate', '$modal', 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService'
    , 'DocumentRepository.InfoService', 'Object.ModelService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate, $modal, Util, UtilDateService, ConfigService, ObjectLookupService
        , DocumentRepositoryInfoService, ObjectModelService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , componentId: "info"
            , retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
            $scope.owningGroup = ObjectModelService.getGroup(objectInfo);

        };

        $scope.save = function () {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (DocumentRepositoryInfoService.validateDocumentRepositoryInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = DocumentRepositoryInfoService.saveDocumentRepository(objectInfo)
                    .then(
                        function (data) {
                            $scope.$emit("report-object-updated", data);
                            return data;
                        }
                        , function (error) {
                            error = error.data.message ? error.data.message : error;
                            $scope.$emit("report-object-update-failed", error);
                            return error;
                        }
                    );
            }
            return promiseSaveInfo;
        };
    }
]);