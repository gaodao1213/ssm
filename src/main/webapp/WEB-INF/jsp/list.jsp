<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>

booklist<br>
<c:if test="${list!=null}">
    <c:forEach var="book" items="${list}">bookName:${book.name}<br></c:forEach>
</c:if>
</body>
</html>
