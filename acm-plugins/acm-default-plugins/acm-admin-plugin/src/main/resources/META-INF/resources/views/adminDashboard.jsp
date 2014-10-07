<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="adminDashboard.page.title" text="Admin | ACM | Armedia Case Management" /></title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/admin.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminObject.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminEvent.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminPage.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminRule.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminService.js'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminCallback.js'/>"></script>--%>

    <%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>--%>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_datepicker}/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_parsley}/parsley.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_wizard}/jquery.bootstrap.wizard.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_wizard}/demo.js"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>


    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/angular.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/angular-resource.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/moment.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/ng-table.js"></script>


    <!-- Multi-Select Field WYSIWYG -->
    <script type="text/javascript" charset="utf-8" src="<c:url value='/'/>resources/vendors/${vd_chosen}/chosen.js"></script>


    <%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/jquery.min.ccd0edd1.js"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_bootstrap}/js/bootstrap.js"></script>--%>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/dashboardConfigServices.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/dashboardConfig.js"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black"><spring:message code="adminDashboard.page.descShort" text="Dashboard Configuration" /></h3>
                    </div>
                </section>
                <section class="panel panel-default">
                    <div class="wrapper" ng-app="config" >
                        <div ng-controller="DemoCtrl">
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="col-xs-3 b-r"><label>Choose Dashboard Widgets</label>
                                    <select ng-model="selectedWidget" ng-change="indexSelections()" ng-options="w as w for w in allWidgets track by w" name=""  size="10" multiple class="form-control">
                                    </select></div>
                                <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/><button type="submit" class="btn btn-primary btn-sm" data-toggle="tooltip" data-title="Load selection" ng-click="select()"> Go <i class="fa fa-chevron-right"></i></button><br/><br/><br/><br/><br/><br/></div>
                                <div class="col-xs-3 b-r"><label>Not Authorized</label>
                                    <select ng-model="selectedNotAuthorized" ng-options="na as na.name for na in notAuthorized"  name="" size="10" multiple class="form-control">
                                    </select></div>
                                <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/><button class="btn btn-rounded btn-sm" data-toggle="tooltip" data-title="Move Right" ng-click="moveRight()"> <i class="fa fa-angle-double-right"></i></button> <br/>
                                    <button class="btn btn-rounded tn-sm" data-toggle="tooltip" data-title="Move Left" ng-click="moveLeft()"> <i class="fa fa-angle-double-left"></i></button><br/><br/><br/><br/></div>
                                <div class="col-xs-4 b-r"><label>Authorized</label>
                                    <select ng-model="selectedAuthorized" ng-options="a as a.name for a in authorized" size="10" multiple  class="form-control">
                                    </select></div>
                            </div>
                        </div>
                    </div>
                    </div>
                </section>

            <%--<section>--%>
                    <%--<div  class="container" ng-app="config"  style="width:100%">--%>
                    <%--<div ng-controller="DemoCtrl">--%>
                        <%--&lt;%&ndash;<table>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<thead >&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<tr>&ndash;%&gt;--%>
                                <%--&lt;%&ndash;<th width="50%">Widget</th>&ndash;%&gt;--%>
                                <%--&lt;%&ndash;<th width="50%">Permitions</th>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</ thead>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<tbody>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<tr ng-repeat="widget in widgets">&ndash;%&gt;--%>
                                <%--&lt;%&ndash;<td width="50%">{{widget.name}}</td>&ndash;%&gt;--%>
                                <%--&lt;%&ndash;<td width="50%">&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;<select ng-model="myRole" ng-options="r as r.name for r in roles track by r.name" multiple chosen>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;&lt;%&ndash;<option value="">Select role</option>&ndash;%&gt;&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;</select>&ndash;%&gt;--%>
                                <%--&lt;%&ndash;</td>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</tr>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;</tbody>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</table>&ndash;%&gt;--%>
                        <%--<table ng-table class="table table-striped b-t b-light">--%>
                            <%--<thead>--%>
                            <%--<tr>--%>
                                <%--<th class="th-sortable" data-toggle="class">Dashboard Widget<span class="th-sort"></span></th>--%>
                                <%--<th width="1202"  class="th-sortable" data-toggle="class">Permissions--%>
                                    <%--<span class="th-sort">--%>
                                        <%--<i class="fa fa-sort-down text"></i>--%>
                                        <%--<i class="fa fa-sort-up text-active"></i>--%>
                                        <%--<i class="fa fa-sort"></i>--%>
                                    <%--</span>--%>
                                <%--</th>--%>
                                <%--<th class="th-sortable" width="50" data-toggle="class">&nbsp;</th>--%>
                            <%--</tr>--%>
                            <%--</thead>--%>
                            <%--<tbody>--%>
                            <%--<tr ng-repeat="widget in widgets">--%>
                                <%--<td>{{widget.widgetName}}</td>--%>
                                <%--<td>--%>
                                    <%--<select ng-model="widget.widgetAuthorizedRoles" ng-options="r as r.name for r in roles track by r.name" multiple chosen data-placeholder="Choose permissions..." class="form-control">--%>
                                    <%--</select>--%>
                                <%--</td>--%>
                                <%--<td data-title=" ">--%>
                                    <%--<button title="" data-original-title="" class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save" ng-click="save($index)"><i class="fa fa-save"></i>--%>
                                    <%--</button>--%>
                                <%--</td>--%>
                            <%--</tr>--%>
                            <%--</tbody>--%>
                        <%--</table>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</section>--%>
        </section>
    </section>
</section>
</jsp:body>
</t:layout>
<script type="text/javascript">
    var config = {
        '.choose-permissions' : {},
        '.choose-intitiatorFlags' : {},
        '.choose-complaintFlags' : {},
        '.choose-approvers' : {},
        '.choose-collab' : {},
        '.choose-notifications' : {}
    }
    for (var selector in config) {
        $(selector).chosen(config[selector]);
    }
</script>
