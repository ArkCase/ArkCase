<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>

    <script type="text/javascript">
        $(document).ready(function () {
            Dashboard.initialize();
        });
    </script>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/dashboard/dashboardCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/app.plugin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/easypiechart/jquery.easy-pie-chart.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/sparkline/jquery.sparkline.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.tooltip.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.spline.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.pie.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.resize.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/jquery.flot.grow.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_charts}/flot/demo.js"></script>
</jsp:attribute>

<jsp:body>
<section id="content">
<section class="vbox">
<section class="scrollable padder">
<section class="row m-b-md">
    <div class="col-sm-6">
        <h3 class="m-b-xs text-black">${pageDescriptor.descShort}</h3>
        <small>Welcome back, ${acm_user.fullName}</small> </div>
    <div class="col-sm-6 text-right text-left-xs m-t-md">
        <div class="btn-group"> <a class="btn btn-rounded btn-default b-2x dropdown-toggle" data-toggle="dropdown">Add Widgets <span class="caret"></span></a>
            <ul class="dropdown-menu text-left pull-right">
                <li><a href="#">Widget 1</a></li>
                <li><a href="#">Widget 2</a></li>
                <li><a href="#">Widget 3</a></li>
            </ul>
        </div>
    </div>
</section>
<div class="row">
    <div class="col-sm-12">
        <div class="panel b-a">
            <div class="row m-n">
                <div class="col-md-3 b-r"> <a href="complaints.html" class="block padder-v hover"> <span class="i-s i-s-2x pull-left m-r-sm"> <i class="i i-hexagon2 i-s-base text-danger hover-rotate"></i> <i class="i i-notice i-1x text-white"></i> </span> <span class="clear"> <span class="h3 block m-t-xs text-danger">2,000</span> <small class="text-muted text-u-c">New Complaints</small> </span> </a> </div>
                <div class="col-md-3 b-r"> <a href="cases.html" class="block padder-v hover"> <span class="i-s i-s-2x pull-left m-r-sm"> <i class="i i-hexagon2 i-s-base text-success-lt hover-rotate"></i> <i class="i i-folder i-sm text-white"></i> </span> <span class="clear"> <span class="h3 block m-t-xs text-success">150</span> <small class="text-muted text-u-c">New Cases</small> </span> </a> </div>
                <div class="col-md-3 b-r"> <a href="documents.html" class="block padder-v hover"> <span class="i-s i-s-2x pull-left m-r-sm"> <i class="i i-hexagon2 i-s-base text-info hover-rotate"></i> <i class="i i-checkmark i-sm text-white"></i> </span> <span class="clear"> <span class="h3 block m-t-xs text-info">25 <span class="text-sm"></span></span> <small class="text-muted text-u-c">New Tasks</small> </span> </a> </div>
                <div class="col-md-3"> <a href="tasks.html" class="block padder-v hover"> <span class="i-s i-s-2x pull-left m-r-sm"> <i class="i i-hexagon2 i-s-base text-primary hover-rotate"></i> <i class="i i-file i-sm text-white"></i> </span> <span class="clear"> <span class="h3 block m-t-xs text-primary">150</span> <small class="text-muted text-u-c">New Documents</small> </span> </a> </div>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-6">
        <section class="panel b-a">
            <div class="panel-heading b-b">
                <div class="pull-right">
                    <div class="btn-group">
                        <button data-toggle="dropdown" class="btn btn-xs btn-rounded btn-default dropdown-toggle"> <span class="dropdown-label">Filter</span> <span class="caret"></span> </button>
                        <ul class="dropdown-menu dropdown-select">
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 1</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 2</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 3</a></li>
                        </ul>
                    </div>
                </div>
                <a href="complaints.html" class="font-bold">My Complaints</a> </div>
            <div class="panel-body max-200 no-padder">
                <table class="table table-striped th-sortable table-hover">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Priority</th>
                        <th>Due</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="odd gradeA">
                        <td>[ID]</td>
                        <td>[Title]</td>
                        <td>[Priority]</td>
                        <td>[Due]</td>
                        <td>[Status]</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
    <div class="col-md-6">
        <section class="panel b-a">
            <div class="panel-heading b-b">
                <div class="pull-right">
                    <div class="btn-group">
                        <button data-toggle="dropdown" class="btn btn-xs btn-rounded btn-default dropdown-toggle"> <span class="dropdown-label">Filter</span> <span class="caret"></span> </button>
                        <ul class="dropdown-menu dropdown-select">
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 1</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 2</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 3</a></li>
                        </ul>
                    </div>
                </div>
                <a href="cases.html" class="font-bold">My Cases</a> </div>
            <div class="panel-body max-200 no-padder">
                <table class="table table-striped th-sortable table-hover">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Priority</th>
                        <th>Due</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="odd gradeA">
                        <td>[ID]</td>
                        <td>[Title]</td>
                        <td>[Priority]</td>
                        <td>[Due]</td>
                        <td>[Status]</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</div>
