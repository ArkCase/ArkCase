<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="audit.page.title" text="Audit | ACM | Armedia Case Management" /></title>
    <div id="auditDetailData" itemscope="true" style="display: none">
        <span itemprop="auditReportUrl">${auditReportUrl}</span>
        <span itemprop="auditCriteria">${auditCriteria}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/audit/audit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/audit/auditService.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="hbox stretch">
            <aside class="aside-md bg-light dker b-r" id="subNav">
                <section class="scrollable">
                    <div class="wrapper">
                        <button class='btn btn-default btn-sm center-block' type='button' id='generateReport'>Generate Audit Report</button>
                    </div>
                </section>
            </aside>
            <aside>
                <iframe src="" name="audit_iframe" style="width:100%; height:100%;" frameborder="0"></iframe>
            </aside>
        </section>
    </section>

</jsp:body>
</t:layout>





