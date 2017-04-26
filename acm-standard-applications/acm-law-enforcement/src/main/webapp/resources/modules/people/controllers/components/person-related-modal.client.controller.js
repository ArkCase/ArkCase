angular.module('people').controller('People.RelatedModalController', ['$scope', '$modal', '$modalInstance', '$translate'
        , 'Object.LookupService', 'UtilService'
        , function ($scope, $modal, $modalInstance, $translate, ObjectLookupService, Util) {

            ObjectLookupService.getPersonRelationTypes().then(
                function (relationshipTypes) {
                    $scope.relationshipTypes = relationshipTypes;
                    return relationshipTypes;
                });

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close({
                    personId: $scope.personId,
                    description: $scope.description,
                    relationshipType: $scope.relationshipType
                });
            };

            $scope.pickPerson = function () {
                var params = {};
                params.header = $translate.instant("people.comp.related.dialogPersonPicker.header");
                params.filter = '"Object Type": PERSON';
                params.config = Util.goodMapValue($scope.config, "dialogPersonPicker");

                var modalInstance = $modal.open({
                    templateUrl: "modules/people/views/components/person-picker-modal.client.view.html",
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
                        $scope.personId = selected.object_id_s;
                        $scope.personName = selected.name;
                    }
                });
            };
        }
    ]
);