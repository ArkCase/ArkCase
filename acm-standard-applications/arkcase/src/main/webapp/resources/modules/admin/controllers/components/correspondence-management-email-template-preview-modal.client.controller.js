'use strict';

angular.module('admin').controller('Admin.CMEmailTemplatePreviewModalController',
    ['$rootScope', '$scope', '$modal', '$modalInstance', '$translate', 'params', '$q', '$state', 'Search.AutoSuggestService', 'Case.InfoService', 'Admin.CMTemplatesService',
        function ($rootScope, $scope, $modal, $modalInstance, $translate, params, $q, $state, AutoSuggestService, CaseInfoService, CorrespondenceService) {

        $scope.templateContent = params.templateContent.replace("${baseURL}", window.location.href.split('/home.html#!')[0]);
        $scope.templateName = params.templateName;
        $scope.objectType = params.objectType;
        $scope.inputQuery = '';

        $rootScope.$on('add-template-content', function () {
            document.getElementById("content").innerHTML=$scope.templateContent;
        });

        $scope.onClickClose = function() {
            $modalInstance.dismiss();
        };

        var isSelected = false;
        $scope.onSelect = function($item, $model, $label) {
            isSelected = true;
            retrieveTemplateContentByCaseNumber();
        };

        $scope.keyDown = function(event) {
            if (event.keyCode == 13) {
                $scope.isSelected = isSelected;
                retrieveTemplateContentByCaseNumber();
            }
        }

            function retrieveTemplateContentByCaseNumber() {
                CaseInfoService.getCaseInfoByNumber($scope.inputQuery).then(function (caseFile) {
                    if (caseFile.id) {
                        var param = {};
                        param.objectType = $scope.objectType;
                        param.objectId = caseFile.id;
                        param.templateName = $scope.templateName;

                    var getTemplateContentPromise = CorrespondenceService.retrieveConvertedTemplateContent(param);
                    getTemplateContentPromise.then(function (response) {
                        $scope.templateContent = response.data.templateContent.replace("${baseURL}", window.location.href.split('/home.html#!')[0]);
                        document.getElementById("content").innerHTML=$scope.templateContent;
                    });
                }
            });
            }

        $scope.queryTypeahead = function(typeaheadQuery) {
            var deferred = $q.defer();
            var typeAheadColumn = "name";
            typeaheadQuery = 'name:*' + typeaheadQuery + '*';

            if (typeaheadQuery.length >= 2) {
                AutoSuggestService.autoSuggest(typeaheadQuery, 'ADVANCED',  'CASE_FILE').then(function(res) {
                    var results = _.pluck(res, typeAheadColumn);
                    deferred.resolve(results);
                });
                return deferred.promise;
            }
        };

        } ]);