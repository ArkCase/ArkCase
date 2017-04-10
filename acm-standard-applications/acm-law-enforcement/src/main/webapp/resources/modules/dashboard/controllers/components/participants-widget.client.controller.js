'use strict';

angular.module('dashboard.participants', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('participants', {
                    title: 'Participants',
                    description: 'Displays Participants',
                    controller: 'Dashboard.ParticipantsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/participants-widget.client.view.html',
                    commonName: 'participants'
                }
            );
    })
    .controller('Dashboard.ParticipantsController', ['$scope', '$stateParams', 'Case.InfoService'
        , 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'DocumentRepository.InfoService'
        , function ($scope, $stateParams, CaseInfoService, ComplaintInfoService
            , HelperObjectBrowserService, HelperUiGridService, DocumentRepositoryInfoService) {

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    validateInfo: CaseInfoService.validateCaseInfo
                }
                , {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                }
                , {
                    name: "DOC_REPO",
                    configName: "document-repository",
                    getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                    validateInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
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
                $scope.gridOptions.data = objectInfo.participants ? objectInfo.participants : [];
                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "participants";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
            };
        }

    ]);