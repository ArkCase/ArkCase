angular.module('organizations').controller('Organizations.RelatedModalController', ['$scope', '$modal', '$modalInstance', '$translate'
        , 'Object.LookupService', 'UtilService'
        , function ($scope, $modal, $modalInstance, $translate, ObjectLookupService, Util) {

            ObjectLookupService.getOrganizationRelationTypes().then(
                function (relationshipTypes) {
                    $scope.relationshipTypes = relationshipTypes;
                    return relationshipTypes;
                });

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close({
                    organizationId: $scope.organizationId,
                    description: $scope.description,
                    relationshipType: $scope.relationshipType
                });
            };

            $scope.pickOrganization = function () {
                var params = {};
                params.header = $translate.instant("organizations.comp.related.dialogOrganizationPicker.header");
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
                        $scope.organizationName = selected.name;
                    }
                });
            };
        }
    ]
);