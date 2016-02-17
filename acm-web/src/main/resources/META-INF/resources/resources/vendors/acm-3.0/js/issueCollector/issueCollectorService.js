/**
 * IssueCollector.Service
 *
 * manages all service call to application server
 *
 * @author manoj.dhungana
 */
IssueCollector.Service = {
    create: function () {
    }

    //will be read from the properties file soon

    ,
    API_GET_JIRA_ISSUE_COLLECTOR: "https://project.armedia.com/jira/s/272b7e5d0b48558abb6f76f2cc38fb4c-T/en_US-f0xdna/6346/2/1.4.16/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?locale=en-US&collectorId=2b76dcde"

    ,
    getIssueCollector: function () {
        Acm.Service.ajax({
            type: "GET"
            , url: this.API_GET_JIRA_ISSUE_COLLECTOR
            , cache: true
            , dataType: "script"
            , nonAcmUrl: true
        });
    }
};

