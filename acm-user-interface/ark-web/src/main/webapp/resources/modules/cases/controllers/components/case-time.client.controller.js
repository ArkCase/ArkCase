'use strict';

angular.module('cases').controller('Cases.TimeController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, $translate, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'time');

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'time') {
                $scope.config = config;

                Util.uiGrid.typicalOptions(config, $scope);
                //Util.uiGrid.externalPaging(config, $scope, updatePageData);
                $scope.gridOptions.columnDefs = config.columnDefs;

                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("name" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.showUrl($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                    } else if ("tally" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].field = "acm$_hours";
                    }
                }
            }
        }

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;

                CasesService.queryTimesheets({
                    objectType: "CASE_FILE",
                    objectId: $scope.caseInfo.id
                }, function (data) {
                    if (Validator.validateTimesheets(data)) {
                        var timesheets = data;
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = $translate.instant("cases.comp.time.formNamePrefix") + " " + Util.goodValue(timesheets[i].startDate) + " - " + Util.goodValue(timesheets[i].endDate);
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                    }

                });
            } //end validate
        });


        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            Util.uiGrid.showObject("TIMESHEET", Util.goodMapValue([rowEntity, "id"], 0), $scope);
        };

    }
]);