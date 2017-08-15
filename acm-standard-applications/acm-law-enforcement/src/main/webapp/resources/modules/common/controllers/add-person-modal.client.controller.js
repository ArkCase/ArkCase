angular.module('common').controller('Common.AddPersonModalController', ['$scope', '$modal', '$modalInstance', '$translate'
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
            $scope.returnValueValidationFunction = params.returnValueValidationFunction;
            $scope.duplicatePersonRoleError = false;

            $scope.showSetPrimary = params.showSetPrimary;

            $scope.personId = params.personId;
            $scope.editMode = !!params.personId;
            $scope.person = params.person;
            $scope.personName = params.personName;
            $scope.isDefault = params.isDefault;
            $scope.description = params.description;
            $scope.type = _.find($scope.types, function (type) {
                return type.type == params.type;
            });
            $scope.isNew = params.isNew;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                var retValue = {
                    personId: $scope.personId,
                    type: $scope.type.type,
                    inverseType: $scope.type.inverseType,
                    person: $scope.person,
                    personImages: $scope.personImages,
                    isNew: $scope.isNew
                };
                if ($scope.showSetPrimary) {
                    retValue['isDefault'] = $scope.isDefault;
                }
                if ($scope.showDescription) {
                    retValue['description'] = $scope.description;
                }
                if ($scope.returnValueValidationFunction) {
                    var validationResult = $scope.returnValueValidationFunction(retValue);
                    if (validationResult.valid) {
                        $modalInstance.close(retValue);
                    } else {
                        $scope.duplicatePersonRoleError = validationResult.duplicatePersonRoleError;
                    }
                } else {
                    $modalInstance.close(retValue);
                }
            };

            $scope.pickPerson = function () {
                $scope.isNew = false;
                $scope.personId = '';
                $scope.personName = '';
                $scope.person = '';

                var params = {};
                params.header = $translate.instant("common.dialogPersonPicker.header");
                params.filter = '"Object Type": PERSON &fq="status_lcs": ACTIVE';
                params.config = Util.goodMapValue($scope.config, "dialogPersonPicker");

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
                        $scope.personId = selected.object_id_s;
                        $scope.personName = selected.name;
                    }
                });
            };

            $scope.addNewPerson = function () {
                $scope.isNew = true;
                $scope.personId = '';
                $scope.personName = '';
                $scope.person = '';

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/new-person-modal.client.view.html',
                    controller: 'Common.NewPersonModalController',
                    size: 'lg'
                });

                modalInstance.result.then(function (data) {
                    $scope.personId = '';
                    $scope.personName = data.person.givenName + ' ' + data.person.familyName;
                    $scope.person = data.person;
                    $scope.personImages = data.images;
                });
            };
        }
    ]
);