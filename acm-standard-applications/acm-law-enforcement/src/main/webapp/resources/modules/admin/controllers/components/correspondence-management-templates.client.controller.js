'use strict';

angular.module('admin').controller('Admin.CMTemplatesController',
        [ '$scope', '$modal', 'Admin.CMTemplatesService', 'Helper.UiGridService', 'MessageService', 'LookupService', 'Acm.StoreService', 'Object.LookupService', 'Admin.CMMergeFieldsService', '$translate',
            function($scope, $modal, correspondenceService, HelperUiGridService, messageService, LookupService, Store, ObjectLookupService, correspondenceMergeFieldsService, $translate) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();
            $scope.selectedRows = [];
            $scope.configVersions = {};

            $scope.map = {};
            $scope.myTree = {};

            var copyVariablePath = $translate.instant('contextMenu.options.copyVariablePath');

            $scope.navigationTreeMilestones = [];

            $scope.gridOptions = {
                enableRowSelection: true,
                enableFiltering: false,
                enableRowHeaderSelection: true,
                enableFullRowSelection: true,
                data: [],
                onRegisterApi: function(gridApi) {
                    $scope.gridApi = gridApi;
                    gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                        $scope.selectedRows = gridApi.selection.getSelectedRows();
                    });

                    gridApi.selection.on.rowSelectionChangedBatch($scope, function(rows) {
                        $scope.selectedRows = gridApi.selection.getSelectedRows();
                    });
                }
            };

            //get config and init grid settings
            $scope.config.$promise.then(function(config) {
                var configVersions = _.find(config.components, {
                    id: 'correspondenceManagementTemplateVersions'
                });
                var config = _.find(config.components, {
                    id: 'correspondenceManagementTemplates'
                });

                correspondenceService.listTemplateModelProviders().then(function(templateModelProviders) {
                    $scope.templateModelProviders = templateModelProviders;
                });
                promiseUsers.then(function(data) {
                    gridHelper.setUserNameFilterToConfig(promiseUsers, config);
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.columnDefs;
                    $scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    $scope.gridOptions.paginationPageSize = config.paginationPageSize;

                    gridHelper.setUserNameFilterToConfig(promiseUsers, configVersions);
                    $scope.configVersions = configVersions;

                    reloadGrid();
                });
            });

            $scope.addTemplate = function() {
                var params = {};
                params.selectedRow = $scope.selectedRows[0];
                var modalScope = $scope.$new();
                modalScope.config = $scope.config;
                $scope.myData = [];
                var modalInstance = $modal.open({
                    scope: modalScope,
                    animation: true,
                    templateUrl: 'modules/admin/views/components/correspondence-management-add-edit-template.modal.client.view.html',
                    controller: 'Admin.AddCMTemplateController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

            };

            $scope.contextMenuMergeFields = function(mergeFieldValue){
                var menuOptions = [
                    {
                        text: copyVariablePath,
                        click: $scope.copyMergeFieldToClipboard(mergeFieldValue)
                    }
                ];
                return menuOptions;
            };

            $scope.copyMergeFieldToClipboard = function (str) {
                var el = document.createElement('textarea');
                el.value = '${' + str + '}';
                document.body.appendChild(el);
                el.select();
                document.execCommand('copy');
                document.body.removeChild(el);
            };

            $scope.versionTemplate = function() {
                var modalScope = $scope.$new();
                modalScope.config = $scope.configVersions;
                var templateVersionsPromise = correspondenceService.getTemplateVersionData($scope.selectedRows[0].templateId);
                templateVersionsPromise.then(function(templateVersionData) {
                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'modules/admin/views/components/correspondence-management-template-versions.modal.client.view.html',
                        controller: function($scope, $modalInstance) {

                            angular.forEach(templateVersionData.data, function(row, index) {
                                row.downloadFileName = correspondenceService.downloadByFilename(row.templateFilename);
                            });
                            $scope.gridOptions = {
                                enableColumnResizing: true,
                                enableRowSelection: true,
                                columnDefs: $scope.config.columnDefs,
                                paginationPageSizes: $scope.config.paginationPageSizes,
                                paginationPageSize: $scope.config.paginationPageSize,
                                data: templateVersionData.data
                            };
                            $scope.onClickOk = function() {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        size: 'lg',
                        backdrop: 'static'
                    });
                });
            };

            $scope.deleteTemplate = function() {

                angular.forEach($scope.selectedRows, function(row, index) {
                    correspondenceService.deleteTemplate(row.templateId).then(function() {
                        clearCachedForms(row);
                        reloadGrid();
                        messageService.succsessAction();
                    }, function() {
                        messageService.errorAction();
                    });
                });

            };

            $scope.activate = function(rowEntity) {
                var template = angular.copy(rowEntity);
                template.activated = !rowEntity.activated;
                correspondenceService.saveTemplateData(template).then(function() {
                    clearCachedForms(template);
                    messageService.succsessAction();
                    reloadGrid();
                }, function() {
                    messageService.errorAction();
                });
            };

            function clearCachedForms(template) {
                var cacheConfigMap = new Store.SessionData(LookupService.SessionCacheNames.CONFIG_MAP);
                var configMap = cacheConfigMap.get();
                if (template.objectType == 'CASE_FILE') {
                    delete configMap['caseCorrespondenceForms'];
                } else if (template.objectType == 'COMPLAINT') {
                    delete configMap['complaintCorrespondenceForms'];
                }
                cacheConfigMap.set(configMap);
            }
            function reloadGrid() {
                var templatesPromise = correspondenceService.retrieveActiveVersionTemplatesList();
                templatesPromise.then(function(templates) {
                    angular.forEach(templates.data, function(row, index) {
                        row.downloadFileName = correspondenceService.downloadByFilename(row.templateFilename);
                    });
                    $scope.gridOptions.data = templates.data;
                    $scope.selectedRows = [];
                });

                ObjectLookupService.getCorrespondenceObjectTypes().then(function(correspondenceObject) {
                    $scope.correspondenceObjectTypes = correspondenceObject;
                    if ($scope.mergingType == undefined)
                    {
                        $scope.mergingType = $scope.correspondenceObjectTypes[0].key;
                    }
                    correspondenceMergeFieldsService.retrieveActiveMergeFieldsByType($scope.mergingType).then(function(mergeFields) {
                        $scope.mergeFieldsByType = mergeFields.data;
                    });
                });
            }

            $scope.getNavBarTree = function(templateModelProvider){
                var modalScope = $scope.$new();
                modalScope.config = $scope.config;
                $scope.myData = [];

                correspondenceService.listAllProperties(templateModelProvider).then(function(allProperties) {
                    $scope.allProperties = allProperties.data.properties;
                    //Get the name from the package of the model that is used for the template provider
                    var keyRoot = _.last(allProperties.data.id.split(':'));
                    $scope.variablePath = '';

                    var traversePropertiesTree = function(propertiesRow, keyRoot) {
                        var mapValues = [];
                        for (var property in propertiesRow) {
                            mapValues.push({'propertyName': property, 'propertyType': propertiesRow[property].type, 'propertyReference': propertiesRow[property].$ref});
                            $scope.map[keyRoot] = mapValues;
                            if ((propertiesRow[property].type) === 'array')
                            {
                                traversePropertiesTree(propertiesRow[property].items.properties, property);
                            }
                            else if ((propertiesRow[property].type) === 'object')
                            {
                                traversePropertiesTree(propertiesRow[property].properties, property);
                            }
                        }
                    };

                    //Traverse through the whole tree to get all properties
                    traversePropertiesTree($scope.allProperties, keyRoot);

                    var generateChildren = function(root){
                        var rootChildren = $scope.map[root];
                        var array = [];
                        for (var children in rootChildren) {
                            if (rootChildren[children].propertyType === 'array' || rootChildren[children].propertyType === 'object'){
                                array.push({
                                    label: rootChildren[children].propertyName,
                                    data: rootChildren[children].propertyType,
                                    children: [{}],
                                    onSelect: function(branch) {
                                        var branchChildren = generateChildren(branch.label);
                                        branch.children.pop();
                                        for (var i=0; i<branchChildren.children.length; i++){
                                            branch.children.push(branchChildren.children[i]);
                                        }
                                    }
                                });
                            }
                            else {
                                array.push({
                                    label: rootChildren[children].propertyName,
                                    data: rootChildren[children].propertyType
                                });
                            }
                        }

                        return {
                            label: root,
                            children: array
                        };
                    };

                    //Generate the root properties
                    var keyRootChildren = generateChildren(keyRoot);

                    $scope.myData = [{
                        label: keyRoot,
                        children: [{}],
                        onSelect: function(branch) {
                            branch.children.pop();
                            for (var i=0; i<keyRootChildren.children.length; i++){
                                branch.children.push(keyRootChildren.children[i]);
                            }
                            branch.children.push(
                                {
                                    label: "currentDate"
                                },
                                {
                                    label: "baseUrl"
                                },
                                {
                                    label: "files"
                                }
                            )
                        }
                    }];
                });

                var modalInstance = $modal.open({
                    scope: modalScope,
                    animation: true,
                    templateUrl: 'modules/admin/views/components/correspondence-management-navigation-tree.modal.client.view.html',
                    controller: 'Admin.NavigationTreeCMTemplateController',
                    size: 'md',
                    backdrop: 'static'
                });

            };

        } ]);