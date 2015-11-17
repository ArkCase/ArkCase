'use strict';

angular.module('complaints').controller('Complaints.ActionsController', ['$scope', 'ConfigService', 'ComplaintsService', 'UtilService', 'ValidationService',
    function ($scope, ConfigService, ComplaintsService, Util, Validator) {
        var z = 1;
        return;
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('complaint-updated', function (e, data) {
            if (Validator.validateComplaint(data)) {
                $scope.complaintInfo = data;
            }
        });

        /**
         * Displays the create new complaint Frevvo form for the user
         */
        $scope.loadNewComplaintFrevvoForm = function () {
            $state.go('wizard');
        };

        /**
         * Displays the change complaint status Frevvo form for the user
         */
        $scope.loadChangeComplaintStatusFrevvoForm = function (complaintInfo) {
            if (complaintInfo && complaintInfo.id && complaintInfo.complaintNumber && complaintInfo.status) {
                $state.go('status', {
                    id: complaintInfo.id,
                    complaintNumber: complaintInfo.complaintNumber,
                    status: complaintInfo.status
                });
            }
        };
    }
]);