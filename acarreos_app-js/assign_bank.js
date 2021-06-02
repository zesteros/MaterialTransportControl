j(document).ready(function(){


	j("#point_to_assign").multiselect({
      multiple: false,
      header: "Selecione una opción",
      noneSelectedText: "Selecione una opción",
      selectedList: 1
    }).multiselectfilter();
 	var userSession = j("#user_session").val();

     j.ajax({
        url: "/cgi-bin/acarreos_app/functions.cgi",
        dataType: "json",
        type: "POST",
        data: {
          cmd:"get_all_active_buildings",
          user:userSession
        },
        success: function( data ) {
          fillBuildings("#building_to_assign", data);
         
        },
        error: function(e){
        }
    });
    j("#assigned_table").DataTable({
	    "order":[],
	    "scrollX": true,
	    "paging":   true,
	    "fixedColumns": true,
	    "language": {"url": "/lib/js/datatables.spanish.json"}
  	});
});

function viewOnMap(){
	var coordinates = j("#point_to_assign").val().split("\|")[1];
   var url = "https://maps.google.com/?q=" + coordinates;
   window.open(url);

}

function removeAssignation(idPunto, building){
	j.ajax({
      type: 'POST',
      url: '/cgi-bin/acarreos_app/functions.cgi',
      dataType: 'json',
      data: {
        cmd:"reject_point",
        id_punto: idPunto,
        building: building,
        borrar: 1
      },
      success: function(d){     
        window.location.href = "points.cgi?cmd=assign_banks";
      },
      error: function(e){
        console.log(e);
      }
    });
}