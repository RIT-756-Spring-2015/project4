<?php
$serverUrl = isset( $_GET['server'] ) ? $_GET['server'] : "http://shaba.zapto.org:8080/LAMService/webresources/LAMSAppointment/";
?>
<html>
<head>

<title>Appointment Management API</title>
<meta name="description" content="basic">
<meta name="author" content="Alex Aiezza">
<meta name="author" content="Sagar Barbhaya">
<meta name="author" content="Salil Rajadhyaksha">

<link rel="icon" type="image/ico" href="images/favicon.ico">

<link rel="stylesheet" type="text/css" href="css/style.css">

<script src='//code.jquery.com/jquery-2.1.3.min.js'></script>
<script src='js/lib/underscore-min.js'></script>
<script src='js/lib/perfect-scrollbar.min.js'></script>
<script src="js/AppointmentWidget.js"></script>
<script>
$(document).ready(function()
{
    $("div#results").perfectScrollbar();
});
</script>

</head>
<body>
	<div id="content">
		<p style="padding-left: 94px;">
			<input id="getServices" type="button" value="Get Services"
				extension="Services">
			<input id="getAppointments" type="button" value="Get Appointments"
				extension="Appointments">
		</p>
		<p>
    	    <select id="appointmentId">
		    </select>
			<input id="getAppointment" type="button" value="Get Appointment"
				extension="Appointments/">
		    <input id="updateAppointment" type="button"
				value="Update Appointment" extension="Appointments/">
		</p>
		<p style="padding-left: 94px;">
			<input id="makeAppointment" type="button" value="Make Appointment"
				onclick="fillForm.php">
				<input id="refreshDatabase" type="button" value="Refresh Database">
		</p>
	</div>

	<div id="results"></div>

</body>
<span id="serverURL"><?= $serverUrl ?></span>
</html>