'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'ObjectService', 'Helper.ObjectBrowserService', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, CaseInfoService, ObjectService, HelperObjectBrowserService, DashboardService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "cases"
            , resetObjectInfo: CaseInfoService.resetCaseInfo
            , getObjectInfo: CaseInfoService.getCaseInfo
            , updateObjectInfo: CaseInfoService.updateCaseInfo
            , getObjectTypeFromInfo: function (objectInfo) {
                return ObjectService.ObjectTypes.CASE_FILE;
            }
            , initComponentLinks: function (config) {
                return HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.CASE_FILE);
            }
        });

    }
]);