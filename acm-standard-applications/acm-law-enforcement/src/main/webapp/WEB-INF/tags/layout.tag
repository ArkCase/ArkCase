<%@tag description="ACM Page Layout template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="endOfHead" fragment="true" %>
<%@attribute name="endOfBody" fragment="true" %>

<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@include file="/WEB-INF/tagf/global.tagf" %>

    <div id="acmData" itemscope="true" style="display: none">
        <span itemprop="contextPath"><%=request.getContextPath()%></span>
        <span itemprop="userName">${sessionScope.acm_username}</span>
    </div>
    <jsp:invoke fragment="endOfHead"/>
</head>
<body class="">
<section class="vbox">
    <%@include file="/WEB-INF/tagf/topbar.tagf"%>

    <section>
        <section class="hbox stretch">
            <%@include file="/WEB-INF/tagf/sidebar.tagf"%>

            <jsp:doBody/>
        </section>
    </section>
    <%@include file="/WEB-INF/tagf/footer.tagf"%>
</section>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acm.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmDialog.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmAjax.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmDispatcher.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/core/acmValidation.js"></script>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbar.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarPage.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarRule.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/topbar/topbarCallback.js"></script>

<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebar.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarObject.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarEvent.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarPage.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarRule.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarService.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_acm}/sidebar/sidebarCallback.js"></script>


<%--<script type="text/javascript" src="//underscorejs.org/underscore-min.js"></script>--%>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_underscore}/underscore-min.js"></script>
<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.js"></script>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/typeahead.jquery.js"></script>--%>
<%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_typeahead}/bloodhound.js"></script>--%>

<script type="text/javascript">
    $(document).ready(function () {
        Acm.initialize();
        Topbar.initialize();
        Sidebar.initialize();

 /*
        var ctrObjs = {};
        var ctrtitles = [];

        //get the data to populate the typeahead (plus an id value)
        var throttledRequest = _.debounce(function(query, process){
            //get the data to populate the typeahead (plus an id value)
            $.ajax({
                url: 'resources/ctrs.json'
                ,cache: false
                ,success: function(data){
                    //reset these ctrs every time the user searches
                    //because we're potentially getting entirely different results from the api
                    ctrObjs = {};
                    ctrtitles = [];

                    //Using underscore.js for a functional approach at looping over the returned data.
                    _.each( data, function(item, ix, list){

                        //for each iteration of this loop the "item" argument contains
                        //1 ctr object from the array in our json, such as:
                        // { "id":7, "title":"Pierce Brosnan" }

                        //add the label to the display array
                        ctrtitles.push( item.title );

                        //also store a hashmap so that when bootstrap gives us the selected
                        //title we can map that back to an id value
                        ctrObjs[ item.title ] = item;
                    });

                    //send the array of results to bootstrap for display
                    process( ctrtitles );
                }
            });
        }, 300);


        $(".typeahead").typeahead({
            source: function ( query, process ) {

                //here we pass the query (search) and process callback arguments to the throttled function
                throttledRequest( query, process );

            }
            ,highlighter: function( item ){
                var ctr = ctrObjs[ item ];
                var icon = "";

                if (ctr.type == "Complaint") {
                    icon = '<i class="i i-notice i-2x"></i>';
                } else if (ctr.type == "Case") {
                    icon = '<i class="i i-folder i-2x"></i>';
                } else if (ctr.type == "Task") {
                    icon = '<i class="i i-checkmark i-2x"></i>';
                } else if (ctr.type == "Document") {
                    icon = '<i class="i i-file i-2x"></i>';
                } else {
                    icon = '<i class="i i-circle i-2x"></i>';
                }

                return '<div class="ctr">'
                        +'<div class="icontype">' + icon + '</div>'
                        +'<div class="title">' + ctr.title + '</div>'
                        +'<div class="identifier">' + ctr.identifier + ' ('+ ctr.type + ')' + '</div>'
                        +'<div class="author">By ' + ctr.author  + '  on '+ ctr.date + '</div>'
                        +'</div>';
            }
            , updater: function ( selectedtitle ) {

                //note that the "selectedtitle" has nothing to do with the markup provided
                //by the highlighter function. It corresponds to the array of titles
                //that we sent from the source function.

                //save the id value into the hidden field
                $( "#ctrId" ).val( ctrObjs[ selectedtitle ].id );

                //return the string you want to go into the textbox (the title)
                return selectedtitle;
            }
        });
*/
    });
</script>
<jsp:invoke fragment="endOfBody"/>
</body>
</html>