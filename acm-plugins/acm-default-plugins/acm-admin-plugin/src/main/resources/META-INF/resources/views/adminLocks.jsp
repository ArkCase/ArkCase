<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout>
<jsp:attribute name="endOfHead">
    <title>${pageDescriptor.title}</title>
</jsp:attribute>


<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/admin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminObject.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminEvent.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminPage.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminRule.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminService.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/META-INF/resources/resources/js/admin/admin/admin/adminCallback.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/'/>resources/vendors/${vd_slimscroll}/${js_slimscroll}"></script>

</jsp:attribute>

<jsp:body>
    <section id="content">
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black">Admin: Locks</h3>
                    </div>
                </section>






            </section>

        </section>
    </section>
</jsp:body>
</t:layout>


