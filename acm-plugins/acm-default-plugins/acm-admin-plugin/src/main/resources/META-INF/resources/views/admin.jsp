<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="admin.page.title" text="Admin | ACM | Armedia Case Management" /></title>
    <%--<div id="detailData" itemscope="true" style="display: none">
        <span itemprop="helpUrl">${helpUrl}</span>
    </div>--%>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/admin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>

    <!-- Fancy Tree -->
    <link href="<c:url value='/resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css'/>" rel="stylesheet">
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/jquery.fancytree.table.js'/>"></script>



    <!-- Dashboard -->
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/angular.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/angular-resource.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/moment.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_angular}/js/ng-table.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/admin/dashboard/angular/dashboardConfigServices.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/dashboard/angular/dashboardConfig.js'/>"></script>



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

    <style>

        #divFacets{
            overflow-y:auto;
            overflow-x:hidden;
            width: 20%;
            float:left;


            min-height:100px;
        }

        #divMembersResults
        {
            float:left;
            width:80%;
            min-height:100px;
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
                                    <div class="pull-left inline">
                                        <div class="btn-group">
                                            <!-- button group -->
                                            <button class="btn btn-default btn-sm" id="btnCreateAdHoc" title="Create Ad-Hoc Group" data-toggle="modal" data-target="#createAdHoc" style="display:none;">
                                                Create Ad-Hoc Group
                                            </button>
                                        </div>
                                    </div>
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


                                                    <%--JTable - Correspondence--%>
                                            <div class="row" id="tabCorrespondenceTemplates" style="display:none;">
                                                <div class="col-md-12">
                                                    <section class="panel panel-default">
                                                        <div id="divCorrespondenceTemplates" style="width:100%">
                                                            <form id="formAddNewTemplate" style="display:none;">
                                                                <input id="addNewTemplate" type="file" name="files[]" multiple/>
                                                            </form>
                                                        </div>
                                                    </section>
                                                </div>
                                            </div>

											<%--   Functional Access controls    --%>
 											<div class="row" id="tabFunctoinalAccessControl" style="display:none;">
				                                <%--Title--%>
				                                <section class="row m-b-md">
				                                    <div class="col-sm-12">
				                                        <h3 class="m-b-xs text-black"><spring:message code="adminFunctionalAccess.page.descShort" text="Functional Access Configuration" /></h3>
				                                    </div>
				                                </section>
				
				                                <section class="panel panel-default">
				                                    <div class="wrapper">
				                                        <div class="row">
				                                            <div class="col-xs-12">
				                                                <div class="col-xs-3 b-r"><label>Choose Application Role</label>
				                                                    <select id="selectRoles" size="10" class="form-control">
				                                                    </select>
				                                                </div>
				                                                <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/><button type="submit" id="btnGo" class="btn btn-primary btn-sm" data-toggle="tooltip" data-title="Load selection"> Go <i class="fa fa-chevron-right"></i></button><br/><br/><br/><br/><br/><br/></div>
				                                                <div class="col-xs-3 b-r"><label>Not Authorized</label>
				                                                    <select id="selectNotAuthorized" size="10" multiple class="form-control">
				                                                    </select>
				                                                </div>
				                                                <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/>
				                                                    <button id="btnMoveRight" class="btn btn-rounded btn-sm" data-toggle="tooltip" data-title="Move Right"> <i class="fa fa-angle-double-right"></i></button> <br/>
				                                                    <button id="btnMoveLeft" class="btn btn-rounded tn-sm" data-toggle="tooltip" data-title="Move Left"> <i class="fa fa-angle-double-left"></i></button><br/><br/><br/><br/>
				                                                 </div>
				                                                <div class="col-xs-4 b-r"><label>Authorized</label>
				                                                    <select id="selectAuthorized" size="10" multiple  class="form-control">
				                                                    </select>
				                                                </div>
				                                            </div>
				                                        </div>
				                                    </div>
				                                </section>
											</div>

                                            <div class="row" id="tOrganization" style="display:none;">
                                                <%--<div class="col-md-12">
                                                    <section class="panel panel-default">--%>

                                                    <table id="treeOrganization">
                                                            <thead>
                                                            <tr>  <th></th> <th></th><th>Name </th> <th> Type </th> <th>Supervisor Name</th> <th></th><th>   Actions </th></tr>

                                                            <tr> <th> </th> <th></th> <th></th> <th></th> <th></th> <th></th><th>  </th></tr>
                                                            </thead>
                                                            <tbody>
                                                            </tbody>
                                                        </table>

                                                        <%--<div id="divTreeOrganization" style="width:100%"></div><th>Location</th>
                                                    </section>
                                                </div>--%>
                                            </div>

                                            <%--Reports Configuration Table--%>
                                            <div class="row" id="tabReports" style="display:none;">

                                                    <%--Title--%>
                                                    <section class="row m-b-md">
                                                        <div class="col-sm-12">
                                                            <h3 class="m-b-xs text-black">Reports Configuration</h3>
                                                        </div>
                                                    </section>

                                                    <section class="panel panel-default">
                                                        <div class="wrapper">
                                                            <div class="row">
                                                                <div class="col-xs-12">
                                                                    <div class="col-xs-3 b-r"><label>Choose Report</label>
                                                                        <select id="selectReport" size="10" class="form-control">
                                                                        </select>
                                                                    </div>
                                                                    <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/><button type="submit" id="btnSelectReport" class="btn btn-primary btn-sm" data-toggle="tooltip" data-title="Load selection"> Go <i class="fa fa-chevron-right"></i></button><br/><br/><br/><br/><br/><br/></div>
                                                                    <div class="col-xs-3 b-r"><label>Not Authorized</label>
                                                                        <select id="selectNotAuthorizedReport" size="10" multiple class="form-control">
                                                                        </select>
                                                                    </div>
                                                                    <div class="col-xs-1 b-r"><br/><br/><br/><br/><br/>
                                                                        <button id="btnAuthorize" class="btn btn-rounded btn-sm" data-toggle="tooltip" data-title="Authorize"> <i class="fa fa-angle-double-right"></i></button> <br/>
                                                                        <button id="btnUnauthorize" class="btn btn-rounded tn-sm" data-toggle="tooltip" data-title="Unauthorize"> <i class="fa fa-angle-double-left"></i></button><br/><br/><br/><br/>
                                                                    </div>
                                                                    <div class="col-xs-4 b-r"><label>Authorized</label>
                                                                        <select id="selectAuthorizedReport" size="10" multiple  class="form-control">
                                                                        </select>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </section>
                                            </div>

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

<div class="modal fade" id="createAdHoc" tabindex="-1" role="dialog" aria-labelledby="modalLabelCreateAdHoc" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="modalLabelCreateAdHoc" >Add Ad-Hoc Group</h4>

            </div>
            <div class="modal-body">
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <label>Name</label>
                            <input type="text" class="input-sm form-control" id="groupName" placeholder="Enter group name">
                        </div>

                        <div class="col-sm-12">
                            <label>Description</label>
                            <textarea class="form-control" id='groupDescription' placeholder="Enter description"></textarea>
                        </div>
                    </div>
                </section>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="btnAddAdHocGroup">Add Ad-Hoc Group</button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="addPeople" tabindex="-1" role="dialog" aria-labelledby="modalLabelPeople" aria-hidden="true">
    <div class="modal-dialog"  style="height:540px; width:1030px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="modalLabelPeople" ></h4>

            </div>

            <div class="modal-body">
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-12">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" id="findMember" placeholder="Search people.." >
                                <span class="input-group-btn">
                                    <button class="btn btn-sm btn-default" id="btnFindMembers" type="button">Go!</button>
                                </span>
                                <%--<span class="input-group-btn">
                                    <button class="btn btn-sm btn-default" id="btnFindMembers" type="button"><i class="fa fa-search"></i></button>
                                  </span>--%>
                            </div>
                        <br>

                        <div class="form-group" id="divFacets">Available Facets
                        <br />
                        </div>



                        <div id="divMembersResults">
                            <%--JTable - People Picker--%>
                            <div class="row" id="tabMembers">
                                <div class="col-md-12">
                                    <%--<section class="panel panel-default">--%>
                                        <div id="divMembers" style="width:100%"></div>
                                    <%--</section>--%>
                                </div>
                            </div>
                        </div>
                    </div>
                    </div>

                </section>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="btnAddPeople" >Add</button>
            </div>
        </div>
    </div>
</div>




