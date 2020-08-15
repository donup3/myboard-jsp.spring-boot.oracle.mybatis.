<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@include file="../includes/header.jsp" %>
<div class="row">
    <div class="col-lg-12">
        <h1 class="page-header">Board List</h1>
    </div>
    <!-- /.col-lg-12 -->
</div>
<!-- /.row -->
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Board List Page
                <button id="regBtn" class="btn btn-default btn-xs pull-right">Register</button>
            </div>
            <!-- /.panel-heading -->
            <div class="panel-body">
                <table width="100%" class="table table-striped table-bordered table-hover" >
                    <thead>
                        <tr>
                            <th>번호</th>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>작성일</th>
                            <th>수정일</th>
                        </tr>
                    </thead>
                    <c:forEach items="${list}" var="board">
                        <tr>
                            <td><c:out value="${board.bno}"/></td>
                            <td><c:out value="${board.title}"/></td>
                            <td><c:out value="${board.writer}"/></td>
                            <td><fmt:formatDate pattern="yyyy-MM-dd" value="${board.regDate}"/></td>
                            <td><fmt:formatDate pattern="yyyy-MM-dd" value="${board.updateDate}"/></td>
                        </tr>
                    </c:forEach>
                </table>
                <!-- Modal -->
                <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                <h4 class="modal-title" id="myModalLabel">Modal title</h4>
                            </div>
                            <div class="modal-body">
                                처리가 완료되었습니다.
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary">Save changes</button>
                            </div>
                        </div>
                        <!-- /.modal-content -->
                    </div>
                    <!-- /.modal-dialog -->
                </div>
                <!-- /.modal -->
            </div>
            <!-- /.panel-body -->
        </div>
        <!-- /.panel -->
    </div>
    <!-- /.col-lg-12 -->
</div>
<!-- /.row -->

<%@include file="../includes/footer.jsp" %>

<script type="text/javascript">
    $(document).ready(function () {
        var result='<c:out value="${result}"/>'

        checkModal(result);

        function checkModal(result) {
            if(result===''){
                return;
            }
            if(parseInt(result)>0){
                $(".modal-body").html("게시글 "+parseInt(result)+" 번이 등록되었습니다.");
            }
            $("#myModal").modal("show");
        }

        $("#regBtn").on("click",function (e) {
            self.location="/board/register";
        })
    })
</script>