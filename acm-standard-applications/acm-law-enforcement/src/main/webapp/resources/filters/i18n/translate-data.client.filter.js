'use strict';
/**
 * @ngdoc filter
 * @name translateData:category
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/filters/i18n/translate-data.client.filter.js filters/i18n/translate-data.client.filter.js}
 *
 * Simulate 'translate' filter translates labels, data-filter translates data from back end data source.
 * Usage:
 * {{ translate_expression | translateData[: category [:interpolateParams]] }}
 *
 * @param {string} category Prefix of resource key. Category of 'cases.comp.info.caseTypes.benefitsAppeal%' is 'cases.comp.info.caseTypes'
 *
 * @example
 <example module="ngView">
 <file name="index.html">
 <div ng-controller="TranslateCtrl">
 <scan>{{ 'Hello there!' | translateData: 'foo.bar' }}</scan>
 <!-- displays '你好!' -->
 </div>
 </file>
 <file name="script.js">
 angular.module('ngView', ['$translate'])
 .config(function ($translateProvider) {
        $translateProvider.translations('en', {
          'foo.bar.greeting_key%': 'Hello there!'
        });
        $translateProvider.translations('zh-cn', {
          'foo.bar.greeting_key%': '你好!'
        });
        $translateProvider.preferredLanguage('zh-cn');
      });
 angular.module('ngView').controller('TranslateCtrl', function ($translate) {
        console.log($translate.data('Hello there', 'foo.bar'));
        //console output is '你好!'
      });
 </file>
 </example>
 */
angular.module('filters').filter('translateData', ['$translate', function ($translate) {
    return function (input, category, interpolateParams) {
        return $translate.data(input, category, interpolateParams);
    };
}]);