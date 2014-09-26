/**
 * Search.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Search.Object = {
    initialize : function() {
        var items = $(document).items();
        var searchExStr = items.properties("searchEx").itemValue();
        this.searchEx = $.parseJSON(searchExStr);
        this.$divSearchQuery = $("#searchQuery").parent();
        Search.Page.buildPanel(this.searchEx);
        this.useSwitches();
        this.useDateFields();

        this.$asideSubNav = $("#subNav");

        this.$lnkToggleSubNav = $("a[href='#subNav']");
        //this.$lnkToggleSubNav.click(function(e) {Search.Event.onClickBtnToggleSubNav(e);});

        this.$edtSearch     = $("#searchQuery");
        this.$btnSearch = this.$edtSearch.next().find("button");
        this.$btnSearch.on("click", function(e) {Search.Event.onClickBtnSearch(e, this);});

        this.$divResults = $("#divResults");
        Search.Object.createJTableResults(this.$divResults);
    }

    ,useSwitches: function() {
        $(".form-group").each(function( index ) {
            var $chkSwitch = $(this).find("label.switch input");
            $chkSwitch.click(function(){
                var $divSibling = $chkSwitch.closest(".form-group").find(">div:last-child");
                $divSibling.slideToggle();
            });
        });
    }
    ,useDateFields: function() {
        $(".datepicker-input").datepicker();
    }
    ,_findDetailLink: function(objectType) {
        if (this.searchEx) {
            var link = null;
            for (var i = 0; i < this.searchEx.length; i++) {
                var pluginEx = this.searchEx[i];
                if (pluginEx.objectType && pluginEx.detailLink) {
                    if (objectType == pluginEx.objectType) {
                        link = pluginEx.detailLink;
                        break;
                    }
                }
            }
        }
        return link;
    }
    ,appendHtmlDivSearchQuery: function(html) {
        this.$divSearchQuery.after(html);
    }

    ,getValueEdtSearch: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtSearch);
    }

    ,getData: function() {
        var data = {};
        var searchTerm = this.getValueEdtSearch();

        $(".form-group").each(function( index ) {
            var $chkSwitch = $(this).find("label.switch input");
            var switchOn = Acm.Object.isChecked($chkSwitch);
            if (1 == $chkSwitch.length) {
                $(this).find(".form-control").each(function( cidx ) {
                    var isOn = switchOn;
                    var val = $(this).val();
                    var term = $(this).attr("term");
                    var id = $(this).attr("id");
                    var z = 1;
                });
            }

            var z = 1;
        });

        return data;
    }

    ,showSubNav: function(show) {
        Acm.Object.show(this.$asideSubNav, show);
        if (show) {
            this.$lnkToggleSubNav.addClass("active");
        } else {
            this.$lnkToggleSubNav.removeClass("active");
        }
    }


    ,setTableTitle: function(title) {
        Acm.Object.setText($(".jtable-title-text"), title);
        //AcmEx.Object.jTableSetTitle(this.$divResults, title);
    }
    ,reloadJTableResults: function() {
        AcmEx.Object.jTableLoad(this.$divResults);
    }
    ,createJTableResults: function($jt) {
        var sortMap = {};
        sortMap["title"] = "title_t";

        AcmEx.Object.jTableCreatePaging($jt
            ,{
                title: 'Search Results'
                //,defaultSorting: 'Name ASC'
                ,selecting: true //Enable selecting
                ,multiselect: true //Allow multiple selecting
                ,selectingCheckboxes: true //Show checkboxes on first column
                //,selectOnRowClick: false //Enable this to only select using checkboxes

                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var term = Topbar.Object.getQuickSearchTerm();
                        if (Acm.isEmpty(term)) {
                            return AcmEx.Object.jTableGetEmptyRecords();
                        }

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
                                    if (data.responseHeader && data.response) {
                                        var responseHeader = data.responseHeader;
                                        if (Acm.isNotEmpty(responseHeader.status)) {
                                            if (0 == responseHeader.status) {
                                                var response = data.response;
                                                //response.start should match to jtParams.jtStartIndex
                                                //response.docs.length should be <= jtParams.jtPageSize

                                                jtData = AcmEx.Object.jTableGetEmptyRecords();
                                                if (response.docs) {
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
                                                }
                                                jtData.TotalRecordCount = Acm.goodValue(response.numFound, 0);


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
                            } else if (App.OBJTYPE_PEOPLE == data.record.type) {
                                url += "/plugin/people/" + data.record.id;
                            }
                            var $lnk = $("<a href='" + url + "'>" + data.record.name + "</a>");
                            //$lnk.click(function(){alert("click" + data.record.id)});
                            return $lnk;
                        }
                    }
                    ,type: {
                        title: 'Type'
                        //,options: [App.OBJTYPE_CASE, App.OBJTYPE_COMPLAINT, App.OBJTYPE_TASK, App.OBJTYPE_DOCUMENT]
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




