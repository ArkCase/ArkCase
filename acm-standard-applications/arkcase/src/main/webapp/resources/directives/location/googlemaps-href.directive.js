/**
 * Created by sasko.tanaskoski
 */

'use strict';

/**
 * @ngdoc directive
 * @name global.directive:googlemapsHref
 * @restrict A
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/directives/location/googlemaps-href.directive.js directives/location/googlemaps-href.directive.js}
 *
 * The googlemapsHref directive builds google maps href for selected address.
 *
 * @param {Boolean} is-primary-address True if it is primary address. Default value is true.
 * @param {Object} location-data Location data object, contains street addresses, city, country, state, zip etc.
 *
 * @example
 <example>
 <file name="index.html">
 <a googlemaps-href is-primary-address='true' location-data='row.entity'>{{row.entity.name}}</a>
 </file>
 </example>
 */
angular.module('directives').directive('googlemapsHref', function() {
    var defaults = {
        isPrimaryAddress: true
    };
    return {
        restrict: 'A',
        scope: {
            isPrimaryAddress: '=',
            locationData: '='
        },
        link: function(scope, element, attrs) {
            scope.$watch('locationData', function(newValue, oldValue) {
                buildUrl(newValue);
            });

            function buildUrl(locationData) {

                var googleMapsUrl = "http://maps.google.com/?q=";
                var locationArray = [ locationData.city, locationData.state, locationData.zip, locationData.country ];

                if (scope.isPrimaryAddress) {
                    locationArray.unshift(locationData.streetAddress);
                } else {
                    locationArray.unshift(locationData.streetAddress2);
                }

                googleMapsUrl += locationArray.filter(Boolean).join();

                element.attr('href', googleMapsUrl);
                element.attr('target', "_blank");
            }

        }
    }
});