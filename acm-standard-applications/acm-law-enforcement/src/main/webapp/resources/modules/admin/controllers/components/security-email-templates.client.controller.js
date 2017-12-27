'use strict';
angular.module('admin').controller('Admin.SecurityEmailTemplatesController', ['$scope', '$translate', '$modal'
    , 'Admin.EmailTemplatesService', 'Helper.UiGridService', 'MessageService','Dialog.BootboxService',
    function ($scope, $translate, $modal, emailTemplatesService, HelperUiGridService, MessageService, DialogService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var config = angular.copy(_.find(config.components, {id: 'emailTemplates'}));
            $scope.objectTypeList = config.objectTypes;
            $scope.actionList = config.actions;
            $scope.sourceList = config.sources;

            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
            ReloadGrid();
        });

        $scope.addNew = function () {

            var template = {};
            $scope.template = template;

            var item = {
                emailPattern: "",
                objectTypes: [],
                source: "",
                templateName: "",
                actions: []
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
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

        $scope.deleteRow = function (rowEntity) {
            emailTemplatesService.deleteEmailTemplate(rowEntity.templateName).then(function () {
                ReloadGrid();
                MessageService.succsessAction();
            }, function () {
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
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                emailTemplatesService.validateEmailTemplate(data.template).then(function (response) {
                    if (response.data.validTemplate) {
                        emailTemplatesService.saveEmailTemplate(data.template, data.file).then(function () {
                            MessageService.succsessAction();
                            ReloadGrid();
                        }, function () {
                            MessageService.errorAction();
                        });
                    } else {
                        DialogService.alert($translate.instant("admin.security.emailTemplates.modal.validationResponse") +
                                ' [' + response.data.objectType + ', ' + response.data.action +
                                ', "' + response.data.emailPattern + '"]');
                    }
                }, function (error) {
                    MessageService.errorAction(error);
                });

            });
        }

        function ReloadGrid() {
            var templatesPromise = emailTemplatesService.listEmailTemplates();
            templatesPromise.then(function (templates) {
                $scope.gridOptions.data = templates;
            });
        }
    }
]);