<#ftl encoding="UTF-8">
<#import "layout.ftl" as layout>
<@layout.layout>

	<!-- Welcome page starts -->
	<style>
		body {
			background: whiteSmoke; height: 100%; font-size: 100%; line-height:1.6em; color:#444;
		}
		
		.header {
			background: #B03F3F; color: white; height: 200px; margin-bottom: 40px;
		}
		
		.header div {
			padding-top:25px;
		}
		
		pre {
			white-space: pre;word-wrap: normal; overflow-x: auto;
		}
		
		div#content {
			padding: 40px; margin-top: -150px; margin-bottom: 40px; background: white; -webkit-border-radius: 5px; -moz-border-radius: 5px; border-radius: 5px; -webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.17); -moz-box-shadow: 0 0 4px rgba(0,0,0,0.17); box-shadow: 0 0 4px rgba(0, 0, 0, 0.17); -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box;
		}
		
		h2 {
			margin-bottom: 12px;
		}
		
		div.list {
			padding-left: 40px; margin-bottom: 20px;
		}
		
		div.list p span.number {
			margin-left: -25px; padding-right: 10px;
		}
		
		@media (max-width: 767px) {
			body {
				padding-right: 0px; padding-left: 0px; font-size: 14px;
			}
			
			.header div {
				padding-left:20px;
			}
			
			div#content {
				padding: 20px; -webkit-border-radius: 0px; -moz-border-radius: 0px; border-radius: 0px; margin-bottom: 0px;
			}
			
			div.list {
				padding-left: 30px;
			}
		}
		
		@media (max-width: 480px) {
			.header div {
				padding-left:15px;
			}
			
			div#content {
				padding: 15px;
			}
		}
	
	</style>
	
	<div class="header">
    	<div class="container">
      		<h1>Jogger Sample App</h1>
    	</div>
	</div>

	<div id="content" class="container">
		<p class="lead">Congrats! You are running a Jogger application.</p>
			
		<h2>Why is this page being displayed?</h2>
		
		<div>	
			<p>This is just a welcome page to get you started and an opportunity to explain you how Jogger works:</p>
			<div class="list">
				<p><span class="number">1.</span> Open the <code>WEB-INF/routes.config</code> file. You should see something like:</p>
				<pre>GET	/	Pages#index</pre>
				<p>Every time a <code>GET</code> to the <code>/</code> path is received, it will be handled by the <code>index</code> method of the <code>Pages</code> controller.</p>
			</div>
			<div class="list">
				<p><span class="number">2.</span> Open the <code>com.company.controller.Pages</code> class. Take a look at the <code>index</code> method.</p>
				<pre>public void index(Request request, Response response) {
    response.render("index.ftl");
}</pre>
				<p>You can play with the <code>response</code> object to render different things.</p>
			</div>
			<div class="list">
				<p><span class="number">3.</span> Open the <code>WEB-INF/freemarker/index.ftl</code> and <code>WEB-INF/freemarker/layout.ftl</code> files. They contain the source code of what you are actually seeing.</p>
			</div>
		</div>
		
		<h2>Want to learn more?</h2>
		
		<p>Checkout the <a href="https://github.com/germanescobar/jogger/wiki">Main Documentation Page</a></p>
		
	</div>
	<!-- Welcome page ends -->
	
</@layout.layout>