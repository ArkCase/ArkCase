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

        this.$btnSearch = this.$edtSearch.next().find("button");
        this.$btnSearch.click(function(e) {Search.Event.onClickBtnSearch(e);});

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

        Acm.Object.jTableCreateSortable($jt
            ,{
                title: 'Tasks'
                //,defaultSorting: 'Name ASC'
                ,selecting: true //Enable selecting
                ,multiselect: true //Allow multiple selecting
                ,selectingCheckboxes: true //Show checkboxes on first column
                //,selectOnRowClick: false //Enable this to only select using checkboxes

                ,actions: {
                    listActionSortable: function (postData, jtParams, sortMap) {
                        return Acm.Object.jTableDefaultListAction(postData, jtParams, sortMap
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

                                                jtData = Acm.Object.jTableGetEmptyResult();
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




