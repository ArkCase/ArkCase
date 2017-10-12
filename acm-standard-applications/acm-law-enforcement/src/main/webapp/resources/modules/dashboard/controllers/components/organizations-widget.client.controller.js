'use strict';

angular.module('dashboard.organizations', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('organizations', {
                    title: 'dashboard.widgets.organizations.title',
                    description: 'dashboard.widgets.organizations.description',
                    controller: 'Dashboard.OrganizationsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/organizations-widget.client.view.html',
                    commonName: 'organizations'
                }
            );
    })
    .controller('Dashboard.OrganizationsController', ['$scope', '$stateParams', '$translate',
        'UtilService', 'Person.InfoService', 'Case.InfoService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $stateParams, $translate,
                  Util, PersonInfoService, CaseInfoService, ComplaintInfoService, HelperObjectBrowserService, HelperUiGridService) {
            var modules = [
                {
                    name: "PERSON",
                    configName: "people",
                    getInfo: PersonInfoService.getPersonInfo,
                    validateInfo: PersonInfoService.validatePersonInfo
                },
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
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            $scope.getPrimaryContact = function (organizationAssiciation) {
                var primaryContact = organizationAssiciation.organization.primaryContact;
                if (!!primaryContact) {
                    var getPrimaryConactGivenName = Util.goodValue(primaryContact.person.givenName);
                    var getPrimaryConactFamilyName = Util.goodValue(primaryContact.person.familyName);
                    return (getPrimaryConactGivenName.trim() + ' ' + getPrimaryConactFamilyName.trim()).trim();
                }

                return '';
            };

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

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
                gridHelper.setWidgetsGridData(objectInfo.organizationAssociations);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "organizations";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };
        }
    ]);