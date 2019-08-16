const os = require("os");
module.exports = {
    title: 'ACM3 Foia',

    // Path to ACM3 source folder
    acm3Src: '../acm-standard-applications/acm-law-enforcement/src/main/webapp/resources',
    
    // Path to ACM3 extension source folder. If extension is missed then null
    acm3ExtensionSrc: '../acm-standard-applications/acm-foia/src/main/resources/META-INF/resources/resources',
    
    // Path to Custom extension source folder. If extension not used then null
    acm3CustomExtensionSrc: null,

    acm3ExtensionPrefix: 'foia_',
    acm3ExtensionAssetsFolder: 'foia_assets',
    acm3ExtensionDirectivesFolder: 'foia_directives',
    acm3ExtensionFiltersFolder: 'foia_filters',
    acm3ExtensionModulesFolder: 'foia_modules',
    acm3ExtensionServicesFolder: 'foia_services',


    // FOIA project has special custom folder prefix "foia_"
    acm3ExtensionJSFiles: [
        'foia_filters/*/*.js',
        'foia_filters/*/**/*.js',
        'foia_directives/*/*.js',
        'foia_directives/*/**/*.js',
        'foia_services/*/*.js',
        'foia_services/*/**/*.js',
        'foia_modules/*/*.js',
        'foia_modules/*/**/*.js'
    ],

    acm3ExtensionCSSFiles: [
        'foia_assets/css/arkcase-extension.css',
        'foia_directives/**/*.css',
        'foia_filters/**/*.css',
        'foia_services/**/*.css',
        'foia_modules/**/*.css'
    ],

    acm3ExtensionHTMLFiles: [
        'foia_directives/**/*.html',
        'foia_filters/**/*.html',
        'foia_modules/**/*.html',
        'foia_services/**/*.html'
    ]
};