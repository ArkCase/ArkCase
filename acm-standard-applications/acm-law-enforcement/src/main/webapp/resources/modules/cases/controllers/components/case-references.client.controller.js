'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', '$modal', 'Object.ReferenceService'
    , function ($scope, $stateParams
        , Util, ConfigService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService, $modal, referenceService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "references"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;

            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = Util.goodArray($scope.objectInfo.references);
            //gridHelper.hidePagingControlsIfAllDataShown($scope.objectInfo.references.length);
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };
        
        ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
        	$scope.modalConfig = _.find(moduleConfig.components, {id: "referenceSearchGrid"});
            return moduleConfig;
        });

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        // open addreference modal
        $scope.addReference = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/case-reference-modal.client.view.html',
                controller: 'Cases.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.modalConfig.searchFilter;
                    },
                    $config: function () {
                        return $scope.modalConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenReference) {
                if (chosenReference) {
                    var reference = {};
                    reference.referenceId = chosenReference.object_id_s;
                    reference.referenceTitle = chosenReference.title_parseable;
                    reference.referenceType = chosenReference.object_type_s;
                    reference.referenceNumber = chosenReference.name;
                    reference.referenceStatus = chosenReference.status_lcs;
                    reference.parentId = $stateParams.id;
                    reference.parentType = 'CASE_FILE';
                    referenceService.addReference(reference).then(
                        function (objectSaved) {
                            $scope.refresh();
                            return objectSaved;
                        },
                        function (error) {
                            return error;
                        }
                    );
                    return;
                }
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

    }
]);