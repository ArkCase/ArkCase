/**
 * SimpleSearch.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
SimpleSearch.Object = {
    initialize : function() {
        this.$edtQuickSearch = $("form[role='search'] input");

        this.$asideSubNav = $("#subNav");
        this.$lnkToggleSubNav = $("a[href='#subNav']");
        this.$tabResults = $("table");

        this.$divResults = $("#divResults");
        SimpleSearch.Object.createJTableResults();
    }

    ,getValueEdtQuickSearch: function() {
        return Acm.Object.getPlaceHolderInput(this.$edtQuickSearch);
    }
    ,setValueEdtQuickSearch: function(val) {
        Acm.Object.setPlaceHolderInput(this.$edtQuickSearch, val);
    }
    ,showSubNav: function(show) {
        Acm.Object.show(this.$asideSubNav, show);
        if (show) {
            this.$lnkToggleSubNav.addClass("active");
        } else {
            this.$lnkToggleSubNav.removeClass("active");
        }
    }

    ,resetTableResults: function() {
        this.$tabResults.find("tbody > tr").remove();
    }
    ,addRowTableResults: function(row) {
        this.$tabResults.find("tbody:last").append(row);
    }

    ,createJTableResults: function() {
        var $s = this.$divResults;
        $s.jtable({
            title: 'Search Results'
            //,selecting: false
            ,paging: true
            ,pageSize: 4
            ,sorting: true

            //,defaultSorting: 'Name ASC'
            ,selecting: true //Enable selecting
            ,multiselect: true //Allow multiple selecting
            ,selectingCheckboxes: true //Show checkboxes on first column
            //,selectOnRowClick: false //Enable this to only select using checkboxes

            ,actions: {
                listAction: function (postData, jtParams) {
                    if (Acm.isEmpty(Acm.getContextPath())) {
                        return [];
                    }

                    var url;
                    if (0 == jtParams.jtStartIndex) {
                        url = Acm.getContextPath() + "/resources/search.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting;
                    } else if (4 == jtParams.jtStartIndex) {
                        url = Acm.getContextPath() + "/resources/search2.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting;
                    } else {
                        url = Acm.getContextPath() + "/resources/search3.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting;
                    }

                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            //url: "acm-law" + "/resources/search.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting,
                            //url: Acm.getContextPath() + "/resources/search.json" + "?jtStartIndex=" + jtParams.jtStartIndex + '&jtPageSize=' + jtParams.jtPageSize + '&jtSorting=' + jtParams.jtSorting,
                            //type: 'POST'
                            url: url,
                            type: 'GET',
                            dataType: 'json',
                            data: postData,
                            success: function (data) {
                                $dfd.resolve(data);
                            },
                            error: function () {
                                $dfd.reject();
                            }
                        });
                    });
                }

//                listAction: function(postData, jtParams) {
//                    return {
//                        "Result": "OK"
//                        ,"Records": [
//                            { "personId":  1, "title": "Mr.", "firstName": "John", "lastName": "Garcia", "type": "Witness", "description": "123 do re mi" }
//                            ,{ "personId": 2, "title": "Ms.", "firstName": "Jane", "lastName": "Doe", "type": "Subject", "description": "xyz abc" }
//                        ]
//                        ,"TotalRecordCount": 2
//                    };
//                }
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
                    ,list: true
                    ,create: false
                    ,edit: false
                    ,sorting: false
                    ,display: function(data) {
                        var $lnk = $("<a href='" + "http://www.google.com" + "'>" + data.record.id + "</a>");
                        $lnk.click(function(){alert("click" + data.record.id)});
                        return $lnk;
                    }
                }
                ,type: {
                    title: 'Type'
                    ,options: ["Case", "Complaint", "Task", "Document"]
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




