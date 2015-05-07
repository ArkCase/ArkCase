<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="caseFile.page.title" text="Case Files | ACM | Ark Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="resourceNamespace">casefile</span>
        <span itemprop="objType">CASE_FILE</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="treeFilter">${treeFilter}</span>
        <span itemprop="treeSort">${treeSort}</span>
        <span itemprop="token">${token}</span>
        <span itemprop="arkcaseUrl">${arkcaseUrl}</span>

        <span itemprop="urlEditCaseFileForm">${editCaseFileFormUrl}</span>
        <span itemprop="urlReinvestigateCaseFileForm">${reinvestigateCaseFileFormUrl}</span>
        <span itemprop="urlChangeCaseStatusForm">${changeCaseStatusFormUrl}</span>
        <span itemprop="urlEditChangeCaseStatusForm">${editChangeCaseStatusFormUrl}</span>
        <span itemprop="enableFrevvoFormEngine">${enableFrevvoFormEngine}</span>
        <span itemprop="formDocuments">${formDocuments}</span>
        <span itemprop="roiFormUrl">${roiFormUrl}</span>
        <span itemprop="electronicCommunicationFormUrl">${electronicCommunicationFormUrl}</span>
        <span itemprop="fileTypes">${fileTypes}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNav.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/objnav/objNavController.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/subscription/subscriptionOp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFile.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTree.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/docTree/docTreeService.js'/>"></script>


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


    <%--fullcalendar--%>
    <script src="<c:url value='/resources/vendors/${vd_fullcalendar}/${js_fullcalendar}'/>"></script>
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_fullcalendar}/fullcalendar.css'/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_fullcalendar}/theme.css'/>" type="text/css"/>

    <%--jquery qtip--%>
    <script src="<c:url value='/resources/vendors/${vd_jquery_qtip}/${js_jquery_qtip}'/>"></script>
    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_jquery_qtip}/${css_jquery_qtip}'/>" type="text/css"/>
<style>
    table.fancytree-ext-table {
        width: 100%;
        outline: 0;
    }

    table.fancytree-ext-table tbody tr td {
        border: 0px;
    }

    /*#divCalendar {
         margin: 40px 10px;
         padding: 0;
         font-family: "Lucida Grande",Helvetica,Arial,Verdana,sans-serif;
         font-size: 14px;
    }

    #divCalendar {
        max-width: 900px;
        margin: 0 auto;
    }*/

</style>

