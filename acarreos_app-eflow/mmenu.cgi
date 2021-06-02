#!/usr/bin/perl

use Eflow::user_check;
binmode STDOUT,':utf8';
use utf8;
print PrintHeader();
use CGI::Carp qw(fatalsToBrowser);
ReadParse();
print qq¡
	<html>
  <head>
  <meta http-equiv="Content-Language" content="es">
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <meta name="GENERATOR" content="Microsoft FrontPage 4.0">
  <meta name="ProgId" content="FrontPage.Editor.Document">
  <title>menu</title>
	<script>
		//parent.document.getElementById("mmenu").style.display = "none";
		//parent.document.getElementById("mmenu").style.display = "block";
		var id = self.parent.document.getElementById("MainFrameSet");
		var char2 = id.cols;
		id.cols = "0,*";
		parent.document.getElementById('work').src ="/cgi-bin/acarreos_app/supervision.cgi?cmd=inicio&IE=$STime";
	</script>
  </head>
  <body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">
  </body>
  </html>	
¡;