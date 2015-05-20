// Wrap code with module pattern
var CreateAppointmentWidget = function()
{
    var global = this;

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeCreateAppointmentWidget = function(parentElement, appointmentId, app)
    {
        ////////////////////////
        // /// Fields /////
        ////////////////////////

        var container = parentElement;

        var appId = appointmentId;

        var serverURL = $("#serverURL").html();

        function collectIds(endpoint, key, node)
        {
            var ids = {};
            $.ajax({
                async : false,
                url : serverURL + endpoint
            }).done(function(response)
            {
                $.each($(response.documentElement).children(), function()
                {
                    var obj = {};
                    if (!node)
                        obj.id = $(this).attr(key);
                    else
                        obj.id = $(this).children(key).html();

                    obj.name = $(this).children("name").html()
                    ids[obj.id] = obj;
                });
            });
            return ids;
        }

        // Appointment Form Template
        var appointmentFormTemplate = _
        .template("\
            <div>\
            <h3><%= title %></h3><input id='pushAppointment' type='button' value='OK'>\
            <p style='padding-left:20px;'><label for='date'>Appointment Date:</label> <input id='date' type='date'></p>\
            <p style='padding-left:20px;'><label for='time'>Appointment Time:</label> <input id='time' type='time'></p>\
            <p style='padding-left:20px;margin-bottom:0;'><label for='patient'>Patient:</label>\
                <select id='patient'>\
            		<% $.each( patients, function( id, patient ){ %>\
                        <option><%= id %></option>\
                    <% }); %>\
        		</select> <span id='patient'></span></p>\
            <p style='padding-left:40px;margin:0;'>>Physician: <span id='physician'></span></p>\
            <p style='padding-left:20px;margin-bottom:0;'><label for='phlebotomist'>Phlebotomist:</label>\
                <select id='phlebotomist'>\
                    <% $.each( phlebotomists, function( i, phlebotomist ){ %>\
                        <option><%= phlebotomist.id %></option>\
                    <% }); %>\
                </select> <span id='phlebotomist'></span></p>\
            <p style='padding-left:20px;margin-bottom:0;'><label for='psc'>PSC:</label>\
                <select id='psc'>\
                    <% $.each( pscs, function( i, psc ){ %>\
                        <option><%= psc.id %></option>\
                    <% }); %>\
                </select> <span id='psc'></span></p>\
            <div id='labTestsBlock'><p style='padding-left:20px;'><label>Lab Tests: </label></p></div>\
            <p style='padding-left:20px;'><input id='addLabTest' type='button' value='Add Lab Test'</p> \
            </div><hr>");

        var labTestTemplate = _
        .template("<div>\
            <p style='padding-left:40px;margin-bottom:0;'><label>Lab Test:</label>\
                <select id='labTests'>\
                    <% $.each( labTests, function( i, labTest ){ %>\
                        <option><%= labTest.id %></option>\
                    <% }); %>\
                </select> <span id='labTest'></p>\
            <p style='padding-left:60px;margin:0;'>><label>Diagnosis:</label>\
                <select id='diagnosis'>\
                    <% $.each( diagnoses, function( i, diagnosis ){ %>\
                        <option><%= diagnosis.id %></option>\
                    <% }); %>\
                </select> <span id='diagnosis'></p></div>\
    		");

        var appointmentXMLTemplate = _
        .template("\
    		<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\
            <appointment>\
                <date><%= date %></date>\
                <time><%= time %></time>\
                <patientId><%= patient %></patientId>\
                <physicianId><%= physician %></physicianId>\
                <pscId><%= psc %></pscId>\
                <phlebotomistId><%= phlebotomist %></phlebotomistId>\
                <% if( labTests.length <= 0 ) { %> <labTests/> <% } else { %>\
                    <labTests>\
                        <% $.each( labTests, function(id, dxcode) { %>\
                            <test id=\"<%= id %>\" dxcode=\"<%= dxcode %>\" />\
                        <% }); %>\
                    </labTests>\
                <% } %>\
            </appointment>\
    		");

        // Appointment Ids
        var appointments = appointmentWidget.getAppointments();

        // Phlebotomist Ids
        var phlebotomists = collectIds("Phlebotomists", "id");

        // PSC Ids
        var pscs = collectIds("PSCs", "id");

        // Lab Tests
        var labTests = collectIds("LabTests", "id");

        // Diagnoses
        var diagnoses = collectIds("Diagnoses", "code", true);

        // Patients and their physicians
        var patients = {};

        $.ajax({
            async : false,
            url : serverURL + "Physicians"
        }).done(function(response)
        {
            $.each($(response.documentElement).children(), function()
            {
                var physician = {
                    id : $(this).attr("id"),
                    uri : $(this).children("uri").html(),
                    name : $(this).children("name").html()
                };

                $.each($(this).children("patients").children(), function()
                {
                    $.ajax({
                        url : serverURL + "Patients/" + $(this).attr("id"),
                        async : false
                    }).done(function(response)
                    {
                        var p = $(response.documentElement).children()[0];
                        patients[$(p).attr("id")] = {
                            id : $(p).attr("id"),
                            name : $(p).children("name").html(),
                            physician : physician
                        };
                    });
                });
            });
        });

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function setUpForm()
        {
            var form = {
                patients : patients,
                phlebotomists : phlebotomists,
                pscs : pscs,
                labTests : labTests,
                diagnoses : diagnoses
            };

            if (appId)
            {
                form.title = "Updating Appointment " + appId;
                // fill form with preexisting appointment info
            } else
                form.title = "Create New Appointment";

            container.append(appointmentFormTemplate(form));

            // Updating fields
            $("select#patient").change(function()
            {
                $("span#patient").html(patients[$(this).val()].name);
                $("span#physician").html(patients[$(this).val()].physician.name);
            }).change();

            $("select#phlebotomist").change(function()
            {
                $("span#phlebotomist").html(phlebotomists[$(this).val()].name);
            }).change();

            $("select#psc").change(function()
            {
                $("span#psc").html(pscs[$(this).val()].name);
            }).change();

            $("input#addLabTest").click(function()
            {
                $("div#labTestsBlock").append(labTestTemplate(form));

                $("select#labTests").change(function()
                {
                    $(this).parent().children("span#labTest").html(labTests[$(this).val()].name);
                }).change();

                $("select#diagnosis").change(function()
                {
                    $(this).parent().children("span#diagnosis").html(diagnoses[$(this).val()].name);
                }).change();
            });

            $("input#pushAppointment").click(function()
            {
                // convert to XML and send to server
                var app = {
                    date : $("input#date").val(),
                    time : $("input#time").val(),
                    patient : $("select#patient").val(),
                    physician : patients[$("select#patient").val()].physician.id,
                    psc : $("select#psc").val(),
                    phlebotomist : $("select#phlebotomist").val(),
                    labTests : {}
                };

                $.each(_.map($("div#labTestsBlock").children("div"), function(val)
                {
                    return {
                        id : $(val).children("p").children("select")[0].value,
                        dxcode : $(val).children("p").children("select")[1].value
                    };
                }), function(i, o)
                {
                    app.labTests[o.id] = o.dxcode;
                });

                var xml = appointmentXMLTemplate(app);

                function success(resp)
                {
                    container.empty();
                    var app = $(resp).children().children("uri").html();
                    var title = appId ? "Appointment " + appId + " Updated" : "New Appointment Created";
                    container.append("<h3>" + title + "</h3><p style='padding-left:40px;'><a href='" + app + "'>" + app + "</a></p>");
                    appointmentWidget.refresh();
                }
                function error(resp)
                {
                    container.empty();
                    container.append($(resp.responseText).children("error"));
                }

                if (appId)
                {
                    $.ajax({
                        url : serverURL + "Appointments/" + appId,
                        method : "PUT",
                        async : false,
                        contentType : "application/xml",
                        data : xml.trim()
                    }).done(success).fail(error);
                } else
                {
                    $.ajax({
                        url : serverURL + "Appointments",
                        method : "POST",
                        async : false,
                        contentType : "application/xml",
                        data : xml.trim()
                    }).done(success).fail(error);
                }
            });

            if (appId)
            {
                var app = appointments[appId];

                $("input#date")[0].defaultValue = app.appointment.date;
                $("input#time")[0].defaultValue = app.appointment.time;
                $("select#patient").val(app.patient.id).change();
                $("select#phlebotomist").val(app.phlebotomist.id).change();
                $("select#psc").val(app.psc.id).change();

                // For as many labtests as this appointment has, click addLabTest
                // They are returned in order, so fill them in order
                for (var i = 0; i < app.labTests.tests.length; i++)
                    $("input#addLabTest").click();

                $.each($("select#labTests"), function(i)
                {
                    $(this).val(app.labTests.tests[i].labTest.id).change();
                });

                $.each($("select#diagnosis"), function(i)
                {
                    $(this).val(app.labTests.tests[i].diagnosis.dxcode).change();
                });

            } else
            {
                $("input#date")[0].defaultValue = "2015-05-20";
                $("input#time")[0].defaultValue = "10:00";
            }
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        setUpForm();

        /////////////////////////////
        // Public Instance Methods //
        /////////////////////////////
        return {
            getRootEl : function()
            {
                return container;
            },
            update : function()
            {},
            log : function(message)
            {}
        };
    };

}();
