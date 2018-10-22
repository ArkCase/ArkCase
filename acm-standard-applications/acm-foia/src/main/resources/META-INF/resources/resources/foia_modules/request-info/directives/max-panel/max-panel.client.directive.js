'use strict';

/**
 * @ngdoc directive
 * @name request-info.directive:maxPanel
 * @restrict A
 *
 * @description
 *
 * {@link https://github.com/Armedia/bactes360/blob/develop/bactes-user-interface/src/main/resources/META-INF/resources/resources/modules/request-info/directives/max-panel/max-panel.client.directive.js request-info/directives/max-panel/max-panel.client.directive.js}
 *
 * The maxPanel directive allows you to display content inside of maximized panel
 *
 * @param {boolean} maxPanel If true then display content in maximized panel.
 * @param {string} panelTitle Maximized panel title.
 * @param {Function} onClose Callback method executed when panel is closed.
 * @param {object} customData Custom data which will be assosiated with the custom header template. It will be available in customData variable.
 * @param {string} customHeaderTemplate Custom header which should be displayed.
 * @param {boolean} closable whether close button should be enabled or no.
 *
 * @example
 <example>
 <file name="index.html">
 <div max-panel="expandDocumentViewer" panel-title="Fullscreen Document Viewer" on-close="expandPreviewClosed()" closable="true">
 <iframe class="snowbound-iframe" style="display:none;" onload="this.style.display='block'" ng-src="{{viewerDocumentUrl}}"></iframe>
 </div>
 </file>
 </example>
 */
angular.module('directives').directive('maxPanel', [ '$templateRequest', '$compile', function($templateRequest, $compile) {
    var parentElement = null;
    var panel = null;
    var element = null;
    var scope = null;

    return {
        restrict: 'A',
        transclude: false,
        scope: {
            maxPanel: '=',
            panelTitle: '@',
            onClose: '&',
            customData: '=',
            customHeaderTemplate: '@',
            closable: '@'
        },

        link: function(elScope, el, attrs) {
            scope = elScope;
            parentElement = el;
            element = el.children();
            scope.$watch('maxPanel', function(newValue, oldValue) {
                if (newValue === true) {
                    expandPanel(true);
                } else if (newValue === false && oldValue === true) {
                    expandPanel(false);
                }
            });
            scope.collapse = function() {
                if (scope.onClose) {
                    scope.onClose()
                }
            };
        }
    };

    function expandPanel(expand) {
        if (expand) {
            $templateRequest('modules/request-info/directives/max-panel/max-panel.client.view.html').then(function(html) {
                // Convert the html to an actual DOM node
                var panelTemplate = angular.element(html);
                angular.element('body').append(panelTemplate);
                panel = $compile(panelTemplate)(scope);

                // Add element into panel's body
                panel.find('.panel-body').append(element);
                element.css({
                    'height': '100%'
                });
                if (scope.customHeaderTemplate) {
                    $templateRequest(scope.customHeaderTemplate).then(function(html) {
                        // Convert the html to an actual DOM node
                        var panelHeaderTemplate = angular.element(html);
                        var panelHeader = $compile(panelHeaderTemplate)(scope);
                        // Add element into panel's body
                        panel.find('.customHeader').html(panelHeaderTemplate);
                    });
                }
            });
        } else {
            parentElement.append(element);
            element.css({
                'height': ''
            });
            panel.remove();
        }
    }

} ]);