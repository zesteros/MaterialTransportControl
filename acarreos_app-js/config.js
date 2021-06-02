var materialsToAddTable;
var count = 0;
var usersTable, devicesTable;

j(document).ready(function(){
  j("input").attr("autocomplete", "off"); 

  count = 0;
  var active = j(".inicio").val();
  j("."+active).parent().addClass("active");

  if(j("#carries").val() == 'false'){
    j("."+active).parent().removeClass("active");    
    j(".carries_tickets_filters").parent().addClass("active");
  }

  usersTable = j("#users-table").DataTable({
    "order":[],
    "scrollX": true,
    "paging":   true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
    "fixedColumns": false,
    "columnDefs": [
    {
      "targets": 2,
      "className": "text-center",
    },
     { "width": "30%", "targets": 2 }],
  });
  devicesTable = j("#devices-table").DataTable({
    "order":[],
    "scrollX": true,
    "paging":   true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
    "fixedColumns": false,
    "columnDefs": [
    {
      "targets": 2,
      "className": "text-center",
    },
     { "width": "30%", "targets": 2 }],
  });
   materialsToAddTable = j("#new-materials-to-building").DataTable({
    "order":[],
    "scrollX": true,
    "paging":   true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
    "fixedColumns": true,
    "columnDefs": [
    {
      "targets": 2,
      "className": "text-center",
    }],
  });

  j("#nombre_empleado").autocomplete({

    minLength: 1,
    source: function( request, response ) {
      j.ajax({
        url: "/cgi-bin/util/functions.cgi",
        dataType: "json",
        data: {
          q: request.term,
          cmd:"getEmpleadoByName"
        },
        success: function( data ) {
          j( "#id_empleado" ).val( "" );
          response( data.u );
        },
        error: function(e){
        }
      });
    },
    focus: function( event, ui ) {
      return false;
    },
    select: function( event, ui ) {
      j( "#nombre_empleado").val( ui.item.value );
      j( "#id_empleado" ).val( ui.item.key );

      j.ajax({
        type: "POST",
        url: "/cgi-bin/acarreos_app/functions.cgi",
        dataType: "json",
        data: {
          cmd : "get_all_active_buildings",
          user : 480
        },
        success: function(buildings){
          var buildingOptions = j("#building");
          if(!buildings.data){
            buildingOptions.empty();
            buildingOptions.change();
            buildingOptions.multiselect({
              multiple: false,
              header: "Selecione una opción",
              noneSelectedText: "Selecione una opción",
              selectedList: 1
            }).multiselectfilter();
            alert("El usuario seleccionado no tiene obras asignadas.");

          }else {
            buildingOptions.empty();
            for (var i = 0; i < buildings.data.length; i++) {
               buildingOptions.append(
                '<option value=' + buildings.data[i].OBRA +'>'
                 + buildings.data[i].OBRA +" - "+  buildings.data[i].DESCRIPCION  + '</option>');
            }
            buildingOptions.change();
            
            
            buildingOptions.multiselect({
              multiple: false,
              header: "Selecione una opción",
              noneSelectedText: "Selecione una opción",
              selectedList: 1
            }).multiselectfilter();
          }
        },
        error: function(errors){
          console.log(errors);
        }
      });

      return false;
    }
  });
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
          fillBuildings("#all-buildings", data);
          fillBuildings("#all-buildings-1", data);
         
        },
        error: function(e){
        }
    });

   

   j("#nombre_material").autocomplete({

        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            data: {
              q: request.term,
              cmd:"get_material"
            },
            success: function( data ) {
              j( "#id_material" ).val( "" );
              response( data.u );
            },
            error: function(e){
            }
          });
        },
        focus: function( event, ui ) {
          return false;
        },
        select: function( event, ui ) {
          j( "#nombre_material").val( ui.item.value );
          j("#id_material").val(ui.item.key);
          j("#material_description").val(ui.item.description);
        
          return false;
        }

      });


});


