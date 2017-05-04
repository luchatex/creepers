<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!-- Main content starts -->
<%
    String path = request.getContextPath();
			String socPath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<div class="content">
	<!-- Main bar -->
	<div class="mainbar">
		<!-- Page heading -->
		<div class="page-head">
			<h2 class="pull-left">
				<i class="icon-table"></i> 全量任务url初始化
			</h2>

			<!-- Breadcrumb -->
			<div class="bread-crumb pull-right">
				<a href="${ctx}"><i class="icon-home"></i> Home</a> <span
					class="divider">/</span> <a href="${ctx}" class="bread-current">Dashboard</a>
			</div>
			<div class="clearfix"></div>
		</div>
		<!-- Page heading ends -->

		<!-- Matter -->
		<div class="matter">
			<div class="container">
				<div class="row">
					<div class="col-md-12">
						<div class="widget wgreen">
							<div class="widget-head">
								<div class="pull-left">爬虫任务队列初始化</div>
								<div class="widget-icons pull-right">
									<a href="#" class="wminimize"><i class="icon-chevron-up"></i></a>
									<a href="#" class="wclose"><i class="icon-remove"></i></a>
								</div>
								<div class="clearfix"></div>
							</div>

							<div class="widget-content">
								<div class="padd">
									<!-- Form starts.  -->
									<form id="queryForm" class="form-horizontal"
										action="${ctx}/taskList/initTaskList" method="get">
										<div class="form-group">
											<div class="col-md-12"></div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">任务类型</label>
											<div class="col-md-3">
												<select id="taskType" name="taskType" class="form-control"  onchange="findTaskTemplate()">
													<c:forEach items="${taskTypeList }" var="item">
														<option value="${item.value }">${item.name }</option>
													</c:forEach>
												</select>
											</div>
											<label class="col-md-2 control-label">请求url</label>
											<div class="col-md-3">
												<input class="form-control" type="text" id="url" name="url"
													value="${url}">
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">请求方式</label>
											<div class="col-md-3">
												<select id="httpType" name="httpType" class="form-control">
													<option value="get" <c:if test="${'get' eq httpType}">selected</c:if>>get</option>
													<option value="post" <c:if test="${'post' eq httpType}">selected</c:if>>post</option>
												</select>
											</div>
											<label class="col-md-2 control-label">请求参数</label>
											<div class="col-md-3">
												<input class="form-control" type="text" id="paramMap"
													name="paramMap" value="${paramMap}">
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-2 control-label">开始下标</label>
											<div class="col-md-3">
												<input class="form-control" type="text" id="startIndex"
													name="startIndex" value="${memo}">
											</div>
											<label class="col-md-2 control-label">结束下标</label>
											<div class="col-md-3">
												<input class="form-control" type="text" id="endIndex"
													name="endIndex" value="">
											</div>
										</div>
										<input type="hidden" id="wsId" value="">
										<input type="hidden" id="id" value="">
										<div class="form-group">
											<div class="col-md-3">
												<a id="submit" herf="#" onclick="initTask()"
													class="btn btn-default">提交</a>
												<button type="reset" class="btn btn-warning">重置</button>
											</div>
										</div>
									</form>
								</div>
							</div>
							<div class="widget-foot"></div>
						</div>
					</div>
				</div>
			</div>
			<!-- container ends -->
			<div class="container">
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-head">
								<span>当前任务完成度：</span>
								<div class="progress progress-striped active">
									<div id="progressbar" class="progress-bar progress-bar-success"
										role="progressbar" aria-valuenow="60" aria-valuemin="0"
										aria-valuemax="100" style="width: 0%;">
										<span id="progressPer" style="color: black">0%</span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- container ends -->
		</div>
		<!-- Matter ends -->
	</div>
</div>

<script type="text/javascript">
	"use strict";
	function initTask() {
		var taskType = $("#taskType").val();
		var url = $("#url").val();
		var httpType = $("#httpType").val();
		var paramMap = $("#paramMap").val();
		var startIndex = $("#startIndex").val();
		var endIndex = $("#endIndex").val();
		var session = $("#wsId").val();
		$.ajax({
			type : "get",
			url : '${ctx}/taskList/initTaskList',
			data : {
				'taskType' : taskType,
				'url' : url,
				'httpType' : httpType,
				'paramMap' : paramMap,
				'startIndex' : startIndex,
				'endIndex' : endIndex,
				'session' : session
			},
			success : function(data) {
// 				alert(data.message);
				$("#submit").removeAttr("disabled");
			},
			error : function() {
				alert("爬虫任务队列初始化失败!");
			},
		});
		$("#submit").attr("disabled","disabled");
	}
	function findTaskTemplate(){
		var taskType = $("#taskType").val();
		$.ajax({
			type:"get",
			url: "${ctx}/taskList/findTaskTemplate",
			data:{
				"taskType":taskType,
			},
			success : function(data) {
				if(null!=data){
					$('#id').attr("value", data.id);
					$('#url').attr("value", data.url);
					$('#httpType').attr("value", data.httpType);
					$('#paramMap').attr("value", data.paramMap);
					$('#startIndex').attr("value", data.memo==null?0:parseInt(data.memo)+1);
// 					$("#startIndex").attr("disabled","disabled");
// 					$("#endIndex").attr("disabled","disabled");
				} else {
					$('#id').attr("value", "");
					$('#url').attr("value", "");
					$('#httpType').attr("value", "get");
					$('#paramMap').attr("value", "");
					$('#startIndex').attr("value", "0");
				}
			},
			error : function() {
				alert("爬虫任务队列初始化失败!");
			},
		})
	}
	var ws = null;
	function connect(wsurl) {
		if ('WebSocket' in window) {
			ws = new WebSocket(wsurl);
		} else if ('MozWebSocket' in window) {
			ws = new MozWebSocket(wsurl);
		} else {
			alert('WebSocket is not supported by this browser.');
			return;
		}
		ws.onopen = function(event) {
		};
		ws.onmessage = function(event) {
			if (event.data.indexOf("id") >= 0) {
				$("#wsId").val(event.data);
			} else {
				var data=event.data.split("|");
				var progressPer = data[0]/data[1]*100;
				$("#progressbar").attr("style", "width: " + progressPer.toFixed(2) + "%;")
				$("#progressbar").parent().parent().attr("title", "已完成" + data[0]+"个，共计"+ data[1] + "个")
				$("#progressPer").html(progressPer.toFixed(2) + "%");
				if(data[0]==data[1]){
					$("#submit").removeAttr("disabled");
				}
			}
		};
		ws.onclose = function(event) {
// 			alert(event.data);
		};
	}

	function echo() {
		if (ws != null) {
			var message = document.getElementById('message').value;
			log('Sent: ' + message);
			ws.send(message);
		} else {
			alert('WebSocket connection not established, please connect.');
		}
	}

	function initialize() {
		if (window.location.protocol == 'http:') {
			var wsuri = "<%=socPath%>wsecho";
			connect(wsuri);
		} else {
			connect("ws://" + request.getServerName() + ":"
					+ request.getServerPort() + path + "/wsecho");
		}
	};
	initialize();
	function cv() {
		var jobGroupDesc = $('#jobGroup option:selected').attr('desc');
		$('#description').attr("value", jobGroupDesc);
		$('#description').html(jobGroupDesc);
	}
</script>
