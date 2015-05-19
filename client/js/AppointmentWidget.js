// Wrap code with module pattern
var AppointmentWidget = function()
{
    var global = this;

    global.sleep = function sleep(milliseconds)
    {
        var start = new Date().getTime();
        for (var i = 0; i < 1e7; i++)
        {
            if ((new Date().getTime() - start) > milliseconds)
            {
                break;
            }
        }
    }

    /////////////////////////////////
    // Widget Constructor Function //
    /////////////////////////////////
    global.makeAppointmentWidget = function(parentElement)
    {
        ////////////////////////
        // /// Fields /////
        ////////////////////////

        var container = parentElement;

        var serverURL = $("#serverURL").html();

        var appointmentTemplate = _
        .template("\
            <p>\
            <p><a href=\"<%= appointment.uri %>\">Appointment: <%= appointment.id %></a> \
            [<%= appointment.date %>, <%= appointment.time %>]</p>\
            <p>&nbsp;&nbsp;<a href=\"<%= patient.uri %>\">Patient: <%= patient.id %></a> \
            [<%= patient.name %>]</p>\
            <p>&nbsp;&nbsp;<a href=\"<%= phlebotomist.uri %>\">Phlebotomist: <%= phlebotomist.id %></a> \
            [<%= phlebotomist.name %>]</p>\
            <p>&nbsp;&nbsp;<a href=\"<%= psc.uri %>\">PSC: <%= psc.id %></a> \
            [<%= psc.name %>]</p>\
            <p>&nbsp;&nbsp;<a href=\"<%= labTests.uri %>\">Labtests</a>:</p> \
                <% $.each( labTests.tests, function( i, test ){ %>\
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;\
                        <a href=\"<%= test.labTest.uri %>\">LabTest: <%= test.labTest.id %></a> [<%= test.labTest.name %>](<%= test.labTest.cost %>)\
                        		, <a href=\"<%= test.diagnosis.uri %>\">Diagnosis: <%= test.diagnosis.dxcode %></a> [<%= test.diagnosis.name %>]</p>\
                <% }); %>\
            </p><hr>");

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function getAppointments()
        {
            $.get(serverURL + $(this).attr("extension")).done(function(response)
            {
                $("#results").empty();
                $.each($(response.documentElement).children(), function()
                {
                    var app = {
                        appointment : {
                            uri : $(this).children("uri").html(),
                            id : $(this).attr("id"),
                            date : $(this).attr("date"),
                            time : $(this).attr("time")
                        },
                        patient : {
                            uri : $(this).children("patient").children("uri").html(),
                            id : $(this).children("patient").attr("id"),
                            name : $(this).children("patient").children("name").html()
                        },
                        phlebotomist : {
                            uri : $(this).children("phlebotomist").children("uri").html(),
                            id : $(this).children("phlebotomist").attr("id"),
                            name : $(this).children("phlebotomist").children("name").html()
                        },
                        psc : {
                            uri : $(this).children("psc").children("uri").html(),
                            id : $(this).children("psc").attr("id"),
                            name : $(this).children("psc").children("name").html()
                        },
                        labTests : {
                            uri : "",
                            tests : []
                        }
                    };

                    $.each($(this).children("allLabTests").children(), function()
                    {
                        if (app.labTests.uri == "")
                            app.labTests.uri = $(this).children("uri").html();

                        var labTest = {
                            id : $(this).attr("labTestId")
                        }, diagnosis = {
                            dxcode : $(this).attr("dxcode")
                        };

                        $.get(serverURL + "LabTests/" + labTest.id).done(function(response)
                        {
                            labTest["uri"] = $(response.documentElement).children("appointmentLabTest").children("uri").html();
                            labTest["name"] = $(response.documentElement).children("appointmentLabTest").children("name").html();
                            labTest["cost"] = $(response.documentElement).children("appointmentLabTest").children("cost").html();

                            $.get(serverURL + "Diagnoses/" + diagnosis.dxcode).done(function(response)
                            {
                                diagnosis["uri"] = $(response.documentElement).children("appointmentLabTest").children("uri").html();
                                diagnosis["name"] = $(response.documentElement).children("appointmentLabTest").children("name").html();

                                app.labTests.tests.push({
                                    labTest : labTest,
                                    diagnosis : diagnosis
                                });

                                $("#results").append(appointmentTemplate(app));
                            });
                        });
                    });
                });
            }).fail(function(resp)
            {
                console.error(resp.responseText);
            });
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        $("input#getAppointments").click(getAppointments);

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

$(document).ready(function()
{
    appointmentWidget = makeAppointmentWidget($("#content"));
});
