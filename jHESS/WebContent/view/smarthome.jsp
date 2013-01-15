<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Smart Home View</title>
</head>
<body>

<table border="1" >
    <tr>
        <th>ID</th>
       <th>Status</th>
    </tr>
    <c:forEach var="device" items="${devices}">
        <tr>
            <td>${device.id}</td>
            <td>${device.currentDeviceStatus}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>