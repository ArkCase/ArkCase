<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title><spring:message code="admin.page.title" text="Admin | ACM | Armedia Case Management" /></title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/admin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/admin/admin/adminCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/jquery.slimscroll.min.js"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black"><spring:message code="admin.page.descShort" text="Admin" /></h3>
                    </div>
                </section>



                test
                <input type="button" value="Test" id="test" />




            </section>

        </section>
    </section>
</jsp:body>
</t:layout>


