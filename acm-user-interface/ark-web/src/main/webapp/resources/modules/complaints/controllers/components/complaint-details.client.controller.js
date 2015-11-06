'use strict';

angular.module('complaints').controller('Complaints.DetailsController', ['$scope', '$stateParams', 'UtilService', 'ValidationService', 'ComplaintsService',
    function ($scope, $stateParams, Util, Validator, ComplaintsService) {
        var z = 1;
        return;
        $scope.$emit('req-component-config', 'details');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('details' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('complaint-retrieved', function (e, data) {
            if (Validator.validateComplaint(data)) {
                $scope.complaintInfo = data;
            }
        });


        $scope.options = {
            focus: true
            //,height: 120
        };

        $scope.saveDetails = function () {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            Util.serviceCall({
                service: ComplaintsService.save
                , data: complaintInfo
            });
        };
    }
]);