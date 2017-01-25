/**
 * Created by sasko.tanaskoski
 */

'use strict';

/**
 * @ngdoc directive
 * @name global.directive:arkcaseHref
 * @restrict A
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/search/arkcase-href.client.directive.js directives/search/arkcase-href.client.directive.js}
 *
 * The arkcaseHref directive builds href for arkcase object.
 *
 * @param {Object} object-data Arkcase object data
 * @param {Boolean} is-parent If true builds href for parent arkcase object. Default value is false
 * @param {String} url If url exists in object-data, it can be used as such and will not be built. For example notification object already has url as attribute.
 *
 * @example
 <example>
 <file name="index.html">
 <a arkcase-href object-data='row.entity' is-parent='false' url='row.entity.notification_link_s'>{{row.entity.name}}</a>
 </file>
 </example>
 */
angular.module('directives').directive('arkcaseHref', ['UtilService', 'ObjectService', 'Object.LookupService'
    , function(Util, ObjectService, ObjectLookupService) {
        var defaults = {
            isParent: false
        };
        return {
            restrict: 'A',
            scope: {
                objectData: '=',
                isParent: '=',
                url: '='
            },
            link: function(scope, element, attrs) {
                scope.$watch('objectData', function(newValue, oldValue) {
                    buildUrl(newValue);
                });
                
                function buildUrl(objectData) {
                    if (scope.url == undefined) {
                        var objectType = Util.goodMapValue(objectData, "object_type_s");
                        var objectId = Util.goodMapValue(objectData, "object_id_s");
                        if (scope.isParent) {
                            var parentReference = Util.goodMapValue(objectData, "parent_ref_s", "-");
                            objectType  = parentReference.substring(parentReference.indexOf('-') + 1);
                            objectId = parentReference.substring(0, parentReference.indexOf('-'));
                        }
                        if (objectType == ObjectService.ObjectTypes.TASK) {
                            objectType = (Util.goodMapValue(objectData, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        }
                        ObjectLookupService.getObjectTypes().then(
                            function (objectTypes) {
                                var objectUrl = '';
                                var foundObjectType = _.find(objectTypes, {key: objectType});
                                if (Util.goodMapValue(foundObjectType, "url", false)) {
                                    objectUrl = foundObjectType.url;
                                    objectUrl = objectUrl.replace(":id", objectId);
                                }
                                element.attr('href', objectUrl);
                            }
                        );
                    } else {
                        element.attr('href', scope.url);
                    }
                };
                
            }
        }
}]);