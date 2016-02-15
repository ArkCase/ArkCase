'use strict';

angular.module('cases').controller('Cases.TimeController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.TimeService', 'Case.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ObjectService, ConfigService, ObjectTimeService, CaseInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "time"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a data-ui-sref=\"time-tracking.main({id: row.entity.id})\">{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_hours";
                }
            }
        };

        if (Util.goodPositive(componentHelper.currentObjectId, false)) {
            ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.CASE_FILE, componentHelper.currentObjectId).then(
                function (timesheets) {
                    componentHelper.promiseConfig.then(function (config) {
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = $translate.instant("cases.comp.time.formNamePrefix") + " " + Util.goodValue(timesheets[i].startDate) + " - " + Util.goodValue(timesheets[i].endDate);
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                        //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                        return config;
                    });
                    return timesheets;
                }
            );
        }
    }
]);