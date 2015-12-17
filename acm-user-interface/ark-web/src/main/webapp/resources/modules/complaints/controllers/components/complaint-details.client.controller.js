'use strict';

angular.module('complaints').controller('Complaints.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'MessageService'
    , function ($scope, $stateParams, $translate, Util, ConfigService, ComplaintInfoService, MessageService) {

        ConfigService.getComponentConfig("complaints", "details").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        //$scope.$on('complaint-updated', function (e, data) {
        //    if (ComplaintInfoService.validateComplaintInfo(data)) {
        //        $scope.complaintInfo = data;
        //    }
        //});
        ComplaintInfoService.getComplaintInfo($stateParams.id).then(function (complaintInfo) {
            $scope.complaintInfo = complaintInfo;
            return complaintInfo;
        });


        $scope.options = {
            focus: true
            //,height: 120
        };

        $scope.saveDetails = function () {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                function (complaintInfo) {
                    MessageService.info($translate.instant("complaints.comp.details.informSaved"));
                    return complaintInfo;
                }
            );
        };
    }
]);