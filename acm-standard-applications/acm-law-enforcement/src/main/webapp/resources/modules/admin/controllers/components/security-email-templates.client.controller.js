'use strict';

angular.module('admin').controller('Admin.SecurityEmailTemplatesController',
        [ '$scope', '$translate', '$modal', 'Admin.EmailTemplatesService', 'Helper.UiGridService', 'MessageService', 'Dialog.BootboxService', 'UtilService', function($scope, $translate, $modal, emailTemplatesService, HelperUiGridService, MessageService, DialogService, Util) {
            $scope.emailReceiverConfiguration = {};

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            //get config and init grid settings
            $scope.config.$promise.then(function(config) {
                var config = angular.copy(_.find(config.components, {
                    id: 'emailTemplates'
                }));
                $scope.objectTypeList = config.objectTypes;
                $scope.actionList = config.actions;
                $scope.sourceList = config.sources;

                gridHelper.setUserNameFilterToConfig(promiseUsers, config).then(function(updatedConfig) {
                    $scope.config = updatedConfig;
                    if ($scope.gridApi != undefined)
                        $scope.gridApi.core.refresh();
                    gridHelper.addButton(updatedConfig, "edit");
                    gridHelper.addButton(updatedConfig, "delete");
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                });
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
                    $scope.gridOptions.data = templates;
                });
            }

            emailTemplatesService.getEmailReceiverConfiguration().then(function(result) {
                $scope.emailReceiverConfiguration = result.data;
                $scope.emailReceiverConfiguration.user = result.data.user.replace('%40', '@');
                $scope.emailReceiverConfiguration.user_complaint = result.data.user_complaint.replace('%40', '@');
            })

            $scope.newEmailReceiverConfiguration = {};
            $scope.save = function() {
                $scope.newEmailReceiverConfiguration.user = $scope.emailReceiverConfiguration.user.replace('@', '%40');
                $scope.newEmailReceiverConfiguration.password = $scope.emailReceiverConfiguration.pass;
                $scope.newEmailReceiverConfiguration.user_complaint = $scope.emailReceiverConfiguration.user_complaint.replace('@', '%40');
                $scope.newEmailReceiverConfiguration.password_complaint = $scope.emailReceiverConfiguration.pass_complaint;
                $scope.newEmailReceiverConfiguration.enableCase = $scope.emailReceiverConfiguration.enableCase;
                $scope.newEmailReceiverConfiguration.enableComplaint = $scope.emailReceiverConfiguration.enableComplaint;
                emailTemplatesService.saveEmailReceiverConfiguration($scope.newEmailReceiverConfiguration).then(function(value) {
                    MessageService.succsessAction();
                }, function(err) {
                    MessageService.errorAction();
                });
            };
        } ]);