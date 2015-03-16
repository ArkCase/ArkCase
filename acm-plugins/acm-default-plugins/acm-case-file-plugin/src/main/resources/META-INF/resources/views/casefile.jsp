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
        <span itemprop="urlRoiForm">${roiFormUrl}</span>
        <span itemprop="urlElectronicCommunicationForm">${electronicCommunicationFormUrl}</span>
        <span itemprop="urlChangeCaseStatusForm">${changeCaseStatusFormUrl}</span>
        <span itemprop="urlEditChangeCaseStatusForm">${editChangeCaseStatusFormUrl}</span>
        <span itemprop="enableFrevvoFormEngine">${enableFrevvoFormEngine}</span>
        <span itemprop="formDocuments">${formDocuments}</span>
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


    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_summernote}/summernote.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_summernote}/${js_summernote}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>


    <link href="<c:url value='/resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css'/>" rel="stylesheet">
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_fancytree}/${js_fancytree_table}'/>"></script>
    <script src="<c:url value='/resources/vendors/${vd_contextmenu}/${js_contextmenu}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>

<style>
    table.fancytree-ext-table {
        width: 100%;
        outline: 0;
    }

    table.fancytree-ext-table tbody tr td {
        border: 0px;
    }
</style>
</jsp:attribute>

<jsp:body>
<section id="content">
    <section class="vbox">
        <section class="scrollable">
            <section class="hbox stretch"><!-- /.aside -->
                <!-- .aside -->

                <aside class="aside-lg bg-light lt">
                    <section class="vbox animated fadeInLeft">
                        <section class="scrollable">
                            <header class="dk header">
                                <h3 class="m-b-xs text-black pull-left"><spring:message code="caseFile.page.descShort" text="Cases" /></h3>
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
                                                <div class="col-xs-2 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="caseTitle" data-type="text" data-pk="1" data-title="Enter Case Title"></a> </div>
                                                    <small class="text-muted"><a href="#" id="caseNumber" ></a></small></div>
                                                <div class="col-xs-2 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-title="Enter Incident Date"></a></div>
                                                    <small class="text-muted">Create Date</small></div>

                                                <div class="col-xs-1 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-title="Enter Priority">High</a></div>
                                                    <small class="text-muted">Priority</small></div>
                                                <div class="col-xs-2 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-title="Enter Assignee"></a></div>
                                                    <small class="text-muted">Assigned To</small></div>
                                                <div class="col-xs-2 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-title="Enter Subject Type"></a></div>
                                                    <small class="text-muted">Case Type</small></div>
                                                <div class="col-xs-2 b-r">
                                                    <div class="h4 font-bold"><a href="#" id="dueDate" data-type="date" data-pk="1" data-title="Enter Due Date"></a></div>
                                                    <small class="text-muted">Due Date</small></div>
                                                <div class="col-xs-1">
                                                    <div class="h4 font-bold"><a href="#" id="status" ></a></div> <small class="text-muted">State</small></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row" id="tabTopBlank">
                                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(No case is selected)</p>
                                </div>
                            </div>

                            <div>
                                <div class="col-md-12" id="tabBlank" style="display:none;">
                                </div>


                                <div class="wrapper" id="tabTitle" style="display:none;">
                                    <div class="pull-left inline">
                                        <div class="btn-group">
                                            <label class="checkbox-inline">
                                                <input type="checkbox" id="restrict"> Restrict ?
                                            </label>
                                        </div>
                                    </div>

                                    <div class="pull-right inline">
                                        <div class="btn-group">
                                        	<button class="btn btn-default btn-sm" data-title="Edit Case File"><i class="fa fa-edit"></i> Edit</button>
                                            <%--<button class="btn btn-default btn-sm" data-title="Change Case Status"  data-toggle="modal" data-target="#closeCase"><i class="fa fa-archive"></i> Close</button>--%>
                                            <button class="btn btn-default btn-sm" data-title="Change Case Status" style="display: none" ><i class="fa fa-edit"></i> Change Case Status</button>
                                            <%--<button class="btn btn-default btn-sm" data-title="Consolidate Case"  data-toggle="modal" data-target="#consolidateCase"><i class="fa fa-random"></i> Consolidate</button>--%>
                                            <button class="btn btn-default btn-sm" data-title="Reinvestigate Case File"><i class="fa fa-reply"></i> Reinvestigate</button>
                                            <button class="btn btn-default btn-sm" data-title="Consolidate Case"><i class="fa fa-random"></i> Consolidate</button>
                                            <button class="btn btn-default btn-sm" id="btnSubscribe"><i class="fa fa-bullhorn"></i> Subscribe</button>
                                            <!-- Modal -->
                                            <div class="modal fade" id="changeCaseStatus" tabindex="-1" role="dialog" aria-labelledby="labChangeCaseStatus" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                            <h4 class="modal-title" id="labChangeCaseStatus">Change Case Status</h4>
                                                        </div>
                                                        <div class="modal-body"> Are you sure you want to change the status for this case? </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                            <button type="button" class="btn btn-primary">Change Case Status</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal fade" id="consolidateCase" tabindex="-1" role="dialog" aria-labelledby="labConsolidateCase" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                            <h4 class="modal-title" id="labConsolidateCase">Consolidate Case</h4>
                                                        </div>
                                                        <div class="modal-body">
                                                            <section class="row">
                                                                <div class="col-sm-12">
                                                                    <label for="edtConsolidateCase" class="label">Enter the case you would like to consolidate with:</label>
                                                                    <input id="edtConsolidateCase" type="text" class="form-control" placeholder="Case #" >
                                                                </div>
                                                            </section>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                            <button type="button" class="btn btn-primary">Consolidate Case</button>
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
                                                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                        <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i></button>
                                                        <ul class="dropdown-menu pull-right">
                                                            <li><a href="#">Other menu items</a></li>
                                                        </ul>
                                                    </div>
                                                </li>
                                                <li>&nbsp;</li>
                                            </ul>
                                            </span> <a href="#" class="font-bold">Details</a></div>
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
                                    <section class="panel b-a ">
                                        <div id="divDocs" style="width:100%"></div>
                                        <form id="formAddDocument" style="display:none;">
                                                <%--<input type="file" id="file" name="file">--%>
                                            <input id="addDocument" type="file" name="files[]" multiple/>
                                                <%--<input type="submit">--%>
                                        </form>
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
                                        <div id="divEvents" style="width:100%"></div>
                                    </section>
                                </div>

                                <div class="col-md-12" id="tabTemplates" style="display:none;">
                                    <section class="panel b-a ">
                                        <div id="divTemplates" style="width:100%"></div>
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

                            </div>
                        </section>
                    </section>
                </aside>
                <!-- /.aside -->

            </section>
        </section>
    </section>
</section>
</jsp:body>
</t:layout>
