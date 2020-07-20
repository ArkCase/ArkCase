'use strict';

angular.module('admin').controller('Admin.LdapConfigController',
    [ '$scope', 'Admin.LdapConfigService', '$modal', 'Helper.UiGridService', 'Admin.ModalDialogService', 'MessageService', '$translate', function($scope, ldapConfigService, $modal, HelperUiGridService, modalDialogService, messageService, $translate) {

        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });

        //get config and init grid settings
        $scope.config.$promise.then(function(config) {
            var componentConfig = _.find(config.components, {
                id: 'securityLdapConfig'
            });
            $scope.directoryTypes = componentConfig.directoryTypes;
            var columnDefs = componentConfig.columnDefs;
            var columnDef = addEditColumn();
            var columnLdapUserTemplate = userTemplate();
            var columnLdapGroupTemplate = groupTemplate();
            var columnEditPassword = addEditPasswordColumn();
            $scope.loadingDirectories = true;

            columnDefs.push(columnLdapGroupTemplate);
            columnDefs.push(columnLdapUserTemplate);
            columnDefs.push(columnDef);
            columnDefs.push(columnEditPassword);

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

        $scope.editRow = function(rowEntity) {
            rowEntity.enableEditingLdapUsers = rowEntity.enableEditingLdapUsers === true;
            rowEntity.syncEnabled = rowEntity.syncEnabled === true;
            if (localStorage.getItem("ldapConfig." + rowEntity.id) !== null) {
                showModal(angular.copy(JSON.parse(localStorage.getItem("ldapConfig." + rowEntity.id))), true);
            } else {
                showModal(angular.copy(rowEntity), true);
            }
        };

        $scope.editPassword = function(rowEntity) {
            showChagePasswordModal(angular.copy(rowEntity), true);
        };


        $scope.showUserTemplate = function(rowEntity) {
            var modalScope = $scope.$new();
            var attributes = [ rowEntity.addUserTemplate ];
            removePrefixInKey(attributes);
            modalScope.attributes = attributes[0];
            $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/security.ldap-config-user-template.popup.html',
                backdrop: 'static',
                controller: function($scope, $modalInstance) {
                    $scope.ok = function() {
                        $modalInstance.close({});
                    };
                    $scope.cancel = function() {
                        $modalInstance.dismiss('cancel');
                    }
                }
            });
        };

        $scope.showGroupTemplate = function(rowEntity) {
            var modalScope = $scope.$new();
            var attributes = [ rowEntity.addGroupTemplate ];
            removePrefixInKey(attributes);
            modalScope.attributes = attributes[0];
            $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/security.ldap-config-group-template.popup.html',
                backdrop: 'static',
                controller: function($scope, $modalInstance) {
                    $scope.ok = function() {
                        $modalInstance.close({});
                    };
                    $scope.cancel = function() {
                        $modalInstance.dismiss('cancel');
                    }
                }
            });
        };

        $scope.deleteRow = function(rowEntity) {
            $scope.deleteDir = rowEntity;
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.headerText'),
                bodyText: $translate.instant('admin.security.ldapConfig.dialog.confirm.delete.bodyText')
            };
            modalDialogService.showModal({}, modalOptions).then(function() {
                ldapConfigService.deleteDirectory($scope.deleteDir.id).then(function() {
                    gridHelper.deleteRow($scope.deleteDir);
                    messageService.info($translate.instant('admin.security.ldapConfig.messages.delete.success'));
                }, function() {
                    messageService.error($translate.instant('admin.security.ldapConfig.messages.delete.error'));
                });
            });
        };

        $scope.cronRegex = /^(((([0-9]|[0-5][0-9])(-([0-9]|[0-5][0-9]))?,)*([0-9]|[0-5][0-9])(-([0-9]|[0-5][0-9]))?)|(([\*]|[0-9]|[0-5][0-9])\/([0-9]|[0-5][0-9]))|([\?])|([\*]))[\s](((([0-9]|[0-5][0-9])(-([0-9]|[0-5][0-9]))?,)*([0-9]|[0-5][0-9])(-([0-9]|[0-5][0-9]))?)|(([\*]|[0-9]|[0-5][0-9])\/([0-9]|[0-5][0-9]))|([\?])|([\*]))[\s](((([0-9]|[0-1][0-9]|[2][0-3])(-([0-9]|[0-1][0-9]|[2][0-3]))?,)*([0-9]|[0-1][0-9]|[2][0-3])(-([0-9]|[0-1][0-9]|[2][0-3]))?)|(([\*]|[0-9]|[0-1][0-9]|[2][0-3])\/([0-9]|[0-1][0-9]|[2][0-3]))|([\?])|([\*]))[\s](((([1-9]|[0][1-9]|[1-2][0-9]|[3][0-1])(-([1-9]|[0][1-9]|[1-2][0-9]|[3][0-1]))?,)*([1-9]|[0][1-9]|[1-2][0-9]|[3][0-1])(-([1-9]|[0][1-9]|[1-2][0-9]|[3][0-1]))?(C)?)|(([1-9]|[0][1-9]|[1-2][0-9]|[3][0-1])\/([1-9]|[0][1-9]|[1-2][0-9]|[3][0-1])(C)?)|(L(-[0-9])?)|(L(-[1-2][0-9])?)|(L(-[3][0-1])?)|(LW)|([1-9]W)|([1-3][0-9]W)|([\?])|([\*]))[\s](((([1-9]|0[1-9]|1[0-2])(-([1-9]|0[1-9]|1[0-2]))?,)*([1-9]|0[1-9]|1[0-2])(-([1-9]|0[1-9]|1[0-2]))?)|(([1-9]|0[1-9]|1[0-2])\/([1-9]|0[1-9]|1[0-2]))|(((JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(-(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?,)*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(-(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)|((JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\/(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))|([\?])|([\*]))[\s]((([1-7](-([1-7]))?,)*([1-7])(-([1-7]))?)|([1-7]\/([1-7]))|(((MON|TUE|WED|THU|FRI|SAT|SUN)(-(MON|TUE|WED|THU|FRI|SAT|SUN))?,)*(MON|TUE|WED|THU|FRI|SAT|SUN)(-(MON|TUE|WED|THU|FRI|SAT|SUN))?(C)?)|((MON|TUE|WED|THU|FRI|SAT|SUN)\/(MON|TUE|WED|THU|FRI|SAT|SUN)(C)?)|(([1-7]|(MON|TUE|WED|THU|FRI|SAT|SUN))?(L|LW)?)|(([1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)#([1-7])?)|([\?])|([\*]))([\s]?(([\*])?|(19[7-9][0-9])|(20[0-9][0-9]))?|(((19[7-9][0-9])|(20[0-9][0-9]))\/((19[7-9][0-9])|(20[0-9][0-9])))?|((((19[7-9][0-9])|(20[0-9][0-9]))(-((19[7-9][0-9])|(20[0-9][0-9])))?,)*((19[7-9][0-9])|(20[0-9][0-9]))(-((19[7-9][0-9])|(20[0-9][0-9])))?)?)$/i;

        $scope.showModal = showModal;

        function showModal(dir, isEdit) {
            $scope.passwordErrorMessages = {
                notSamePasswordsMessage: ''
            };
            var modalScope = $scope.$new();
            modalScope.dir = dir || {};
            modalScope.isEdit = isEdit || false;
            if (!modalScope.isEdit) {
                modalScope.dir.enableEditingLdapUsers = false;
                modalScope.dir.syncEnabled = false;
            }
            modalScope.directoryTypes = $scope.directoryTypes;

            var modalInstance = $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/security.ldap-config.popup.html',
                backdrop: 'static',
                controller: function($scope, $modalInstance) {
                    $scope.ok = function() {
                        $modalInstance.close({
                            dir: $scope.dir,
                            isEdit: $scope.isEdit
                        });
                    };
                    $scope.cancel = function() {
                        $modalInstance.dismiss('cancel');
                    };
                }
            });

            modalInstance.result.then(function(data) {
                var currentLdapDir = angular.copy(data.dir);
                addPrefixInKey(data.dir, "ldapConfig");
                if (data.isEdit) {
                    ldapConfigService.updateDirectory(data.dir).then(function() {
                        localStorage.setItem("ldapConfig."+ currentLdapDir.id, JSON.stringify(currentLdapDir));
                        $scope.loadingDirectories = false;
                        messageService.info($translate.instant('admin.security.ldapConfig.messages.update.success'));
                    }, function() {
                        messageService.error($translate.instant('admin.security.ldapConfig.messages.update.error'));
                    });
                } else {
                    ldapConfigService.createDirectory(data.dir).then(function() {
                        reloadGrid();
                        messageService.info($translate.instant('admin.security.ldapConfig.messages.insert.success'));
                    }, function() {
                        messageService.error($translate.instant('admin.security.ldapConfig.messages.insert.error'));
                    });
                }
            });
        }

        function showChagePasswordModal(dir, isEdit) {
            $scope.passwordErrorMessages = {
                notSamePasswordsMessage: ''
            };
            var modalScope = $scope.$new();
            modalScope.authUserPassword = {};

            var modalInstance = $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/security.ldap-change-password.popup.html',
                backdrop: 'static',
                controller: function ($scope, $modalInstance) {
                    $scope.ok = function () {
                        $modalInstance.close({
                            authUserPassword: modalScope.authUserPassword.value,
                        });
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                }
            });
            modalInstance.result.then(function (data) {

                dir.authUserPassword = data.authUserPassword;

                addPrefixInKey(dir, "ldapConfig");
                ldapConfigService.updateDirectory(dir).then(function () {
                    reloadGrid();
                    messageService.info($translate.instant('admin.security.ldapConfig.messages.update.success'));
                }, function () {
                    messageService.error($translate.instant('admin.security.ldapConfig.messages.update.error'));
                });

            });
        }


        function addEditColumn() {
            return {
                name: "edit",
                cellEditableCondition: false,
                width: 40,
                cellClass: 'text-center',
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span><i class='fa fa-pencil fa-lg' style='cursor :pointer' " + "ng-click='grid.appScope.editRow(row.entity)'></i></span>"
            };
        }


        function addEditPasswordColumn() {
            return {
                name: "editPassword",
                cellEditableCondition: false,
                width: 40,
                cellClass: 'text-center',
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span><i class='fa fa-lock fa-lg' style='cursor :pointer' " + "ng-click='grid.appScope.editPassword(row.entity)'></i></span>"
            };
        }

        function userTemplate() {
            return {
                name: "userTemplate",
                cellEditableCondition: false,
                width: 40,
                cellClass: 'text-center',
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span title=\"{{'admin.security.ldapConfig.table.userAttributesConfig' | translate}}\">" + "<i class='fa fa-user fa-lg' style='cursor :pointer' " + "ng-click='grid.appScope.showUserTemplate(row.entity)'></i></span>"
            };
        }

        function groupTemplate() {
            return {
                name: "addGroupTemplate",
                cellEditableCondition: false,
                width: 40,
                cellClass: 'text-center',
                headerCellTemplate: "<span></span>",
                cellTemplate: "<span title=\"{{'admin.security.ldapConfig.table.groupAttributesConfig' | translate}}\">" + "<i class='fa fa-users fa-lg' style='cursor :pointer' " + "ng-click='grid.appScope.showGroupTemplate(row.entity)'></i></span>"
            };
        }


        //we need this because key name contains '.'
        function removePrefixInKey(data) {
            angular.forEach(data, function(row, index) {
                angular.forEach(row, function(element, key) {
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
            angular.forEach(dir, function(element, key) {
                if (key.match('.') !== -1) {
                    delete dir[key];
                    var newKey = prefix + '.' + key;
                    dir[newKey] = element;
                }
            });
        }

        function reloadGrid() {
            var tempLdapPromise = ldapConfigService.retrieveDirectories();
            tempLdapPromise.then(function(directories) {
                removePrefixInKey(directories.data);
                $scope.gridOptions.data = _.values(directories.data);
                if($scope.loadingDirectories && $scope.gridOptions.data.length > 0){
                    for(var i = 0; i < $scope.gridOptions.data.length; i++){
                        localStorage.removeItem("ldapConfig." + $scope.gridOptions.data[i].id);
                    }
                }
            });
        }

    } ]);
