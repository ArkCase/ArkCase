'use strict';

angular.module('cases').controller('Cases.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'correspondence');

        var promiseUsers = Util.AcmGrid.getUsers($scope);

        var promiseObjectTypes = Util.serviceCall({
            service: LookupService.getObjectTypes
            , onSuccess: function (data) {
                $scope.objectTypes = [];
                _.forEach(data, function (item) {
                    $scope.objectTypes.push(item);
                });
                return $scope.objectTypes;
            }
        });


        $scope.correspondenceForms = [{"value": "noop", "name": "(Select One)"}];
        $scope.correspondenceForm = {"value": "noop", "name": "(Select One)"};
        var promiseCorrespondenceForms = Util.serviceCall({
            service: LookupService.getCorrespondenceForms
            , onSuccess: function (data) {
                $scope.correspondenceForms = Util.omitNg(Util.goodArray(data));
                $scope.correspondenceForms.unshift({"value": "noop", "name": "(Select One)"});
                return $scope.correspondenceForms;
            }
        });


        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'correspondence') {
                Util.AcmGrid.setColumnDefs($scope, config);
                Util.AcmGrid.setBasicOptions($scope, config);
                Util.AcmGrid.setExternalPaging($scope, config, $scope.retrieveGridData);
                Util.AcmGrid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;
            }
        });

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            CasesService.queryCorrespondence(Util.AcmGrid.withPagingParams($scope, {
                parentType: Util.Constant.OBJTYPE_CASE_FILE,
                parentId: $scope.currentId
            }), function (data) {
                if (Validator.validateCorrespondences(data)) {
                    $q.all([promiseUsers]).then(function () {
                        var correspondences = data.children;
                        $scope.gridOptions.data = correspondences;
                        $scope.gridOptions.totalItems = Util.goodValue(data.totalChildren, 0);
                        Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                    });
                }
            })
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            promiseObjectTypes.then(function (data) {
                var found = _.find($scope.objectTypes, {type: Util.Constant.OBJTYPE_FILE});
                if (found) {
                    var url = Util.goodValue(found.url);
                    var id = Util.goodMapValue(rowEntity, "objectId");
                    url = url.replace(":id", id);
                    $window.location.href = url;
                }
            });
        };

        $scope.addNew = function () {
            var caseId = Util.goodValue($scope.caseInfo.id, 0);
            var folderId = Util.goodMapValue($scope.caseInfo, "container.folder.cmisFolderId", "");
            var template = $scope.correspondenceForm.value;
            CasesService.createCorrespondence({
                    parentType: Util.Constant.OBJTYPE_CASE_FILE,
                    parentId: $scope.currentId,
                    folderId: folderId,
                    template: template
                }
                , {}
                , function (successData) {
                    if (Validator.validateNewCorrespondence(successData)) {
                        var newCorrespondence = successData;
                        $q.all([promiseUsers]).then(function () {
                            var correspondence = {};
                            correspondence.objectId = Util.goodValue(newCorrespondence.fileId);
                            correspondence.name = Util.goodValue(newCorrespondence.fileName);
                            correspondence.creator = Util.goodValue(newCorrespondence.creator);
                            correspondence.created = Util.goodValue(newCorrespondence.created);
                            correspondence.objectType = "file";
                            correspondence.category = "Correspondence";
                            $scope.gridOptions.data.push(correspondence);
                            $scope.gridOptions.totalItems++;
                            Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);

                            //var lastPage = $scope.gridApi.pagination.getTotalPages();
                            //$scope.gridApi.pagination.seek(lastPage);
                        });
                    }
                }
                , function (errorData) {
                    var z = 1;
                }
            );
        };

    }
]);