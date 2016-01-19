'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        ConfigService.getComponentConfig("complaints", "references").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            return config;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            ComplaintInfoService.getComplaintInfo(currentObjectId).then(function (complaintInfo) {
                updateData(complaintInfo);
                return complaintInfo;
            });
        }

        $scope.$on('object-refreshed', function (e, complaintInfo) {
            updateData(complaintInfo);
        });

        var updateData = function (complaintInfo) {
            $scope.complaintInfo = complaintInfo;
            var references = [];
            _.each($scope.complaintInfo.childObjects, function (childObject) {
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