<%@include file="/resources/include/dlgSearch.jspf" %>
<%@include file="/resources/include/dlgDocTree.jspf" %>
</jsp:attribute>

    <jsp:body>
        <section class="vbox">
            <section class="scrollable">
                <section class="hbox stretch"><!-- /.aside -->
                    <!-- .aside -->

                    <aside class="aside-lg bg-light lt">
                        <section class="vbox animated fadeInLeft">
                            <section class="scrollable">
                                <header class="dk header">
                                    <h3 class="m-b-xs text-black pull-left" data-i18n="casefile:title">Case</h3>
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
                                        <input type="text" class="input-sm form-control" id="searchQuery" data-i18n="[placeholder]casefile:navigation.search.search" placeholder="Search">
                        <span class="input-group-btn">
                        <button class="btn btn-sm btn-default" type="button" data-i18n="casefile:navigation.search.btn-go">Go!</button>
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

                                                    <div class="row">
                                                        <div class="col-xs-6  b-r">
                                                            <h4><a href="#" id="caseTitle" data-type="text" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-case-title"  data-title="Enter Case Title"></a><a href="#" id="status" ></a></h4>
                                                        </div>
                                                        <div class="col-xs-6  b-r text-right">
                                                            <h4><a href="#" id="caseNumber"></a></h4>
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
                                                            <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-i18n="[data-title]casefile:header.labels.enter-priority;priority-high" data-title="Enter Priority">High</a></div>
                                                            <small class="text-muted" data-18n="casefile:header.labels.priority">Priority</small> </div>
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
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row" id="tabTopBlank">
                                        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span data-i18n=casefile:"msg.no-case-selected">(No case is selected)</span></p>
                                    </div>
                                </div>

                                <div>
                                    <div class="col-md-12" id="tabBlank" style="display:none;">
                                    </div>


                                    <div class="wrapper" id="tabTitle" style="display:none;">
                                        <div class="pull-left inline">
                                            <div class="btn-group">
                                                <label class="checkbox-inline">
                                                    <input type="checkbox" id="restrict" > <span data-i18n="casefile:header.buttons.restrict">Restrict ?</span>
                                                </label>
                                            </div>
                                        </div>

                                        <div class="pull-right inline">
                                            <div class="btn-group">
                                                <button class="btn btn-default btn-sm" id="btnEditCaseFile" data-i18n="[data-title]casefile:header.buttons.edit-case-file" data-title="Edit Case File"><i class="fa fa-edit"></i>
                                                    <span data-i18n="casefile:header.buttons.edit">Edit</span>
                                                </button>
                                                    <%--<button class="btn btn-default btn-sm" data-title="Change Case Status"  data-toggle="modal" data-target="#closeCase"><i class="fa fa-archive"></i> Close</button>--%>
                                                <button class="btn btn-default btn-sm" id="btnChangeCaseStatus" data-i18n="[data-title]casefile:header.buttons.change-case-status"data-title="Change Case Status" style="display: none" ><i class="fa fa-edit"></i>
                                                    <span data-i18n="casefile:header.buttons.change-case-status">Change Case Status</span>
                                                </button>
                                                    <%--<button class="btn btn-default btn-sm" data-title="Consolidate Case"  data-toggle="modal" data-target="#consolidateCase"><i class="fa fa-random"></i> Consolidate</button>--%>
                                                <button class="btn btn-default btn-sm" id="btnReinvestigate" data-i18n="[data-title]casefile:header.buttons.reinvestigte-case-file" data-title="Reinvestigate Case File"><i class="fa fa-reply"></i>
                                                    <span data-i18n="casefile:header.buttons.reinvestigate">Reinvestigate</span>
                                                </button>
                                                <button class="btn btn-default btn-sm" id="btnConsolidateCase" data-i18n="[data-title]casefile:header.buttons.consolidate-case" data-title="Consolidate Case"><i class="fa fa-random"></i>
                                                    <span data-i18n="casefile:header.buttons.consolidate">Consolidate</span>
                                                </button>
                                                <button class="btn btn-default btn-sm" id="btnSubscribe" data-i18n="[data-title]casefile:header.buttons.subscribe"><i class="fa fa-bullhorn"></i>
                                                </button>
                                                <!-- Modal -->
                                                <div class="modal fade" id="changeCaseStatus" tabindex="-1" role="dialog" aria-labelledby="labChangeCaseStatus" aria-hidden="true">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="casefile:change-case-status-dialog.btn-close">Close</span></button>
                                                                <h4 class="modal-title" id="labChangeCaseStatus" data-i18n="casefile:change-case-status-dialog.title">Change Case Status</h4>
                                                            </div>
                                                            <div class="modal-body" data-i18n="casefile:change-case-status-dialog.body"> Are you sure you want to change the status for this case? </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="casefile:change-case-status-dialog.btn-cancel">Cancel</button>
                                                                <button type="button" class="btn btn-primary" data-i18n="casefile:change-case-status-dialog.btn-ok">Change Case Status</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal fade" id="consolidateCase" tabindex="-1" role="dialog" aria-labelledby="labConsolidateCase" aria-hidden="true">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="casefile:consolidate-case-dialog.btn-close">Close</span></button>
                                                                <h4 class="modal-title" id="labConsolidateCase" data-i18n="casefile:consolidate-case-dialog.title">Consolidate Case</h4>
                                                            </div>
                                                            <div class="modal-body">
                                                                <section class="row">
                                                                    <div class="col-sm-12">
                                                                        <label for="edtConsolidateCase" class="label" data-i18n="casefile:consolidate-case-dialog.enter-case-like-consolidate">
                                                                            Enter the case you would like to consolidate with:
                                                                        </label>
                                                                        <input id="edtConsolidateCase" type="text" class="form-control" placeholder="Case #" >
                                                                    </div>
                                                                </section>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="casefile:consolidate-case-dialog.btn-cancel">Cancel</button>
                                                                <button type="button" class="btn btn-primary" data-i18n="casefile:consolidate-case-dialog.btn-ok">Consolidate Case</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                            <%--<h4 class="m-n">Case Details</h4>--%>
                                        <h4 class="m-n">&nbsp;</h4>
                                        <hr/>

                                            <%--<ol class="track-progress" data-steps="5">--%>
                                            <%--<li class="done">--%>
                                            <%--<span>Initiated</span>--%>
                                            <%--<i></i>--%>
                                            <%--</li>--%>
                                            <%--<li class="done">--%>
                                            <%--<span>Waiver</span>--%>
                                            <%--<i></i>--%>
                                            <%--</li>--%>
                                            <%--<li>--%>
                                            <%--<span>Adjudication</span>--%>
                                            <%--<i></i>--%>
                                            <%--</li>--%>
                                            <%--<li>--%>
                                            <%--<span>Issued</span>--%>
                                            <%--<i></i>--%>
                                            <%--</li>--%>
                                            <%--<li>--%>
                                            <%--<span>Closed</span>--%>
                                            <%--<i></i>--%>
                                            <%--</li>--%>
                                            <%--</ol>--%>
                                            <%--<br/>--%>
                                        <ol class="track-progress" id="anotherTracker">
                                        </ol>
                                    </div>

                                    <br/>

                                    <div class="col-md-12" id="tabDetail" style="display:none;">
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

                                    <div class="col-md-12" id="tabPeople" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divPeople" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabDocs" style="display:none;">
                                            <%--<section class="panel b-a ">--%>
                                            <%--<div id="divDocs" style="width:100%"></div>--%>
                                            <%--<form id="formAddDocument" style="display:none;">--%>
                                            <%--&lt;%&ndash;<input type="file" id="file" name="file">&ndash;%&gt;--%>
                                            <%--<input id="addDocument" type="file" name="files[]" multiple/>--%>
                                            <%--&lt;%&ndash;<input type="submit">&ndash;%&gt;--%>
                                            <%--</form>--%>
                                            <%--</section>--%>


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


                                    <div class="col-md-12" id="tabParticipants" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divParticipants" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabNotes" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divNotes" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabTasks" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divTasks" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabRefs" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divRefs" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabHistory" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divHistory" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabCorrespondence" style="display:none;">
                                        <section class="panel b-a ">
                                            <div id="divCorrespondence" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabTime" style="display:none;">
                                        <section class="panel b-a">
                                            <div id="divTime" style="width:100%"></div>
                                        </section>
                                    </div>

                                    <div class="col-md-12" id="tabCost" style="display:none;">
                                        <section class="panel b-a">
                                            <div id="divCost" style="width:100%"></div>
                                        </section>
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
                                                            <span data-i18n="casefile:outlook-calendar.label.month">Month</span>
                                                        </label>
                                                        <label class="btn btn-sm btn-bg btn-default" id="weekview">
                                                            <input type="radio" name="options">
                                                            <span data-i18n="casefile:outlook-calendar.label.week">Week</span>
                                                        </label>
                                                        <label class="btn btn-sm btn-bg btn-default" id="dayview">
                                                            <input type="radio" name="options">
                                                            <span data-i18n="casefile:outlook-calendar.label.day">Day</span>
                                                        </label>
                                                    </div>
                                                    <button class="btn btn-sm btn-bg btn-default pull-right" id="refreshCalendar" data-i18n="casefile:outlook-calendar.label.refresh">Refresh</button>

                                                <span class="m-t-xs inline text-white" data-i18n="casefile:outlook-calendar.label.calendar">
                                                  Calendar
                                                </span>
                                                </header>
                                                <div class="calendar">
                                                </div>
                                            </section>
                                        </aside>
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