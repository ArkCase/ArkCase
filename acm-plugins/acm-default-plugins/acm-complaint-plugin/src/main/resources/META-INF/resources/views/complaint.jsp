<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="complaint.page.title" text="Complaints | ACM | Armedia Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">complaint</span>
        <span itemprop="objType">COMPLAINT</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="treeFilter">${treeFilter}</span>
        <span itemprop="treeSort">${treeSort}</span>
        <span itemprop="token">${token}</span>
        <span itemprop="arkcaseUrl">${arkcaseUrl}</span>
        <span itemprop="arkcasePort">${arkcasePort}</span>

        <span itemprop="closeComplaintFormUrl">${closeComplaintFormUrl}</span>
        <%--<span itemprop="editCloseComplaintFormUrl">${editCloseComplaintFormUrl}</span>--%>
        <span itemprop="roiFormUrl">${roiFormUrl}</span>
        <span itemprop="electronicCommunicationFormUrl">${electronicCommunicationFormUrl}</span>
        <span itemprop="formDocuments">${formDocuments}</span>
        <span itemprop="fileTypes">${fileTypes}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNav.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavController.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaint.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/complaint/complaintService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTree.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionOp.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <!-- File Manager -->
    <%--<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_knob}/js/${js_knob}'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_upload}/js/${js_upload_fileupload}'/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value='/resources/vendors/${vd_upload}/js/${js_upload_iframe}'/>"></script>--%>

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
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_gridnav}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_edit}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_dnd}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>

    <!-- X-Editable -->
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>

    <%--fullcalendar--%>
    <script src="<c:url value='/resources/vendors/${vd_fullcalendar}/${js_fullcalendar}'/>"></script>
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_fullcalendar}/fullcalendar.css'/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_fullcalendar}/theme.css'/>" type="text/css"/>

    <%--jquery qtip--%>
    <script src="<c:url value='/resources/vendors/${vd_jquery_qtip}/${js_jquery_qtip}'/>"></script>
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_jquery_qtip}/${css_jquery_qtip}'/>" type="text/css"/>

    <%@include file="/resources/include/dlgSearch.jspf" %>
    <%@include file="/resources/include/dlgDocTree.jspf" %>
</jsp:attribute>

