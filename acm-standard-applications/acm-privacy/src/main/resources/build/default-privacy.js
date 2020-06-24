const os = require("os");
module.exports = {
    title: 'ACM3 Privacy',

    // Path to ACM3 source folder
    acm3Src: '../acm-standard-applications/acm-law-enforcement/src/main/webapp/resources',
    
    // Path to ACM3 extension source folder. If extension is missed then null
    acm3ExtensionSrc: '../acm-standard-applications/acm-privacy/src/main/resources/META-INF/resources/resources',
    
    // Path to Custom extension source folder. If extension not used then null
    acm3CustomExtensionSrc: null,

    acm3ExtensionPrefix: 'privacy_',
    acm3ExtensionAssetsFolder: 'privacy_assets',
    acm3ExtensionDirectivesFolder: 'privacy_directives',
    acm3ExtensionFiltersFolder: 'privacy_filters',
    acm3ExtensionModulesFolder: 'privacy_modules',
    acm3ExtensionServicesFolder: 'privacy_services',


    // Privacy project has special custom folder prefix "privacy_"
    acm3ExtensionJSFiles: [
        'privacy_filters/*/*.js',
        'privacy_filters/*/**/*.js',
        'privacy_directives/*/*.js',
        'privacy_directives/*/**/*.js',
        'privacy_services/*/*.js',
        'privacy_services/*/**/*.js',
        'privacy_modules/*/*.js',
        'privacy_modules/*/**/*.js'
    ],

    acm3ExtensionCSSFiles: [
        'privacy_assets/css/arkcase-extension.css',
        'privacy_directives/**/*.css',
        'privacy_filters/**/*.css',
        'privacy_services/**/*.css',
        'privacy_modules/**/*.css'
    ],

    acm3ExtensionHTMLFiles: [
        'privacy_directives/**/*.html',
        'privacy_filters/**/*.html',
        'privacy_modules/**/*.html',
        'privacy_services/**/*.html'
    ]
};