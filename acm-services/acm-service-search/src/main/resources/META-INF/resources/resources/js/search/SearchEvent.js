/**
 * Search.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Search.Event = {
    initialize : function() {
    }

    ,onClickBtnSearch : function(e) {
//alert("go");
        var search = Search.Object.getValueEdtSearch();
        var hasComplaints = Search.Object.isCheckChkComplaints();
        var hasCases = Search.Object.isCheckChkCases();
        var hasTasks = Search.Object.isCheckChkTasks();
        var hasDocuments = Search.Object.isCheckChkDocuments();
        var hasPeople = Search.Object.isCheckChkPeople();

        e.preventDefault();
    }

    ,onClickToggle: function(e) {
        var id = $(e).attr("id");
        Search.Object.slideToggle(id);

    }


    ,onClickBtnToggleSubNav : function(e) {
        //alert("toggle");
        //Search.Object.showSubNav(true);
        //e.preventDefault();
    }

    ,onClickBtnSearchComplaints : function(e) {
        var search = Search.Object.getValueEdtSearch();
        var complaintTitle = Search.Object.getValueComplaintTitle();
        var complaintID = Search.Object.getValueComplaintID();
        var complaintStartDate = Search.Object.getValueComplaintDateStartRange();
        var complaintEndDate = Search.Object.getValueComplaintDateEndRange();
        var complaintPriority = Search.Object.getValueComplaintPriority();
        var complaintAssignee = Search.Object.getValueComplaintAssignee();
        var complaintSubjectType = Search.Object.getValueComplaintSubjectType();
        var complaintStatus = Search.Object.getValueComplaintStatus();
        //+ "</br>" + hasComplaints
        //Acm.Dialog.info(complaintTitle + "<br>" +  complaintID + "</br>" + complaintStartDate + "</br>" + complaintEndDate + "<br>" +  complaintPriority + "</br>" + complaintAssignee + "<br>" +  complaintSubjectType + "</br>" + complaintStatus + "</br>" + search );
    }

    ,onClickBtnSearchCases : function(e) {
        var search = Search.Object.getValueEdtSearch();
        var caseTitle = Search.Object.getValueCaseTitle();
        var caseID = Search.Object.getValueCaseID();
        var caseStartDate = Search.Object.getValueCaseDateStartRange();
        var caseEndDate = Search.Object.getValueCaseDateEndRange();
        var casePriority = Search.Object.getValueCasePriority();
        var caseAssignee = Search.Object.getValueCaseAssignee();
        var caseSubjectType = Search.Object.getValueCaseSubjectType();
        var caseStatus = Search.Object.getValueCaseStatus();
    }

    ,onClickBtnSearchTasks : function(e) {
        var search = Search.Object.getValueEdtSearch();
        var taskTitle = Search.Object.getValueTaskTitle();
        var taskID = Search.Object.getValueTaskID();
        var taskStartDate = Search.Object.getValueTaskDateStartRange();
        var taskEndDate = Search.Object.getValueTaskDateEndRange();
        var taskPriority = Search.Object.getValueTaskPriority();
        var taskAssignee = Search.Object.getValueTaskAssignee();
        var taskSubjectType = Search.Object.getValueTaskSubjectType();
        var taskStatus = Search.Object.getValueTaskStatus();
    }
    ,onClickBtnSearchDocuments : function(e) {
        var search = Search.Object.getValueEdtSearch();
        var documentTitle = Search.Object.getValueDocumentTitle();
        var documentID = Search.Object.getValueDocumentID();
        var documentStartDate = Search.Object.getValueDocumentDateStartRange();
        var documentEndDate = Search.Object.getValueDocumentDateEndRange();
        var documentPriority = Search.Object.getValueDocumentPriority();
        var documentAssignee = Search.Object.getValueDocumentAssignee();
        var documentFormType = Search.Object.getValueDocumentFormType();
        var documentStatus = Search.Object.getValueDocumentStatus();
    }
    ,onClickBtnSearchPeople : function(e) {
        var search = Search.Object.getValueEdtSearch();
        var peopleTitle = Search.Object.getValuePeopleTitle();
        var peopleFirstName = Search.Object.getValuePeopleFirstName();
        var peopleLastName = Search.Object.getValuePeopleLastName();
        var peopleType = Search.Object.getValuePeopleType();
        var peoplePhoneNumber = Search.Object.getValuePeoplePhoneNumber();
        var peopleOrganization = Search.Object.getValuePeopleOrganization();
        var peopleAddress = Search.Object.getValuePeopleAddress();
        var peopleCity = Search.Object.getValuePeopleCity();
        var peopleState = Search.Object.getValuePeopleState();
        var peopleZIPCode = Search.Object.getValuePeopleZIPCode();
    }


    ,onPostInit: function() {
        var term = Topbar.Object.getQuickSearchTerm();
        if (Acm.isNotEmpty(term)) {
            Search.Object.reloadJTableResults();
        }
    }
};
