<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:layout>
	<jsp:attribute name="endOfHead">
	    <title>${pageDescriptor.title}</title>
	</jsp:attribute>

	<jsp:body>	
		<section id="content">
			<section class="vbox">
				<section class="scrollable padder">
					<section class="row m-b-md">
					    <div class="col-sm-12">
					        <h3 class="m-b-xs text-black">${pageDescriptor.descShort}</h3>
					    </div>
					</section>
					
					<div class="row">
						<div class="col-sm-12">

							<script xmlns="http://www.w3.org/1999/xhtml"
                    				src="${newComplaintFormUrl}"
                    				type="text/javascript">
							</script>

						</div>
					</div>

				</section>
			</section>
		</section>
	</jsp:body>
</t:layout>