'use strict';

/**
 * @ngdoc controller
 * @name complaints.controller:Complaints.ActionsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/complaints/controllers/components/complaint-actions.client.controller.js modules/complaints/controllers/components/complaint-actions.client.controller.js}
 *
 * The Complaints module actions controller
 */
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

        $scope.$on('complaint-retrieved', function (e, data) {
            if (Validator.validateComplaint(data)) {
                $scope.complaintInfo = data;
            }
        });

        /**
         * @ngdoc method
         * @name loadNewComplaintFrevvoForm
         * @methodOf complaints.controller:Complaints.ActionsController
         *
         * @description
         * Displays the create new complaint Frevvo form for the user
         */
        $scope.loadNewComplaintFrevvoForm = function () {
            $state.go('wizard');
        };

        /**
         * @ngdoc method
         * @name loadChangeComplaintStatusFrevvoForm
         * @methodOf complaints.controller:Complaints.ActionsController
         *
         * @param {Object} complaintInfo contains the metadata for the existing complaint which will be edited
         *
         * @description
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