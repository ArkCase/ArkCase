'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , 'Object.ReferenceService', 'ObjectService', 'SearchService', 'Search.QueryBuilderService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService
        , referenceService, ObjectService, SearchService, SearchQueryBuilder
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

            modalInstance.result.then(function (chosenReference) {
                if (chosenReference) {
                    var reference = {};
                    reference.referenceId = chosenReference.object_id_s;
                    reference.referenceTitle = chosenReference.title_parseable;
                    reference.referenceType = chosenReference.object_type_s;
                    reference.referenceNumber = chosenReference.name;
                    reference.referenceStatus = chosenReference.status_lcs;
                    reference.parentId = $stateParams.id;
                    reference.parentType = ObjectService.ObjectTypes.CASE_FILE;
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