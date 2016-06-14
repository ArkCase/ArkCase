'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', '$modal', 'Object.ReferenceService'
    , function ($scope, $stateParams
        , Util, ConfigService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService, $modal, referenceService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "references"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
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
            var references = [];
            _.each($scope.objectInfo.childObjects, function (childObject) {
                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                    references.push(childObject);
                }
            });
            $scope.gridOptions.data = references;
            //gridHelper.hidePagingControlsIfAllDataShown(references.length);
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            gridHelper.showObject(targetType, targetId);
        };


        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
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
                templateUrl: 'modules/complaints/views/components/complaint-reference-modal.client.view.html',
                controller: 'Complaints.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                    	var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.currentObjectId + "-COMPLAINT";
                        if ($scope.gridOptions.data.length > 0) {
                            for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                var data = $scope.gridOptions.data[i];
                                filter += "&-id:" + data.targetId + "-" + data.targetType;
                            }
                        }
                        return filter.replace(/&/gi, '%26');
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
                    reference.parentType = 'COMPLAINT';
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
