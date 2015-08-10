'use strict';
var appRoot = require('app-root-path');

module.exports = {
    assets: {
        lib: {
            css: [
                'src/main/webapp/lib/bootstrap/dist/css/bootstrap.css',
                'src/main/webapp/lib/bootstrap/dist/css/bootstrap-theme.css',
                'src/main/webapp/lib/fancytree/dist/skin-win8/ui.fancytree.css',
                'src/main/webapp/lib/angular-ui-grid/ui-grid.css'
            ],
            js: [
                'src/main/webapp/lib/jquery/dist/jquery.js',
                'src/main/webapp/lib/jquery-ui/jquery-ui.js',
                'src/main/webapp/lib/fancytree/dist/jquery.fancytree.js',
                'src/main/webapp/lib/tv4/tv4.js',
                'src/main/webapp/lib/objectpath/lib/ObjectPath.js',
                'src/main/webapp/lib/lodash/lodash.js',
                'src/main/webapp/lib/angular/angular.js',
                'src/main/webapp/lib/angular-resource/angular-resource.js',
                'src/main/webapp/lib/angular-cookies/angular-cookies.js',
                'src/main/webapp/lib/angular-animate/angular-animate.js',
                'src/main/webapp/lib/angular-touch/angular-touch.js',
                'src/main/webapp/lib/angular-sanitize/angular-sanitize.js',
                'src/main/webapp/lib/angular-ui-router/release/angular-ui-router.js',
                'src/main/webapp/lib/angular-bootstrap/ui-bootstrap-tpls.js',
                'src/main/webapp/lib/angular-ui-grid/ui-grid.js',
                'src/main/webapp/lib/angular-schema-form/dist/schema-form.js',
                'src/main/webapp/lib/angular-schema-form/dist/bootstrap-decorator.js',
                'src/main/webapp/lib/angular-translate/angular-translate.js',
                'src/main/webapp/lib/angular-translate-loader-partial/angular-translate-loader-partial.js'
            ]
        },
        css: [
            'src/main/webapp/assets/css/application.css',
            'src/main/webapp/modules/**/css/*.css',
            'src/main/webapp/custom_modules/**/css/*.css'
        ],
        scss: [
            'src/main/webapp/modules/**/scss/*.scss',
            'src/main/webapp/custom_modules/**/scss/*.scss'
        ],
        js: [
            'src/main/webapp/config.js',
            'src/main/webapp/application.js',
            'src/main/webapp/services/*.js',
            'src/main/webapp/services/*/*.js',
            'src/main/webapp/directives/*.js',
            'src/main/webapp/directives/*/*.js'
        ],
        jsModules: [
            'src/main/webapp/modules/*/*.js',
            'src/main/webapp/modules/*/**/*.js'
        ],
        jsCustomModules: [
            'src/main/webapp/custom_modules/*/*.js',
            'src/main/webapp/custom_modules/*/**/*.js'
        ]
    }
}