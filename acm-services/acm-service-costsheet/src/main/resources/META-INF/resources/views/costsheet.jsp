<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
	<jsp:attribute name="endOfHead">
	    <title><spring:message code="costsheet.page.title" text="Cost Tracking | ACM | ArkCase" /></title>
        <div id="detailData" itemscope="true" style="display: none">
            <span itemprop="resourceNamespace">costsheet</span>
            <span itemprop="newCostsheetFormUrl">${newCostsheetFormUrl}</span>
            <span itemprop="objType">COSTSHEET</span>
            <span itemprop="objId">${objId}</span>
            <span itemprop="treeSort">${treeSort}</span>
        </div>
	</jsp:attribute>

    <jsp:attribute name="endOfBody">
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNav.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavService.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavModel.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavView.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavController.js'/>"></script>


        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheet.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetModel.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetService.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetView.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/resources/js/costsheet/costsheetController.js'/>"></script>

        <!-- Summernote WYSIWYG -->
        <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_summernote}/summernote.css'/>" type="text/css"/>
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_summernote}/${js_summernote}'/>"></script>

        <!-- JTable -->
        <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
        <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>

        <!-- Fancy Tree -->
        <link href="<c:url value='/resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css'/>" rel="stylesheet">
        <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree}'/>"></script>
        <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_table}'/>"></script>
        <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>

        <!-- X-Editable -->
        <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
        <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>

    </jsp:attribute>

    <jsp:body>
        <section class="vbox">
            <section class="scrollable">
                <section class="hbox stretch"><!-- /.aside -->

                    <aside class="aside-lg bg-light lt">
                        <section class="vbox animated fadeInLeft">
                            <section class="scrollable">
                                <header class="dk header">
                                    <h3 class="m-b-xs text-black pull-left" data-i18n="costsheet:title">Cost Tracking</h3>

                                    <div class="btn-group inline select pull-right">
                                        <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span> </button>
                                        <ul class="dropdown-menu text-left text-sm" id="ulSort"></ul>
                                    </div>
                                    <div class="btn-group select pull-right">
                                        <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                                        <ul class="dropdown-menu text-left text-sm" id="ulFilter"></ul>
                                    </div>
                                </header>

                                <div class="wrapper">
                                    <div class="input-group">
                                    <label for="searchQuery" style="display:none;'">Search Costsheets</label>
                                        <input type="text" class="input-sm form-control" id="searchQuery" data-i18n="[placeholder]costsheet:label.search" placeholder="Search">
                                        <span class="input-group-btn">
                                            <button class="btn btn-sm btn-default" type="button" data-i18n="costsheet:button.go">Go!</button>
                                        </span>
                                    </div>
                                </div>

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
                            <section class="scrollable">
                                <div class="wrapper dk  clearfix">
                                    <div class="row" id="tabTop"  style="display:none;">
                                        <div class="wrapper dk  clearfix" id="divParentDetail" style="display:none;">
                                            <div class="row">
                                                <div class="col-xs-12">
                                                    <div class="">
                                                        <div class=" clearfix">
                                                            <div class="col-xs-4 b-r">
                                                                <div class="h4 font-bold"><a href="#" id="parentObjTitle" data-type="text" data-pk="1" data-title="Title" title="Title"></a></div>
                                                                <small class="text-muted"><a href="#" id="parentObjNumber" ></a></small></div>
                                                            <div class="col-xs-2 b-r">
                                                                <div class="h4 font-bold"><a href="#" id="parentObjIncidentDate" data-type="date" data-pk="1" data-title="Incident Date" title="Incident Date"></a></div>
                                                                <small class="text-muted">Incident Date</small></div>
                                                            <div class="col-xs-1 b-r">
                                                                <div class="h4 font-bold"><a href="#" id="parentObjPriority" data-type="select" data-pk="1" data-title="Priority" title="Priority"></a></div>
                                                                <small class="text-muted">Priority</small></div>
                                                            <div class="col-xs-2 b-r">
                                                                <div class="h4 font-bold"><a href="#" id="parentObjAssignee" data-type="select" data-pk="1"  data-title="Assignee" title="Assignee"></a></div>
                                                                <small class="text-muted">Assigned To</small></div>
                                                            <div class="col-xs-2 b-r">
                                                                <div class="h4 font-bold"><a href="#" id="parentObjSubjectType" data-type="select" data-pk="1"  data-title="Subject Type" title="Subject Type"></a></div>
                                                                <small class="text-muted">Subject Type</small></div>
                                                            <div class="col-xs-1">
                                                                <div class="h4 font-bold"><a href="#" id="parentObjStatus" ></a></div>
                                                                <small class="text-muted">State</small></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="row" id="tabTopBlank">
                                        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span data-i18n="costsheet:msg.no-costsheet-selected">(No costsheet is selected)</span></p>
                                    </div>
                                </div>

                                <div>
                                    <div class="wrapper">

                                        <div class="row" id="tabAction">
                                            <div class="col-md-12">
                                                <div class="pull-right inline">
                                                    <div class="btn-group">
                                                        <button class="btn btn-default btn-sm" id = "btnNewCostsheetForm" data-i18n="costsheet:button.new-costsheet"><i class="fa fa-pencil"></i> New Costsheet </button>
                                                        <button class="btn btn-default btn-sm" id = "btnEditCostsheetForm" data-i18n="costsheet:button.edit-costsheet"><i class="fa fa-save"></i> Edit Costsheet </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <hr/>

                                        <div class="row" id="tabBlank" style="display:none;">
                                        </div>

                                        <div class="row" id="tabDetail" style="display:none;">
                                            <div class="col-md-12">
                                                <section class="panel b-a ">
                                                    <div class="panel-heading b-b bg-info">
                                                        <ul class="nav nav-pills pull-right">
                                                            <li>
                                                                <div class="btn-group padder-v2">
                                                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]costsheet:label.edit" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]costsheet:label.save" data-title="Save"><i class="fa fa-save"></i></button>
                                                                    <ul class="dropdown-menu pull-right">
                                                                        <li><a href="#" data-i18n="costsheet:label.other-menu-items">Other menu items</a></li>
                                                                    </ul>
                                                                </div>
                                                            </li>
                                                            <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                        </ul>
                                                        </span> <a href="#" class="font-bold" data-i18n="costsheet:label.details" title="Edit Details">Details</a> </div>
                                                    <div class="panel-body">
                                                        <div class="divDetail"></div>
                                                    </div>
                                                </section>
                                            </div>
                                        </div>

                                        <div class="row" id="tabPerson" style="display:none;">
                                            <div class="col-md-12">
                                                <section class="panel b-a">
                                                    <div id="divPerson" style="width:100%"></div>
                                                </section>
                                            </div>
                                        </div>

                                        <div class="row" id="tabCostSummary" style="display:none;">
                                            <div class="col-md-12">
                                                <section class="panel b-a">
                                                    <div id="divCostSummary" style="width:100%"></div>
                                                </section>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </section>
                        </section>
                    </aside>


                </section>
            </section>
        </section>
    </jsp:body>
</t:layout>