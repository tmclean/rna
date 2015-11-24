<!DOCTYPE html>

	<head>
		<title>Synthetic Service Registry</title>
		<link rel="stylesheet" type="text/css" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
	  
		<style>
			body
			{
				font-size: 10pt;
			}
			
	  		.table
	  		{
	  			margin-bottom: 0px;
	  		}
	  		
	  		.header
	  		{
	  			margin-top: 0px;
	  			margin-bottom: 10px;
	  			background-color: #55AAFF;
	  		}
	  		
	  		.cons-divider
	  		{
	  			height: 1px;
	  			background-color: #DDD;
	  			margin-bottom: 2px;
	  		}
		</style>
	</head>
	
	<body>
		<div class="container">
			<div class="row">
				<div class="col-lg-12">
				<nav class="navbar navbar-default" role="navigation">
					<div class="navbar-header">
						<a class="navbar-brand" href="#">Synthetic Service Registry</a>
					</div>
				</nav>
				<div>
					<h4>Synthetic Models</h4>
				</div>
				<table class="table table-condensed table-bordered">
					<tr>
						<th>Model Name</th>
						<th>Internal Class</th>
						<th>Constructors</th>
						<th>Fields</th>
						<th>Methods</th>
					</tr>
					<tbody>
						<#list models?keys as modelName>
							<tr>
								<td>${modelName}</td>
								<td>${models[modelName]['className']}</td>
								<td>
									<#list models[modelName]['constructors'] as constructor>
										<#if constructor?has_content>
											<table class="table table-condensed">
												
													<tr>
														<th>Constructor</th>
													</tr>
												
													<#list constructor as consParam>
														<tr>
															<td>${consParam}</td>
														</tr>
													</#list>
													
											</table>
										<#else>
											<b>Default Constructor</b>
										</#if>
										
										<div class="cons-divider"></div>
									</#list>
								</td>
								<td>
									<table class="table table-condensed">
										<tr>
											<th>Name</th>
											<th>Type</th>
										</tr>
										<#list models[modelName]['fields']?keys as fieldName>
											<tr>
												<td>${fieldName}</td>
												<td>${models[modelName]['fields'][fieldName]}</td>
											</tr>
										</#list>
									</table>
								</td>
								<td>
									<table class="table table-condensed">
										<tr>
											<th>Name</th>
											<th>Parameters</th>
											<th>Returns</th>
										</tr>
										<#list models[modelName]['methods']?keys as methodName>
											<tr>
												<td>${methodName}</td>
												<td>
													<#list models[modelName]['methods'][methodName]['params'] as param>
														${param}<br/>
													</#list>
												</td>
												<td>${models[modelName]['methods'][methodName].returns}</td>
											</tr>
										</#list>
									</table>
								</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		</div>
	</body>
</html>