<div class="row">
    <div class="col-md-6">
        <section class="panel b-a">
            <div class="panel-heading b-b">
                <div class="pull-right">
                    <div class="btn-group">
                        <button data-toggle="dropdown" class="btn btn-xs btn-rounded btn-default dropdown-toggle"> <span class="dropdown-label">Filter</span> <span class="caret"></span> </button>
                        <ul class="dropdown-menu dropdown-select">
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 1</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 2</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 3</a></li>
                        </ul>
                    </div>
                </div>
                <a href="documents.html" class="font-bold">My Documents</a> </div>
            <div class="panel-body max-200 no-padder">
                <table class="table table-striped th-sortable table-hover">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Priority</th>
                        <th>Due</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr class="odd gradeA">
                        <td>[ID]</td>
                        <td>[Title]</td>
                        <td>[Priority]</td>
                        <td>[Due]</td>
                        <td>[Status]</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
    <div class="col-md-6">
        <section class="panel b-a">
            <div class="panel-heading b-b">
                <div class="pull-right">
                    <div class="btn-group">
                        <button data-toggle="dropdown" class="btn btn-xs btn-rounded btn-default dropdown-toggle"> <span class="dropdown-label">Filter</span> <span class="caret"></span> </button>
                        <ul class="dropdown-menu dropdown-select">
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 1</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 2</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Filter 3</a></li>
                        </ul>
                    </div>
                </div>
                <a href="tasks.html" class="font-bold">My Tasks</a> </div>
            <div class="panel-body max-200 no-padder">
                <table class="table table-striped th-sortable table-hover" id="tabMyTasks">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Priority</th>
                        <th>Due</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%--<tr class="odd gradeA">--%>
                        <%--<td>[ID]</td>--%>
                        <%--<td>[Title]</td>--%>
                        <%--<td>[Priority]</td>--%>
                        <%--<td>[Due]</td>--%>
                        <%--<td>[Status]</td>--%>
                    <%--</tr>--%>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</div>
<div class="row bg-light dk m-b">
    <div class="col-md-6 dker">
        <section>
            <header class="font-bold padder-v">
                <div class="pull-right">
                    <div class="btn-group">
                        <button data-toggle="dropdown" class="btn btn-xs btn-rounded btn-default dropdown-toggle"> <span class="dropdown-label">Week</span> <span class="caret"></span> </button>
                        <ul class="dropdown-menu dropdown-select">
                            <li><a href="#">
                                <input type="radio" name="b">
                                Month</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Week</a></li>
                            <li><a href="#">
                                <input type="radio" name="b">
                                Day</a></li>
                        </ul>
                    </div>
                </div>
                Documents In Workflow </header>
            <div class="panel-body">
                <div id="flot-sp1ine" style="height:210px"></div>
            </div>
            <div class="row text-center no-gutter">
                <div class="col-xs-3"> <span class="h4 font-bold m-t block">5,860</span> <small class="text-muted m-b block">Submitted</small> </div>
                <div class="col-xs-3"> <span class="h4 font-bold m-t block">10,450</span> <small class="text-muted m-b block">Pending Approval</small> </div>
                <div class="col-xs-3"> <span class="h4 font-bold m-t block">21,230</span> <small class="text-muted m-b block">Rejected</small> </div>
                <div class="col-xs-3"> <span class="h4 font-bold m-t block">7,230</span> <small class="text-muted m-b block">Approved</small> </div>
            </div>
        </section>
    </div>
    <div class="col-md-6">
        <header class="font-bold padder-v">
            <div class="btn-group pull-right">
                <button data-toggle="dropdown" class="btn btn-xs btn-rounded btn-default dropdown-toggle"> <span class="dropdown-label">Last 24 Hours</span> <span class="caret"></span> </button>
                <ul class="dropdown-menu dropdown-select">
                    <li><a href="#">
                        <input type="radio" name="a">
                        Today</a></li>
                    <li><a href="#">
                        <input type="radio" name="a">
                        Yesterday</a></li>
                    <li><a href="#">
                        <input type="radio" name="a">
                        Last 24 Hours</a></li>
                    <li><a href="#">
                        <input type="radio" name="a">
                        Last 7 Days</a></li>
                    <li><a href="#">
                        <input type="radio" name="a">
                        Last 30 days</a></li>
                    <li><a href="#">
                        <input type="radio" name="a">
                        Last Month</a></li>
                    <li><a href="#">
                        <input type="radio" name="a">
                        All Time</a></li>
                </ul>
            </div>
            Team Workload </header>
        <div class="panel-body flot-legend">
            <div id="flot-pie-donut"  style="height:240px"></div>
        </div>
    </div>
</div>
<p class="h4">More Content...</p>
</section>
<footer class="footer bg-white b-t b-light">
    <p>Powered by Armedia Case Management 3.0.</p>
</footer>
</section>
</section>
</jsp:body>
</t:layout>