<jsp:body>
    <section class="vbox">
        <section class="scrollable">
            <section class="hbox stretch"><!-- /.aside -->
                <!-- .aside -->

                    <%--<aside class="aside-xl bg-light lt">   used with tree table--%>
                <aside class="aside-lg bg-light lt">
                    <section class="vbox animated fadeInLeft">
                        <section class="scrollable">
                            <header class="dk header">
                                <h3 class="m-b-xs text-black pull-left" data-i18n="complaint:title">Complaints</h3>
                                <div class="btn-group inline select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm" id="ulSort">
                                    </ul>
                                </div>
                                <div class="btn-group select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm" id="ulFilter">
                                    </ul>
                                </div>
                            </header>
                            <div class="wrapper">
                                <div class="input-group">
                                    <input type="text" class="input-sm form-control" id="searchQuery" placeholder="Search">
                <span class="input-group-btn">
                <button class="btn btn-sm btn-default" type="button">Go!</button>
                </span> </div>
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
                                    <div class="col-xs-12">
                                        <div class="">
                                            <div class=" clearfix">

                                                <div class="wrapper dk  clearfix">
                                                    <div class="row">
                                                        <div class="col-xs-6  b-r">
                                                            <h4><a href="#" id="complaintTitle" data-i18n="[data-title]complaint:header.labels.enter-complaint-title" data-type="text" data-pk="1" data-title="Enter Complaint Title"></a><a href="#" id="status" ></a></h4>
                                                        </div>
                                                        <div class="col-xs-6  b-r text-right">
                                                            <h4><a href="#" id="complaintNum"></a></h4>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-xs-4 b-r">
                                                            <div class="h4 font-bold"><a href="#" id="type" data-i18n="[data-title]complaint:header.labels.enter-incident-category" data-type="select" data-pk="1" data-title="Enter Incident Category"></a></div>
                                                            <small class="text-muted" data-i18n="complaint:header.labels.incident-category">Incident Category</small>
                                                        </div>
                                                        <div class="col-xs-4 b-r">
                                                            <div class="h4 font-bold"><a href="#" id="incident" data-i18n="[data-title]complaint:header.labels.enter-incident-date" data-type="date" data-pk="1" data-title="Enter Incident Date"></a></div>
                                                            <small class="text-muted" data-i18n="complaint:header.labels.incident-date">Incident Date</small>
                                                        </div>
                                                        <div class="col-xs-4 b-r">
                                                            <div class="h4 font-bold"><a href="#" id="priority" data-i18n="[data-title]complaint:header.labels.enter-priority" data-type="select" data-pk="1" data-title="Enter Priority"></a></div>
                                                            <small class="text-muted" data-i18n="complaint:header.labels.priority">Priority</small>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-xs-4 b-r">
                                                            <div class="h4 font-bold"><a href="#" id="assigned" data-i18n="[data-title]complaint:header.labels.enter-assignee" data-type="select" data-pk="1" data-title="Enter Assignee"></a></div>
                                                            <small class="text-muted" data-i18n="complaint:header.labels.assigned-to">Assigned To</small>
                                                        </div>
                                                        <div class="col-xs-4  b-r">
                                                            <div class="h4 font-bold"><a href="#" id="group"  data-i18n="[data-title]complaint:header.labels.enter-owning-group"  data-type="select" data-pk="1" data-title="Enter Owning Group"></a></div>
                                                            <small class="text-muted" data-i18n="complaint:header.labels.owning-group">Owning Group</small>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>




                                <div class="row" id="tabTopBlank">
                                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span data-i18n="complaint:msg.no-complaint-selected"></span></p>
                                </div>
                            </div>

                            <div>
                                <div class="wrapper">
                                    <div class="row" id="tabBlank" style="display:none;">
                                    </div>

                                    <div class="row" id="tabAction" style="display:none;">
                                        <div class="col-md-12">
                                            <div class="pull-right inline">
                                                <div class="btn-group">
                                                    <button class="btn btn-default btn-sm" data-i18n="[data-title]complaint:header.buttons.close-complaint" data-toggle="tooltip" id = "closeComplaint" data-title="Close Complaint"><i class="fa fa-archive"></i> <span data-i18n="complaint:header.buttons.close-complaint">Close Complaint</span></button>
                                                    <%--<input id="closeComplaintFormUrl" type="hidden" value="${closeComplaintFormUrl}" />--%>

                                                    <button class="btn btn-default btn-sm" data-i18n="[data-title]complaint:header.buttons.subscribe" id="btnSubscribe"><i class="fa fa-bullhorn"></i>
                                                    </button>
                                                </div>
                                            </div>

                                            <div class="pull-left inline">
                                                <div class="btn-group">
                                                    <label class="checkbox-inline">
                                                        <input type="checkbox" id="restrict"> <span data-i18n="complaint:header.buttons.restrict">Restrict ?</span>
                                                    </label>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                    <hr/>


                                    <div class="row" id="tabDetail" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a ">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]complaint:detail.buttons.edit" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-i18n="[data-title]complaint:detail.buttons.save" data-title="Save"><i class="fa fa-save"></i></button>
                                                                <ul class="dropdown-menu pull-right">
                                                                    <li><a href="#" data-i18n="complaint:detail.buttons.other">Other menu items</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    </span> <a href="#" class="font-bold" data-i18n="complaint:detail.buttons.details">Details</a> </div>
                                                <div class="panel-body">
                                                    <div class="divDetail"></div>
                                                </div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabLocation" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divLocation" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <%--<div class="row" id="tabInitiator" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divInitiator" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>--%>


                                    <div class="row" id="tabPeople" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divPeople" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabDocuments" style="display:none;">
                                        <%--<div class="col-md-12">--%>
                                            <%--<section class="panel b-a">--%>
                                                <%--<div id="divDocuments" style="width:100%"></div>--%>
                                                <%--<form id="formAddDocument" style="display:none;">--%>
                                                        <%--&lt;%&ndash;<input type="file" id="file" name="file">&ndash;%&gt;--%>
                                                    <%--<input id="addDocument" type="file" name="files[]" multiple/>--%>
                                                        <%--&lt;%&ndash;<input type="submit">&ndash;%&gt;--%>
                                                <%--</form>--%>
                                                    <%--&lt;%&ndash;<input id="roiFormUrl" type="hidden" value="${roiFormUrl}" />&ndash;%&gt;--%>
                                                    <%--&lt;%&ndash;<input id="electronicCommunicationFormUrl" type="hidden" value="${electronicCommunicationFormUrl}" />&ndash;%&gt;--%>
                                                    <%--&lt;%&ndash;<input id="formDocuments" type="hidden" value='${formDocuments}' />&ndash;%&gt;--%>
                                            <%--</section>--%>
                                        <%--</div>--%>
                                    <%--</div>--%>

                                    <div class="col-md-12">
                                    <section class="panel b-a">
                                        <div class="panel-heading b-b bg-info">
                                            <%--<ul class="nav nav-pills pull-right">--%>
                                                <%--<li style="margin-right:5px"></li>--%>
                                                <%--<li>--%>
                                                    <%--<div class="btn-group padder-v2">--%>
                                                        <%--<button class="btn btn-default btn-sm" data-toggle="modal" data-target="#dlgDocTreeDnd"><i class="fa fa-folder"></i> New Folder</button>--%>
                                                    <%--</div>--%>
                                                <%--</li>--%>
                                                <%--<li>--%>
                                                    <%--<div class="btn-group padder-v2">--%>
                                                        <%--<button class="btn btn-default btn-sm" data-toggle="modal" data-target="#emailDocs"><i class="fa fa-share"></i> <span data-i18n="complaint:documents-to-retire.buttons.email">Email</span></button>--%>
                                                    <%--</div>--%>
                                                <%--</li>--%>
                                                <%--<li>--%>
                                                    <%--<div class="btn-group padder-v2">--%>
                                                        <%--<button class="btn btn-default btn-sm" onClick="window.open('documents.html', '_blank');"><i class="fa fa-print"></i> <span data-i18n="complaint:documents-to-retire.buttons.print">Print</span></button>--%>
                                                    <%--</div>--%>
                                                <%--</li>--%>
                                                <%--<li> </li>--%>
                                            <%--</ul>--%>

                                            <a href="#" class="font-bold"><div data-i18n="complaint:documents-to-retire.title">Documents</div> </a>
                                        </div>




                                        <table id="treeDoc" class="table table-striped th-sortable table-hover">
                                            <thead>
                                            <tr>
                                                <%--<th width2="6%"><span class='fancytree-checkbox'></span></th>--%>
                                                <th width2="6%"><input type="checkbox"/></th>
                                                <th width2="4%" data-i18n="complaint:documents-to-retire.tree.field.id">ID</th>
                                                <th width="35%" data-i18n="complaint:documents-to-retire.tree.field.title">Title</th>
                                                <th width="12%" data-i18n="complaint:documents-to-retire.tree.field.type">Type</th>
                                                <th width="10%" data-i18n="complaint:documents-to-retire.tree.field.created">Created</th>
                                                <th width="16%" data-i18n="complaint:documents-to-retire.tree.field.author">Author</th>
                                                <th width="6%" data-i18n="complaint:documents-to-retire.tree.field.version">Version</th>
                                                <th width="8%" data-i18n="complaint:documents-to-retire.tree.field.status">Status</th>
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
                                    </div>



                                    <div class="row" id="tabTasks" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div class="panel-body max-200 no-padder">
                                                    <div id="divTasks" style="width:100%"></div>
                                                </div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabNotes" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divNotes" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabParticipants" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divParticipants" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabRefs" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divReferences" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabHistory" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divHistory" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabTime" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divTime" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabCost" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divCost" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>


                                    <div class="col-md-12"  id="tabOutlookCalendar" style="display:none;">
                                        <aside class="wrapper">
                                                <%--<div class="pull-right inline">
                                                    <div class="dropdown">
                                                        <div class="btn-group">
                                                            <button class="btn btn-default btn-sm" data-title="Download ICS"  data-toggle="modal" data-target="#downloadICS" style="display:none;"><i class="fa fa-calendar"></i> Download ICS</button>
                                                        </div>
                                                    </div>
                                                </div>--%>
                                                <%--<h4 class="m-n" style="display:none;">Complaint Calendar</h4>--%>
                                                <%--<hr/>--%>

                                            <section class="panel no-border bg-light">
                                                <header class="panel-heading bg-primary clearfix">
                                                    <div class="btn-group pull-right" data-toggle="buttons">
                                                        <label class="btn btn-sm btn-bg btn-default active" id="monthview">
                                                            <input type="radio" name="options">
                                                            <span data-i18n="complaint:outlook-calendar.label.month">Month</span>
                                                        </label>
                                                        <label class="btn btn-sm btn-bg btn-default" id="weekview">
                                                            <input type="radio" name="options">
                                                            <span data-i18n="complaint:outlook-calendar.label.week">Week</span>
                                                        </label>
                                                        <label class="btn btn-sm btn-bg btn-default" id="dayview">
                                                            <input type="radio" name="options">
                                                            <span data-i18n="complaint:outlook-calendar.label.day">Day</span>
                                                        </label>
                                                    </div>
                                                    <button class="btn btn-sm btn-bg btn-default pull-right" id="refreshCalendar" data-i18n="complaint:outlook-calendar.label.refresh">Refresh</button>

                                                    <span class="m-t-xs inline text-white" data-i18n="complaint:outlook-calendar.label.calendar">
                                                      Calendar
                                                    </span>
                                                </header>
                                                <div class="calendar">
                                                </div>
                                            </section>
                                        </aside>
                                    </div>






                                <%--<div class="row" id="tabRefComplaints" style="display:none;">--%>
                                            <%--&lt;%&ndash;Other Complaints&ndash;%&gt;--%>
                                    <%--</div>--%>

                                    <%--<div class="row" id="tabRefCases" style="display:none;">--%>
                                            <%--&lt;%&ndash;Other Cases&ndash;%&gt;--%>
                                    <%--</div>--%>

                                    <%--<div class="row" id="tabRefTasks" style="display:none;">--%>
                                            <%--&lt;%&ndash;Other Tasks&ndash;%&gt;--%>
                                    <%--</div>--%>

                                    <%--<div class="row" id="tabRefDocuments" style="display:none;">--%>
                                            <%--&lt;%&ndash;Other Documents&ndash;%&gt;--%>
                                    <%--</div>--%>

                                    <%--<div class="row" id="tabApprovers" style="display:none;">--%>
                                            <%--&lt;%&ndash;Approvers&ndash;%&gt;--%>
                                    <%--</div>--%>

                                    <%--<div class="row" id="tabCollaborators" style="display:none;">--%>
                                            <%--&lt;%&ndash;Collaborators&ndash;%&gt;--%>
                                    <%--</div>--%>

                                    <%--<div class="row" id="tabWatchers" style="display:none;">--%>
                                            <%--&lt;%&ndash;Watchers&ndash;%&gt;--%>
                                    <%--</div>--%>

                                </div>
                            </div>
                        </section>
                    </section>
                </aside>
                <!-- /.aside -->

            </section>
        </section>
    </section>
</jsp:body>
</t:layout>



