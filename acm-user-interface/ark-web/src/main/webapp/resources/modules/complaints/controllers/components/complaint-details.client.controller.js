'use strict';

angular.module('complaints').controller('Complaints.DetailsController', ['$scope', '$stateParams', '$translate', 'UtilService', 'Complaint.InfoService', 'MessageService',
    function ($scope, $stateParams, $translate, Util, ComplaintInfoService, MessageService) {
        $scope.$emit('req-component-config', 'details');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('details' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('complaint-updated', function (e, data) {
            if (ComplaintInfoService.validateComplaintInfo(data)) {
                $scope.complaintInfo = data;
            }
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