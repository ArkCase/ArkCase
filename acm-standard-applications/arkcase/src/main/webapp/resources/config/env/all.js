'use strict';
var path = require('path');

module.exports = {

    appPath : '/arkcase/',

    config : {
        modulesConfigFolder : 'modules_config/config/',
        modulesConfigFile : 'modules_config/config/modules.json',
        modulesPermissionsFolder : 'modules_config/permission/',
        modulesSchemasFolder : 'modules_config/schemas/'
    },

    modules : {
        defaultModulesFolder : 'modules/'
    },

    homePage : {
        title : "ArkCase Application",
        template : 'templates/home.tpl.html',
        target : 'home.html'
    },

    assets : {
        lib : {
            css : [ 'node_modules/@bower_components/jquery-ui/themes/ui-lightness/jquery-ui.css', 'node_modules/@bower_components/bootstrap/dist/css/bootstrap.css',
                    'node_modules/@bower_components/bootstrap/dist/css/bootstrap-theme.css', 'node_modules/@bower_components/angular-ui-grid/ui-grid.css',
                    'node_modules/@bower_components/components-font-awesome/css/font-awesome.css', 'node_modules/@bower_components/fancytree/dist/skin-win8/ui.fancytree.css',
                    'node_modules/@bower_components/angular-xeditable/dist/css/xeditable.css',
                    'node_modules/@bower_components/angular-dashboard-framework/dist/angular-dashboard-framework.min.css', 'node_modules/@bower_components/summernote/dist/summernote.css',
                    'node_modules/@bower_components/angular-notify/dist/angular-notify.css', 'node_modules/@bower_components/handsontable/dist/handsontable.full.css',
                    'node_modules/@bower_components/ng-tags-input/ng-tags-input.css', 'node_modules/@bower_components/fullcalendar/dist/fullcalendar.css',
                    'node_modules/@bower_components/angular-chart.js/dist/angular-chart.css',
                    'node_modules/@bower_components/pdf.js-viewer/viewer.css',
                    'node_modules/angular-bootstrap-nav-tree/dist/abn_tree.css',
                    'node_modules/angular-moment-picker/dist/angular-moment-picker.min.css'],
            customJs : [ 'https://project.armedia.com/jira/s/bcc668048eb1a31f8f63c11740708fe0-T/-wpypks/814001/6411e0087192541a09d88223fb51a6a0/4.0.3/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector-embededjs/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector-embededjs.js?locale=en-US&collectorId=16f3c361' ],
            js : [ 'node_modules/@bower_components/jquery/dist/jquery.min.js', 'node_modules/@bower_components/jquery-ui/jquery-ui.min.js', 'node_modules/@bower_components/bootstrap/dist/js/bootstrap.min.js',
                    'node_modules/@bower_components/lodash/lodash.min.js', 'node_modules/@bower_components/moment/min/moment.min.js', 'node_modules/@bower_components/multi-download/browser.js',
                    'node_modules/@bower_components/angular/angular.min.js', 'node_modules/@bower_components/angular-moment/angular-moment.min.js',
                    'node_modules/@bower_components/angular-resource/angular-resource.min.js', 'node_modules/@bower_components/angular-cookies/angular-cookies.min.js',
                    'node_modules/@bower_components/angular-animate/angular-animate.min.js', 'node_modules/@bower_components/angular-touch/angular-touch.min.js',
                    'node_modules/@bower_components/angular-sanitize/angular-sanitize.min.js', 'node_modules/@bower_components/angular-ui-router/release/angular-ui-router.min.js',
                    'node_modules/@bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js', 'node_modules/@bower_components/angular-ui-grid/ui-grid.min.js',
                    'node_modules/@bower_components/angular-notify/dist/angular-notify.min.js', 'node_modules/@bower_components/angular-summernote/dist/angular-summernote.min.js',
                    'node_modules/@bower_components/angular-translate/angular-translate.min.js',
                    'node_modules/@bower_components/angular-translate-loader-partial/angular-translate-loader-partial.min.js',
                    'node_modules/@bower_components/angular-dynamic-locale/dist/tmhDynamicLocale.min.js', 'node_modules/@bower_components/angular-xeditable/dist/js/xeditable.min.js',
                    'node_modules/@bower_components/angular-dashboard-framework/dist/angular-dashboard-framework.min.js', 'node_modules/@bower_components/Sortable/Sortable.min.js',
                    'node_modules/@bower_components/fancytree/dist/jquery.fancytree.min.js', 'node_modules/@bower_components/fancytree/dist/src/jquery.fancytree.table.js',
                    'node_modules/@bower_components/fancytree/dist/src/jquery.fancytree.gridnav.js', 'node_modules/@bower_components/fancytree/dist/src/jquery.fancytree.edit.js',
                    'node_modules/@bower_components/fancytree/dist/src/jquery.fancytree.dnd.js', 'node_modules/@bower_components/fancytree/dist/src/jquery.fancytree.filter.js',
                    'node_modules/@bower_components/ng-file-upload/ng-file-upload.min.js', 'node_modules/@bower_components/ng-file-upload-shim/ng-file-upload-shim.min.js',
                    'node_modules/@bower_components/ng-tags-input/ng-tags-input.min.js', 'node_modules/@bower_components/summernote/dist/summernote.min.js',
                    'node_modules/@bower_components/ui-contextmenu/jquery.ui-contextmenu.min.js', 'node_modules/bootbox/bootbox.js', 'node_modules/@bower_components/ngBootbox/ngBootbox.js', // Don't use minified version here. It is broken.
                    'node_modules/@bower_components/handsontable/dist/handsontable.full.min.js', 'node_modules/@bower_components/ngHandsontable/dist/ngHandsontable.min.js',
                    'node_modules/@bower_components/ace-builds/src-min-noconflict/ace.js', 'node_modules/@bower_components/angular-ui-ace/ui-ace.min.js',
                    'node_modules/@bower_components/fullcalendar/dist/fullcalendar.min.js', 'node_modules/@bower_components/fullcalendar/dist/gcal.js',
                    'node_modules/@bower_components/angular-ui-calendar/src/calendar.js', 'node_modules/@bower_components/Chart.js/Chart.min.js',
                    'node_modules/@bower_components/angular-chart.js/dist/angular-chart.min.js', 'node_modules/@bower_components/sockjs-client/dist/sockjs.min.js',
                    'node_modules/@bower_components/stomp-websocket/lib/stomp.min.js', 'node_modules/@bower_components/angular-hotkeys/build/hotkeys.js', 'node_modules/@bower_components/crypto-js/crypto-js.js',
                    'node_modules/@bower_components/angular-google-analytics/dist/angular-google-analytics.js', 'node_modules/@bower_components/rrule/lib/rrule.js', 'node_modules/@bower_components/jsog/lib/JSOG.js',
                    'node_modules/@bower_components/videogular/videogular.js', 'node_modules/@bower_components/videogular-controls/vg-controls.js',
                    'node_modules/@bower_components/videogular-overlay-play/vg-overlay-play.js', 'node_modules/@bower_components/videogular-poster/vg-poster.js',
                    'node_modules/@bower_components/videogular-buffering/vg-buffering.js', 'node_modules/@bower_components/angular-utf8-base64/angular-utf8-base64.js',
                    'node_modules/@bower_components/ui-grid-draggable-rows/js/draggable-rows.js', 'node_modules/@bower_components/angular-cache/dist/angular-cache.min.js',
                    'node_modules/@bower_components/angular-file-saver/dist/angular-file-saver.bundle.js',
                    'node_modules/@bower_components/pdf.js-viewer/pdf.js',
                    'node_modules/@bower_components/angular-pdfjs-viewer/dist/angular-pdfjs-viewer.js',
                    'node_modules/angular-aria/angular-aria.js',
                    'node_modules/@bower_components/combodate/src/combodate.js',
                    'node_modules/@bower_components/ment.io/dist/mentio.js',
                    'node_modules/angular-bootstrap-contextmenu/contextMenu.js',
                    'node_modules/angular-bootstrap-nav-tree/dist/abn_tree_directive.js',
                    'node_modules/angular-moment-picker/dist/angular-moment-picker.min.js']
        },
        css : [ 'assets/css/application.css', 'modules/**/css/*.css', 'assets/css/arkcase-extension.css'],
        scss : [ 'modules/**/scss/*.scss'],
        js : [ 'config.js', 'application.js', 'scripts/*/**/*.js', 'services/*.js', // Includes services/services.client.module.js file only
        'directives/*.js', // Includes directives/directives.client.js file only
        'filters/*.js', 'filters/*/*.js' ],
        distJs : [ 'assets/dist/vendors.min.js', 'assets/dist/application.min.js' ],
        distCss : [ 'assets/dist/application.min.css' ], // Can't use this, because CSS has fonts and images dependencies
        jsModules : [ 'modules/*/*.js', 'modules/*/**/*.js' ],
        jsDirectives : [ 'directives/*/*.js', 'directives/*/**/*.js' ],
        jsServices : [ 'services/*/*.js', 'services/*/**/*.js' ],
        jsCustomModules : [ '_modules/*/*.js', '_modules/*/**/*.js' ],
        jsCustomDirectives : [ '_directives/*/*.js', '_directives/*/**/*.js' ],
        jsCustomServices : [ '_services/*/*.js', '_services/*/**/*.js' ],
        jsCustomCss: [ '_modules/**/css/*.css', '_assets/css/arkcase-extension.css'],
        jsCustomScss: ['_modules/**/scss/*.scss']
    }
};
