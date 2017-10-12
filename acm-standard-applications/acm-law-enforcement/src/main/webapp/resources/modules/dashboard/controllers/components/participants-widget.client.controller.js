'use strict';

angular.module('dashboard.participants', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('participants', {
                title: 'dashboard.widgets.participants.title',
                description: 'dashboard.widgets.participants.description',
                controller: 'Dashboard.ParticipantsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/participants-widget.client.view.html',
                commonName: 'participants'
            });
    })
    .controller('Dashboard.ParticipantsController', ['$scope', '$stateParams', '$translate', 'Object.LookupService',
        'Case.InfoService', 'Complaint.InfoService', 'DocumentRepository.InfoService', 'Person.InfoService', 'Organization.InfoService', 'OrganizationAssociation.Service', 'PersonAssociation.Service', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $stateParams, $translate, ObjectLookupService,
                  CaseInfoService, ComplaintInfoService, DocumentRepositoryInfoService, PersonInfoService, OrganizationInfoService, OrganizationAssociationService, PersonAssociationService, HelperObjectBrowserService, HelperUiGridService) {

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    validateInfo: CaseInfoService.validateCaseInfo
                },
                {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                },
                {
                    name: "DOC_REPO",
                    configName: "document-repository",
                    getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                    validateInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
                },
                {
                    name: "PERSON",
                    configName: "people",
                    getInfo: PersonInfoService.getPersonInfo,
                    validateInfo: PersonInfoService.validatePersonInfo
                },
                {
                    name: "ORGANIZATION",
                    configName: "organizations",
                    getInfo: OrganizationInfoService.getOrganizationInfo,
                    validateInfo: OrganizationInfoService.validateOrganizationInfo
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
                gridHelper.setWidgetsGridData(objectInfo.participants);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "participants";
                });
                gridHelper.setColumnDefs(widgetInfo);
                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
            };

            var promiseTypes = ObjectLookupService.getParticipantTypes(module.name).then(
                function (participantTypes) {
                    $scope.participantTypes = participantTypes;
                    return participantTypes;
                }
            );

            $scope.getLookupValue = function(value, key){
                return ObjectLookupService.getLookupValue(value, key);
            };

        }

    ]);