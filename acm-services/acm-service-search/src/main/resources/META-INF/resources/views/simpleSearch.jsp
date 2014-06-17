<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>

    <script type="text/javascript">
    $(document).ready(function () {
        SimpleSearch.initialize();
    });
</script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearch.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/simple/simpleSearchCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/app.plugin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/easypiechart/jquery.easy-pie-chart.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/sparkline/jquery.sparkline.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.tooltip.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.spline.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.pie.min.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.resize.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.grow.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/demo.js"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<section class="scrollable padder">
<section class="row m-b-md">
    <div class="col-sm-12">
        <h3 class="m-b-xs text-black">${pageDescriptor.descShort}</h3>
    </div>
</section>

<div class="row">
    <%--<div class="col-md-6">--%>
        <%--<section class="panel b-a">--%>
            <%--<div class="panel-body max-200 no-padder">--%>
                <table class="table table-striped th-sortable table-hover" id="tabMyTasks">
                    <thead>
                    <tr>
                        <%--<th>ID</th>--%>
                        <%--<th>Title</th>--%>
                        <%--<th>Priority</th>--%>
                        <%--<th>Due</th>--%>
                        <%--<th>Status</th>--%>
                    </tr>
                    </thead>
                    <tbody>
                    <!--
                    <tr class="odd gradeA">
                        <td>[ID]</td>
                        <td>[Title]</td>
                        <td>[Priority]</td>
                        <td>[Due]</td>
                        <td>[Status]</td>
                    </tr>
                    -->
                    </tbody>
                </table>
            <%--</div>--%>
        <%--</section>--%>
    <%--</div>--%>

</div>

</section>
<footer class="footer bg-white b-t b-light">
    <p>Powered by Armedia Case Management 3.0.</p>
</footer>
</section>
</section>
</jsp:body>
</t:layout>


