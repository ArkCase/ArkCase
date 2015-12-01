'use strict';

angular.module('complaints').controller('Complaints.TimeController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Helper.UiGridService', 'Helper.ConfigService', 'Object.TimeService'
    , function ($scope, $stateParams, $translate, Util, ObjectService, HelperUiGridService, HelperConfigService, ObjectTimeService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseConfig = HelperConfigService.requestComponentConfig($scope, "time", function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.onClickObjLink($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_hours";
                }
            }
        });

        if (Util.goodPositive($stateParams.id)) {
            ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id).then(
                function (timesheets) {
                    promiseConfig.then(function (config) {
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = $translate.instant("complaints.comp.time.formNamePrefix") + " " + Util.goodValue(timesheets[i].startDate) + " - " + Util.goodValue(timesheets[i].endDate);
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                        gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                        return config;
                    });
                    return timesheets;
                }
            );
        }

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            gridHelper.showObject(ObjectService.ObjectTypes.TIMESHEET, Util.goodMapValue(rowEntity, "id", 0));
        };

    }
]);