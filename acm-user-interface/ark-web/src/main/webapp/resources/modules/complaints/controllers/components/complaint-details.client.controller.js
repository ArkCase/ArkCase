'use strict';

angular.module('complaints').controller('Complaints.DetailsController', ['$scope', '$stateParams', '$translate', 'UtilService', 'CallComplaintsService', 'MessageService',
    function ($scope, $stateParams, $translate, Util, CallComplaintsService, MessageService) {
        var z = 1;
        return;
        $scope.$emit('req-component-config', 'details');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('details' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('complaint-retrieved', function (e, data) {
            $scope.complaintInfo = data;
        });


        $scope.options = {
            focus: true
            //,height: 120
        };

        $scope.saveDetails = function () {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            CallComplaintsService.saveComplaintInfo(complaintInfo).then(
                function (complaintInfo) {
                    MessageService.info($translate.instant("complaints.comp.details.informSaved"));
                    return complaintInfo;
                }
            );
        };
    }
]);