<!DOCTYPE html>
<%@include file="/fragments/header.jspf" %>
</head>

<body>
<%@include file="/fragments/topbar.jspf" %>

<div id="content">
    <div id="divComplaint">Here we are in the new complaint wizard</div>
    <form name="complaintWizard" method="POST" action="<c:url value='/plugin/complaint'/>" enctype="application/x-www-form-urlencoded">

        <label for="complaintNumber">Complaint Number: </label>
        <input type="text" name="complaintNumber" id="complaintNumber" value="${complaint.complaintNumber}"/>
        <br/>
        <label for="complaintType">Complaint Type: </label>
        <input type="text" name="complaintType" id="complaintType" value="${complaint.complaintType}"/>
        <br/>
        <label for="complaintTitle">Complaint Title: </label>
        <input type="text" name="complaintTitle" id="complaintTitle" value="${complaint.complaintTitle}"/>
        <br/>
        <label for="details">Complaint Details: </label>
        <input type="text" name="details" id="details" value="${complaint.details}"/>
        <br/>
        <label for="priority">Priority: </label>
        <input type="text" name="priority" id="priority" value="${complaint.priority}"/>
        <br/>
        <label for="incidentDate">Incident Date: </label>
        <input type="text" name="incidentDate" id="incidentDate" value="${complaint.incidentDate}"/>
        <br/>

        <input type="hidden" name="complaintId" id="complaintId" value="${complaint.complaintId}"/>
        <button type="submit" value="Save">Save</button>
        <button type="reset" value="Reset">Reset</button>

    </form>
</div>

</body>

<%@include file="/fragments/footer.jspf" %>
