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

            var copyVariablePath = $translate.instant("contextMenu.options.copyVariablePath");

            $scope.navigationTreeMilestones = [];
            var index = 0;

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
                var modalScope = $scope.$new();
                modalScope.config = $scope.config;
                $scope.myData = [];
                var modalInstance = $modal.open({
                    scope: modalScope,
                    animation: true,
                    templateUrl: 'modules/admin/views/components/correspondence-management-add-edit-template.modal.client.view.html',
                    controller: [ '$scope', '$modalInstance', 'Admin.CMTemplatesService', 'ObjectService', function($scope, $modalInstance, correspondenceService, ObjectService) {

                        $scope.objectTypes = $scope.config.objectTypes;
                        $scope.selectedFiles = [];
                        $scope.template = {};

                        $scope.upload = function upload(files) {
                            $scope.selectedFiles = files;
                        };
                        $scope.onClickOk = function(files) {
                            correspondenceService.uploadTemplateWithTimestamp(files).then(function(result) {
                                correspondenceService.getTemplateData($scope.selectedRows.length > 0 ? $scope.selectedRows[0].templateId : $scope.selectedRows.length, result.data[0].name).then(function(template) {
                                    $scope.template.templateId = template.data.templateId;
                                    $scope.template.templateVersion = template.data.templateVersion;
                                    $scope.template.templateVersionActive = template.data.templateVersionActive;
                                    $scope.template.documentType = template.data.documentType;
                                    $scope.template.templateFilename = template.data.templateFilename;
                                    $scope.template.dateFormatString = template.data.dateFormatString;
                                    $scope.template.numberFormatString = template.data.numberFormatString;
                                    //we will activate it by default
                                    $scope.template.activated = true;

                                    var template = $scope.template;
                                    correspondenceService.saveTemplateData(template).then(function() {
                                        clearCachedForms(template);
                                        messageService.succsessAction();
                                        reloadGrid();
                                        $modalInstance.close();
                                    }, function() {
                                        messageService.errorAction();
                                    });
                                });
                            });
                        };
                        $scope.onClickCancel = function() {
                            $modalInstance.dismiss('cancel');
                        };

                        $scope.change = function(selectedName){
                            $scope.objectType = selectedName;
                            $scope.template.objectType = $scope.objectType.key;
                        };

                        $scope.changeTemplateModelProvider = function(templateModelProvider){
                            $scope.templateModelProvider = templateModelProvider;
                            $scope.template.templateModelProvider = $scope.templateModelProvider;

                            var templateModelProviderName = _.last(templateModelProvider.split('.'));
                            if (templateModelProviderName == 'ComplaintTemplateModelProvider'){
                                $scope.mergingType = ObjectService.ObjectTypes.COMPLAINT;
                            }
                            else if (templateModelProvider == 'CaseFileTemplateModelProvider'){
                                $scope.mergingType = ObjectService.ObjectTypes.CASE_FILE;
                            }

                            correspondenceMergeFieldsService.retrieveActiveMergeFieldsByType($scope.mergingType).then(function(mergeFields) {
                                $scope.mergeFieldsByType = mergeFields.data;
                            });
                        };

                        $scope.getNavBarTree = function(){
                            var modalScope = $scope.$new();
                            modalScope.config = $scope.config;
                            $scope.myData = [];

                            correspondenceService.listAllProperties($scope.templateModelProvider).then(function(allProperties) {
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
                                    }
                                }];
                            });

                            var modalInstance = $modal.open({
                                scope: modalScope,
                                animation: true,
                                templateUrl: 'modules/admin/views/components/correspondence-management-navigation-tree.modal.client.view.html',
                                controller: [ '$scope', '$modalInstance', '$translate', function($scope, $modalInstance, $translate) {
                                    setTimeout(function(){
                                        $scope.myTree.collapse_all();
                                    });

                                    $scope.onClickCancel = function() {
                                        $modalInstance.dismiss('Cancel');
                                    };

                                    $scope.contextMenuOptions = function() {
                                        $scope.variablePath = '${';

                                        $scope.navigationTreeMilestones = [];
                                        index = 0;

                                        var menuOptions = [
                                            {
                                                text: copyVariablePath,
                                                click: $scope.selectParentBranch()
                                            }
                                        ];

                                        //Remove the keyRoot
                                        $scope.navigationTreeMilestones.pop();

                                        for (var i = $scope.navigationTreeMilestones.length-1; i>=0; i--)
                                        {
                                            var milestoneLabel = $scope.navigationTreeMilestones[i].label;
                                            //Removing the dot on the last label
                                            if (i == 0) {
                                                $scope.variablePath += milestoneLabel.substring(0, milestoneLabel.length - 1);
                                            }
                                            else {
                                                $scope.variablePath += milestoneLabel;
                                            }
                                        }

                                        $scope.variablePath += '}';

                                        $scope.copyToClipboard($scope.variablePath);
                                        return menuOptions;
                                    };

                                    $scope.copyToClipboard = function (str) {
                                        var el = document.createElement('textarea');
                                        el.value = str;
                                        document.body.appendChild(el);
                                        el.select();
                                        document.execCommand('copy');
                                        document.body.removeChild(el);
                                    };

                                    $scope.selectParentBranch = function(branch) {
                                        var parent;
                                        if (branch == null) {
                                            branch = $scope.myTree.get_selected_branch();
                                            var branchLabel = branch.label;
                                            if (branch.data == "array"){
                                                branchLabel += '[X].';
                                            }
                                            else {
                                                branchLabel += '.';
                                            }

                                            var milestoneBranch = {
                                                label: branchLabel,
                                                index: index
                                            };

                                            index ++;
                                            $scope.navigationTreeMilestones.push(milestoneBranch);
                                        }
                                        if (branch != null) {
                                            parent = $scope.myTree.get_parent_branch(branch);
                                            if (parent != undefined) {
                                                var parentLabel = parent.label;
                                                if (parent.data == "array"){
                                                    parentLabel += '[X].';
                                                }
                                                else {
                                                    parentLabel += '.';
                                                }
                                                var milestoneParent = {
                                                    label: parentLabel,
                                                    index: index
                                                };

                                                index ++;
                                                $scope.navigationTreeMilestones.push(milestoneParent);
                                                $scope.selectParentBranch(parent);
                                            }
                                        }

                                    };


                                } ],
                                size: 'md',
                                backdrop: 'static'
                            });
                        }
                    } ],
                    size: 'md',
                    backdrop: 'static'
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
                el.value = "${" + str + "}";
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
            }

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
                    controller: [ '$scope', '$modalInstance', '$translate', function($scope, $modalInstance, $translate) {
                        setTimeout(function(){
                            $scope.myTree.collapse_all();
                        });

                        $scope.onClickCancel = function() {
                            $modalInstance.dismiss('Cancel');
                        };

                        $scope.contextMenuOptions = function() {
                            $scope.variablePath = '${';

                            $scope.navigationTreeMilestones = [];
                            index = 0;

                            var menuOptions = [
                                {
                                    text: copyVariablePath,
                                    click: $scope.selectParentBranch()
                                }
                            ];

                            //Remove the keyRoot
                            $scope.navigationTreeMilestones.pop();

                            for (var i = $scope.navigationTreeMilestones.length-1; i>=0; i--)
                            {
                                var milestoneLabel = $scope.navigationTreeMilestones[i].label;
                                //Removing the dot on the last label
                                if (i == 0) {
                                    $scope.variablePath += milestoneLabel.substring(0, milestoneLabel.length - 1);
                                }
                                else {
                                    $scope.variablePath += milestoneLabel;
                                }
                            }

                            $scope.variablePath += '}';

                            $scope.copyToClipboard($scope.variablePath);
                            return menuOptions;
                        };

                        $scope.copyToClipboard = function (str) {
                            var el = document.createElement('textarea');
                            el.value = str;
                            document.body.appendChild(el);
                            el.select();
                            document.execCommand('copy');
                            document.body.removeChild(el);
                        };

                        $scope.selectParentBranch = function(branch) {
                            var parent;
                            if (branch == null) {
                                branch = $scope.myTree.get_selected_branch();
                                var branchLabel = branch.label;
                                if (branch.data == "array"){
                                    branchLabel += '[X].';
                                }
                                else {
                                    branchLabel += '.';
                                }

                                var milestoneBranch = {
                                    label: branchLabel,
                                    index: index
                                };

                                index ++;
                                $scope.navigationTreeMilestones.push(milestoneBranch);
                            }
                            if (branch != null) {
                                parent = $scope.myTree.get_parent_branch(branch);
                                if (parent != undefined) {
                                    var parentLabel = parent.label;
                                    if (parent.data == "array"){
                                        parentLabel += '[X].';
                                    }
                                    else {
                                        parentLabel += '.';
                                    }
                                    var milestoneParent = {
                                        label: parentLabel,
                                        index: index
                                    };

                                    index ++;
                                    $scope.navigationTreeMilestones.push(milestoneParent);
                                    $scope.selectParentBranch(parent);
                                }
                            }

                        };

                    } ],
                    size: 'md',
                    backdrop: 'static'
                });

            };

        } ]);