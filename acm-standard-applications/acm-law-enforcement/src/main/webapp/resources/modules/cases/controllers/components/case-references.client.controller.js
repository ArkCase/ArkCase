'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , 'Object.ReferenceService', 'ObjectService', 'SearchService', 'Search.QueryBuilderService', 'ObjectAssociation.Service'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService
        , referenceService, ObjectService, SearchService, SearchQueryBuilder, ObjectAssociationService
    ) {

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

            // If the reference is a CASE_FILE, retrieve its title, as it may have changed since reference was created
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
            function updateIterableTitleReferences(index) {
                var i = index;

                // build solr query
                var size = 1;
                var start = 0;
                var searchQuery = '*';

                // build the solr filter based on the object's ID as well as its type
                var query = 'object_type_s:' + $scope.objectInfo.references[i].targetType + '+AND+object_id_s:'
                    + $scope.objectInfo.references[i].targetId;

                SearchService.querySimpleSearch({
                        query: query
                    },
                    // If the solr query fails, the title won't get updated, so it will just use whatever is in the DB
                    function (data) {
                        var caseTitle = data.response.docs[0].title_parseable
                        $scope.objectInfo.references[i].targetTitle = caseTitle;
                    });
            }

            // See above, this iterates over all found references and updates case titles where required
            for (var i = 0; i < $scope.objectInfo.references.length; i++) {
                updateIterableTitleReferences(i);
            }
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = Util.goodArray($scope.objectInfo.references);
        };

        $scope.onClickObjLink = function (event, rowEntity, targetNameColumnClicked) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            var parentId = Util.goodMapValue(rowEntity, "parentId");
            var parentType = Util.goodMapValue(rowEntity, "parentType");
            var fileName = Util.goodMapValue(rowEntity, "targetName");

            if(targetType == ObjectService.ObjectTypes.FILE && targetNameColumnClicked){
                gridHelper.openObject(targetId, parentId, parentType, fileName);
            }else{
                gridHelper.showObject(targetType, targetId);
            }
            
            if (ObjectService.ObjectTypes.CASE_FILE == targetType) {
                $scope.$emit('request-show-object', {objectId: targetId, objectType: targetType});
            }

            //$scope.$bus.publish('object-tree.select', {
            //	nodeId : targetId,
            //	nodeType : targetType,
            //	pageStart : 0,
            //	subKey : null
            //});
        };

        ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
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
                templateUrl: 'modules/cases/views/components/case-reference-modal.client.view.html',
                controller: 'Cases.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.CASE_FILE;
                        if ($scope.gridOptions.data.length > 0) {
                            for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                var data = $scope.gridOptions.data[i];
                                filter += "&-id:" + data.targetId + "-" + data.targetType;
                            }
                        }
                        filter += "&-parent_ref_s:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.CASE_FILE;
                        return filter.replace(/&/gi, '%26');
                    },
                    $config: function () {
                        return $scope.modalConfig;
                    }
                }
            });

            //chosenReference - target
            //parent - objectInfo
            modalInstance.result.then(function (chosenReference) {
                var association = {};
                var parent=$scope.objectInfo;
                var target=chosenReference;
                if (target) {
                    association.parentId = $stateParams.id;
                    association.parentType = ObjectService.ObjectTypes.CASE_FILE;

                    //association.targetId = target.id;
                    association.targetType = ObjectService.ObjectTypes.CASE_FILE;

                    association.referenceId = target.object_id_s;
                    association.referenceTitle = target.title_parseable;
                    association.referenceType = target.object_type_s;
                    association.referenceNumber = target.name;
                    association.referenceStatus = target.status_lcs;
                    //association.parentId = $stateParams.id;
                    //association.parentType = ObjectService.ObjectTypes.CASE_FILE;

                    association.inverseAssociation={};
                    if (association.inverseAssociation.inverseAssociation != association) {
                        association.inverseAssociation.inverseAssociation = association;
                    }
                    association.inverseAssociation.parentId = target.object_id_s;
                    association.inverseAssociation.parentType = target.object_type_s;

                    //association.inverseAssociation.targetId = parent.id;
                    association.inverseAssociation.targetType = ObjectService.ObjectTypes.CASE_FILE;

                    association.inverseAssociation.referenceId = $stateParams.id;
                    association.inverseAssociation.referenceTitle = parent.title_parseable;
                    association.inverseAssociation.referenceType = ObjectService.ObjectTypes.CASE_FILE;
                    association.inverseAssociation.referenceNumber = parent.name;
                    association.inverseAssociation.referenceStatus = parent.status_lcs;
                }
                ObjectAssociationService.saveObjectAssociation(association).then(function (payload,rowEntity) {
                    //success
                    if (!rowEntity) {
                        //append new entity as last item in the grid
                        rowEntity = {
                            target_object: {}
                        };
                        $scope.gridOptions.data.push(rowEntity);
                    }

                    //update row immediately
                    rowEntity.target_object.referenceType = ObjectService.ObjectTypes.CASE_FILE;
                    rowEntity.target_object.referenceStatus = payload.status_lcs;
                    rowEntity.target_object.parentId = payload.object_id_s;
                });
                return;
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

    }
]);