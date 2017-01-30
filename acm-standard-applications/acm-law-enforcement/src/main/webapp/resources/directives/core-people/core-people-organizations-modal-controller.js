'use strict';

angular.module('directives').controller('Directives.CorePeopleOrganizationsModalController', ['$scope', '$modal', '$modalInstance',
    '$translate', 'UtilService',
    function ($scope, $modal, $modalInstance, $translate, Util) {

        $scope.selectExisting = 0;

        $scope.onClickOk = function () {
            $modalInstance.close({
                organization: $scope.organization,
                isEdit: $scope.isEdit
            });
        };
        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.radioChanged = function () {
            if (!$scope.isEdit && $scope.selectExisting != 0) {
                $scope.pickOrganization();
            }
            else {
                $scope.organization.organizationId = '';
                $scope.organization.organizationValue = '';
                $scope.organization.organizationType = '';
            }
        };

        $scope.pickOrganization = function () {
            if (!$scope.isEdit && $scope.selectExisting != 0) {
                var params = {
                    header: $translate.instant("common.directive.corePeople.organizations.modal.dialogOrganizationPicker.header"),
                    filter: '"Object Type": ORGANIZATION',
                    config: Util.goodMapValue($scope.config, "dialogOrganizationPicker")
                };
                var modalInstance = $modal.open({
                    templateUrl: "directives/core-people/core-people-organizations-picker-modal.client.view.html",
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
                        $scope.organization.organizationId = selected.object_id_s;
                        $scope.organization.organizationValue = selected.name;
                        $scope.organization.organizationType = selected.type_lcs;
                    }
                });
            }
        };
    }
]);