'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', 'UtilService', 'Helper.UiGridService'
    , 'Complaint.InfoService'
    , function ($scope, Util, HelperUiGridService, ComplaintInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.$emit('req-component-config', 'references');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("references" == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
            }
        });

        $scope.$on('complaint-updated', function (e, data) {
            if (!ComplaintInfoService.validateComplaintInfo(data)) {
                return;
            }
            $scope.complaintInfo = data;
            var references = [];
            _.each($scope.complaintInfo.childObjects, function (childObject) {
                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                    references.push(childObject);
                }
            });
            $scope.gridOptions.data = references;
            gridHelper.hidePagingControlsIfAllDataShown(references.length);
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);