const os = require("os");
module.exports = {
    title: 'ACM3 Foia',

    acm3Url: 'https://acm-arkcase',

    // Path to ACM3 source folder
    acm3Src: '../acm-standard-applications/acm-law-enforcement/src/main/webapp/resources',

    // Path to ACM3 extension source folder. If extension is missed then null
    acm3ExtensionSrc: '../acm-standard-applications/acm-foia/src/main/resources/META-INF/resources/resources',
    
    // Distribution folder location
    acm3Dist: 'dist'
};