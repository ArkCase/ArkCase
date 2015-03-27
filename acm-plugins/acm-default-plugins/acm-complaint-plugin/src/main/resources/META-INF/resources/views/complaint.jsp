<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="complaint.page.title" text="Complaints | ACM | Armedia Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="objType">COMPLAINT</span>
        <span itemprop="objId">${objId}</span>
        <span itemprop="treeFilter">${treeFilter}</span>
        <span itemprop="treeSort">${treeSort}</span>
        <span itemprop="token">${token}</span>

        <span itemprop="closeComplaintFormUrl">${closeComplaintFormUrl}</span>
        <%--<span itemprop="editCloseComplaintFormUrl">${editCloseComplaintFormUrl}</span>--%>
        <span itemprop="roiFormUrl">${roiFormUrl}</span>
        <span itemprop="electronicCommunicationFormUrl">${electronicCommunicationFormUrl}</span>
        <span itemprop="formDocuments">${formDocuments}</span>
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

/////////////////////////////////////////////////////////////////////
<%--<style>--%>
    <%--table.fancytree-ext-table {--%>
        <%--width: 100%;--%>
        <%--outline: 0;--%>
    <%--}--%>

    <%--table.fancytree-ext-table tbody tr td {--%>
        <%--border: 0px;--%>
    <%--}--%>
<%--</style>--%>
//////////////////////////////////////////////////////////////////////
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
                                <h3 class="m-b-xs text-black pull-left"><spring:message code="complaint.page.descShort" text="Complaints" /></h3>
                                <div class="btn-group inline select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm" id="ulSort">
                                        <%--<li><a href="#">Sort Date Ascending</a></li>--%>
                                        <%--<li><a href="#">Sort Date Descending</a></li>--%>
                                        <%--<li><a href="#">Sort Complaint ID Ascending</a></li>--%>
                                        <%--<li><a href="#">Sort Complaint ID Ascending</a></li>--%>
                                    </ul>
                                </div>
                                <div class="btn-group select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm" id="ulFilter">
                                        <%--<li><a href="#">All Open Complaints</a></li>--%>
                                        <%--<li><a href="#">All Complaints I've Created</a></li>--%>
                                        <%--<li><a href="#">All Closed No Further Action</a></li>--%>
                                        <%--<li><a href="#">All Closed Refer External</a></li>--%>
                                        <%--<li><a href="#">All Closed Added to Existing Case</a></li>--%>
                                        <%--<li><a href="#">All Closed Open Investigation </a></li>--%>
                                            <%--<li><a href="<c:url value='/'/>plugin/complaint/wizard">New Complaint</a></li>--%>
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
                                                <div class="col-xs-4 b-r">
                                                        <%--<div class="h4 font-bold"><a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Complaint Title"></a> </div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="complaintTitle" data-type="text" data-pk="1" data-title="Enter Complaint Title"></a> </div>
                                                    <small class="text-muted"><a href="#" id="complaintNum" ></a></small></div>
                                                <div class="col-xs-2 b-r">
                                                        <%--<div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-url="/post" data-title="Enter Incident Date"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="incident" data-type="date" data-pk="1" data-title="Enter Incident Date"></a></div>
                                                    <small class="text-muted">Incident Date</small></div>
                                                <div class="col-xs-1 b-r">
                                                        <%--<div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-url="/post" data-title="Enter Priority"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="select" data-pk="1" data-title="Enter Priority"></a></div>
                                                    <small class="text-muted">Priority</small></div>
                                                <div class="col-xs-2 b-r">
                                                        <%--<div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-url="/post" data-title="Enter Assignee"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="assigned" data-type="select" data-pk="1" data-title="Enter Assignee"></a></div>
                                                    <small class="text-muted">Assigned To</small></div>
                                                <div class="col-xs-2 b-r">
                                                        <%--<div class="h4 font-bold"><a href="#" id="group" data-type="select" data-pk="1" data-url="/post" data-title="Enter Owning Group"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="group" data-type="select" data-pk="1" data-title="Enter Owning Group"></a></div>
                                                    <small class="text-muted">Owning Group</small></div>
                                                <div class="col-xs-2 b-r">
                                                        <%--<div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-url="/post" data-title="Enter Subject Type"></a></div>--%>
                                                    <div class="h4 font-bold"><a href="#" id="type" data-type="select" data-pk="1" data-title="Enter Incident Category"></a></div>
                                                    <small class="text-muted">Incident Category</small></div>
                                                <div class="col-xs-1">
                                                    <div class="h4 font-bold"><a href="#" id="status" ></a></div>
                                                    <small class="text-muted">State</small></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>


                                <div class="row" id="tabTopBlank">
                                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(No complaint is selected)</p>
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
                                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" id = "closeComplaint" data-title="Close Complaint"><i class="fa fa-archive"></i> Close Complaint</button>
                                                    <%--<input id="closeComplaintFormUrl" type="hidden" value="${closeComplaintFormUrl}" />--%>

                                                    <button class="btn btn-default btn-sm" id="btnSubscribe"><i class="fa fa-bullhorn"></i> Subscribe</button>
                                                </div>
                                            </div>

                                            <div class="pull-left inline">
                                                <div class="btn-group">
                                                    <label class="checkbox-inline">
                                                        <input type="checkbox" id="restrict"> Restrict ?
                                                    </label>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                    <hr/>



