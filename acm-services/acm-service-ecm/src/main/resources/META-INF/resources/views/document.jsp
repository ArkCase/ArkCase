<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:noNav>
<jsp:attribute name="endOfHead">
    <title><spring:message code="document.page.title" text="Document | ACM | Ark Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">search,docdetail</span>
        <span itemprop="objType">FILE</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="participantTypes">${participantTypes}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/document/document.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/search/searchBase.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/search/searchBaseController.js'/>"></script>


    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>


    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>

    <%@include file="/resources/include/dlgSearch.jspf" %>
    <%@include file="/resources/include/dlgDocTree.jspf" %>
</jsp:attribute>

<jsp:body>
<header class="header bg-white b-b clearfix">
    <div class="row m-t-sm">
        <div class="col-sm-12 m-b-xs">
            <div class="pull-right inline">
                <div class="btn-group">



                    <div class="modal fade" id="dlgObjectPicker" tabindex="-1" role="dialog" aria-labelledby="labPoTitle" aria-hidden="true" style="display: none;">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">×<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="labPoTitle"></h4>
                                </div>
                                <header class="header bg-gradient b-b clearfix">
                                    <div class="row m-t-sm">
                                        <div class="col-md-12 m-b-sm">
                                            <div class="input-group">
                                                <input type="text" class="input-md form-control" id="edtPoSearch">
                                                <span class="input-group-btn">
                                                    <button class="btn btn-md" type="button" data-i18n="docdetail:participants.picker.btnTextGo"></button>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </header>
                                <div class="modal-body">
                                    <div class="row">
                                        <div class="col-xs-3">
                                            <div class="facets" id="divPoFacets">
                                                <div name="filter_fields">
                                                    <h6></h6>
                                                    <div class="list-group auto" name="Object Type">
                                                        <label class="list-group-item">
                                                            <input type="checkbox" value="USER" checked="" disabled="">
                                                            <span class="badge bg-info">
                                                            </span>
                                                            USER
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-xs-9">
                                            <section class="panel panel-default">
                                                <div class="table-responsive" id="divPoResults"></div>

                                            </section>

                                            <div>
                                                <label  class="label" data-i18n="docdetail:participants.picker.participant-type"></label>
                                                <select class="input-sm form-control inline v-middle" id="participantType">
                                                    <option value="null" data-i18n="docdetail:participants.picker.select-participant-type"></option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="docdetail:participants.picker.btnTextCancel"></button>
                                    <button type="button" class="btn btn-primary" data-i18n="docdetail:participants.picker.btnTextOk"></button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal fade" id="modalNewTag" tabindex="-1" role="dialog" aria-labelledby="newTag" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="newTag" data-i18n="docdetail:tags.newtag.title"></h4>
                                </div>
                                <div class="modal-body">


                                    <p data-i18n="docdetail:tags.newtag.instructions.first-instruction"></p>
                                    <p data-i18n="docdetail:tags.newtag.instructions.second-instruction"></p>
                                        <div class="form-group" id="newTagForm">
                                            <label for="newTagName" class="control-label" data-i18n="docdetail:tags.newtag.name"></label>
                                            <input type="text" class="form-control" id="newTagName">
                                            <label for="newTagDesc" class="control-label" data-i18n="docdetail:tags.newtag.description"></label>
                                            <input type="text" class="form-control" id="newTagDesc">
                                            <label for="newTagText" class="control-label" data-i18n="docdetail:tags.newtag.name"></label>
                                            <input type="text" class="form-control" id="newTagText">
                                            </br>
                                        </div>
                                    <%--[Insert tree view with checkboxes]--%>

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="docdetail:tags.newtag.button.cancel"></button>
                                    <button type="button" class="btn btn-primary" data-i18n="docdetail:tags.newtag.button.add-tag"></button>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <h4 class="m-n"> <a href="#" id="docTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Document Title">&nbsp;&nbsp;&nbsp;&nbsp; </a><small class="text-muted" id="activeVersion"></small></h4>

        </div>
    </div>
