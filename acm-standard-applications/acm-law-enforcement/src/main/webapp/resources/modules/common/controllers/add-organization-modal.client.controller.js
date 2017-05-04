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

            $scope.radioChanged = function () {
                if ($scope.selectExisting != 0) {
                    $scope.isNew = false;
                    $scope.organizationId = '';
                    $scope.organizationName = '';
                    $scope.organization = '';
                    $scope.pickOrganization();
                }
                else {
                    $scope.isNew = true;
                    $scope.organizationId = '';
                    $scope.organizationName = '';
                    $scope.organization = '';
                    $scope.addNewOrganization();
                }
            };


            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close({
                    organizationId: $scope.organizationId,
                    description: $scope.description,
                    type: $scope.type,
                    organization: $scope.organization,
                    isNew: $scope.isNew
                });
            };

            $scope.pickOrganization = function () {
                var params = {};
                params.header = $translate.instant("common.dialogOrganizationPicker.header");
                params.filter = '"Object Type": ORGANIZATION';
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
                    if (!Util.isEmpty(selected)) {
                        $scope.organizationId = selected.object_id_s;
                        $scope.organizationValue = selected.name;
                    }
                });
            };

            $scope.addNewOrganization = function () {

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/new-organization-modal.client.view.html',
                    controller: 'Common.NewOrganizationModalController',
                    size: 'lg'
                });

                modalInstance.result.then(function (data) {
                    $scope.organizationId = '';
                    $scope.organizationValue = data.organization.organizationValue;
                    $scope.organization = data.organization;
                });
            };
        }
    ]
);