<<<<<<< HEAD

                                    <div class="row" id="tabDetail" style="display:none;">
                                        <div class="col-md-12">
                                            <section class="panel b-a ">
                                                <div class="panel-heading b-b bg-info">
                                                    <ul class="nav nav-pills pull-right">
                                                        <li>
                                                            <div class="btn-group padder-v2">
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Edit"><i class="fa fa-pencil"></i></button>
                                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Save"><i class="fa fa-save"></i></button>
                                                                <ul class="dropdown-menu pull-right">
                                                                    <li><a href="#">Other menu items</a></li>
                                                                </ul>
                                                            </div>
                                                        </li>
                                                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                                                    </ul>
                                                    </span> <a href="#" class="font-bold">Details</a> </div>
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
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divDocuments" style="width:100%"></div>
                                                <form id="formAddDocument" style="display:none;">
                                                        <%--<input type="file" id="file" name="file">--%>
                                                    <input id="addDocument" type="file" name="files[]" multiple/>
                                                        <%--<input type="submit">--%>
                                                </form>
                                                    <%--<input id="roiFormUrl" type="hidden" value="${roiFormUrl}" />--%>
                                                    <%--<input id="electronicCommunicationFormUrl" type="hidden" value="${electronicCommunicationFormUrl}" />--%>
                                                    <%--<input id="formDocuments" type="hidden" value='${formDocuments}' />--%>
                                            </section>
                                        </div>
                                    <%--</div>--%>

