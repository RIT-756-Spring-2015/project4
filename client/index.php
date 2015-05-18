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

<link rel="stylesheet" type="text/css" href="css/GUI.css">

<script src='//code.jquery.com/jquery-2.1.3.min.js'></script>
<script src="js/AppointmentWidget.js"></script>

</head>
<body>
	<div id="content">
		<table>
			<tr>
				<td><input type="button" value="Get Services" extension="Services"></td>
			</tr>
			<tr>
				<td><input type="button" value="Get Appointments"
					extension="Appointments"></td>
			</tr>
			<tr>
				<td><input id="getAppointment" type="button" value="Get Appointment"
					extension="Appointments/" appointmentId=""></td>
				<td><input type="text" value="Appointment ID"></td>
			</tr>
			<tr>
				<td><input type="button" value="Make Appointment"
					onclick="fillForm.php"></td>

			</tr>
			<tr>
				<td><input type="button" value="Update Appointment" appointmentId=""></td>
				<td><input type="text" value="Appointment ID"></td>
			</tr>
		</table>
	</div>

	<div id="results"></div>

</body>
<span id="serverURL"><?= $serverUrl ?></span>
</html>