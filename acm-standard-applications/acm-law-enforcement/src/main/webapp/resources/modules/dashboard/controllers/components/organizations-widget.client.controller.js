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
        'UtilService', 'Person.InfoService', 'Case.InfoService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Object.ModelService', 'Object.LookupService'
        ,function ($scope, $stateParams, $translate,
                   Util, PersonInfoService, CaseInfoService, ComplaintInfoService, HelperObjectBrowserService, HelperUiGridService, ObjectModelService, ObjectLookupService) {
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

            ObjectLookupService.getPersonOrganizationRelationTypes().then(
                function (organizationTypes) {
                    $scope.organizationTypes = [];
                    for (var i = 0; i < organizationTypes.length; i++) {
                        $scope.organizationTypes.push({
                            "key": organizationTypes[i].inverseKey,
                            "value": organizationTypes[i].inverseValue,
                            "inverseKey": organizationTypes[i].key,
                            "inverseValue": organizationTypes[i].value
                        });
                    }
                    return organizationTypes;
                });

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
            $scope.isDefault = function(data){
                return ObjectModelService.isObjectReferenceSame($scope.objectInfo, data, "defaultOrganization");
            }

            $scope.getRelationship = function (org) {
                $scope.relationshipType = _.find($scope.organizationTypes, function (obj) {
                    return obj.key === org;
                });
                return $scope.relationshipType.inverseValue;
            };
        }
    ]);