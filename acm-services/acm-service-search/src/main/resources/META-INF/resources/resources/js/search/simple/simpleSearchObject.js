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
            ,selecting: true
            ,paging: true
            ,pageSize: 4
            ,sorting: true
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
                personId: {
                    title: 'ID'
                    ,key: true
                    ,list: false
                    ,create: false
                    ,edit: false
                }

                ,title: {
                    title: 'Title'
                    ,width: '10%'
                    ,options: ['Mr.', 'Mrs.', 'Ms.', 'Dr.']
                }
                ,firstName: {
                    title: 'First Name'
                    ,width: '15%'
                }
                ,lastName: {
                    title: 'Last Name'
                    ,width: '15%'
                }
                ,type: {
                    title: 'Type'
                    ,options: ["Witness", "Subject", "Spouse"]
                }
                ,description: {
                    title: 'Description'
                    ,type: 'textarea'
                    ,width: '30%'
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




