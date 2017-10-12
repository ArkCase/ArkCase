'use strict';

angular.module('dashboard.emails', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('emails', {
                title: 'dashboard.widgets.emails.title',
                description: 'dashboard.widgets.emails.description',
                controller: 'Dashboard.EmailsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/emails-widget.client.view.html',
                commonName: 'emails'
            });
    })
    .controller('Dashboard.EmailsController', ['$scope', '$stateParams', '$translate',
        'Person.InfoService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'UtilService', 'Object.LookupService',
            function ($scope, $stateParams, $translate,
                  PersonInfoService, OrganizationInfoService, HelperObjectBrowserService, HelperUiGridService, Util, ObjectLookupService) {

            var modules = [
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
                $scope.objectInfo = objectInfo;
                var emails = _.filter($scope.objectInfo.contactMethods, {type: 'email'});
                gridHelper.setWidgetsGridData(emails);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "emails";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

            ObjectLookupService.getContactMethodTypes(module.name).then(
                    function (contactMethodTypes) {
                        var found = _.find(contactMethodTypes, {key: 'email'});
                        if(!Util.isArray(found)){
                            $scope.emailTypes = found.subLookup;
                        }
                        return contactMethodTypes;
                    });

            $scope.getLookupValue = function(value, key){
                    return ObjectLookupService.getLookupValue(value, key);
                };

            }
    ]);