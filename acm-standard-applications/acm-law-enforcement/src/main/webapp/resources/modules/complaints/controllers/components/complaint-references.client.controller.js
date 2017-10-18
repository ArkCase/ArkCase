'use strict';

angular.module('complaints').controller('Complaints.ReferencesController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , 'Object.ReferenceService', 'ObjectService', 'SearchService', 'Search.QueryBuilderService', 'ObjectAssociation.Service'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService
        , referenceService, ObjectService, SearchService, SearchQueryBuilder, ObjectAssociationService) {

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
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            refreshGridData(objectInfo.id);
            var references = [];
            _.each($scope.objectInfo.childObjects, function (childObject) {
                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                    references.push(childObject);
                }
            });
            $scope.gridOptions.data = references;
        };

        $scope.onClickObjLink = function (event, rowEntity, targetNameColumnClicked) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            var parentId = Util.goodMapValue(rowEntity, "parentId");
            var parentType = Util.goodMapValue(rowEntity, "parentType");
            var fileName = Util.goodMapValue(rowEntity, "targetName");

            if (targetType == ObjectService.ObjectTypes.FILE && targetNameColumnClicked) {
                gridHelper.openObject(targetId, parentId, parentType, fileName);
            } else {
                gridHelper.showObject(targetType, targetId);
            }

            if (ObjectService.ObjectTypes.COMPLAINT == targetType) {
                $scope.$emit('request-show-object', {objectId: targetId, objectType: targetType});
            }
        };


        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.modalConfig = _.find(moduleConfig.components, {id: "referenceSearchGrid"});
            return moduleConfig;
        });

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        // open add reference modal
        $scope.addReference = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/complaints/views/components/complaint-reference-modal.client.view.html',
                controller: 'Complaints.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.objectInfo.complaintId + "-" + ObjectService.ObjectTypes.COMPLAINT;
                        if ($scope.gridOptions.data.length > 0) {
                            for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                var data = $scope.gridOptions.data[i];
                                filter += "&-id:" + data.targetId + "-" + data.targetType;
                            }
                        }
                        filter += "&-parent_ref_s:" + $scope.objectInfo.complaintId + "-" + ObjectService.ObjectTypes.COMPLAINT;
                        return filter.replace(/&/gi, '%26');
                    },
                    $config: function () {
                        return $scope.modalConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenReference) {
                var association = {};
                var parent = $scope.objectInfo;
                var target = chosenReference;
                if (target) {
                    association.parentId = parent.id;
                    association.parentType = parent.objectType;
                    association.parentTitle = parent.title;
                    association.parentName = parent.complaintNumber;

                    association.targetId = target.object_id_s;
                    association.targetType = target.object_type_s;
                    association.targetTitle = target.title_parseable;
                    association.targetName = target.name;
                    var hasPrimaryDocket = false;
                    association.associationType = 'REFERENCE';

                    association.inverseAssociation = {};
                    if (association.inverseAssociation.inverseAssociation != association) {
                        association.inverseAssociation.inverseAssociation = association;
                    }
                    association.inverseAssociation.parentId = target.object_id_s;
                    association.inverseAssociation.parentType = target.object_type_s;
                    association.inverseAssociation.parentTitle = target.title_parseable;
                    association.inverseAssociation.parentName = target.name;

                    association.inverseAssociation.targetId = parent.id;
                    association.inverseAssociation.targetType = ObjectService.ObjectTypes.COMPLAINT;
                    association.inverseAssociation.targetTitle = parent.title;
                    association.inverseAssociation.targetName = parent.complaintNumber;

                    association.inverseAssociation.associationType = 'REFERENCE';
                }
                ObjectAssociationService.saveObjectAssociation(association).then(function (payload) {
                    //success
                    //append new entity as last item in the grid
                    var rowEntity = {
                        object_id_s: payload.id,
                        target_object: {
                            name: target.name,
                            title_parseable: target.title_parseable,
                            parent_ref_s: target.parent_ref_s,
                            modified_date_tdt: payload.modified,
                            object_type_s: target.object_type_s,
                            status_lcs: target.status_lcs
                        },
                        target_type_s: payload.targetType,
                        target_id_s: payload.targetId
                    };

                    if (rowEntity.target_object.parent_ref_s) {
                        updateIterableTitleReferences(rowEntity.target_object);
                    }

                    $scope.gridOptions.data.push(rowEntity);

                });
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };


        /**
         * Initially attemped to use the below code
         * However, the index i is out of scope in the onFulfilled function
         *
         * if($scope.objectInfo.references[i].targetType == "CASE_FILE") {
                    CaseInfoService.getCaseInfo($scope.objectInfo.references[i].targetId).then(
						function(caseInfo) {
							$scope.objectInfo.references[i].targetTitle = caseInfo.title;
						}
					);
                }
         *
         * As a result, we need to use Closures to be able to pass the index variable into the onFulfilled case
         *
         * Since i and the fulfillment function of getCaseInfo are both defined in the distinct scope of
         * getIterablePromises, the fulfillment function still has access to i
         *
         * The function has been updated to use solr SearchService instead of CaseInfoService
         *
         * @param index
         */
        function updateIterableTitleReferences(doc) {

            // build the solr filter based on the object's ID as well as its type
            var query = 'id:' + doc.parent_ref_s;

            SearchService.querySimpleSearch({
                    query: query
                },
                // If the solr query fails, the title won't get updated, so it will just use whatever is in the DB
                function (data) {
                    if (data.response.docs && data.response.docs.length > 0) {
                        doc.parent_name = data.response.docs[0].title_parseable
                    }
                });
        }

        function refreshGridData(objectId) {
            // If the reference is a CASE_FILE, retrieve its title, as it may have changed since reference was created


            ObjectAssociationService.getObjectAssociations(objectId, ObjectService.ObjectTypes.COMPLAINT, null).then(function (response) {
                // See above, this iterates over all found references and updates case titles where required
                angular.forEach(response.response.docs, function (doc) {
                    updateIterableTitleReferences(doc.target_object);
                });
                $scope.gridOptions.data = response.response.docs;
            });
        }

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
            ObjectAssociationService.deleteAssociationInfo(id).then(function (data) {
                //success
                //remove it from the grid
                _.remove($scope.gridOptions.data, function (row) {
                    return row === rowEntity;
                });
            });
        };
    }
]);
