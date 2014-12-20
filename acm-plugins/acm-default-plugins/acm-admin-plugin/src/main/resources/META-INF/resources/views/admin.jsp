<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="admin.page.title" text="Admin | ACM | Armedia Case Management" /></title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/admin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>

    <!-- Fancy Tree -->
    <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/${js_fancytree}"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/${js_contextmenu}"></script>


    <!-- Dashboard -->
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/angular.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/angular-resource.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/moment.min.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_angular}/js/ng-table.js"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/js/admin/dashboard/angular/dashboardConfigServices.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/js/admin/dashboard/angular/dashboardConfig.js"></script>



    /////////////////////////////////////////////////////////////////////
    <style>
        table.fancytree-ext-table {
            width: 100%;
            outline: 0;
        }

        table.fancytree-ext-table tbody tr td {
            border: 0px;
        }
    </style>
    //////////////////////////////////////////////////////////////////////
</jsp:attribute>

    <jsp:body>

        <section id="content">
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

                        <aside id="email-content" class="bg-light lter">

                            <section class="vbox">
                                <header class="header bg-white b-b clearfix">
                                    <div class="row m-t-sm"></div>
                                </header>

                                <section class="scrollable wrapper w-f">

                                    <div>
                                        <div class="wrapper">

                                            <%--Main Page table--%>
                                            <div class="row" id="tabMainPage">
                                                <h3><i class="fa fa-long-arrow-left"></i> Configure settings in the application.</h3>
                                            </div>

                                            <%--Blank table--%>
                                            <div class="row" id="tabBlank" style="display:none;">
                                            </div>

                                            <%--Dashboard Control Table--%>
                                            <%--<div class="panel panel-default">--%>
                                                <div class="row" id="tabDashboard" ng-app="config" style="display:none;">

                                                <%--<div id = "tabDashboard" class="wrapper" ng-app="config" style="display:none;">--%>
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
                                            <%--</div>--%>


                                            <%--JTable - Access Control Policy--%>
                                            <div class="row" id="tabACP" style="display:none;">
                                                <div class="col-md-12">
                                                    <section class="panel panel-default">
                                                        <div id="divACP" style="width:100%"></div>
                                                    </section>
                                                </div>
                                            </div>

                                                    <%--JTable - Access Control Policy--%>
                                            <div class="row" id="tabCorrespondence" style="display:none;">
                                                <div class="col-md-12">
                                                    <section class="panel panel-default">
                                                        <div id="divCorrespondence" style="width:100%">
                                                            <form id="formAddNewTemplate" style="display:none;">
                                                                <input id="addNewTemplate" type="file" name="files[]" multiple/>
                                                            </form>
                                                        </div>
                                                    </section>
                                                </div>
                                            </div>



                                            <%--Reports Configuration Table--%>
                                            <div class="row" id="tabReports" style="display:none;">
                                                <div class="col-xs-12">
                                                    <div class="col-xs-3 b-r"><label>Choose Reports</label>
                                                        <select name=""  size="10" multiple class="form-control">
                                                            <option>Case Summary Report</option>
                                                            <option>Complaint Report</option>
                                                            <%--<option>Report 3</option>
                                                            <option>Report 4</option>--%>
                                                        </select></div>
                                                    <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/><button class="btn btn-primary btn-sm" data-toggle="tooltip" data-title="Load selection" onclick="save()"> Go <i class="fa fa-chevron-right"></i></button><br/><br/><br/><br/><br/><br/></div>
                                                    <div class="col-xs-3 b-r"><label>Not Authorized</label>
                                                        <select name="" size="10" multiple class="form-control">
                                                            <%--<option> ROLE_ADMINISTRATOR</option>
                                                            <option>ROLE_INVESTIGATOR_SUPERVISOR</option>
                                                            <option>ROLE_INVESTIGATOR_SUPERVISOR</option>
                                                            <option>ROLE_ANALYST</option>
                                                            <option>ACM_ANALYST_DEV</option>
                                                            <option>ACM_ADMINISTRATOR_DEV</option>
                                                            <option>ACM_SUPERVISOR_DEV</option>
                                                            <option>ACM_SUPERVISOR_DEV</option>
                                                            <option>ACM_INVESTIGATOR_DEV</option>--%>
                                                        </select></div>
                                                    <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/><button class="btn btn-rounded btn-sm" data-toggle="tooltip" data-title="Move Right" onclick="save()"> <i class="fa fa-angle-double-right"></i></button> <br/>
                                                        <button class="btn btn-rounded tn-sm" data-toggle="tooltip" data-title="Move Left" onclick="save()"> <i class="fa fa-angle-double-left"></i></button><br/><br/><br/><br/></div>
                                                    <div class="col-xs-4 b-r"><label>Authorized</label>
                                                        <select name=""  size="10" multiple  class="form-control">
                                                            <%--<option>ROLE_INVESTIGATOR</option>--%>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>

                                            <%--JTable - Reports--%>
                                            <%--<div class="row" id="tabReports" style="display:none;">
                                                <div class="col-md-12">
                                                    <section class="panel panel-default">
                                                        <div id="divRPT" style="width:100%"></div>
                                                    </section>
                                                </div>
                                            </div>--%>

                                        </div>
                                    </div>
                                </section>
                            </section>
                        </aside>

                    </section>
                </section>
            </section>
        </section>
    </jsp:body>
</t:layout>


