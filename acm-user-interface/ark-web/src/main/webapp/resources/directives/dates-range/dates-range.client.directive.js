'use strict';

/**
 * @ngdoc directive
 * @name global.directive:datesRange
 * @restrict E
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/dates-range/dates-range.client.directive.js directives/dates-range/dates-range.client.directive.js}
 *

 */
angular.module('directives').directive('datesRange', ['$q',
    function($q) {
        return {
            restrict: 'E',
            scope: {
                treeData: '=',
                onSelect: '&'
            },

            link: function(scope, element, attrs){
                // Create popup with 2 datepicker widgets

                //var treeOptions = {
                //    source: [],
                //    click: function (event, data) {
                //        scope.onSelect()(data.node.data);
                //    }
                //};
                //$(element).fancytree(treeOptions);
                //
                //
                //if (scope.treeData) {
                //    scope.$watchCollection('treeData', function(newValue, oldValue) {
                //        $q.when(newValue).then(function (treeData) {
                //            var tree = $(element).fancytree('getTree');
                //            tree.reload(treeData);
                //        }, true);
                //    });
                //}
            }
        };
    }
]);
