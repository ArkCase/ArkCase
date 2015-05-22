<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:noNav>
<jsp:attribute name="endOfHead">
    <title><spring:message code="caseFile.page.title" text="Case Files | ACM | Ark Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">casefile,search</span>
        <span itemprop="parentCasefileId">${parentCasefileId}</span>
        <span itemprop="fileTypes">${fileTypes}</span>
        <span itemprop="arkcaseUrl">${arkcaseUrl}</span>
        <span itemprop="arkcasePort">${arkcasePort}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">

    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTree.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/casefileSplit/caseFileSplit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefileSplit/caseFileSplitModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefileSplit/caseFileSplitView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefileSplit/caseFileSplitController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefileSplit/caseFileSplitService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_summernote}/summernote.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_summernote}/${js_summernote}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>

    <link href="<c:url value='/resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css'/>" rel="stylesheet">
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_table}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_gridnav}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_edit}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_dnd}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>


    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>

    <script src="<c:url value='/resources/vendors/${vd_wizard}/${js_wizard_bootstrap}'/>"></script>


<style>
    table.fancytree-ext-table {
        width: 100%;
        outline: 0;
    }

    table.fancytree-ext-table tbody tr td {
        border: 0px;
    }

</style>

<%@include file="/resources/include/dlgDocTree.jspf" %>
</jsp:attribute>

    <jsp:body>
        <section class="vbox">
            <section>
                <section class="hbox stretch">
                    <!-- .aside --><!-- /.aside -->

                    <section id="content">
                        <header class="header bg-white b-b clearfix">
                            <div class="row m-t-sm">
                                <div class="col-sm-12 m-b-xs">
                                    <div class="pull-right inline">
                                        <div class="btn-group">
                                            <button class="btn btn-default btn-sm" onClick="window.close();"><i class="fa fa-times"></i></button>
                                        </div>
                                    </div>
                                    <h4 class="m-n"> Split Case</h4>
                                </div>
                            </div>
                        </header>
                        <section class="hbox stretch">
                            <aside>
                                <section class="vbox">
                                    <section class="scrollable">
                                        <div class="wrapper dk  clearfix">
                                            <div class="row">
                                                <div class="col-xs-6  b-r">
                                                    <h4><a href="#" id="caseTitle" data-type="text" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-case-title"  data-title="Enter Case Title"></a><a href="#" id="status" ></a></h4>
                                                </div>
                                                <div class="col-xs-6  b-r text-right">
                                                    <div class="h4 font-bold"><a href="#" id="originalCaseID" ></a></div>
                                                    <small class="text-muted">Original ID</small>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-subject-type" data-title="Enter Subject Type"></a></div>
                                                    <small class="text-muted" data-18n="casefile:header.labels.case-type">Case Type</small> </div>
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-incident-date" data-title="Enter Incident Date"></a></div>
                                                    <small class="text-muted" data-18n="casefile:header.labels.create-date">Create Date</small></div>
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-priority;priority-high" data-title="Enter Priority"></a></div>
                                                    <small class="text-muted" data-18n="casefile:header.labels.priority">Priority</small>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-xs-4 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-assignee" data-title="Enter Assignee"></a></div>
                                                    <small class="text-muted" data-18n="casefile:header.labels.assigned-to">Assigned To</small>
                                                </div>
                                                <div class="col-xs-4  b-r">
                                                    <div class="h4 font-bold"><a href="#" id="group" data-type="select" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-owning-group" data-title="Enter Owning Group"></a></div>
                                                    <small class="text-muted" data-18n="casefile:header.labels.owning-group">Owning Group</small>
                                                </div>
                                                <div class="col-xs-4 b-r ">
                                                    <div class="h4 font-bold"><a href="#" id="dueDate" data-type="date" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-due-date" data-title="Enter Due Date"></a></div>
                                                    <small class="text-muted" data-18n="casefile:header.labels.due-date">Due Date</small>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="wrapper ">
                                            <div class="row">
                                                <div class="col-md-12">

                                                    <section class="panel b-a ">
                                                        <div class="panel-heading b-b bg-info">
                                                            <a href="#" class="font-bold">Split Case</a> <span class="navbar-right"></span></div>
                                                        <form id="wizardform" method="get" action="">
                                                            <div class="panel panel-default">
                                                                <div class="panel-heading">



                                                                    <ul class="nav nav-tabs font-bold">
                                                                        <li class=""><a href="#tabDetail" data-toggle="tab">Details</a></li>
                                                                        <li class=""><a href="#tabPeople" data-toggle="tab">People</a></li>
                                                                        <li class=""><a href="#tabDocs" data-toggle="tab">Documents</a></li>
                                                                        <li class=""><a href="#tabParticipants" data-toggle="tab">Participants</a></li>
                                                                        <li class=""><a href="#tabNotes" data-toggle="tab">Notes</a></li>
                                                                        <li class=" pull-right"><a href="#summary" data-toggle="tab">Summary</a></li>
                                                                    </ul>


                                                                </div>
                                                                <div class="panel-body no-padder">

                                                                    <div class="tab-content">

                                                                        <div class="col-md-12 tab-pane" id="tabDetail">
                                                                            <section class="panel b-a ">
                                                                                <div class="panel-heading b-b bg-info">
                                                                                    <ul class="nav nav-pills pull-right">
                                                                                        <li>
                                                                                            <div class="btn-group padder-v2">
                                                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]casefile:detail.buttons.edit" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]casefile:detail.buttons.save" data-title="Save"><i class="fa fa-save"></i></button>
                                                                                                <ul class="dropdown-menu pull-right">
                                                                                                    <li><a href="#" data-i18n="casefile:detail.other-menu-items">Other menu items</a></li>
                                                                                                </ul>
                                                                                            </div>
                                                                                        </li>
                                                                                        <li>&nbsp;</li>
                                                                                    </ul>
                                                                                    </span> <a href="#" class="font-bold" data-i18n="casefile:detail.details">Details</a></div>
                                                                                <div class="panel-body">
                                                                                    <div class="divDetail"></div>
                                                                                </div>
                                                                            </section>
                                                                        </div>

                                                                        <div class="col-md-12 tab-pane" id="tabPeople">
                                                                            <section class="panel b-a ">
                                                                                <div id="divPeople" style="width:100%"></div>
                                                                            </section>
                                                                        </div>

                                                                        <div class="col-md-12 tab-pane"  id="tabDocs">
                                                                            <section class="panel b-a">
                                                                                <div class="panel-heading b-b bg-info">
                                                                                    <a href="#" class="font-bold"><div class="casefile:documents.title">Documents</div> </a>
                                                                                </div>


                                                                                <table id="treeDoc" class="table table-striped th-sortable table-hover">
                                                                                    <thead>
                                                                                    <tr>
                                                                                            <%--<th width2="6%"><span class='fancytree-checkbox'></span></th>--%>
                                                                                        <th width2="6%"><input type="checkbox"/></th>
                                                                                        <th width2="4%" data-i18n="casefile:documents.table.field.id">ID</th>
                                                                                        <th width="35%" data-i18n="casefile:documents.table.field.title">Title</th>
                                                                                        <th width="12%" data-i18n="casefile:documents.table.field.type">Type</th>
                                                                                        <th width="10%" data-i18n="casefile:documents.table.field.created">Created</th>
                                                                                        <th width="16%" data-i18n="casefile:documents.table.field.author">Author</th>
                                                                                        <th width="6%" data-i18n="casefile:documents.table.field.version">Version</th>
                                                                                        <th width="8%" data-i18n="casefile:documents.table.field.status">Status</th>
                                                                                    </tr>
                                                                                    </thead>
                                                                                    <tbody>
                                                                                    <tr>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                    </tr>
                                                                                    </tbody>
                                                                                </table>

                                                                            </section>
                                                                        </div>


                                                                        <div class="col-md-12 tab-pane" id="tabParticipants">
                                                                            <section class="panel b-a ">
                                                                                <div id="divParticipants" style="width:100%"></div>
                                                                            </section>
                                                                        </div>

                                                                        <div class="col-md-12 tab-pane" id="tabNotes">
                                                                            <section class="panel b-a ">
                                                                                <div id="divNotes" style="width:100%"></div>
                                                                            </section>
                                                                        </div>


                                                                        <div class="col-md-12 tab-pane panel-body" id="summary">

                                                                            <hr />

                                                                            <div class="text-center">
                                                                                <button class="btn btn-info btn-lg" onClick="window.close();"><i class="fa fa-save"></i> Split Case</button>
                                                                            </div>

                                                                        </div>

                                                                        <ul class="pager wizard m-b-sm">
                                                                            <li class="previous first" style="display:none;"><a href="#">Details</a></li>
                                                                            <li class="previous"><a href="#">Previous</a></li>
                                                                            <li class="next last disabled" style="display:none"><a href="#">Summary</a></li>
                                                                            <li class="next"><a href="#">Next</a></li>
                                                                        </ul>

                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </form>
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
            </section>
        </section>
    </jsp:body>
</t:noNav>