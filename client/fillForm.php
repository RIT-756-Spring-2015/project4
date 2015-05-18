<?php
/**
 * Created by PhpStorm.
 * User: Sagar Barbhaya
 * Date: 5/18/2015
 * Time: 12:17 PM
 */
?>
<html>
<head>
<title>Send Request Form</title>
</head>
<body>
	<div class="form">
		<form name="Make Appointment" action="" method="get">
			<table>
				<tr>
					<td><label>Appointment Date:</label></td>
					<td><input type="date" name="Appointment Date" max="1979-12-31"><br>
					<br></td>
				</tr>
				<tr>
					<td><label>Appointment Time:</label></td>
					<td><input type="text" name="AppointmentTime" value=""></td>
					<td><select>
							<option value="AM">AM</option>
							<option value="PM">PM</option>
					</select></td>
				</tr>

				<tr>
					<td><label>Patient Name:</label></td>
					<td><select>

					</select></td>
				</tr>
				<tr>
					<td><label>Physician Name:</label></td>
					<td><select>

					</select></td>
				</tr>
				<tr>
					<td><label>PSC Name:</label></td>
					<td><select>

					</select></td>
				</tr>
				<tr>
					<td>Phlebotomist Name:</td>
					<td><select>

					</select></td>
				</tr>
				<tr>
					<td>Test Name:</td>
					<td><select>

					</select></td>
				</tr>
				<tr>
					<td><input type="submit" value="submit"></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>