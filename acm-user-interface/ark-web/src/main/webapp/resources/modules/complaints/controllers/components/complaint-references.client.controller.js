'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'Complaint.InfoService'
    , function ($scope, $stateParams, Util, ConfigService, HelperUiGridService, ComplaintInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        ConfigService.getComponentConfig("complaints", "references").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        //$scope.$on('complaint-updated', function (e, data) {
        //    if (!ComplaintInfoService.validateComplaintInfo(data)) {
        //        return;
        //    }
        //    $scope.complaintInfo = data;
        //    var references = [];
        //    _.each($scope.complaintInfo.childObjects, function (childObject) {
        //        if (ComplaintInfoService.validateReferenceRecord(childObject)) {
        //            references.push(childObject);
        //        }
        //    });
        //    $scope.gridOptions.data = references;
        //    gridHelper.hidePagingControlsIfAllDataShown(references.length);
        //});
        ComplaintInfoService.getComplaintInfo($stateParams.id).then(function (complaintInfo) {
            $scope.complaintInfo = complaintInfo;
            var references = [];
            _.each($scope.complaintInfo.childObjects, function (childObject) {
                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                    references.push(childObject);
                }
            });
            $scope.gridOptions.data = references;
            gridHelper.hidePagingControlsIfAllDataShown(references.length);
            return complaintInfo;
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);