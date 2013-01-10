<!DOCTYPE html>
<html>
	<head>
		<title>${title}</title>
		<style>
			html, body {
				width: 100%;
				height: 100%;
			}
			html {
				margin: 0;
				padding: 0;
			}
			body {
				margin: auto;
				width: 100%;
				font-family: Helvetica, Arial, sans-serif;
				color: #222;
			}
			header {
				background-color: #136F92;
				padding: 20px 0;
			}
			div.content, h1 {
				width: 960px;
				margin: 0 auto;
				padding: 0 10px;
			}
			h1 {
				color: white;
				font-size: 40px;
			}
			p {
				font-size: 24px;
				color: #888;
			}
			p.small {
				font-size: 14px;
			}
		</style>
	</head>
	<body>
		<header>
			<h1>${title}</title>
		</header>
		
		<div class="content">
			<p>${message}</p>
			<hr>
			<p class="small">Powered by <a href="https://github.com/germanescobar/jogger">Jogger</a></p>
		</div>
	</body>
</html>