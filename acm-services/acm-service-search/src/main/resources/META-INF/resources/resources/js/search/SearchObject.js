/**
 * Search.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Search.Object = {
    initialize : function() {
        this.$asideSubNav = $("#subNav");

        this.$lnkToggleSubNav = $("a[href='#subNav']");
        //this.$lnkToggleSubNav.click(function(e) {Search.Event.onClickBtnToggleSubNav(e);});

        this.$edtSearch     = $("#searchQuery");
        this.$chkComplaints = $("#chkComplaints");
        this.$chkCases      = $("#chkCases");
        this.$chkTasks      = $("#chkTasks");
        this.$chkDocuments  = $("#chkDocuments");
        this.$chkPeople  = $("#chkPeople");

        //Complaint
        this.$edtComplaintTitle = $("#edtComplaintTitle");
        this.$edtComplaintID = $("#edtComplaintID");
        this.$edtComplaintDateStartRange = $("#edtComplaintDateStartRange");
        this.$edtComplaintDateEndRange = $("#edtComplaintDateEndRange");
        this.$selComplaintPriority = $("#selComplaintPriority");
        this.$selComplaintAssignee = $("#selComplaintAssignee");
        this.$selComplaintSubjectType = $("#selComplaintSubjectType");
        this.$selComplaintStatus = $("#selComplaintStatus");

        //Case
        this.$edtCaseTitle = $("#edtCaseTitle");
        this.$edtCaseID = $("#edtCaseID");
        this.$edtCaseDateStartRange = $("#edtCaseDateStartRange");
        this.$edtCaseDateEndRange = $("#edtCaseDateEndRange");
        this.$selCasePriority = $("#selCasePriority");
        this.$selCaseAssignee = $("#selCaseAssignee");
        this.$selCaseSubjectType = $("#selCaseSubjectType");
        this.$selCaseStatus = $("#selCaseStatus");

        //Task
        this.$edtTaskTitle = $("#edtTaskTitle");
        this.$edtTaskID = $("#edtTaskID");
        this.$edtTaskDateStartRange = $("#edtTaskDateStartRange");
        this.$edtTaskDateEndRange = $("#edtTaskDateEndRange");
        this.$selTaskPriority = $("#selTaskPriority");
        this.$selTaskAssignee = $("#selTaskAssignee");
        this.$selTaskSubjectType = $("#selTaskSubjectType");
        this.$selTaskStatus = $("#selTaskStatus");

        //Document
        this.$edtDocumentTitle = $("#edtDocumentTitle");
        this.$edtDocumentID = $("#edtDocumentID");
        this.$edtDocumentDateStartRange = $("#edtDocumentDateStartRange");
        this.$edtDocumentDateEndRange = $("#edtDocumentDateEndRange");
        this.$selDocumentPriority = $("#selDocumentPriority");
        this.$selDocumentAssignee = $("#selDocumentAssignee");
        this.$selDocumentFormType = $("#selDocumentFormType");
        this.$selDocumentStatus = $("#selDocumentStatus");

        //People
        this.$edtPeopleTitle = $("#edtPeopleTitle");
        this.$edtPeopleFirstName = $("#edtPeopleFirstName");
        this.$edtPeopleLastName = $("#edtPeopleLastName");
        this.$selPeopleType = $("#selPeopleType");
        this.$edtPeoplePhoneNumber = $("#edtPeoplePhoneNumber");
        this.$edtPeopleOrganization = $("#edtPeopleOrganization");
        this.$edtPeopleAddress = $("#edtPeopleAddress");
        this.$edtPeopleCity = $("#edtPeopleCity");
        this.$selPeopleState = $("#selPeopleState");
        this.$edtPeopleZIPCode = $("#edtPeopleZIPCode");


        this.$btnSearch = this.$edtSearch.next().find("button");
        this.$btnSearch.click(function(e) {Search.Event.onClickBtnSearch(e);});
        this.$btnSearch.click(function(e) {Search.Event.onClickBtnSearchComplaints(e);});

//        this.$tabResults = $("table");

        this.$divResults = $("#divResults");
        Search.Object.createJTableResults(this.$divResults);
    }

    ,showSubNav: function(show) {
        Acm.Object.show(this.$asideSubNav, show);
        if (show) {
            this.$lnkToggleSubNav.addClass("active");
        } else {
            this.$lnkToggleSubNav.removeClass("active");
        }
    }

//    ,resetTableResults: function() {
//        this.$tabResults.find("tbody > tr").remove();
//    }
//    ,addRowTableResults: function(row) {
//        this.$tabResults.find("tbody:last").append(row);
//    }

    //getters Complaints
    ,getValueComplaintTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtComplaintTitle);
    }
    ,getValueComplaintID: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtComplaintID);
    }
    ,getValueComplaintDateStartRange: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtComplaintDateStartRange);
    }
    ,getValueComplaintDateEndRange: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtComplaintDateEndRange );
    }
    ,getValueComplaintPriority: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selComplaintPriority);
    }
    ,getValueComplaintAssignee: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selComplaintAssignee);
    }
    ,getValueComplaintSubjectType: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selComplaintSubjectType);
    }
    ,getValueComplaintStatus: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selComplaintStatus);
    }

    //getters Cases
    ,getValueCaseTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCaseTitle);
    }
    ,getValueCaseID: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCaseID);
    }
    ,getValueCaseDateStartRange: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtCaseDateStartRange);
    }
    ,getValueCaseDateEndRange: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtCaseDateEndRange );
    }
    ,getValueCasePriority: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selCasePriority);
    }
    ,getValueCaseAssignee: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selCaseAssignee);
    }
    ,getValueCaseSubjectType: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selCaseSubjectType);
    }
    ,getValueCaseStatus: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selCaseStatus);
    }

    //getters Tasks
    ,getValueTaskTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtTaskTitle);
    }
    ,getValueTaskID: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtTaskID);
    }
    ,getValueTaskDateStartRange: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtTaskDateStartRange);
    }
    ,getValueTaskDateEndRange: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtTaskDateEndRange );
    }
    ,getValueTaskPriority: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selTaskPriority);
    }
    ,getValueTaskAssignee: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selTaskAssignee);
    }
    ,getValueTaskSubjectType: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selTaskSubjectType);
    }
    ,getValueTaskStatus: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selTaskStatus);
    }

    //getters Documents
    ,getValueDocumentTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtDocumentTitle);
    }
    ,getValueDocumentID: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtDocumentID);
    }
    ,getValueDocumentDateStartRange: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtDocumentDateStartRange);
    }
    ,getValueDocumentDateEndRange: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtDocumentDateEndRange );
    }
    ,getValueDocumentPriority: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selDocumentPriority);
    }
    ,getValueDocumentAssignee: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selDocumentAssignee);
    }
    ,getValueDocumentFormType: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selDocumentFormType);
    }
    ,getValueDocumentStatus: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selDocumentStatus);
    }


    //getters People
    ,getValuePeopleTitle: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtPeopleTitle);
    }
    ,getValuePeopleFirstName: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtPeopleFirstName);
    }
    ,getValuePeopleLastName: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtPeopleLastName);
    }
    ,getValuePeopleDateEndRange: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtPeopleDateEndRange );
    }
    ,getValuePeopleType: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selPeopleType);
    }
    ,getValuePeoplePhoneNumber: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtPeoplePhoneNumber);
    }
    ,getValuePeopleOrganization: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtPeopleOrganization);
    }
    ,getValuePeopleAddress: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtPeopleAddress);
    }
    ,getValuePeopleCity: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtPeopleCity);
    }
    ,getValuePeopleState: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$selPeopleState);
    }
    ,getValuePeopleZIPCode: function() {
        return Acm.Object.getSelectValueIgnoreFirst(this.$edtPeopleZIPCode);
    }




    ,getValueEdtSearch: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtSearch);
    }
    ,isCheckChkComplaints: function() {
        return Acm.Object.isChecked(this.$chkComplaints);
    }
    ,isCheckChkCases: function() {
        return Acm.Object.isChecked(this.$chkCases);
    }
    ,isCheckChkTasks: function() {
        return Acm.Object.isChecked(this.$chkTasks);
    }
    ,isCheckChkDocuments: function() {
        return Acm.Object.isChecked(this.$chkDocuments);
    }
    ,isCheckChkPeople: function() {
        return Acm.Object.isChecked(this.$chkPeople);
    }
    ,setTableTitle: function(title) {
        Acm.Object.setText($(".jtable-title-text"), title);
    }


    ,reloadJTableResults: function() {
        var $s = this.$divResults;
        $s.jtable('load');
    }
    ,createJTableResults: function($jt) {
        var sortMap = {};
        sortMap["title"] = "title_t";

        AcmEx.Object.jTableCreatePaging($jt
            ,{
                title: 'Tasks'
                //,defaultSorting: 'Name ASC'
                ,selecting: true //Enable selecting
                ,multiselect: true //Allow multiple selecting
                ,selectingCheckboxes: true //Show checkboxes on first column
                //,selectOnRowClick: false //Enable this to only select using checkboxes

                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                            ,function() {
                                var term = Topbar.Object.getQuickSearchTerm();
                                var url;
                                url =  App.getContextPath() + Search.Service.API_QUICK_SEARCH;
                                url += "?q=" + term;
                                return url;
                            }
                            ,function(data) {
                                var jtData = null;
                                var err = "Invalid search data";
                                if (data) {
                                    if (Acm.isNotEmpty(data.responseHeader)) {
                                        var responseHeader = data.responseHeader;
                                        if (Acm.isNotEmpty(responseHeader.status)) {
                                            if (0 == responseHeader.status) {
                                                var response = data.response;
                                                //response.start should match to jtParams.jtStartIndex
                                                //response.docs.length should be <= jtParams.jtPageSize

                                                jtData = AcmEx.Object.jTableGetEmptyResult();
                                                for (var i = 0; i < response.docs.length; i++) {
                                                    var Record = {};
                                                    Record.id = response.docs[i].object_id_s;
                                                    Record.name = Acm.goodValue(response.docs[i].name);
                                                    Record.type = Acm.goodValue(response.docs[i].object_type_s);
                                                    Record.title = Acm.goodValue(response.docs[i].title_t);
                                                    Record.owner = Acm.goodValue(response.docs[i].owner_s);
                                                    Record.created = Acm.goodValue(response.docs[i].create_dt);
                                                    jtData.Records.push(Record);

                                                }
                                                jtData.TotalRecordCount = response.numFound;


                                            } else {
                                                if (Acm.isNotEmpty(data.error)) {
                                                    err = data.error.msg + "(" + data.error.code + ")";
                                                }
                                            }
                                        }
                                    }
                                }

                                return {jtData: jtData, jtError: err};
                            }
                        );
                    }
                }


                ,fields: {
//                RowCheckbox: {
//                    title: 'Status',
//                    width: '12%',
//                    type: 'checkbox',
//                    values: { 'false': 'Passive', 'true': 'Active' },
//                    defaultValue: 'true'
//                },
                    id: {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: false
                        ,edit: false
                        ,sorting: false
                    }
                    ,name: {
                        title: 'Name'
                        ,width: '15%'
                        ,sorting: false
                        ,display: function(data) {
                            var url = App.getContextPath();
                            if (App.OBJTYPE_CASE == data.record.type) {
                                url += "/plugin/case/" + data.record.id;
                            } else if (App.OBJTYPE_COMPLAINT == data.record.type) {
                                url += "/plugin/complaint/" + data.record.id;
                            } else if (App.OBJTYPE_TASK == data.record.type) {
                                url += "/plugin/task/" + data.record.id;
                            } else if (App.OBJTYPE_DOCUMENT == data.record.type) {
                                url += "/plugin/document/" + data.record.id;
                            }
                            else if (App.OBJTYPE_PEOPLE == data.record.type) {
                                url += "/plugin/people/" + data.record.id;
                            }
                            var $lnk = $("<a href='" + url + "'>" + data.record.name + "</a>");
                            //$lnk.click(function(){alert("click" + data.record.id)});
                            return $lnk;
                        }
                    }
                    ,type: {
                        title: 'Type'
                        ,options: [App.OBJTYPE_CASE, App.OBJTYPE_COMPLAINT, App.OBJTYPE_TASK, App.OBJTYPE_DOCUMENT]
                        ,sorting: false
                    }
                    ,title: {
                        title: 'Title'
                        ,width: '30%'
                    }
                    ,owner: {
                        title: 'Owner'
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,created: {
                        title: 'Created'
                        ,type: 'textarea'
                        ,width: '20%'
                        ,sorting: false
                    }
                } //end field
            } //end arg
            ,sortMap
        );
    }


};




