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

        this.$edtSearch = $("#searchQuery");

        this.$btnSearch = this.$edtSearch.next().find("button");
        this.$btnSearch.click(function(e) {Search.Event.onClickBtnSearch(e);});

//        this.$tabResults = $("table");

        this.$divResults = $("#divResults");
        Search.Object.createJTableResults();
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

    ,reloadJTableResults: function() {
        var $s = this.$divResults;
        $s.jtable('load');
    }
    ,createJTableResults: function() {
        var $s = this.$divResults;
        $s.jtable({
            title: 'Search Results'
            //,selecting: false
            ,paging: true
            ,pageSize: Search.DEFAULT_PAGE_SIZE
            ,sorting: true

            //,defaultSorting: 'Name ASC'
            ,selecting: true //Enable selecting
            ,multiselect: true //Allow multiple selecting
            ,selectingCheckboxes: true //Show checkboxes on first column
            //,selectOnRowClick: false //Enable this to only select using checkboxes

            ,actions: {
                listAction: function (postData, jtParams) {
                    if (Acm.isEmpty(Acm.getContextPath())) {
                        return Search.EMPTY_RESULT; //[];
                    }

                    var term = Search.getQuickSearchTerm();
                    if (Acm.isEmpty(term)) {
                        return Search.EMPTY_RESULT;
                    }

                    var url;
                    url =  Acm.getContextPath() + Search.Service.API_QUICK_SEARCH;
                    url += "?q=" + term;
                    if (Acm.isNotEmpty(jtParams.jtStartIndex)) {
                        url += "&start=" + jtParams.jtStartIndex;
                    }
                    if (Acm.isNotEmpty(jtParams.jtPageSize)) {
                        url += "&n=" + jtParams.jtPageSize;
                    }
                    if (Acm.isNotEmpty(jtParams.jtSorting)) {
                        var arr = jtParams.jtSorting.split(" ");
                        if (2 == arr.length) {
                            if ("title" == arr[0]) {
                                url += "&s=title_t%20" + arr[1];
                            }
                        }
                    }

//                    if (0 == jtParams.jtStartIndex) {
//                        url = Acm.getContextPath() + "/resources/search.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting;
//                    } else if (4 == jtParams.jtStartIndex) {
//                        url = Acm.getContextPath() + "/resources/search2.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting;
//                    } else {
//                        url = Acm.getContextPath() + "/resources/search3.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting;
//                    }

                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: url,
                            type: 'GET',
                            dataType: 'json',
                            data: postData,
                            success: function (data) {
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

                                                jtData = {"Result": "OK","Records": [],"TotalRecordCount": 0};
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

                                if (jtData) {
                                    $dfd.resolve(jtData);
                                } else {
                                    $dfd.reject();
                                    Acm.Dialog.error(err);
                                }
                            },
                            error: function () {
                                $dfd.reject();
                            }
                        });
                    });
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
                        var url = Acm.getContextPath();
                        if (Search.OBJTYPE_CASE == data.record.type) {
                            url += "/plugin/case/" + data.record.id;
                        } else if (Search.OBJTYPE_COMPLAINT == data.record.type) {
                            url += "/plugin/complaint/" + data.record.id;
                        } else if (Search.OBJTYPE_TASK == data.record.type) {
                            url += "/plugin/task/" + data.record.id;
                        } else if (Search.OBJTYPE_DOCUMENT == data.record.type) {
                            url += "/plugin/document/" + data.record.id;
                        }
                        var $lnk = $("<a href='" + url + "'>" + data.record.name + "</a>");
                        //$lnk.click(function(){alert("click" + data.record.id)});
                        return $lnk;
                    }
                }
                ,type: {
                    title: 'Type'
                    ,options: [Search.OBJTYPE_CASE, Search.OBJTYPE_COMPLAINT, Search.OBJTYPE_TASK, Search.OBJTYPE_DOCUMENT]
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
            }
            ,recordAdded: function(event, data){
                $s.jtable('load');
            }
            ,recordUpdated: function(event, data){
                $s.jtable('load');
            }
        });

        $s.jtable('load');
    }


};




