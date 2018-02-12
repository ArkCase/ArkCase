angular.module('common').controller(
        'Common.AddObjectAssociationModalController',
        [ '$scope', '$modal', '$modalInstance', '$translate', 'Object.LookupService', 'UtilService', 'ConfigService', 'params',
                function($scope, $modal, $modalInstance, $translate, ObjectLookupService, Util, ConfigService, params) {

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.config = moduleConfig;
                        return moduleConfig;
                    });

                    $scope.types = params.types;
                    $scope.showDescription = params.showDescription;

                    $scope.objectId = params.objectId;
                    $scope.editMode = !!params.objectId;
                    $scope.objectName = params.objectName;
                    $scope.description = params.description;
                    $scope.configName = params.customConfigName ? params.customConfigName : 'dialogObjectPicker';
                    $scope.filter = params.customFilter ? params.customFilter : '';

                    $scope.headerLabel = $translate.instant("common.addObjectAssociation.title." + ($scope.editMode ? 'edit' : 'add'), {
                        objectLabel : params.objectTypeLabel
                    });
                    $scope.selectExistingLabel = $translate.instant("common.addObjectAssociation.selectExisting.label", {
                        objectLabel : params.objectTypeLabel
                    });

                    $scope.objectNameProperty = params.objectNameProperty;
                    $scope.objectTypeLabel = params.objectTypeLabel;
                    $scope.type = _.find($scope.types, function(type) {
                        return type.key == params.type;
                    });

                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };

                    $scope.onClickOk = function() {
                        var retValue = {
                            solrDocument : $scope.solrDocument
                        };
                        if ($scope.types && $scope.type) {
                            retValue.type = $scope.type.key;
                            retValue.inverseType = $scope.type.inverseKey;
                        }
                        if ($scope.showDescription) {
                            retValue.description = $scope.description;
                        }
                        $modalInstance.close(retValue);
                    };

                    $scope.pickObject = function() {
                        var params = {};
                        params.header = $translate.instant("common.dialogObjectPicker.header", {
                            objectLabel : $scope.objectTypeLabel
                        });
                        params.filter = $scope.filter;
                        params.config = Util.goodMapValue($scope.config, $scope.configName);

                        var modalInstance = $modal.open({
                            templateUrl : "modules/common/views/object-picker-modal.client.view.html",
                            controller : [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
                                $scope.config = params.config;
                            } ],
                            animation : true,
                            size : 'lg',
                            backdrop : 'static',
                            resolve : {
                                params : function() {
                                    return params;
                                }
                            }
                        });
                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {
                                $scope.objectName = $scope.objectNameProperty ? selected[$scope.objectNameProperty] : selected.name;
                                $scope.solrDocument = selected;
                            }
                        });
                    };
                } ]);