function addMaterialToTable(){
  var building = j("#all-buildings").val();
  var material = j("#id_material").val();
  var materialDescription = j("#material_description").val();
  var eflowId = j("#eflow-id").val();
  var unitPrice = j("#unit_price").val();

  if(building && material && materialDescription && unitPrice){
    if(!materialAlreadyExists(material, building)){
        j.ajax({
          url: "/cgi-bin/acarreos_app/functions.cgi",
          dataType: "json",
          type: "POST",
          data: {
            cmd:"get_user_data_by_eflow_id",
            eflow_id: eflowId
          },
          success: function( data ) {
            var data = [
               material,
               materialDescription,
               unitPrice,
               data.user.name+"("+data.user.super_nomina_id+")",
               building,
               '<button  type="button" class="btn btn-danger" onclick="removeRow(\''+count+'\')"><i class="glyphicon glyphicon-remove"></i></button>'
            ];
            data.DT_RowId="row_"+count;
            var rowNode = materialsToAddTable.row.add(data).draw().node();
          },
          error: function(e){}
        });
        count++;
    
    } else alert("El material "+material + " ya está asignado a la obra "+building);
  } else alert("falta un campo");

  /**/
}

function deleteAssignment(idEmpleado){
  var r = confirm("¿Estás seguro(a) de retirar la asignación?");
  if (r == true) {
      window.location.href = "config.cgi?cmd=update_user&id_empleado="+idEmpleado;
  } else {
  }
}

function approveDevice(idEmpleado, model, imei){
  var r = confirm("¿Estás seguro(a) de aprobar el dispositivo "+model+"?");
  if (r == true) {
      window.location.href = "config.cgi?cmd=update_user&id_empleado="+idEmpleado+"&update_imei=true";
  } else {
  }
}

function fillBuildings(id, data){
  var buildings = j(id);
  buildings.empty();
  
  for(var i = 0; i < data.data.length; i++){

    var selectedBuilding = j("#selected_building").val();
    
    var selected = data.data[i].OBRA === selectedBuilding ? "selected" :"";

    buildings.append(
        '<option value=' + data.data[i].OBRA +' '+selected+'>'
         + data.data[i].OBRA +" - "+  data.data[i].DESCRIPCION  + '</option>');
  }
  buildings.multiselect({
      multiple: false,
      header: "Selecione una opción",
      noneSelectedText: "Selecione una opción",
      selectedList: 1
    }).multiselectfilter();
}

function materialAlreadyExists(material, buildings){


  var idMaterialIndex;

  materialsToAddTable
    .column( 0 )
    .data()
    .each(
      function ( value, index ) {
        if(material === value){
          idMaterialIndex = index;
        }
      }
    );

  var buildingIndex;

    materialsToAddTable
      .column(4)
      .data()
      .each( function ( value, index ) {
        if(buildings === value){
          buildingIndex = index;
         }
      });
  

  //if(exists)
    //alert("El material "+material+", ya está asignado a la obra "+existingValue);

  if(!buildingIndex) return false;
  if(!idMaterialIndex) return false;
  ;
  return buildingIndex == idMaterialIndex;
}


function removeRow(id){
  materialsToAddTable.row('#row_'+id).remove().draw( false );
}

function saveMaterials(){
  var dataToSave = [];

  materialsToAddTable  .column( 0 )
    .data()
    .each( function ( value, index ) {
        var data = {"id_material": value,"precio_unitario":"","obra":""};
        dataToSave.push(data);
  } );
  materialsToAddTable  .column( 2 )
    .data()
    .each( function ( value, index ) {
        dataToSave[index].precio_unitario = value;
  } );
  materialsToAddTable  .column( 4 )
    .data()
    .each( function ( value, index ) {
        dataToSave[index].obra = value;
  } );
  
  if(dataToSave.length > 0){

    j.ajax({
      url: "/cgi-bin/acarreos_app/functions.cgi",
      dataType: "json",
      type: "POST",
      data: {
        cmd:"save_materials",
        data_to_save: JSON.stringify(dataToSave)
      },
      success: function( data ) {
        if(data.result){
          window.location.href = "config.cgi?cmd=config_materials";
        }
      },
      error: function(e){}
    });
  } else {
    alert("No hay materiales para agregar.");
  }

  j("#nombre_material").val("");
  j("#unit_price").val("0.00");
}

function deleteMaterial(idMaterial,buildingToShow){
  var r = confirm("¿Estás seguro(a) de retirar el material?");
  if (r == true) {
      window.location.href = "config.cgi?cmd=update_material&id_material="+idMaterial+"&building_to_show="+buildingToShow;
  } else {
  }
}


function saveUnitPrice(idAsignacion){

}

function editField(idAsignacion, row){
  var rowData = usersTable.row(row).data()[3];
  var priceToEdit = rowData.substring(rowData.indexOf("$")+1,rowData.indexOf("\" disabled"));

  alert(priceToEdit);
}