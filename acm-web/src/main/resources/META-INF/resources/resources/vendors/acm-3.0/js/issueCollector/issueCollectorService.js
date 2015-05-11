/**
 * IssueCollector.Service
 *
 * manages all service call to application server
 *
 * @author manoj.dhungana
 */
IssueCollector.Service = {
    create : function() {
    }

    //will be read from the properties file soon

    ,API_GET_JIRA_ISSUE_COLLECTOR             : "***REMOVED***/s/31413758042897b94fd2d74d89768365-T/en_US9cltp4/6346/2/1.4.16/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector-embededjs/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector-embededjs.js?locale=en-US&collectorId=aad5f79b"

    ,getIssueCollector: function(issueCollectorFlag) {
        if(issueCollectorFlag){
            Acm.Service.ajax({
                type: "GET"
                ,url: this.API_GET_JIRA_ISSUE_COLLECTOR
                ,cache: true
                ,dataType: "script"
            });
        }
    }
};