</header>
<section class="hbox stretch">
    <aside class="aside-xxl bg-light dker b-r" id="subNav">
        <section class="vbox">
            <section class="scrollable">
                <div class="wrapper">
                    <section class="panel panel-default portlet-item">
                        <header class="panel-heading acm-DocDetailTitleText">
                            <ul class="nav nav-pills pull-right">
                                <li><div class="btn-group padder-v2"><button class="btn btn-default btn-sm" id = "newParticipant" data-toggle="tooltip" data-title="New Partcipant"><i class="fa fa-user"></i> New</button></div></li>
                                <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                            </ul>
                            Participants <span class="badge" id="labParticipants"></span> </header>
                        <ul class="list-group alt panel-body" id="tabParticipants">

                        </ul>

                        <div class="modal fade" id="modalParticipantChangeRole" tabindex="-1" role="dialog" aria-labelledby="changeParticipantRole" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" data-i18n="docdetail:participants.role.button.close">&times;<span class="sr-only"></span></button>
                                        <h4 class="modal-title" id="changeParticipantRole" data-i18n="docdetail:participants.role.labels.change-role"></h4>
                                    </div>

                                    <div class="modal-body">
                                        <p data-i18n="docdetail:participants.role.labels.please-select-role"></p>
                                        <label  class="label" data-i18n="docdetail:participants.role.labels.available-roles"></label>
                                        <select class="input-sm form-control inline v-middle" id="participantRoles">
                                            <option value="null" data-i18n="docdetail:participants.role.labels.select-role"></option>
                                        </select>
                                    </div>

                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="docdetail:participants.role.button.cancel"></button>
                                        <button type="button" class="btn btn-primary" data-i18n="docdetail:participants.role.button.change-role"></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section class="panel panel-default portlet-item">
                        <header class="panel-heading acm-DocDetailTitleText">
                            <ul class="nav nav-pills pull-right">
                                <li><div class="btn-group padder-v2"><button class="btn btn-default btn-sm"  data-toggle="modal" id="btnNewTag"><i class="fa fa-tag"></i> New</button></div></li>
                                <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                            </ul>
                            Tags <span class="badge" id="labTags"></span>
                        </header>
                        <table class="panel-body table table-striped b-light" id="tabTags">
                            <thead>
                            <tr>

                                <th class="th-sortable" data-toggle="class" data-i18n="docdetail:tags.table.field.tag"><span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                                <th width="10" data-i18n="docdetail:tags.table.field.action"></th>
                            </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </section>

                    <section class="panel panel-default portlet-item">
                        <header class="panel-heading acm-DocDetailTitleText" data-i18n="docdetail:version-history.table.title">
                            <ul class="nav nav-pills pull-right">
                                <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                            </ul>
                        </header>


                        <table class="panel-body table table-striped b-light" id="tabVersionHistory">
                            <thead>
                            <tr>

                                <th class="th-sortable" data-toggle="class" data-i18n="docdetail:version-history.table.field.version"><span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                                <th data-i18n="docdetail:version-history.table.field.date"></th>
                                <th data-i18n="docdetail:version-history.table.field.user"></th>
                                <th width="10" data-i18n="docdetail:version-history.table.field.action"></th>
                            </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </section>

                    <div class="row" id="tabEventHistory">
                        <div class="col-md-12">
                            <section class="panel b-a">
                                <div id="divEventHistory" style="width:100%"></div>
                            </section>
                        </div>
                    </div>

                    <div class="row" id="tabNotes">
                        <div class="col-md-12">
                            <section class="panel b-a">
                                <div id="divNotes" style="width:100%"></div>
                            </section>
                        </div>
                    </div>
                </div>
                <br/>
                <br/>
            </section>
        </section>
    </aside>
    <aside>
    <section class="vbox">
        <section class="scrollable">
            <div class="wrapper bg-empty  clearfix">
                <div class="row" id="parentDetails">
                    <div class="col-xs-12">
                        <div class="">
                            <div class=" clearfix">
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="owner"></a></div>
                                    <small class="text-muted"  data-i18n="docdetail:header.labels.owner"></small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="createDate"></a></div>
                                    <small class="text-muted" data-i18n="docdetail:header.labels.create-date"></small></div>
                                <div class="col-xs-3 b-r">
                                    <div class="h4 font-bold"><a href="#" id="assignee"></a></div>
                                    <small class="text-muted" data-i18n="docdetail:header.labels.assigned-to"></small></div>
                                <div class="col-xs-3 b-r">
                                    <div class="h4 font-bold"><a href="#" id="type"></a></div>
                                    <small class="text-muted" data-i18n="docdetail:header.labels.type"></small></div>
                                <div class="col-xs-2">
                                    <div class="h4 font-bold"><a href="#" id="status"></a></div>
                                    <small class="text-muted" data-i18n="docdetail:header.labels.state"></small></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="wrapper ">

                <div class="row" id="tabDocViewer">
                    <div class="col-md-12">
                        <section class="panel b-a">

                            <div id="divDocViewer" style="width:100%">
                                <div id="divDocViewerHeader" stile = "width:100%">
                                    <p>Document Viewer</p>
                                </div>
                                <iframe src = "${link}" width='100%' height='700' allowfullscreen webkitallowfullscreen></iframe>
                            </div>
                        </section>
                    </div>
                </div>




            </div>
            </div>
        </section>
    </section>
</aside>
</section>
</jsp:body>
</t:noNav>
