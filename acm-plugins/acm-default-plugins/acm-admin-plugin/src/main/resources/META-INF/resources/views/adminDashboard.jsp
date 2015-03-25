<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
    <jsp:attribute name="endOfHead">
        <title><spring:message code="adminDashboard.page.title" text="Admin | ACM | Armedia Case Management" /></title>
    </jsp:attribute>


    <jsp:attribute name="endOfBody">
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

        <script type="text/javascript" src="<c:url value='/'/>resources/js/admin/dashboard/angular/dashboardConfigServices.js"></script>
        <script type="text/javascript" src="<c:url value='/'/>resources/js/admin/dashboard/angular/dashboardConfig.js"></script>

        <%--Fancy Tree--%>
        <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
        <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/${js_fancytree}"></script>
        <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/${js_contextmenu}"></script>

    </jsp:attribute>

    <jsp:body>
        <section class="vbox">
            <section class="scrollable">
                <section class="hbox stretch"><!-- /.aside -->

                    <aside class="aside-lg bg-light lt">
                        <section class="vbox animated fadeInLeft">
                            <section class="scrollable">

                                <header class="dk header">
                                    <h3 class="m-b-xs text-black pull-left"><spring:message code="admin.page.descLong" text="Administration" /></h3>
                                </header>

                                <div class="row m-b">
                                    <div class="col-sm-12">
                                        <div id="tree"></div>
                                    </div>
                                </div>

                            </section>
                        </section>
                    </aside>

                    <%--   Dashboard controls    --%>

                    <aside id="email-content" class="bg-light lter">
                        <section class="vbox">
                            <section class="scrollable padder">

                                <%--Title--%>

                                <section class="row m-b-md">
                                    <div class="col-sm-12">
                                        <h3 class="m-b-xs text-black"><spring:message code="adminDashboard.page.descShort" text="Dashboard Configuration" /></h3>
                                    </div>
                                </section>

                                <section class="panel panel-default">
                                    <div class="wrapper" ng-app="config">
                                        <div ng-controller="DemoCtrl">
                                            <div class="row">
                                                <div class="col-xs-12">
                                                    <div class="col-xs-3 b-r"><label>Choose Dashboard Widgets</label>
                                                        <select ng-model="selectedWidget" ng-change="indexSelections()" ng-options="w as w.name for w in allWidgets track by w.name" name="" size="10" class="form-control">
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
                            </section>
                        </section>
                    </aside>
                </section>
            </section>
        </section>
    </jsp:body>
</t:layout>
