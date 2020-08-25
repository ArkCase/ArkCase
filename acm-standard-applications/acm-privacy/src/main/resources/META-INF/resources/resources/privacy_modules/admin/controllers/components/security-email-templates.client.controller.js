'use strict';

angular.module('admin').controller('Admin.SecurityEmailTemplatesController',
        [ '$scope', '$translate', '$modal', 'Admin.EmailTemplatesService', 'Helper.UiGridService', 'MessageService', 'Dialog.BootboxService', 'UtilService', function($scope, $translate, $modal, emailTemplatesService, HelperUiGridService, MessageService, DialogService, Util) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            //get config and init grid settings
            $scope.config.$promise.then(function(config) {
                $scope.emailConfig = angular.copy(_.find(config.components, {
                    id: 'emailTemplates'
                }));
                $scope.objectTypeList = $scope.emailConfig.objectTypes;
                $scope.actionList = $scope.emailConfig.actions;
                $scope.sourceList = $scope.emailConfig.sources;

                gridHelper.addButton($scope.emailConfig, "edit");
                gridHelper.addButton($scope.emailConfig, "delete");
                gridHelper.setColumnDefs($scope.emailConfig);
                gridHelper.setBasicOptions($scope.emailConfig);
                gridHelper.disableGridScrolling($scope.emailConfig);
                gridHelper.setUserNameFilterToConfig(promiseUsers, $scope.emailConfig);
                ReloadGrid();
            });

            $scope.addNew = function() {

                var template = {};
                $scope.template = template;

                var item = {
                    emailPattern: "",
                    objectTypes: [],
                    source: "MANUAL",
                    templateName: "",
                    actions: []
                };
                showModal(item, false);
            };

            $scope.editRow = function(rowEntity) {
                $scope.template = rowEntity;
                var item = {
                    emailPattern: rowEntity.emailPattern,
                    objectTypes: rowEntity.objectTypes,
                    source: rowEntity.source,
                    templateName: rowEntity.templateName,
                    actions: rowEntity.actions
                };
                showModal(item, true);
            };

            $scope.deleteRow = function(rowEntity) {
                emailTemplatesService.deleteEmailTemplate(rowEntity.templateName).then(function() {
                    ReloadGrid();
                    MessageService.succsessAction();
                }, function() {
                    MessageService.errorAction();
                });
            };

            function showModal(template, isEdit) {
                var params = {};
                params.template = template || {};
                params.isEdit = isEdit || false;
                params.objectTypeList = $scope.objectTypeList;
                params.actionList = $scope.actionList;
                params.sourceList = $scope.sourceList;

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: "modules/admin/views/components/security-email-templates-modal.view.html",
                    controller: 'Admin.EmailTemplatesModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function(data) {
                    if (!Util.isEmpty(data.file) && !containsExtensionHtml(data.file.name)) {
                        DialogService.alert($translate.instant("admin.security.emailTemplates.modal.uploadError"));
                    } else {
                        emailTemplatesService.validateEmailTemplate(data.template).then(function(response) {
                            if (response.data.validTemplate) {
                                if (!containsExtensionHtml(data.template.templateName)) {
                                    data.template.templateName = data.template.templateName.replace(/\s/g, "") + ".html";
                                }
                                emailTemplatesService.saveEmailTemplate(data.template, data.file).then(function() {
                                    MessageService.succsessAction();
                                    ReloadGrid();
                                }, function() {
                                    MessageService.errorAction();
                                });
                            } else {
                                DialogService.alert($translate.instant("admin.security.emailTemplates.modal.validationResponse") + ' [' + response.data.objectType + '] [' + response.data.action + '] [' + response.data.emailPattern + ']');
                            }
                        }, function(error) {
                            MessageService.errorAction(error);
                        });
                    }
                });
            }

            function containsExtensionHtml(templateName) {
                var validExtensionFormats = [ ".htm", ".html", ".xhtml" ];
                var templateNameLength = templateName.length;
                var isHtml;
                var i;
                for (i in validExtensionFormats) {
                    var extensionLength = validExtensionFormats[i].length;
                    isHtml = templateName.toLowerCase().indexOf(validExtensionFormats[i], templateNameLength - extensionLength) >= 0;
                    if (isHtml)
                        break;
                }

                return isHtml;
            }

            function ReloadGrid() {
                var templatesPromise = emailTemplatesService.listEmailTemplates();
                templatesPromise.then(function(templates) {
                    var objectTypes = $scope.emailConfig.objectTypes;
                    templates = templates.map(function(item){ 
                        item.objectTypesNames = [];
                        for (var i in item.objectTypes) {
                            item.objectTypesNames[i] = takeNameByIdFromModuleConfig(item.objectTypes[i]);
                        }
                        return item;
                    });
                    $scope.gridOptions.data = templates;
                });
            }

            function takeNameByIdFromModuleConfig(objectTypeId) {
                var objectTypes = $scope.emailConfig.objectTypes;
                for (var i in objectTypes) {
                    if (objectTypeId == objectTypes[i].id)
                        return objectTypes[i].name.toUpperCase();
                }
            }

            emailTemplatesService.getEmailReceiverConfiguration().then(function(result) {
                $scope.emailReceiverConfiguration = {
                    enableBurstingAttachments: result.data["email.enableBurstingAttachments"]
                };
            });
            
            $scope.saveBurstingConfiguration = function () {
                var emailReceiverConfiguration = {
                    "email.enableBurstingAttachments": $scope.emailReceiverConfiguration.enableBurstingAttachments
                };
                emailTemplatesService.saveEmailReceiverConfiguration(emailReceiverConfiguration).then(function() {
                    MessageService.succsessAction();
                }, function() {
                    MessageService.errorAction();
                });
            };

        } ]);