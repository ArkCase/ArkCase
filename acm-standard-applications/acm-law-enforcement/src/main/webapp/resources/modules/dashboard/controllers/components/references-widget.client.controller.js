'use strict';

angular.module('dashboard.references', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('references', {
                    title: 'References',
                    description: 'Displays references',
                    controller: 'Dashboard.ReferencesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/references-widget.client.view.html',
                    commonName: 'references'
                }
            );
    })
    .controller('Dashboard.ReferencesController', ['$scope', '$stateParams', 'Case.InfoService'
        , 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Helper.UiGridService'
        , function ($scope, $stateParams, CaseInfoService, ComplaintInfoService
            , HelperObjectBrowserService, ObjectService, HelperUiGridService) {

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    validateInfo: CaseInfoService.validateCaseInfo
                }
                ,
                {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});
            var promiseUsers = gridHelper.getUsers();

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {

                /**
                 * Complaints and CaseFiles return their references in a different way.
                 */
                if (module.name == ObjectService.ObjectTypes.COMPLAINT) {
                    var references = [];
                    _.forEach(objectInfo.childObjects, function (childObject) {
                        if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                            references.push(childObject);
                        }
                    });
                    $scope.gridOptions.data = references;
                } else {
                    $scope.gridOptions.data = objectInfo.references ? objectInfo.references : [];
                }

                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "references";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
            };

        }
    ]);