<!-- 11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111 -->
                                    <%--<div class="row">--%>
                                    <div class="col-md-12">
                                    <section class="panel b-a">
                                        <div class="panel-heading b-b bg-info">  <ul class="nav nav-pills pull-right">
                                            <li style="margin-right:5px"></li>
                                            <li>
                                                <div class="btn-group padder-v2">
                                                    <button class="btn btn-default btn-sm" data-toggle="modal" data-target="#createnewfolder"><i class="fa fa-folder"></i> New Folder</button>








                                                </div>
                                            </li>
                                            <li>
                                                <div class="btn-group padder-v2">
                                                    <button class="btn btn-default btn-sm" data-toggle="modal" data-target="#emailDocs"><i class="fa fa-share"></i> Email</button>
                                                </div>
                                            </li>
                                            <li>
                                                <div class="btn-group padder-v2">
                                                    <button class="btn btn-default btn-sm" onClick="window.open('documents.html', '_blank');"><i class="fa fa-print"></i> Print</button>
                                                </div>
                                            </li>
                                            <li> </li>
                                        </ul>








                                            <a href="#" class="font-bold"><div>Documents5 <img id="imgFileLoading" src="<c:url value='/'/>resources/vendors/${acm_theme}/images/ajax-loader.gif" class="dker" style="display:block;"></div> </a>
                                            <a id="lnkChangePicture" href="#">&nbsp;&nbsp;&nbsp;&nbsp;<u>Change Picture</u></a>
                                            <form id="formDoc" style="display:block;">
                                                <input type="file" id="file" name="file">
                                                    <input type="submit">
                                            </form>
                                        </div>


                                        <div class="modal fade" id="createnewfolder" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                        <h4 class="modal-title" id="myModalLabel">Create Folder</h4>
                                                    </div>
                                                    <div class="modal-body">

                                                        <p>Enter a name for the folder you would like to create:</p>

                                                        <label for="folderName2">Folder Name</label><br/>
                                                        <input type="text" id="folderName2" class="input-lg" placeholder="Folder Name" />

                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                        <button type="button" class="btn btn-primary">Create Folder<</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>


                                        <div class="modal fade" id="emailDocs" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                        <h4 class="modal-title" id="myModalLabel">Send Email</h4>
                                                    </div>
                                                    <div class="modal-body">

                                                        <p>Where would you like to email this file?</p>

                                                        <label for="emailaddy">Email Address</label><br/>
                                                        <input type="text" id="emailaddy" class="input-lg" placeholder="Email Address" />

                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                        <button type="button" class="btn btn-primary">Send Email</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <table id="treeDoc" class="table table-striped th-sortable table-hover">

                                            <thead>
                                            <tr>
                                                <th><span class='fancytree-checkbox'></span></th>
                                                <th>ID</th>
                                                <th width="40%">Title</th>
                                                <th>Type</th>
                                                <th>Created</th>
                                                <th>Author</th>
                                                <th>Version</th>
                                                <th>Status</th>
                                                <th colspan="2"></th>
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
                                                <td></td>
                                            </tr>
                                            </tbody>
                                        </table>

                                    </section>



                                    <%--<h3>Additional Action Examples</h3>--%>
                                    <%--<p>I added these modal window examples below to save time (it takes longer for me to code it into the FancyTree).</p>--%>


                                    <button class="btn btn-default" data-toggle="modal" data-target="#createsubfolder">

                                        <span class="text">Create Subfolder</span>
                                    </button> <div class="modal fade" id="createsubfolder" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                    <h4 class="modal-title" id="myModalLabel">Create Subfolder</h4>
                                                </div>
                                                <div class="modal-body">

                                                    <p>Enter a name for the subfolder you would like to create:</p>

                                                    <label for="folderName">Folder Name</label><br/>
                                                    <input type="text" id="folderName" class="input-lg" placeholder="Folder Name" />

                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                    <button type="button" class="btn btn-primary">Create Subfolder</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>






                                    <button class="btn btn-default" data-toggle="modal" data-target="#deletesubfolder">

                                        <span class="text">Delete Subfolder</span>
                                    </button> <div class="modal fade" id="deletesubfolder" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                    <h4 class="modal-title" id="myModalLabel">Delete Subfolder</h4>
                                                </div>
                                                <div class="modal-body">


                                                    <p>Are you sure you want to delete [folder name] from [partent folder]?</p>


                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                                    <button type="button" class="btn btn-primary">Delete Subfolder</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>



                                    <button class="btn btn-default" data-toggle="modal" data-target="#replaceFile">

                                        <span class="text">Replace File</span>
                                    </button> <div class="modal fade" id="replaceFile" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                    <h4 class="modal-title" id="myModalLabel">Replace File</h4>
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



                                    <button class="btn btn-default" data-toggle="modal" data-target="#delete">

                                        <span class="text">Delete</span>
                                    </button> <div class="modal fade" id="delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                    <h4 class="modal-title" id="myModalLabel">Delete</h4>
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



                                    <button class="btn btn-default" data-toggle="modal" data-target="#copy">

                                        <span class="text">Copy</span>
                                    </button> <div class="modal fade" id="copy" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                    <h4 class="modal-title" id="myModalLabel">Copy</h4>
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


                                    <button class="btn btn-default" data-toggle="modal" data-target="#move">

                                        <span class="text">Move</span>
                                    </button> <div class="modal fade" id="move" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                                    <h4 class="modal-title" id="myModalLabel">Move</h4>
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


                                    <button class="btn btn-default" onClick="window.open('documents.html', '_blank');">Edit</button>
                                    <button class="btn btn-default" onClick="window.open('documents.html', '_blank');">View</button>




                                    <div class="btn-group">
                                        <button type="buton" class="dropdown-toggle" data-toggle="dropdown">
                                            <i class="fa fa-cog"></i>
                                        </button>
                                        <ul class="dropdown-menu">
                                            <li><a href="#">Add Subfolder</a></li>
                                            <li><a href="#">Add Document</a></li>
                                            <li><a href="#">Delete Subfolder</a></li>
                                        </ul>
                                    </div>


                                    <div class="btn-group">
                                        <button type="buton" class="dropdown-toggle" data-toggle="dropdown">
                                            <i class="fa fa-cog"></i>
                                        </button>
                                        <ul class="dropdown-menu">
                                            <li><a href="#">Download</a></li>
                                            <li><a href="#">Replace</a></li>
                                            <li><a href="#">History</a></li>
                                            <li><a href="#">Delete</a></li>
                                            <li><a href="#">Copy</a></li>
                                            <li><a href="#">Move</a></li>
                                            <li><a href="#">Edit</a></li>
                                            <li><a href="#">View</a></li>
                                        </ul>
                                    </div>



                                    </div>
                                    </div>
<!-- 22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222 -->



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



