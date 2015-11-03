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
angular.module('cases').controller('CasesController', ['$scope', '$stateParams', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'ConfigService', 'CasesService',
    function ($scope, $stateParams, $translate, Store, Util, Validator, Helper, ConfigService, CasesService) {
        //$scope.config = ConfigService.getModule({moduleId: 'cases'});
        //$scope.$on('req-component-config', function (e, componentId) {
        //    $scope.config.$promise.then(function (config) {
        //        var componentConfig = _.find(config.components, {id: componentId});
        //        $scope.$broadcast('component-config', componentId, componentConfig);
        //    });
        //});

        var cacheCasesConfig = new Store.SessionData(Helper.SessionCacheNames.CASES_CONFIG);
        var config = cacheCasesConfig.get();
        var promiseGetModule = Util.serviceCall({
            service: ConfigService.getModule
            , param: {moduleId: 'cases'}
            , result: config
            , onSuccess: function (data) {
                if (Validator.validateCasesConfig(data)) {
                    config = data;
                    cacheCasesConfig.set(config);
                    return config;
                }
            }
        }).then(
            function (config) {
                $scope.config = config;
                return config;
            }
        );
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModule.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });


        //
        // relocated to actions controller
        //
        ///**
        // * @ngdoc method
        // * @name loadNewCaseFrevvoForm
        // * @methodOf cases.controller:CasesController
        // *
        // * @description
        // * Displays the create new case Frevvo form for the user
        // */
        //$scope.loadNewCaseFrevvoForm = function () {
        //    $state.go('wizard');
        //};
        //
        ///**
        // * @ngdoc method
        // * @name loadChangeCaseStatusFrevvoForm
        // * @methodOf cases.controller:CasesController
        // *
        // * @param {Object} caseInfo contains the metadata for the existing case which will be edited
        // *
        // * @description
        // * Displays the change case status Frevvo form for the user
        // */
        //$scope.loadChangeCaseStatusFrevvoForm = function (caseInfo) {
        //    if (caseInfo && caseInfo.id && caseInfo.caseNumber && caseInfo.status) {
        //        $state.go('status', {id: caseInfo.id, caseNumber: caseInfo.caseNumber, status: caseInfo.status});
        //    }
        //};

        $scope.progressMsg = $translate.instant("cases.progressNoCase");
        $scope.$on('req-select-case', function (e, selectedCase) {
            $scope.$broadcast('case-selected', selectedCase);

            var id = Util.goodMapValue(selectedCase, "nodeId", null);
            loadCase(id);
        });

        var cacheCaseInfo = new Store.CacheFifo(Helper.CacheNames.CASE_INFO);
        var loadCase = function (id) {
            if (id) {
                $scope.caseInfo = null;
                $scope.progressMsg = $translate.instant("cases.progressLoading") + " " + id + "...";
                var caseInfo = cacheCaseInfo.get(id);
                Util.serviceCall({
                    service: CasesService.get
                    , param: {id: id}
                    , result: caseInfo
                    , onSuccess: function (data) {
                        if (Validator.validateCaseFile(data)) {
                            cacheCaseInfo.put(id, data);
                            return data;
                        }
                    }
                }).then(
                    function (caseInfo) {
                        $scope.progressMsg = null;
                        $scope.caseInfo = caseInfo;
                        $scope.$broadcast('case-retrieved', caseInfo);
                        return caseInfo;
                    }
                    , function (errorData) {
                        $scope.caseInfo = null;
                        $scope.progressMsg = $translate.instant("cases.progressError") + " " + id;
                        return errorData;
                    }
                );
            }
        };
        var id = Util.goodMapValue($stateParams, "id", null);
		loadCase(id);
	}
]);