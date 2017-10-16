angular.module('common').controller('Common.AddOrganizationModalController', ['$scope', '$modal', '$modalInstance', '$translate'
        , 'Object.LookupService', 'UtilService', 'ConfigService', 'params'
        , function ($scope, $modal, $modalInstance, $translate
        , ObjectLookupService, Util, ConfigService, params) {

            ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.config = moduleConfig;
                return moduleConfig;
            });

            $scope.selectExisting = 0;
            $scope.types = params.types;
            $scope.showDescription = params.showDescription;
            $scope.showSetPrimary = params.showSetPrimary;
            $scope.returnValueValidationFunction = params.returnValueValidationFunction;
            $scope.duplicateOrganizationRoleError = false;
            $scope.editMode = !!params.organizationId;
            $scope.organizationId = params.organizationId;
            $scope.organizationValue = params.organizationValue;
            $scope.isValid = true;
            $scope.isDefault = params.isDefault;
            $scope.isSelectedParent = !!params.isSelectedParent;
            $scope.isEditParent = false;
            $scope.description = params.description;
            $scope.hideNoField = true;
            if (params.isSelectedParent) {
                $scope.organization = params.organization;
                if (!!params.organization.parentOrganization) {
                    $scope.isEditParent = !!params.organization.parentOrganization.organizationId;
                    $scope.organizationId = params.organization.organizationId;
                    $scope.organizationValue = params.organization.parentOrganization.organizationValue;
                }
            }
            if (params.isFirstOrganization) {
                $scope.isDefault = params.isFirstOrganization;
                $scope.hideNoField = !params.isFirstOrganization;
            }
            $scope.type = _.find($scope.types, function (type) {
                return type.key == params.type;
            });
            $scope.isNew = params.isNew;

            $scope.onClickCancel = function () {
                if ($scope.isSelectedParent && !!$scope.organization.parentOrganization && !(!!$scope.organization.parentOrganization.organizationId)) {
                    $scope.organization.parentOrganization = null;
                }
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                var retValue = {
                    organizationId: $scope.organizationId,
                    organizationValue: $scope.organizationValue,
                    organization: $scope.organization,
                    isNew: $scope.isNew
                };
                if ($scope.types && $scope.type) {
                    retValue.type = $scope.type.key;
                    retValue.inverseType = $scope.type.inverseKey;
                }
                if ($scope.showSetPrimary) {
                    retValue.isDefault = $scope.isDefault;
                }
                if ($scope.showDescription) {
                    retValue.description = $scope.description;
                }
                if ($scope.returnValueValidationFunction) {
                    var validationResult = $scope.returnValueValidationFunction(retValue);
                    if (validationResult.valid) {
                        $modalInstance.close(retValue);
                    } else {
                        $scope.duplicateOrganizationRoleError = validationResult.duplicateOrganizationRoleError;
                    }
                } else {
                    $modalInstance.close(retValue);
                }
            };

            $scope.pickOrganization = function () {
                $scope.isNew = false;

                var params = {};
                params.isSelectedParent = $scope.isSelectedParent;
                params.header = $translate.instant("common.dialogOrganizationPicker.header");
                params.filter = '"Object Type": ORGANIZATION &fq="status_lcs": ACTIVE';
                params.config = Util.goodMapValue($scope.config, "dialogOrganizationPicker");

                var modalInstance = $modal.open({
                    templateUrl: "modules/common/views/object-picker-modal.client.view.html",
                    controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = params.header;
                        $scope.filter = params.filter;
                        $scope.config = params.config;
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (selected) {
                    $scope.isValid = false;
                    if (!Util.isEmpty(selected)) {
                        $scope.organizationId = selected.object_id_s;
                        $scope.organizationValue = selected.name;
                    }
                });
            };
            
            $scope.isChanged = function () {
                $scope.isValid = false;
            };

            $scope.addNewOrganization = function () {
                $scope.isNew = true;

                var params = {};
                params.isSelectedParent = $scope.isSelectedParent;

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/new-organization-modal.client.view.html',
                    controller: 'Common.NewOrganizationModalController',
                    size: 'lg',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.isValid = false;
                    $scope.organizationId = '';
                    $scope.organizationValue = data.organization.organizationValue;
                    $scope.organization = data.organization;
                });
            };
        }
    ]
);