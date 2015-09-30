'use strict';

angular.module('cases').controller('Cases.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'correspondence');

        $scope.currentId = $stateParams.id;
        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];


        var promiseUsers = Util.servicePromise({
            service: LookupService.getUsers
            , callback: function (data) {
                $scope.userFullNames = [];
                var arr = Util.goodArray(data);
                for (var i = 0; i < arr.length; i++) {
                    var obj = Util.goodJsonObj(arr[i]);
                    if (obj) {
                        var user = {};
                        user.id = Util.goodValue(obj.object_id_s);
                        user.name = Util.goodValue(obj.name);
                        $scope.userFullNames.push(user);
                    }
                }
                return $scope.userFullNames;
            }
        });
        var promiseObjectTypes = Util.servicePromise({
            service: LookupService.getObjectTypes
            , callback: function (data) {
                $scope.objectTypes = [];
                _.forEach(data, function (item) {
                    $scope.objectTypes.push(item);
                });
                return $scope.objectTypes;
            }
        });


        $scope.correspondenceForms = [{"value": "noop", "name": "(Select One)"}];
        $scope.correspondenceForm = {"value": "noop", "name": "(Select One)"};
        var promiseCorrespondenceForms = Util.servicePromise({
            service: LookupService.getCorrespondenceForms
            , callback: function (data) {
                $scope.correspondenceForms = Util.omitNg(Util.goodArray(data));
                $scope.correspondenceForms.unshift({"value": "noop", "name": "(Select One)"});
                return $scope.correspondenceForms;
            }
        });


        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'correspondence') {
                $scope.config = config;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: false,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    useExternalPagination: true,
                    useExternalSorting: true,

                    //comment out filtering until service side supports it
                    ////enableFiltering: config.enableFiltering,
                    //enableFiltering: true,
                    //useExternalFiltering: true,

                    columnDefs: config.columnDefs,
                    onRegisterApi: function (gridApi) {
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


                $q.all([promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("userFullNames" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";
                        }
                    }
                });

                $scope.pageSize = config.paginationPageSize;
                $scope.updatePageData();
            }
        }

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;
            }
        });

        $scope.updatePageData = function () {
            var sort = "";
            if ($scope.sort) {
                if (!_.isEmpty($scope.sort.by) && !_.isEmpty($scope.sort.dir)) {
                    sort = $scope.sort.by + " " + $scope.sort.dir;
                }
            }
            //implement filtering here when service side supports it
            //var filter = "";
            ////$scope.filters = [{by: "eventDate", with: "term"}];

            CasesService.queryCorrespondence({
                parentType: "CASE_FILE",
                parentId: $scope.currentId,
                startWith: $scope.start,
                count: $scope.pageSize,
                sort: sort
            }, function (data) {

                //for testing
                //data = {
                //    category: "Correspondence"
                //    , containerObjectId: 103
                //    , containerObjectType: "CASE_FILE"
                //    , folderId: 119
                //    , maxRows: 10
                //    , sortBy: "name"
                //    , sortDirection: "ASC"
                //    , startRow: 0
                //    , totalChildren: 3
                //    , children: [
                //        {
                //            objectId: 137,
                //            objectType: "file",
                //            name: "Notice of Investigation.docx",
                //            version: "1.0",
                //            created: "2015-08-25T16:27:30.000-0400",
                //            creator: "ian-acm",
                //            category: "Correspondence",
                //            versionList: []
                //        },
                //        {
                //            objectId: 138,
                //            objectType: "file",
                //            name: "noi138.docx",
                //            version: "1.0",
                //            created: "2015-08-25T16:27:30.000-0400",
                //            creator: "ian-acm",
                //            category: "Correspondence",
                //            versionList: []
                //        }
                //    ]
                //};

                if (Validator.validateCorrespondences(data)) {
                    $q.all([promiseUsers]).then(function () {
                        var correspondences = data.children;
                        $scope.gridOptions.data = correspondences;
                        $scope.gridOptions.totalItems = Util.goodValue(data.totalChildren, 0);
                    });
                }
            })
        };

        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            $q.all([promiseObjectTypes]).then(function (data) {
                var find = _.where($scope.objectTypes, {type: "FILE"});
                if (0 < find.length) {
                    var url = Util.goodValue(find[0].url);
                    var id = Util.goodMapValue([rowEntity, "objectId"]);
                    url = url.replace(":id", id);
                    $window.location.href = url;
                }
            });
        };

        $scope.addNew = function () {
            var caseId = Util.goodValue($scope.caseInfo.id, 0);
            var folderId = Util.goodMapValue([$scope.caseInfo, "container", "folder", "cmisFolderId"], "");
            var template = $scope.correspondenceForm.value;
            CasesService.createCorrespondence({
                    parentType: "CASE_FILE",
                    parentId: $scope.currentId,
                    folderId: folderId,
                    template: template
                }
                , {}
                , function (successData) {
                    //for testing
                    //successData.created = "2015-09-25T16:27:30.000-0400";
                    //successData.creator = "ann-acm";
                    //successData.fileId = "fid";

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