'use strict';

/**
 * @ngdoc controller
 * @name cases:Cases.ActionsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/controllers/components/case-actions.client.controller.js modules/cases/controllers/components/case-actions.client.controller.js}
 *
 * The Cases module actions controller
 */
angular.module('cases').controller('Cases.ActionsController', ['$scope', 'ConfigService', 'CasesService', 'UtilService', 'ValidationService',
    function ($scope, ConfigService, CasesService, Util, Validator) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;
            }
        });

        /**
         * @ngdoc method
         * @name loadNewCaseFrevvoForm
         * @methodOf cases.Cases.ActionsController
         *
         * @description
         * Displays the create new case Frevvo form for the user
         */
        $scope.loadNewCaseFrevvoForm = function () {
            $state.go('wizard');
        };

        /**
         * @ngdoc method
         * @name loadChangeCaseStatusFrevvoForm
         * @methodOf cases.Cases.ActionsController
         *
         * @param {Object} caseInfo contains the metadata for the existing case which will be edited
         *
         * @description
         * Displays the change case status Frevvo form for the user
         */
        $scope.loadChangeCaseStatusFrevvoForm = function (caseInfo) {
            if (caseInfo && caseInfo.id && caseInfo.caseNumber && caseInfo.status) {
                $state.go('status', {id: caseInfo.id, caseNumber: caseInfo.caseNumber, status: caseInfo.status});
            }
        };
    }
]);