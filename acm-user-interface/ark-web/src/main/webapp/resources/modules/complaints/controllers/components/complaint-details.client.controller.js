'use strict';

angular.module('complaints').controller('Complaints.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, ComplaintInfoService, MessageService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("complaints", "details").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            ComplaintInfoService.getComplaintInfo(currentObjectId).then(function (complaintInfo) {
                $scope.complaintInfo = complaintInfo;
                return complaintInfo;
            });
        }

        $scope.$on('object-refreshed', function (e, complaintInfo) {
            $scope.complaintInfo = complaintInfo;
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