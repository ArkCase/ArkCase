<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="audit.page.title" text="Audit | ACM | Armedia Case Management" /></title>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/audit/audit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

    <!-- JTable -->
    <link rel="stylesheet" href="<c:url value='/'/>resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_jtable}/${js_jtable}"></script>
</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black">Audit</h3>
                    </div>
                </section>
                <section class="panel panel-default">
                    <div class="row wrapper">
                        <div class="col-sm-4 m-b-xs">
                            <select class="input-sm form-control input-s-sm inline v-middle">
                                <option value="0">Action</option>
                                <option value="1">Export to Excel</option>
                            </select>
                            <button class="btn btn-sm btn-default">Apply</button>
                        </div>
                        <div class="col-sm-5 m-b-xs">
                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-sm btn-default active">
                                    <input type="radio" name="options" id="option1">
                                    Today </label>
                                <label class="btn btn-sm btn-default">
                                    <input type="radio" name="options" id="option2">
                                    This Week </label>
                                <label class="btn btn-sm btn-default">
                                    <input type="radio" name="options" id="option3">
                                    This Month </label>
                            </div>
                        </div>
                        <div class="col-sm-3">
                            <div class="input-group">
                                <input type="text" class="input-sm form-control" placeholder="Search">
                                <span class="input-group-btn">
                                    <button class="btn btn-sm btn-default" type="button">Go!</button>
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <section class="panel b-a ">
                            <div id="divAudit" style="width:100%"></div>
                        </section>
                    </div>

                </section>
            </section>
        </section>
    </section>

</jsp:body>
</t:layout>





