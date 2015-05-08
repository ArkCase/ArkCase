<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:layout>
	<jsp:attribute name="endOfHead">
	    <title><spring:message code="caseFileNew.page.title" text="Case File | ACM | Armedia Case Management" /></title>
	</jsp:attribute>

	<jsp:body>
        <section class="vbox">
            <section class="scrollable padder">
                <section class="row m-b-md">
                    <div class="col-sm-12">
                        <h3 class="m-b-xs text-black" data-i18n="casefile:wizard.title">New Case File</h3>
                    </div>
                </section>

                <div class="row">
                    <div class="col-sm-12">

                        <script xmlns="http://www.w3.org/1999/xhtml"
                                src="${newCaseFileFormUrl}"
                                type="text/javascript">
                        </script>

                    </div>
                </div>

            </section>
        </section>
	</jsp:body>
</t:layout>