<#--
    we are using:

    .data_model["variable.name"]

    instead of

    ${variable.name}

    because of dot character in names, otherwise
    it tries to evaluate name as a property of variable

    ?c suffix expands boolean variable as a string (therefore we use false?c)
-->
var GOOGLE_ANALYTICS_ENABLED = ${.data_model["ga.enabled"]!false?c};
var GOOGLE_ANALYTICS_TRACKING_ID = "${.data_model["ga.trackingId"]!'N/A'}";
var GOOGLE_ANALYTICS_DEBUG = ${.data_model["ga.debug"]!false?c};
