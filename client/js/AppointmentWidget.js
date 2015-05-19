// Wrap code with module pattern
var AppointmentWidget = function()
{
    var global = this;

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
                <% $.each( labTests.tests, function( id, dxcode ){ %>\
                        <p>&nbsp;&nbsp;&nbsp;&nbsp;LabTest: <%= id %>, Diagnosis: <%= dxcode %></p>\
                <% }); %>\
            </p><hr>");

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function getData()
        {
            $.get(serverURL + $(this).attr("extension")).done(
            function(response)
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
                            uri : $(this).children("patient").children("uri")
                            .html(),
                            id : $(this).children("patient").attr("id"),
                            name : $(this).children("patient").children("name")
                            .html()
                        },
                        phlebotomist : {
                            uri : $(this).children("phlebotomist").children(
                            "uri").html(),
                            id : $(this).children("phlebotomist").attr("id"),
                            name : $(this).children("phlebotomist").children(
                            "name").html()
                        },
                        psc : {
                            uri : $(this).children("psc").children("uri")
                            .html(),
                            id : $(this).children("psc").attr("id"),
                            name : $(this).children("psc").children("name")
                            .html()
                        },
                        labTests : {
                            tests : {}
                        }
                    };

                    $.each($(this).children("allLabTests").children(),
                    function()
                    {
                        app.labTests.tests[$(this).attr("labTestId")] = $(this).attr("dxcode");
                    });

                    $("#results").append(appointmentTemplate(app));
                });
            }).fail(function(resp)
            {
                console.error(resp.responseText);
            });
        }

        //////////////////////////////////////////
        // Find Pieces and Enliven DOM Fragment //
        //////////////////////////////////////////
        $("input[type=button]").click(getData);

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
