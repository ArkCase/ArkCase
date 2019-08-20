'use strict';

angular.module('admin').controller('Admin.AddCMTemplateController', [ '$scope', '$modal', '$modalInstance', 'LookupService', 'Acm.StoreService', 'Admin.CMTemplatesService', 'ObjectService', 'Admin.CMMergeFieldsService', 'MessageService','params',
    function($scope, $modal, $modalInstance, LookupService, Store, correspondenceService, ObjectService, correspondenceMergeFieldsService, messageService, params) {

        $scope.objectTypes = $scope.config.objectTypes;
        $scope.selectedFiles = [];
        $scope.template = {};
        $scope.selectedRow = params['selectedRow'];
        $scope.isEdit = $scope.selectedRow !== undefined;
        $scope.objec = '';



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
        }

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
            else if (templateModelProvider == 'CaseFileTemplateModelProvider' || templateModelProvider =='FOIARequestTemplateModelProvider'){
                $scope.mergingType = ObjectService.ObjectTypes.CASE_FILE;
            }

            correspondenceMergeFieldsService.retrieveActiveMergeFieldsByType($scope.mergingType).then(function(mergeFields) {
                $scope.mergeFieldsByType = mergeFields.data;
            });
        };

        if($scope.isEdit){
            var currentSelectedObjectType = $scope.correspondenceObjectTypes.find(function(objectType) {
                return objectType.key === $scope.selectedRow.objectType;
            });
            $scope.selectedName = currentSelectedObjectType;
            $scope.template.label = $scope.selectedRow.label;
            $scope.changeTemplateModelProvider($scope.selectedRow.templateModelProvider);
            $scope.change(currentSelectedObjectType);
        }

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
                controller: 'Admin.NavigationTreeCMTemplateController',
                size: 'md',
                backdrop: 'static'
            });
        };
    }
]);