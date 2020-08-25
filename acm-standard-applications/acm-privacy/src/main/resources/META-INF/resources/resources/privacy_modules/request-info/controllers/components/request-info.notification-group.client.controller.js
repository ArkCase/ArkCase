'use strict';

angular.module('request-info').controller(
    'RequestInfo.NotificationGroupClientController',
    [ '$scope', '$stateParams', '$translate', '$timeout', 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService', 'Case.LookupService', 'Case.InfoService', 'Object.ModelService', 'Helper.ObjectBrowserService', 'DueDate.Service', 'Admin.HolidayService',
        'MessageService', '$modalInstance', '$modal', 'LookupService', 'Admin.PrivacyConfigService',
        function ($scope, $stateParams, $translate, $timeout, Util, UtilDateService, ConfigService, ObjectLookupService, CaseLookupService, CaseInfoService, ObjectModelService, HelperObjectBrowserService, DueDateService, AdminHolidayService, MessageService, $modalInstance, $modal, LookupService, AdminPrivacyConfigService) {


            AdminPrivacyConfigService.getPrivacyConfig().then(function (response) {
                $scope.isNotificationGroupEnabled = response.data.notificationGroupsEnabled;
            },function(err){
                MessageService.errorAction();
            });

            ObjectLookupService.getGroups().then(function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({
                        value: group.name,
                        text: group.name
                    });
                });
                $scope.owningGroups = options;
                return groups;
            });
            ObjectLookupService.getLookupByLookupName("notificationGroups").then(function (notificationGroups) {
                $scope.notificationGroups = notificationGroups;
                if ($scope.objectInfo.hasOwnProperty('notificationGroup')) {
                    var notification = _.find($scope.notificationGroups, {
                        key: $scope.objectInfo.notificationGroup
                    });
                    if (typeof notification !== "undefined") {
                        $scope.notificationGroup = notification.value;
                    }
                }
            });

            CaseLookupService.getCaseTypes().then(function (caseTypes) {
                var options = [];
                _.forEach(caseTypes, function (item) {
                    options.push({
                        value: item,
                        text: item
                    });
                });
                $scope.caseTypes = options;
                return caseTypes;
            });

            ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                    id: "userOrGroupSearch"
                });
            });

            $scope.privacyConfig = {};

            $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;

            /**
             * Persists the updated casefile metadata to the ArkCase database
             */
            function saveCase() {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = CaseInfoService.saveSubjectAccessRequestInfo(objectInfo);
                    promiseSaveInfo.then(function(caseInfo) {
                        $scope.$emit("report-object-updated", caseInfo);
                        $scope.$emit('notificationGroupSaved', $scope);
                        return caseInfo;
                    }, function(error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    });
                }
                return promiseSaveInfo;
            }

            // Updates the ArkCase database when the user changes a case attribute
            // in a case top bar menu item and clicks the save check button
            $scope.saveCase = function() {
                saveCase();
            };

            $scope.updateOwningGroup = function() {
                ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
            };

            $scope.updateAssignee = function() {
                ObjectModelService.setAssignee($scope.objectInfo, $scope.assignee);
            };

            $scope.updateNotificationGroup = function() {
                var notification = _.find($scope.notificationGroups, {
                    key: $scope.objectInfo.notificationGroup
                });
                if(typeof notification !== "undefined") {
                    $scope.notificationGroup = notification.value;
                }
                saveCase().then(function () {
                    $modalInstance.dismiss();
                }, function () {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            };
        } ]);