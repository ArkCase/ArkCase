'use strict';

angular.module('cases').controller('Cases.TasksController', ['$scope', '$stateParams', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, Util, Validator, LookupService, CasesService) {
		$scope.$emit('req-component-config', 'tasks');

        $scope.currentId = $stateParams.id;
        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];

        $scope.config = null;
        $scope.gridOptions = {};
        $scope.$on('component-config', applyConfig);
		function applyConfig(e, componentId, config) {
			if (componentId == 'tasks') {
				$scope.config = config;
				$scope.gridOptions = {
					enableColumnResizing: true,
					enableRowSelection: true,
					enableRowHeaderSelection: false,
					enableFiltering: config.enableFiltering,
					multiSelect: false,
					noUnselect : false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    useExternalPagination: true,
                    useExternalSorting: true,

                    //comment out filtering until service side supports it
                    ////enableFiltering: config.enableFiltering,
                    //enableFiltering: true,
                    //useExternalFiltering: true,

					columnDefs: config.columnDefs,
					onRegisterApi: function(gridApi) {
						$scope.gridApi = gridApi;
                        $scope.gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                            if (0 >= sortColumns.length) {
                                $scope.sort.by = null;
                                $scope.sort.dir = null;
                            } else {
                                $scope.sort.by = sortColumns[0].field;
                                $scope.sort.dir = sortColumns[0].sort.direction;
                            }
                            $scope.updatePageData();
                        });
                        $scope.gridApi.core.on.filterChanged($scope, function () {
                            var grid = this.grid;
                            $scope.filters = [];
                            for (var i = 0; i < grid.columns.length; i++) {
                                if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                    var filter = {};
                                    filter.by = grid.columns[i].field;
                                    filter.with = grid.columns[i].filters[0].term;
                                    $scope.filters.push(filter);
                                }
                            }
                            $scope.updatePageData();
                        });
                        $scope.gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                            $scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                            $scope.pageSize = pageSize;
                            $scope.updatePageData();
                        });
					}
				};

                $scope.pageSize = config.paginationPageSize;
                $scope.updatePageData();

                //var id = $stateParams.id;
                //CasesService.queryTasks({
                //	id: id,
                //	startWith: 0,
                //	count: 10
                //}, function(data) {
                //	//numFound  start
                //	$scope.gridOptions.data = data.response.docs;
                //})
			}
		}


        $scope.updatePageData = function () {
            var sort = "";
            if ($scope.sort) {
                if (!_.isEmpty($scope.sort.by) && !_.isEmpty($scope.sort.dir)) {
                    sort = $scope.sort.by + "%20" + $scope.sort.dir;
                }
            }
            //implement filtering here when service side supports it
            //var filter = "";
            ////$scope.filters = [{by: "eventDate", with: "term"}];

            CasesService.queryTasks({
                id: $scope.currentId,
                startWith: $scope.start,
                count: $scope.pageSize,
                sort: sort
            }, function (data) {

                data = {
                    "responseHeader": {
                        "status": 0,
                        "QTime": 1,
                        "params": {
                            "q": "parent_object_type_s:CASE_FILE AND parent_object_id_i:113 AND object_type_s:TASK AND -status_s:DELETED",
                            "indent": "true",
                            "topLevel": "if(exists(parent_ref_s), 0, 1)",
                            "dac": "{!join from=id to=parent_ref_s}(not(exists(protected_object_b)) OR protected_object_b:false OR public_doc_b:true  OR allow_acl_ss:ann-acm OR allow_acl_ss:ROLE_INVESTIGATOR OR allow_acl_ss:ROLE_ADMINISTRATOR OR allow_acl_ss:ACM_INVESTIGATOR_DEV OR allow_acl_ss:ROLE_ANALYST OR allow_acl_ss:ROLE_INVESTIGATOR_SUPERVISOR OR allow_acl_ss:ACM_ADMINISTRATOR_DEV OR allow_acl_ss:ROLE_CALLCENTER ) AND -deny_acl_ss:ann-acm AND -deny_acl_ss:ROLE_INVESTIGATOR AND -deny_acl_ss:ROLE_ADMINISTRATOR AND -deny_acl_ss:ACM_INVESTIGATOR_DEV AND -deny_acl_ss:ROLE_ANALYST AND -deny_acl_ss:ROLE_INVESTIGATOR_SUPERVISOR AND -deny_acl_ss:ACM_ADMINISTRATOR_DEV AND -deny_acl_ss:ROLE_CALLCENTER",
                            "start": "0",
                            "fq": ["{!frange l=1}sum(if(exists(protected_object_b), 0, 1), if(protected_object_b, 0, 1), if(public_doc_b, 1, 0), termfreq(allow_acl_ss, ann-acm), termfreq(allow_acl_ss, ROLE_INVESTIGATOR), termfreq(allow_acl_ss, ROLE_ADMINISTRATOR), termfreq(allow_acl_ss, ACM_INVESTIGATOR_DEV), termfreq(allow_acl_ss, ROLE_ANALYST), termfreq(allow_acl_ss, ROLE_INVESTIGATOR_SUPERVISOR), termfreq(allow_acl_ss, ACM_ADMINISTRATOR_DEV), termfreq(allow_acl_ss, ROLE_CALLCENTER))",
                                "-deny_acl_ss:ann-acm AND -deny_acl_ss:ROLE_INVESTIGATOR AND -deny_acl_ss:ROLE_ADMINISTRATOR AND -deny_acl_ss:ACM_INVESTIGATOR_DEV AND -deny_acl_ss:ROLE_ANALYST AND -deny_acl_ss:ROLE_INVESTIGATOR_SUPERVISOR AND -deny_acl_ss:ACM_ADMINISTRATOR_DEV AND -deny_acl_ss:ROLE_CALLCENTER",
                                "{!frange l=1}sum($topLevel, $dac)"],
                            "sort": "",
                            "rows": "10",
                            "wt": "json"
                        }
                    },
                    "response": {
                        "numFound": 1, "start": 0, "docs": [
                            {
                                "id": "2943-TASK",
                                "status_s": "CLOSED",
                                "last_modified_tdt": "2015-09-15T19:47:05Z",
                                "due_tdt": "2015-09-15T00:00:00Z",
                                "name": "Please step in on this one, Sally",
                                "object_id_s": "2943",
                                "object_type_s": "TASK",
                                "assignee_s": "sally-acm",
                                "priority_s": "Medium",
                                "parent_object_type_s": "CASE_FILE",
                                "adhocTask_b": true,
                                "public_doc_b": true,
                                "protected_object_b": true,
                                "title_parseable": "Please step in on this one, Sally",
                                "description_no_html_tags_parseable": "\n                        ",
                                "parent_object_id_i": 113,
                                "hidden_b": false,
                                "parent_ref_s": "113-CASE_FILE",
                                "_version_": 1512409845180923904
                            }]
                    }
                };


                $scope.gridOptions.data = Util.goodMapValue([data, "response", "docs"], []);
                $scope.gridOptions.totalItems = Util.goodMapValue([data, "response", "numFound"], 0);
            })
        };

        $scope.cellAction = cellAction;
        function cellAction(action, entity) {
            alert('make task completed');
        }


	}
]);