<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="casefile.page.title" text="Case Files | ACM | Armedia Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="caseFileId">${caseId}</span>
        <span itemprop="roiFormUrl">${roiFormUrl}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">

    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFile.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFilePage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileCallback.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/casefile/caseFileJTable.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <!-- File Manager -->
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_knob}/js/jquery.knob.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/jquery.fileupload.js"></script>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_upload}/js/jquery.iframe-transport.js"></script>

    <!-- Summernote WYSIWYG -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_summernote}/summernote.js"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>


    <link href="<c:url value='/'/>resources/vendors/${vd_fancytree}/skin-win8/ui.fancytree.css" rel="stylesheet">
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/jquery.fancytree.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_fancytree}/jquery.fancytree.table.js"></script>
    <script src="<c:url value='/'/>resources/vendors/${vd_contextmenu}/jquery.ui-contextmenu.js"></script>

    <!-- X-Editable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css" type="text/css"/>
    <script src="<c:url value='/'/>resources/vendors/${vd_x_editable}/js/${js_x_editable}"></script>


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
                                <h3 class="m-b-xs text-black pull-left"><spring:message code="casefile.page.descShort" text="Cases" /></h3>
                                <div class="btn-group inline select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-sort"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm">
                                        <li><a href="#">Sort Date Ascending</a></li>
                                        <li><a href="#">Sort Date Descending</a></li>
                                        <li><a href="#">Sort Case ID Ascending</a></li>
                                        <li><a href="#">Sort Case ID Ascending</a></li>
                                    </ul>
                                </div>
                                <div class="btn-group select pull-right">
                                    <button class="btn btn-default btn-sm  dropdown-toggle" data-toggle="dropdown"> <span class="dropdown-label" style="width: 65px;"><i class="fa fa-filter"></i></span> <span class="caret"></span> </button>
                                    <ul class="dropdown-menu text-left text-sm">
                                        <li><a href="#">All Open Cases</a></li>
                                        <li><a href="#">Cases I've Opened</a></li>
                                        <li><a href="#">Unapproved Cases</a></li>
                                        <li><a href="#">Approved Cases</a></li>
                                        <li><a href="#">Cases From Group</a></li>
                                        <li><a href="#">Closed or Expired Cases</a></li>
                                        <li><a href="#">New Cases</a></li>
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
                                <div class="row" id="tabTop" style="display:none;">
                                    <div class="col-xs-12">
                                        <div class="">
                                            <div class=" clearfix">
                                                <div class="col-xs-2 b-r">

                                                    <div class="h4 font-bold"><a href="#" id="caseTitle" data-type="text" data-pk="1" data-title="Enter Case Title"></a> </div>
                                                    <small class="text-muted" id="caseNumber"></small></div>
                                                <div class="col-xs-2 b-r">

                                                    <div class="h4 font-bold"><a href="#" id="incidentDate" data-type="date" data-pk="1" data-title="Incident Date"></a></div>
                                                    <small class="text-muted">Incident Date</small></div>
                                                <div class="col-xs-2 b-r">

                                                    <div class="h4 font-bold"><a href="#" id="priority" data-type="text" data-pk="1" data-title="Priority"></a></div>
                                                    <small class="text-muted">Priority</small></div>
                                                <div class="col-xs-2 b-r">

                                                    <div class="h4 font-bold"><a href="#" id="assignee" data-type="text" data-pk="1" data-title="Assigned To"></a></div>
                                                    <small class="text-muted">Assigned To</small></div>
                                                <div class="col-xs-2 b-r">

                                                    <div class="h4 font-bold"><a href="#" id="subjectType" data-type="text" data-pk="1" data-title="Subject Type"></a></div>
                                                    <small class="text-muted">Subject Type</small></div>
                                                <div class="col-xs-2 b-r">

                                                    <div class="h4 font-bold"><a href="#" id="status" data-type="text" data-pk="1" data-title="Status"></a></div>
                                                    <small class="text-muted">Status</small></div>
                                                <div class="col-xs-2 b-r">

                                                    <%--<div class="h4 font-bold"><a href="#" id="closeDate" data-type="date" data-pk="1" data-title="Close Date"></a></div>
                                                    <small class="text-muted">Close Date</small></div>--%>

                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row" id="tabTopBlank">
                                    <p>(No case is selected)</p>
                                </div>
                            </div>
                            </div>
                            </div>
                            </div>
                            <div>
                                <div class="wrapper" style="margin: 0;">
                                    <div class="row" id="tabBlank" style="display:none;">
                                        <p></p>
                                    </div>


                                    <div class="row" id="tabTitle" style="display:none;">
                                        <div class="pull-right inline">
                                            <div class="btn-group">
                                                <button class="btn btn-default btn-sm" data-toggle="tooltip" id = "closeCase" data-title="Close Case"><i class="fa fa-archive"></i> Close Case</button>
                                            </div>
                                        </div>
                                        <%--<h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Case Title"> Case Title</a></h4>--%>
                                        <hr/>
                                    </div>



                                    <div class="row" id="tabPerson" style="display:none;">

                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divPerson" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                    <div class="row" id="tabItems" style="display:none;">

                                    <div class="row" id="tabRois" style="display:none;">


                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divRois" style="width:100%"></div>
                                                <input id="roiFormUrl" type="hidden" value="${roiFormUrl}" />
                                            </section>
                                        </div>
                                    </div>


                                    <div class="row" id="tabRoi" style="display:none;">
                                        <p>tabRoi</p>
                                        <div class="col-md-12">
                                            <section class="panel b-a">
                                                <div id="divRoi" style="width:100%"></div>
                                            </section>
                                        </div>
                                    </div>

                                </div>
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





