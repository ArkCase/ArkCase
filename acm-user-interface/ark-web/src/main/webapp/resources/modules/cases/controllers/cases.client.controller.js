'use strict';

/**
 * @ngdoc controller
 * @name cases.controller:CasesController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/controllers/cases.client.controller.js modules/cases/controllers/cases.client.controller.js}
 *
 * The Cases module main controller
 */
angular.module('cases').controller('CasesController', ['$scope', '$state', '$stateParams', 'UtilService', 'ValidationService', 'ConfigService', 'CasesService',
    function ($scope, $state, $stateParams, Util, Validator, ConfigService, CasesService) {
        $scope.config = ConfigService.getModule({moduleId: 'cases'});
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }

        /**
         * @ngdoc method
         * @name loadNewCaseFrevvoForm
         * @methodOf cases.controller:CasesController
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
         * @methodOf cases.controller:CasesController
         *
         * @param {Object} caseData contains the metadata for the existing case which will be edited
         *
         * @description
         * Displays the change case status Frevvo form for the user
         */
        $scope.loadChangeCaseStatusFrevvoForm = function (caseData) {
            if (caseData && caseData.id && caseData.caseNumber && caseData.status) {
                $state.go('status', {id: caseData.id, caseNumber: caseData.caseNumber, status: caseData.status});
            }
        };

        $scope.$on('req-select-case', function (e, selectedCase) {
            $scope.$broadcast('case-selected', selectedCase);

            var id = Util.goodMapValue(selectedCase, "nodeId", null);
            loadCase(id);
        });


        var loadCase = function (id) {
            if (id) {
                CasesService.get({
                    id: id
                }, function (data) {
                    if (Validator.validateCaseFile(data)) {
                        $scope.caseData = data;
                        $scope.$broadcast('case-retrieved', data);
                    }
                });
            }
        };
        var id = Util.goodMapValue($stateParams, "id", null);
		loadCase(id);
	}
]);