'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "references"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            var references = [];
            _.each($scope.objectInfo.childObjects, function (childObject) {
                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                    references.push(childObject);
                }
            });
            $scope.gridOptions.data = references;
            //gridHelper.hidePagingControlsIfAllDataShown(references.length);
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);