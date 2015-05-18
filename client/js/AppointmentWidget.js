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

        //////////////////////////////
        // Private Instance Methods //
        //////////////////////////////
        function getData()
        {
            $.ajax({
                accepts : "application/xml",
                method : "GET",
                url : serverURL + $(this).attr("extension")
            }).done(function(response)
            {
                $("#results").empty();
                $("#results").append(response);
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
