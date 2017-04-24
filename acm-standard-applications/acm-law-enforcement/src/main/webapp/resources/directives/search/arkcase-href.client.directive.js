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
    , function (Util, ObjectService, ObjectLookupService) {
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
                        var parentReference = Util.goodMapValue(objectData, "parent_ref_s", "-");
                        var objectType = Util.goodMapValue(objectData, "object_type_s");
                        var objectId = Util.goodMapValue(objectData, "object_id_s");
                        var objectUrlKey = 'url';
                        var pathVariables = {":id": objectId};

                        if (scope.isParent) {
                            objectType = parentReference.substring(parentReference.indexOf('-') + 1);
                            objectId = parentReference.substring(0, parentReference.indexOf('-'));
                        }
                        if (objectType == ObjectService.ObjectTypes.TASK) {
                            objectType = (Util.goodMapValue(objectData, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        }
                        if (objectType == ObjectService.ObjectTypes.FILE) {
                            var containerType = parentReference.substring(parentReference.indexOf('-') + 1);
                            if (containerType == ObjectService.ObjectTypes.DOC_REPO) {
                                var containerId = parentReference.substring(0, parentReference.indexOf('-'));
                                var name = Util.goodMapValue(objectData, "title_parseable");
                                objectUrlKey = "viewerUrl";
                                pathVariables[":containerId"] = containerId;
                                pathVariables[":containerType"] = containerType;
                                pathVariables[":name"] = name;
                                pathVariables[":selectedIds"] = objectId;
                            }
                        }
                        ObjectLookupService.getObjectTypes().then(
                            function (objectTypes) {
                                var objectUrl = '';
                                var foundObjectType = _.find(objectTypes, {key: objectType});
                                if (Util.goodMapValue(foundObjectType, objectUrlKey, false)) {
                                    objectUrl = foundObjectType[objectUrlKey];
                                    objectUrl = replacePathVariables(pathVariables, objectUrl);
                                }
                                element.attr('href', objectUrl);
                            }
                        );
                    } else {
                        element.attr('href', scope.url);
                    }
                }

                function replacePathVariables(pathVars, url) {
                    _.forEach(pathVars, function (value, key) {
                        url = url.replace(key, value);
                    });
                    return url;
                }

            }
        }
}]);