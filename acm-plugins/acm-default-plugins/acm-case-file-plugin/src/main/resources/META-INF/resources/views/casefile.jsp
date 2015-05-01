<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="caseFile.page.title" text="Case Files | ACM | Ark Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="objType">CASE_FILE</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="treeFilter">${treeFilter}</span>
        <span itemprop="treeSort">${treeSort}</span>
        <span itemprop="token">${token}</span>

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
                                                <%--<li><a href="#">Sort Date Ascending</a></li>--%>
                                                <%--<li><a href="#">Sort Date Descending</a></li>--%>
                                                <%--<li><a href="#">Sort Case ID Ascending</a></li>--%>
                                                <%--<li><a href="#">Sort Case ID Ascending</a></li>--%>
                                        </ul>
                                    </div>

                                    <div class="btn-group select pull-right">
                                        <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                                        <ul class="dropdown-menu text-left text-sm" id="ulFilter">
                                                <%--<li><a href="#">All Open Cases</a></li>--%>
                                                <%--<li><a href="#">All Closed</a></li>--%>
                                                <%--<li><a href="#">All Inactive</a></li>--%>

                                                <%--<li><a href="#">Cases I've Opened</a></li>--%>
                                                <%--<li><a href="#">Unapproved Cases</a></li>--%>
                                                <%--<li><a href="#">Approved Cases</a></li>--%>
                                                <%--<li><a href="#">Cases From Group</a></li>--%>
                                                <%--<li><a href="#">Closed or Expired Cases</a></li>--%>
                                                <%--<li><a href="#">New Cases</a></li>--%>
                                                <%--<li><a href="#">All Destroyed</a></li>--%>
                                                <%--<li><a href="#">All Archived</a></li>--%>
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
                                                <button class="btn btn-default btn-sm" data-i18n="[data-title]casefile:header.buttons.edit-case-file" data-title="Edit Case File"><i class="fa fa-edit"></i>
                                                    <span data-i18n="casefile:header.buttons.edit">Edit</span>
                                                </button>
                                                    <%--<button class="btn btn-default btn-sm" data-title="Change Case Status"  data-toggle="modal" data-target="#closeCase"><i class="fa fa-archive"></i> Close</button>--%>
                                                <button class="btn btn-default btn-sm" data-i18n="[data-title]casefile:header.buttons.change-case-status"data-title="Change Case Status" style="display: none" ><i class="fa fa-edit"></i>
                                                    <span data-i18n="casefile:header.buttons.change-case-status">Change Case Status</span>
                                                </button>
                                                    <%--<button class="btn btn-default btn-sm" data-title="Consolidate Case"  data-toggle="modal" data-target="#consolidateCase"><i class="fa fa-random"></i> Consolidate</button>--%>
                                                <button class="btn btn-default btn-sm" data-i18n="[data-title]casefile:header.buttons.reinvestigte-case-file" data-title="Reinvestigate Case File"><i class="fa fa-reply"></i>
                                                    <span data-i18n="casefile:header.buttons.reinvestigate">Reinvestigate</span>
                                                </button>
                                                <button class="btn btn-default btn-sm" data-i18n="[data-title]casefile:header.buttons.consolidate-case" data-title="Consolidate Case"><i class="fa fa-random"></i>
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
                                                                <%--<button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit" onclick="edit()"><i class="fa fa-pencil"></i></button>--%>
                                                                <%--<button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save" onclick="save()"><i class="fa fa-save"></i></button>--%>
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
                                            <div class="panel-heading b-b bg-info">  <ul class="nav nav-pills pull-right">
                                                <li style="margin-right:5px"></li>
                                                    <%--<li>--%>
                                                    <%--<div class="btn-group padder-v2">--%>
                                                    <%--<button class="btn btn-default btn-sm" data-toggle="modal" data-target="#createnewfolder"><i class="fa fa-folder"></i> New Folder</button>--%>
                                                    <%--</div>--%>
                                                    <%--</li>--%>
                                                    <%--<li>--%>
                                                    <%--<div class="btn-group padder-v2">--%>
                                                    <%--<button class="btn btn-default btn-sm" data-toggle="modal" data-target="#emailDocs"><i class="fa fa-share"></i> Email</button>--%>
                                                    <%--</div>--%>
                                                    <%--</li>--%>
                                                    <%--<li>--%>
                                                    <%--<div class="btn-group padder-v2">--%>
                                                    <%--<button class="btn btn-default btn-sm" onClick="window.open('documents.html', '_blank');"><i class="fa fa-print"></i> Print</button>--%>
                                                    <%--</div>--%>
                                                    <%--</li>--%>
                                                <li> </li>
                                            </ul>


                                                <a href="#" class="font-bold"><div class="casefile:documents.title">Documents</div> </a>
                                            </div>


                                            <div class="modal fade" id="createnewfolder" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only" data-i18n="casefile:create-new-folder-dialog.btn-close">Close</span></button>
                                                            <h4 class="modal-title" id="myModalLabel" data-i18n="casefile:create-new-folder-dialog.title">Create Folder</h4>
                                                        </div>
                                                        <div class="modal-body">

                                                            <p data-i18n="casefile:create-new-folder-dialog.body">Enter a name for the folder you would like to create:</p>

                                                            <label for="folderName2" data-i18n="casefile:create-new-folder-dialog.label.folder-name">Folder Name</label><br/>
                                                            <input type="text" id="folderName2" class="input-lg" data-i18n="[placeholder]casefile:create-new-folder-dialog.label.folder-name"placeholder="Folder Name" />

                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="casefile:create-new-folder-dialog.btn-cancel">Cancel</button>
                                                            <button type="button" class="btn btn-primary" data-i18n="casefile:create-new-folder-dialog.btn-ok">Create Folder</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>


                                            <div class="modal fade" id="emailDocs" tabindex="-1" role="dialog" aria-labelledby="emailDocsLabel" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <button type="button" class="close" data-dismiss="modal">&times;
                                                                <span class="sr-only" data-i18n="casefile:email-documents-dialog.btn-close">Close</span>
                                                            </button>
                                                            <h4 class="modal-title" id="emailDocsLabel" data-i18n="casefile:email-documents-dialog.title">Send Email</h4>
                                                        </div>
                                                        <div class="modal-body">

                                                            <p data-i18n="casefile:email-documents-dialog.body">Where would you like to email this file?</p>

                                                            <label for="emailaddy" data-i18n="casefile:email-documents-dialog.label.email-address">Email Address</label><br/>
                                                            <input type="text" id="emailaddy" class="input-lg" data-i18n="[placeholder]casefile:email-documents-dialog.label.email-address" placeholder="Email Address" />

                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-default" data-dismiss="modal" data-i18n="casefile:email-documents-dialog.btn-cancel">Cancel</button>
                                                            <button type="button" class="btn btn-primary" data-i18n="casefile:email-documents-dialog.btn-ok">Send Email</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                                <%--<div class="modal fade" id="emailDocs" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
                                                    <div class="modal-dialog modal-lg">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <button type="button" class="close" data-dismiss="modal">×<span class="sr-only">Close</span></button>
                                                                <h4 class="modal-title" id="myModalLabel">Send Email</h4>
                                                            </div>
                                                            <header class="header bg-gradient b-b clearfix">
                                                                <div class="row m-t-sm">
                                                                    <div class="col-md-12 m-b-sm">
                                                                        <div class="input-group">
                                                                            <input type="text" class="input-md form-control" id="searchQuery" placeholder="Enter an email address OR search for users within ArkCase.">
                                                <span class="input-group-btn">
                                                <button class="btn btn-md btn-default" type="button">Go!</button>
                                                </span> </div>
                                                                    </div>
                                                                </div>
                                                            </header>
                                                            <div class="modal-body">
                                                                <div class="row">
                                                                    <div class="col-xs-3">
                                                                        <div class="facets">
                                                                            <h6>Create User</h6>
                                                                            <div class="list-group ">
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">5</span> sally-acm </label>
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">2</span> ann-acm </label>
                                                                            </div>
                                                                            <h6>Person, Organization Type</h6>
                                                                            <div class="list-group auto">
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">2</span> initiator </label>
                                                                            </div>
                                                                            <h6>Object Type</h6>
                                                                            <div class="list-group auto">
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">6</span> TASK </label>
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">2</span> PERSON-ASSOCIATION </label>
                                                                            </div>
                                                                            <h6>Assignee Full Name</h6>
                                                                            <div class="list-group auto">
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">6</span> sally supervisor </label>
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">2</span> ann administrator </label>
                                                                            </div>
                                                                            <h6>Priority</h6>
                                                                            <div class="list-group ">
                                                                                <label class="list-group-item">
                                                                                    <input type="checkbox">
                                                                                    <span class="badge bg-info">6</span> medium </label>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="col-xs-9">
                                                                        <section class="panel panel-default">
                                                                            <div class="table-responsive">
                                                                                <table class="table table-striped b-t b-light">
                                                                                    <thead>
                                                                                    <tr>
                                                                                        <th width="10"><input type="checkbox"> </th>
                                                                                        <th class="th-sortable" data-toggle="class">Type <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                                                        <th class="th-sortable" data-toggle="class">ID <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                                                        <th class="th-sortable" data-toggle="class">Title <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                                                        <th class="th-sortable" data-toggle="class">Created <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>
                                                                                        <th class="th-sortable" data-toggle="class">Status <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i> </span> </th>



                                                                                    </tr>
                                                                                    </thead>
                                                                                    <tbody>
                                                                                    <tr>
                                                                                        <td><input type="checkbox"></td>
                                                                                        <td>[Type]</td>
                                                                                        <td>[ID]</td>
                                                                                        <td>[Title]</td>
                                                                                        <td>[Created]</td>
                                                                                        <td>[Status]</td>



                                                                                    </tr>
                                                                                    </tbody>
                                                                                </table>
                                                                            </div>
                                                                        </section>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">

                                                                <div class="pull-left"><button type="button" class="btn btn-default">View Recipients (3)</button></div>


                                                                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                                <button type="button" class="btn btn-default">Add Selected Users (6)</button>
                                                                <button type="button" class="btn btn-primary">Send Email</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>--%>


                                            <table id="treeDoc" class="table table-striped th-sortable table-hover">
                                                    <%--<form id="formUploadDoc" style="display:none;">--%>
                                                    <%--<input type="file" id="file" name="files[]" multiple />--%>
                                                    <%--</form>--%>
                                                    <%--<form action="#" id="formDownloadDoc" style="display:none;">--%>
                                                    <%--</form>--%>

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
                                                        <%--<th width2="6%" colspan="2"></th>--%>
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
                                                        <%--<td></td>--%>
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