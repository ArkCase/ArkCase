<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:detail>
<jsp:attribute name="endOfHead">
    <title><spring:message code="document.page.title" text="Document | ACM | Ark Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
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

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebar.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_acm}/js/sidebar/sidebarController.js'/>"></script>

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
                                                <input type="text" class="input-md form-control" id="edtPoSearch" placeholder="Enter to search for members.">
                                                <span class="input-group-btn">
                                                    <button class="btn btn-md" type="button">Go!</button>
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
                                                <label  class="label">Participant Type</label>
                                                <select class="input-sm form-control inline v-middle" id="participantType">
                                                    <option value="null">Select Participant Type</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Add</button>
                                </div>
                            </div>
                        </div>
                    </div>


                    <button class="btn btn-default  btn-sm" data-toggle="modal" id="btnReplaceFile">
                        <span class="text">Replace File</span>
                    </button>

                    <div class="modal fade" id="modalReplaceFile" tabindex="-1" role="dialog" aria-labelledby="replaceFile" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="replaceFile">Replace File</h4>
                                </div>

                                <div class="modal-body">
                                    <p>Choose a file from your computer to replace [document name]:</p>
                                    <label for="fileName">File</label><br/>
                                    <input type="file" id="fileName" class="input-lg" />
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Replace File</button>
                                </div>
                            </div>
                        </div>
                    </div>



                    <button class="btn btn-default  btn-sm" data-toggle="modal" id="btnDeleteFile">
                        <span class="text">Delete</span>
                    </button>

                    <div class="modal fade" id="modalDeleteFile" tabindex="-1" role="dialog" aria-labelledby="deleteFile" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="deleteFile">Delete</h4>
                                </div>

                                <div class="modal-body">
                                    <p>Are you sure you want to delete [file name] from [partent folder]?</p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Delete</button>
                                </div>
                            </div>
                        </div>
                    </div>



                    <button class="btn btn-default  btn-sm" data-toggle="modal" id="btnCopyFile">
                        <span class="text">Copy</span>
                    </button>

                    <div class="modal fade" id="modalCopyFile" tabindex="-1" role="dialog" aria-labelledby="copyFile" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="copyFile">Copy</h4>
                                </div>

                                <div class="modal-body">
                                    <p>Where would you like to copy this file? Choose the directory from the box below:</p>
                                    <p>[place tree view here]</p>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Copy</button>
                                </div>
                            </div>
                        </div>
                    </div>


                    <button class="btn btn-default  btn-sm" data-toggle="modal" id="btnMoveFile">
                        <span class="text">Move</span>
                    </button>

                    <div class="modal fade" id="modalMoveFile" tabindex="-1" role="dialog" aria-labelledby="moveFile" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="moveFile">Move</h4>
                                </div>

                                <div class="modal-body">
                                    <p>Where would you like to move this file? Choose the directory from the box below:</p>
                                    <p>[place tree view here]</p>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Move</button>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="modal fade" id="modalNewTag" tabindex="-1" role="dialog" aria-labelledby="newTag" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="newTag">New Tag</h4>
                                </div>
                                <div class="modal-body">


                                    <p>Choose a tag to associate with this document: </p>

                                    [Insert tree view with checkboxes]

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Add Tag</button>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Case Title"> Sample Document Title</a> (12321)</h4>

        </div>
    </div>
</header>
<section class="hbox stretch">
<aside class="aside-xxl bg-light dker b-r" id="subNav">
    <section class="scrollable">
        <div class="wrapper">
            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li><div class="btn-group padder-v2"><button class="btn btn-default btn-sm" id = "newParticipant" data-toggle="tooltip" data-title="New Partcipant"><i class="fa fa-user"></i> New</button></div></li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Participants <span class="badge bg-info" id="labParticipants"></span> </header>
                <ul class="list-group alt panel-body" id="tabParticipants">

                </ul>
            </section>


            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li><div class="btn-group padder-v2"><button class="btn btn-default btn-sm"  data-toggle="modal" id="btnNewTag"><i class="fa fa-tag"></i> New</button></div></li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Tags <span class="badge bg-info" id="labTags"></span>
                </header>
                <table class="panel-body table table-striped b-light" id="tabTags">
                    <thead>
                    <tr>

                        <th class="th-sortable" data-toggle="class">Tag <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                        <th width="10">Action</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </section>


            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Version History
                </header>


                <table class="panel-body table table-striped b-light" id="tabVersionHistory">
                    <thead>
                    <tr>

                        <th class="th-sortable" data-toggle="class">Version <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                        <th>Date/Time</th>
                        <th>User</th>
                        <th width="10">Action</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </section>

            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Event History </header>
                <table class="panel-body table table-striped b-light" id ="tabEventHistory">
                    <thead>
                    <tr>

                        <th class="th-sortable" data-toggle="class">Event <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                        <th>Date/Time</th>
                        <th>User</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </section>
        </div>
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
                                    <div class="h4 font-bold"><a href="#" id="owner" data-title="Owner">AJ McClary</a></div>
                                    <small class="text-muted">Owner</small></div>
                                <div class="col-xs-3 b-r">
                                    <div class="h4 font-bold"><a href="#" id="createDate" data-title="Create Date">MM/DD/YYYY</a></div>
                                    <small class="text-muted">Created Date</small></div>
                                <div class="col-xs-3 b-r">
                                    <div class="h4 font-bold"><a href="#" id="assignee" data-title="Assignee">AJ McClary</a></div>
                                    <small class="text-muted">Assigned To</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#" id="type" data-title="Type">Evidence</a></div>
                                    <small class="text-muted">Type</small></div>
                                <div class="col-xs-2">
                                    <div class="h4 font-bold"><a href="#" id="status" data-title="Status">Draft</a></div>
                                    <small class="text-muted">Status</small></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="wrapper ">

                <div class="row" id="tabDocViewer">
                    <div class="col-md-12">
                        <section class="panel b-a">
                            <div id="divDocViewer" style="width:100%"></div>
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
            </div>
        </section>
    </section>
</aside>
</section>
</jsp:body>
</t:detail>
