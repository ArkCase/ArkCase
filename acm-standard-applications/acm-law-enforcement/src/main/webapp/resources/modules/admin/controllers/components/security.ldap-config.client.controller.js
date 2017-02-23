'use strict';

angular.module('admin').controller('Admin.LdapConfigController', ['$scope', 'Admin.LdapConfigService', '$modal'
    , 'Helper.UiGridService', 'Admin.ModalDialogService', 'MessageService', '$translate',
    function ($scope, ldapConfigService, $modal, HelperUiGridService, modalDialogService, messageService, $translate) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var componentConfig = _.find(config.components, {id: 'securityLdapConfig'});
            var columnDefs = componentConfig.columnDefs;
            var columnDef = addEditColumn();
            var columnAddUserTemplate = addUserTemplate();
            columnDefs.push(columnAddUserTemplate);
            columnDefs.push(columnDef);

            gridHelper.addDeleteButton(columnDefs, "grid.appScope.deleteRow(row.entity)");

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: columnDefs,
                totalItems: 0,
                data: []
            };

            reloadGrid();
        });

        $scope.editRow = function (rowEntity) {
            rowEntity.enableEditingLdapUsers = rowEntity.enableEditingLdapUsers === "true";
            showModal(angular.copy(rowEntity), true);
        };

        $scope.addUserTemplate = function (rowEntity) {
            var modalScope = $scope.$new();
            modalScope.directoryName = rowEntity.id;
            var modalInstance = $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/security.ldap-config-add-user-template.popup.html',
                backdrop: 'static',
                controller: function ($scope, $modalInstance) {
                    $scope.template = {};
                    $scope.templateType = "";
                    $scope.ok = function () {
                        $modalInstance.close({
                            template: $scope.template,
                            templateType: $scope.templateType,
                            templateName: $scope.directoryName
                        });
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    }
                }
            });

            modalInstance.result.then(function (data) {
                var promiseConfig;
                if (data.templateType == "openLdap") {
                    promiseConfig = ldapConfigService.createOpenLdapUserTemplate(data.template, data.templateName)
                } else if (data.templateType == "activeDirectory") {
                    promiseConfig = ldapConfigService.createActiveDirectoryUserTemplate(data.template, data.templateName)
                }
                promiseConfig.then(function (data) {
                    messageService.info($translate.instant('admin.security.ldapConfig.messages.insert.template.success'));
                }, function () {
                    messageService.error($translate.instant('admin.security.ldapConfig.messages.insert.template.error'));
                });
            });
        };

        $scope.deleteRow = function (rowEntity) {
            $scope.deleteDir = rowEntity;
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.headerText'),
                bodyText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.bodyText')
            };
            modalDialogService.showModal({}, modalOptions).then(function () {
                ldapConfigService.deleteDirectory($scope.deleteDir.id).then(function () {
                    gridHelper.deleteRow($scope.deleteDir);
                    messageService.info($translate.instant('admin.security.ldapConfig.messages.delete.success'));
                }, function () {
                    messageService.error($translate.instant('admin.security.ldapConfig.messages.delete.error'));
                });
            });
        };


        $scope.showModal = showModal;


        function showModal(dir, isEdit) {
            var modalScope = $scope.$new();
            modalScope.dir = dir || {};
            modalScope.isEdit = isEdit || false;

            var modalInstance = $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/security.ldap-config.popup.html',
                backdrop: 'static',
                controller: function ($scope, $modalInstance) {
                    $scope.ok = function () {
                        $modalInstance.close({dir: $scope.dir, isEdit: $scope.isEdit});
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    }
                }
            });

            modalInstance.result.then(function (data) {
                    addPrefixInKey(data.dir, "ldapConfig");
                    if (data.isEdit) {
                        ldapConfigService.updateDirectory(data.dir).then(function () {
                            reloadGrid();
                            messageService.info($translate.instant('admin.security.ldapConfig.messages.update.success'));
                        }, function () {
                            messageService.error($translate.instant('admin.security.ldapConfig.messages.update.error'));
                        });
                    } else {
                        ldapConfigService.createDirectory(data.dir).then(function () {
                            reloadGrid();
                            messageService.info($translate.instant('admin.security.ldapConfig.messages.insert.success'));
                        }, function () {
                            messageService.error($translate.instant('admin.security.ldapConfig.messages.insert.error'));
                        });
                    }
                }
            );
        }

        function addEditColumn() {
            var columnDef = {
                name: "edit",
                cellEditableCondition: false,
                width: 40,
                cellClass: 'text-center',
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span><i class='fa fa-pencil fa-lg' style='cursor :pointer' " +
                "ng-click='grid.appScope.editRow(row.entity)'></i></span>"
            };
            return columnDef;
        }

        function addUserTemplate() {
            var columnDef = {
                name: "addUserTemplate",
                cellEditableCondition: false,
                width: 40,
                cellClass: 'text-center',
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span title='Add user LDAP configuration'><i class='fa fa-user-plus fa-lg' style='cursor :pointer' " +
                "ng-click='grid.appScope.addUserTemplate(row.entity)'></i></span>"
            };
            return columnDef;
        }

//we need this because key name contains '.'
        function removePrefixInKey(data) {
            angular.forEach(data, function (row, index) {
                angular.forEach(row, function (element, key) {
                    if (key.match('.') !== -1) {
                        delete row[key];
                        var newKey = key.replace(/[a-zA-Z]*?\./, '');
                        row[newKey] = element;
                    }
                });
            });
        }

//we need this because backend expects keys with prefix
        function addPrefixInKey(dir, prefix) {
            angular.forEach(dir, function (element, key) {
                if (key.match('.') !== -1) {
                    delete dir[key];
                    var newKey = prefix + '.' + key;
                    dir[newKey] = element;
                }
            });
        }

        function reloadGrid() {
            var tempLdapPromise = ldapConfigService.retrieveDirectories();
            tempLdapPromise.then(function (directories) {
                removePrefixInKey(directories.data);
                $scope.gridOptions.data = directories.data;
            });
        }
    }
]);

