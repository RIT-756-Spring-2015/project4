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
<script src="js/AppointmentWidget.js"></script>

</head>
<body>
	<div id="content">
		<table>
			<tr>
				<td><input id="getServices" type="button" value="Get Services" extension="Services"></td>
			</tr>
			<tr>
				<td><input id="getAppointments" type="button" value="Get Appointments"
					extension="Appointments"
					></td>
			</tr>
			<tr>
				<td><input id="getAppointment" type="button" value="Get Appointment"
					extension="Appointments/" appointmentId=""></td>
				<td><input id="getAppointmentId" type="text" value="Appointment ID"></td>
			</tr>
			<tr>
				<td><input id="makeAppointment" type="button" value="Make Appointment"
					onclick="fillForm.php"></td>

			</tr>
			<tr>
				<td><input id="updateAppointment" type="button" value="Update Appointment" appointmentId=""></td>
				<td><input id="updateAppointmentId" type="text" value="Appointment ID"></td>
			</tr>
		</table>
	</div>

	<div id="results"></div>

</body>
<span id="serverURL"><?= $serverUrl ?></span>
</html>