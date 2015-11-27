'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', 'UtilService', 'HelperService'
    , 'Complaint.InfoService'
    , function ($scope, Util, Helper, ComplaintInfoService) {

        $scope.$emit('req-component-config', 'references');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("references" == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
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
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, references.length);
        });

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);