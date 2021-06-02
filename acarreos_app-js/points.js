var mapThrows;
var mapBanks;
var mapDrawThrows;
var poly;
var markersThrows = [];
var mapListener;
var throwsTable;

function test(){
    throwsTable.DataTable().rows().select();

}

j(document).ready(function(){

  // j('#time_elapsed').datetimepicker({
  //     pickDate: false
  //   });


  var active = j(".active-button").val();
  j("."+active).parent().addClass("active");
  if(j("#authorized-flag").val() == 1){
    j("."+active).parent().removeClass("active");    
    j(".autorizados").parent().addClass("active");
  }

   
  j("#banks-table").DataTable({
    "order":[],
    "scrollX": true,
    "paging":   true,
    "fixedColumns": true,
    "language": {"url": "/lib/js/datatables.spanish.json"},

    /*"ajax": {
      "type":"POST",
      "url": "/cgi-bin/acarreos_app/functions.cgi",
      "data": { 
        "cmd":"get_points_by_type_and_authorized",
        "tipo_punto": "1",
        "autorizado": "0"
      },
      "dataSrc":"data"
    },
    "language": {"url": "/lib/js/datatables.spanish.json"},
    "columns": [
      { "data": "autorizado","defaultContent": "<button>Click!</button>" },
      { "data": "latitud" },
      { "data": "nombre_banco" },
      { "data": "radio" },
      { "data": "fecha_registro" },
      { "data": "fecha_agregado" }
    ],*/
    
  });
  throwsTable =  j("#throws-table").DataTable({
    "order":[2],
    "scrollX": true,
    "paging":   true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
    "fixedColumns": true,
    "columnDefs": [
    {
      "targets": 3,
      "className": "text-center",
    }],
  
  });

  /*j('#throws-table  tbody').on( 'click', 'tr', function () {
        j(this).toggleClass('selected');
    } );*/
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
          fillBuildings("#points_buildings_select", data);
        },
        error: function(e){
        }
    });

});




function initMap() {

  var authorized = j("#authorized-flag").val();
  if (window.location.href.indexOf("cmd=draw_throws") != -1){
    mapDrawThrows = new google.maps.Map(document.getElementById('map-draw-throws'), {
      center: {lat: -34.397, lng: 150.644},
      zoom: 14
    });  
  }else {

    mapThrows = new google.maps.Map(document.getElementById('map-throws'), {
      center: {lat: -34.397, lng: 150.644},
      zoom: 18
    });

    mapBanks = new google.maps.Map(document.getElementById('map-banks'), {
      center: {lat: -34.397, lng: 150.644},
      zoom: 18
    });

  }
  /* if (navigator.geolocation) {
     navigator.geolocation.getCurrentPosition(function (position) {
        initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
        if (window.location.href.indexOf("cmd=draw_throws") != -1){

          mapDrawThrows.setCenter(initialLocation);
        } else {
          mapThrows.setCenter(initialLocation);
          mapBanks.setCenter(initialLocation);
        }

     });*/

    loadPoints(mapThrows, 2, authorized);
    loadPoints(mapBanks, 1, authorized);
    loadPoints(mapBanks, 3, authorized);

 

}

function authorizeAll(building){

     swal({
    title: "Autorizar todos los tiros",
    text: "¿Estás seguro(a) de autorizar los tiros?",
    type: "warning",
    showCancelButton: true,
    confirmButtonClass: "btn-danger",
    confirmButtonText: "Sí",
    cancelButtonText: "Cancelar",
    closeOnConfirm: true  
  },
  function(){
    j.ajax({
      type: 'POST',
      url: '/cgi-bin/acarreos_app/functions.cgi',
      dataType: 'json',
      data: { 
        cmd:"authorize_all_throws",
        obra: building
      },
      success: function(d){     
        //console.log(d.result);
        if(d.result)
          window.location.href = "points.cgi?cmd=authorize_points&autorizado=1&points_buildings_select="+building ;
      },
      error: function(e){
        console.log(e);
      }
    }); 
  });

  
}


function loadPoints(map, pointType, authorized){

  var selectedBuilding = j("#selected_building").val();

  j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: { 
      cmd:"get_points_by_type_and_authorized",
      tipo_punto: pointType,
      autorizado: authorized,
      obra: selectedBuilding
    },
    success: function(d){     
      if(!d.data){

      }else{
        if(pointType == 2){
          var initialLocationThrows = new google.maps.LatLng(parseFloat(d.data[d.data.length-1].latitud), parseFloat(d.data[d.data.length-1].longitud));
          mapThrows.setCenter(initialLocationThrows);
        }else if(pointType == 1 || pointType == 3 ){
           var initialLocationBanks = new google.maps.LatLng(parseFloat(d.data[0].latitud), parseFloat(d.data[0].longitud));
          mapBanks.setCenter(initialLocationBanks);
        }
        for(var i = 0; i < d.data.length; i++){
          var name = pointType == 1 || pointType == 3 ? d.data[i].nombre_banco : d.data[i].cadenamiento;
          addMarker(name, map, d.data[i].latitud,d.data[i].longitud,d.data[i].radio, d.data[i].id_punto,pointType);
        }
      }
    },
    error: function(e){
      console.log(e);
    }
  });
}

function addMarker(name, map, latitude, longitude, radius, id_punto,pointType){


  var myLatlng = new google.maps.LatLng(latitude,longitude);
  var image = {
    url: 'https://eflow.vise.com.mx/images/bank_icon.png',
    // This marker is 20 pixels wide by 32 pixels high.
    size: new google.maps.Size(20, 64),
    // The origin for this image is (0, 0).
    origin: new google.maps.Point(0, 0),
    // The anchor for this image is the base of the flagpole at (0, 32).
    anchor: new google.maps.Point(0, 32)
  };

  var iconThrow = "https://icon-icons.com/descargaimagen.php?id=122839&root=1965/ICO/32/&file=tool10_122839.ico";
  //"https://icon-icons.com/descargaimagen.php?id=122838&root=1965/ICO/32/&file=tool11_122838.ico";
  //"https://icon-icons.com/descargaimagen.php?id=107531&root=1559/ICO/48/&file=3440906-direction-location-map-marker-navigation-pin_107531.ico";
  // "https://icon-icons.com/descargaimagen.php?id=60431&root=671/ICO/96/&file=1-47_icon-icons.com_60431.ico";
  //"https://icon-icons.com/descargaimagen.php?id=122839&root=1965/ICO/32/&file=tool10_122839.ico";
  //"https://icon-icons.com/descargaimagen.php?id=122839&root=1965/ICO/128/&file=tool10_122839.ico";
  //"https://icon-icons.com/descargaimagen.php?id=70617&root=908/ICO/32/&file=shovel-and-ground_icon-icons.com_70617.ico";
  //"https://icon-icons.com/descargaimagen.php?id=70617&root=908/ICO/128/&file=shovel-and-ground_icon-icons.com_70617.ico";
  //"https://icon-icons.com/descargaimagen.php?id=108580&root=1603/ICO/48/&file=shipping-delivery-box-drop_108580.ico";
  var iconBank = "https://icon-icons.com/descargaimagen.php?id=54388&root=567/ICO/64/&file=marker_icon-icons.com_54388.ico";
  //"https://icon-icons.com/descargaimagen.php?id=70440&root=907/ICO/32/&file=mountain-summit_icon-icons.com_70440.ico";

  var icons = pointType == 1 || pointType == 3  ? iconBank : iconThrow;
  var marker = new google.maps.Marker({
      position: myLatlng, 
      map: map, 
      //label: {text: name, color: "black", fontSize: "24px", "margin-bottom":"12px"},
      icon:icons,
      //"https://icon-icons.com/descargaimagen.php?id=108580&root=1603/ICO/48/&file=shipping-delivery-box-drop_108580.ico", 
      //"https://icon-icons.com/descargaimagen.php?id=72656&root=933/ICO/128/&file=mountain-range_icon-icons.com_72656.ico",//"https://eflow.vise.com.mx/images/vise.ico",
      //alt:name,
      draggable:true,

  });
  marker.addListener('click', function() {
    infowindow.open(map, marker);
  });

  var throwColors = ['#11C1CF','#33F0FF'];
  var bankColors = ['#11CF9E', '#27F5C0'];
  //var bankColors = 
  var circle = new google.maps.Circle({
    strokeColor: pointType == 1 || pointType == 3 ? bankColors[0] : throwColors[0],
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: pointType == 1 || pointType == 3 ? bankColors[1] : throwColors[1],
    fillOpacity: 0.1,
    map: map,
    center: {lat:latitude, lng: longitude},
    radius: radius
  });

   var infowindow = new google.maps.InfoWindow({
    content: name
  });

  marker.addListener('click', function() {
    infowindow.open(map, marker);
  });

  marker.addListener('dragend', function() {
    circle.setCenter(marker.getPosition());

    setTimeout(function(){changeLocation(marker.getPosition(), id_punto);},800);
    
  });

  var contentString = '<h4><b>'+name+'</b> <br>'+latitude+', '+longitude+'</h4>';//+'Cambiar radio <button>+</button><button>-</button>';
  var infowindow = new google.maps.InfoWindow({
    content: contentString
  });


}

function changeLocation(latLng, point){

    var selectedBuilding = j("#selected_building").val();

    swal({
    title: "Actualizar ubicación del punto",
    text: "¿Estás seguro(a) de actualizar el punto?",
    type: "warning",
    showCancelButton: true,
    confirmButtonClass: "btn-danger",
    confirmButtonText: "Sí",
    cancelButtonText: "Cancelar",
    closeOnConfirm: true  
  },
  function(){
    j.ajax({
      type: 'POST',
      url: '/cgi-bin/acarreos_app/functions.cgi',
      dataType: 'json',
      data: {
        cmd:"update_coordinates",
        latitud:latLng.lat(),
        longitud:latLng.lng(),
        id_punto: point
      },
      success: function(d){     
        /*
          Cambiar logica a no actualizar vista solo mapa y tabla 

        */
        //window.location.href = "points.cgi?cmd=authorize_points&autorizado=1&points_buildings_select="+selectedBuilding;

      },
      error: function(e){
        console.log(e);
      }
    });
  });
}

function showInMap(latitude, longitude, pointType){
  if(pointType == 1 || pointType == 3 )
    mapBanks.setCenter(new google.maps.LatLng(parseFloat(latitude) , parseFloat(longitude) ));
  else if(pointType == 2)
    mapThrows.setCenter(new google.maps.LatLng(parseFloat(latitude) , parseFloat(longitude) ));
}

function updatePoint(idPoint, edit){

  if(parseInt(edit) == 1){
    j(".modal-title").text("Edición de puntos");
    j("#authorize-button").text("Editar");
  }else{
    j(".modal-title").text("Autorización de puntos");
    j("#authorize-button").text("Autorizar");
  }

  j('#DescModal').modal("show");

  j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: { 
      cmd:"get_point_by_id",
      id_punto: idPoint,
    },
    success: function(d){     
      if(!d.data){

      }else{
        j("#point-type-dialog option[value='"+d.data[0].tipo_punto+"']")
          .prop('selected', true);
        j("#point-name-dialog").val(d.data[0].nombre_banco);
        j("#chainage-dialog").val(d.data[0].cadenamiento);
        j("#radio-dialog").val(d.data[0].radio);
        j("#latitude-dialog").val(d.data[0].latitud);
        j("#longitude-dialog").val(d.data[0].longitud);
        j("#is-bank-and-throw").prop('checked',d.data[0].es_banco_y_tiro == 1);
        j("#authorize-button" ).click(function() {
          var newPoint = {
            cmd:"save_point",
            id_punto: d.data[0].id_punto,
            tipo_punto: j("#point-type-dialog").val(),
            nombre_banco: j("#point-name-dialog").val(),
            cadenamiento: j("#chainage-dialog").val(),
            radio: j("#radio-dialog").val(),
            latitud: j("#latitude-dialog").val(),
            longitud: j("#longitude-dialog").val(),
            es_banco_y_tiro: j("#is-bank-and-throw").is(':checked') ? 1 : 0
          };
          update(newPoint);
        });
      }
      
    },
    error: function(e){
      console.log(e);
    }
  });
}

function update(point){

  var selectedBuilding = j("#selected_building").val();


  j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: point,
    success: function(d){     
      window.location.href = "points.cgi?cmd=authorize_points&autorizado=1&points_buildings_select="+selectedBuilding;
    },
    error: function(e){
      console.log(e);
    }
  });
}

function rejectPoint(point, deletepoint){

  var deletePointText = 
    j("#authorized-flag").val() != 1 ? 
    "Rechazar" :
    "Dar de baja";

  swal({
    title: deletePointText+" punto",
    text: "¿Estás seguro(a) de "+ deletePointText + " el punto?",
    type: "warning",
    showCancelButton: true,
    confirmButtonClass: "btn-danger",
    confirmButtonText: "Sí",
    cancelButtonText: "Cancelar",
    closeOnConfirm: false
  },
  function(){
    var building = j("#selected_building").val();
    j.ajax({
      type: 'POST',
      url: '/cgi-bin/acarreos_app/functions.cgi',
      dataType: 'json',
      data: {
        cmd:"reject_point",
        id_punto: point,
        building: building,
        borrar: j("#authorized-flag").val()
      },
      success: function(d){     
        window.location.href = "points.cgi?cmd=reject_point&building="+building+"&authorized="+j("#authorized-flag").val();
      },
      error: function(e){
        console.log(e);
      }
    });
  });

 
}


var materialsByPointSelect;

function loadMaterialsToAssignByBuilding(point){
  var building = j("#selected_building").val();
  j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: { 
      cmd:"get_materials_by_building",
      obra: building,
      id_punto:point
    },
    success: function(d){



      //var selectMaterials = j("#materials-select-dialog");
      j("#materials-select-dialog").empty();
      j("#materials-select-dialog").change();

      j.each(d.materials,function(i,o){

        var selected = o.id_material_por_punto != 0 ? "selected":"";
         j("#materials-select-dialog").append(
        '<option value="' + o.id_asignacion+'" '+selected+'>'
         + o.id_material_navision+" - "+ o.descripcion    + '</option>');
      });
      j("#materials-select-dialog").multiSelect({
        afterSelect: function(values){
          alert("Select value: "+values);
        },
        afterDeselect: function(values){
          alert("Deselect value: "+values);
        }
      });
    },
    error: function(e){
      console.log(e);
    }
  }); 
}


function assignUnitPrice(idMaterial){
  alert(idMaterial);
}


function assignMaterial(point){
  j("#selected_point").val(point);
  loadMaterialsToAssignByBuilding(point);
  j('#assign-material-dialog').modal("show");
}

function assignDistances(point){
  j("#selected_point").val(point);
  loadDistancesByPoint(point);
  j("#assign-distances-dialog").modal("show");
}

function loadDistancesByPoint(point){
  //<li class="list-group-item">1<button onclick="removeDistance()">X</button></li>
  j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: { 
      cmd:"get_distances_by_point",
      id_punto:point
    },
    success: function(d){

      j("#distances-list").empty();

      if(d.distances.length>0){

        j.each(d.distances,function(i,o){


          var fee =o.precio_km_inicial ?  ", tarifa: "+o.precio_km_inicial+"+"+o.precio_km_subsecuente : ", sin tarifas";

         j("#distances-list")
          .append('<li class="list-group-item" id="'+o.id_distance+'">'+o.distance+' km'+fee+'  <button onclick="removeDistance(\''+o.id_distance+'\',\''+point+'\')"><b>X</b></button></li>');
        });
      } else {
        j("#distances-list")
          .append('<li class="list-group-item">No hay distancias disponibles</li>');
      }
      j("#distances-list").change();
    },
    error: function(e){
      console.log(e);
    }
  }); 
}

function removeDistance(idDistance,point){
    j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: { 
      cmd:"remove_distance",
      id_distance:idDistance
    },
    success: function(d){
        if(d.success){
          loadDistancesByPoint(point);
        }else {

        }
    },
    error: function(e){
      console.log(e);
    }
  }); 
}

function addDistance(){
  var idPoint = j("#selected_point").val();
  var distance = j("#distance_to_add").val();

  var startKm = j("#km_inicial").val();
  var nextKm = j("#km_subsecuente").val();

  if(!distance || distance == ''){
    swal("Ingresa una distancia.");
    return;
  }
  if(!startKm || startKm == ''){
    swal("Ingresa una tarifa de KM inicial.");
    return;
  }

  if(!nextKm || nextKm == ''){
    swal("Ingresa una tarifa de KM subsecuente.")
    return;
  }

  j.ajax({
      type: 'POST',
      url: '/cgi-bin/acarreos_app/functions.cgi',
      dataType: 'json',
      data: { 
        cmd:"save_distance",
        distance_to_add:distance,
        start_km:startKm,
        next_km:nextKm,
        id_point:idPoint
      },
      success: function(d){     
      
        if(d.success){
          loadDistancesByPoint(idPoint);
          j("#distance_to_add").val("");
          j("#km_inicial").val("");
          j("#km_subsecuente").val("");
        }else {

        }
      },
      error: function(e){
        console.log(e);
      }
    });
  }


function saveMaterialsAssignation(){
  var idPoint = j("#selected_point").val();
  var selectedMaterials = (j("#materials-select-dialog").val()+"").split(",").map(function(item) {return parseInt(item, 10);});;
  var building = j("#points_buildings_select").val();

  var materialsJSON = j("#materials-select-dialog").val() ?
   { point : parseInt(idPoint), materials: selectedMaterials} :
   { point : parseInt(idPoint), materials: [0]} ;

  j.ajax({
    type: 'POST',
    url: '/cgi-bin/acarreos_app/functions.cgi',
    dataType: 'json',
    data: { 
      cmd:"save_materials_to_point",
      materiales: JSON.stringify(materialsJSON)
    },
    success: function(d){     
    
      if(d.success){
        swal(
        {
         title: "Materiales asignados exitosamente"
        }, function(){
         
        });
      }else {

      }
    },
    error: function(e){
      console.log(e);
    }
  });
}

var distance;
var chainage;

function startEndDrawing(){
  if(!j("#km-chainage").val() &&
    j("#km-chainage").val() == ''){
    swal("Ingresa los kilometros del cadenamiento.");
    return;
  }
  if(!j("#m-chainage").val() &&
    j("#m-chainage").val() == ''){
    swal("Ingresa los metros del cadenamiento.");
    return;
  }
  if(!j("#radio-draw").val() &&
    j("#radio-draw").val() == ''){
    swal("Ingresa un radio");
    return;
  }

  j("#km-chainage").hide();
  j("#m-chainage").hide();
  j("#label-chainage").hide();
  j("#plus-sign").hide();


  //start-end-drawing
  //Comenzar dibujo de coordenadas
  if(j("#draw-panel-title").html()=='Dibujar tiros'){
    
      startDrawing();
  }

  if(j("#start-end-drawing").html() == "Finalizar tiros"){ 
    loadNewThrowsTable();
  }

  if(j("#start-end-drawing").html()=='Comenzar dibujo de coordenadas'){
    j("#start-end-drawing").html("Finalizar tiros");
  } 

 
  

}

function startDrawing(){
   poly = new google.maps.Polyline({
        strokeColor: '#000000',
        strokeOpacity: 1.0,
        strokeWeight: 3
      });
      poly.setMap(mapDrawThrows);

      distance = 
        parseFloat(j("#km-chainage").val()) +
        (parseFloat(j("#m-chainage").val())/1000);

      chainage = distance;


      mapListener = google.maps.event.addListener(mapDrawThrows,'click',function(event) {
          addPoint(event);
      });
      j("#draw-panel-title").html("Dibujando tiros");
      j("#panel-drawing").toggleClass('panel-danger');
      j("#start-end-drawing").toggleClass('btn-danger');
      j("#clean-drawing-map").show();
      j("#backward-drawing-map").show();
}

function addPoint(event){
   //alert(event.latLng.lat()+","+event.latLng.lng());
    //

  if(!j("#radio-draw").val() || j("#radio-draw").val() == ''){
    swal("Escribe el radio del tiro");
    return;
  }

  var path = poly.getPath();

      // Because path is an MVCArray, we can simply append a new coordinate
      // and it will automatically appear.

  var backwardPoint = markersThrows[markersThrows.length-1];
  if(backwardPoint){
    distance = getDistance(backwardPoint.latLng, event.latLng);
    distance = distance < 0 ? distance * -1 : distance;
    chainage+=(distance/1000);
  }


  path.push(event.latLng);

  var marker = new google.maps.Marker({
    position: event.latLng,
    title: '#' + path.getLength(),
    map: mapDrawThrows
  });

  var radius =  parseFloat(j("#radio-draw").val());

  var circle = new google.maps.Circle({
    strokeColor: '#FF0000',
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: '#FF0000',
    fillOpacity: 0.1,
    map: mapDrawThrows,
    center: event.latLng,
    radius: radius
  });

  var point = new Object();
  point.marker = marker;
  point.circle = circle;
  point.latLng = event.latLng;
  point.radius = radius;
  point.distance = distance;
  point.chainage = chainage;
  markersThrows.push(point);
}

function loadNewThrowsTable(){
  if ( j.fn.dataTable.isDataTable( '#new-drawn-throw' ) ) {
      table = j('#new-drawn-throw').DataTable();
  }
  else {
      table = j('#new-drawn-throw').DataTable({
          "language": {"url": "/lib/js/datatables.spanish.json"},
          "sScrollX": "100%",
          "scrollCollapse": true
      });
  }

  table
    .clear()
    .draw();

  //j("#new-drawn-throw tbody tr").remove();
  j('#assign-throw-to-building').modal("show");

  for (var i =0; i < markersThrows.length; i++) {
    var chainage =
    [
        (markersThrows[i].chainage > 0) ? Math.floor(markersThrows[i].chainage) : Math.ceil(markersThrows[i].chainage),
        markersThrows[i].chainage % 1
    ];
    var rowNode = table.row.add([
      (i+1),
      markersThrows[i].latLng.lat() + ',' + markersThrows[i].latLng.lng(),
      markersThrows[i].radius +" M",
      chainage[0]+"+"+parseInt(chainage[1]*1000)
    ])
    .draw()
    .node();
  }
}

function clearMap(){
  j("#radio-draw").val("");
  j("#m-chainage").val("");
  j("#km-chainage").val("");
  for (var i = 0; i < markersThrows.length; i++) {
    markersThrows[i].marker.setMap(null);
    markersThrows[i].circle.setMap(null);
  }
  poly.setMap(null);
  markersThrows = [];

}

function saveDrawnThrows(){
  var selectedBuilding = j("#points_buildings_select").val();

  /*
  * Si no hay obra regresa
  */
  if(!selectedBuilding){
    swal("Selecciona una obra");
    return;
  } else {
    selectedBuilding = selectedBuilding[0];
  }

  /*
    Guarda los datos y la obra
  */
  var throwsToSave = [];

  /*
  * Extrae los datos de la tabla
  */
  var data = table.rows().data();

  if(data.length == 0){
    swal("No hay tiros que agregar.");
    return;
  }

  /*
  * Recorre cada fila de la tabla para concatenar el arreglo de puntos
  */
  data.each(function (value, index) {
    var chainageInTable = value[3];
    var coordinatesInTable = value[1];

    var latitudeInTable = parseFloat(coordinatesInTable.split(",")[0]);
    var longitudeInTable = parseFloat(coordinatesInTable.split(",")[1]);

    var radiusInTable = parseFloat(value[2].replace(/ M/g, ""));
    var throwToSave = {
      "point_type" : 2,
      "point_name" : chainageInTable,
      "radio" : radiusInTable,
      "chainage" : chainageInTable,
      "generate_royalty": 1,
      "latitude" : latitudeInTable,
      "longitude" : longitudeInTable, 
    };

    throwsToSave.push(throwToSave);

  });
  /*
  * Envia los datos
  */
  j.ajax({
    url: "/cgi-bin/acarreos_app/functions.cgi",
    dataType: "json",
    type: "POST",
    data: {
      cmd:"add_points",
      points_to_add: JSON.stringify(throwsToSave),
      building:selectedBuilding
    },
    success: function( data ) {
      /*
      * Limpia el mapa si todo salio bien
      */
      cleanMap();
      swal("Puntos agregados correctamente.");
    },
    error: function(e){}
  });



}

function cleanMap(){
  google.maps.event.removeListener(mapListener);
  j("#start-end-drawing").html("Comenzar dibujo de coordenadas");
  j("#draw-panel-title").html("Dibujar tiros");
  clearMap();
  j("#panel-drawing").toggleClass('panel-danger');
  j("#start-end-drawing").toggleClass('btn-danger');
  j("#clean-drawing-map").hide();
  j("#backward-drawing-map").hide();
  j("#km-chainage").show();
  j("#m-chainage").show();
  j("#label-chainage").show();
  j("#plus-sign").show();
}

function backwardDrawing(){
  if (markersThrows === undefined || markersThrows.length <= 1) {
    // array empty or does not exist
    cleanMap();
  } else {
    markersThrows[markersThrows.length-1].marker.setMap(null);
    markersThrows[markersThrows.length-1].circle.setMap(null);
    markersThrows.pop();
    poly.getPath().pop();
  }
}

var rad = function(x) {
  return x * Math.PI / 180;
};

var getDistance = function(p1, p2) {
  var R = 6378137; // Earth’s mean radius in meter
  var dLat = rad(p2.lat() - p1.lat());
  var dLong = rad(p2.lng() - p1.lng());
  var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(rad(p1.lat())) * Math.cos(rad(p2.lat())) *
    Math.sin(dLong / 2) * Math.sin(dLong / 2);
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  var d = R * c;
  return d; // returns the distance in meter
};

j(function () {
  j("#coordinates_file").on("change", function () {

    poly = new google.maps.Polyline({
        strokeColor: '#000000',
        strokeOpacity: 1.0,
        strokeWeight: 3
    });
    poly.setMap(mapDrawThrows);

    var excelFile, fileReader = new FileReader();

    j("#result").hide();

    fileReader.onload = function (e) {
      var buffer = new Uint8Array(fileReader.result);

      j.ig.excel.Workbook.load(buffer, function (workbook) {
        var column, row, newRow, cellValue, columnIndex, i,
        worksheet = workbook.worksheets(0),
        columnsNumber = 0,
        gridColumns = [],
        data = [],
        worksheetRowsCount;

        // Both the columns and rows in the worksheet are lazily created and because of this most of the time worksheet.columns().count() will return 0
        // So to get the number of columns we read the values in the first row and count. When value is null we stop counting columns:
        while (worksheet.rows(0).getCellValue(columnsNumber)) {
            columnsNumber++;
        }

        // Iterating through cells in first row and use the cell text as key and header text for the grid columns
        /*for (columnIndex = 0; columnIndex < columnsNumber; columnIndex++) {
            column = worksheet.rows(0).getCellText(columnIndex);
            gridColumns.push({ headerText: column, key: column });
        }*/

        // We start iterating from 1, because we already read the first row to build the gridColumns array above
        // We use each cell value and add it to json array, which will be used as dataSource for the grid
        distance = 
          parseFloat(j("#km-chainage").val()) +
          (parseFloat(j("#m-chainage").val())/1000);

        chainage = distance;

        j("#km-chainage").val(worksheet.rows(1).getCellText(1).split("+")[0]);
        j("#m-chainage").val(worksheet.rows(1).getCellText(1).split("+")[1]);
        j("#radio-draw").val(cleanNumber(worksheet.rows(1).getCellText(4)));

        startDrawing();

        for (i = 1, worksheetRowsCount = worksheet.rows().count() ; i < worksheetRowsCount; i++) {
            newRow = {};
            row = worksheet.rows(i);
            var utm = "+proj=utm +zone=14 +ellps=GRS80 +no_defs";
            var wgs84 = "+proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees +no_defs";
            var convertedValues = proj4(
                utm,
                wgs84,
                [
                  cleanNumber(row.getCellText(2)), 
                  cleanNumber(row.getCellText(3))
                ]
              );
            var lat = convertedValues[1];
            var lon = convertedValues[0];

            var latLng = new google.maps.LatLng(lat, lon);

            mapDrawThrows.panTo({lat:lat,lng:lon});

            j("#radio-draw").val(cleanNumber(row.getCellText(4)));
            var event = {latLng : latLng};

            addPoint(event);

           /* console.log(
              "x:"+cleanNumber(row.getCellText(2))+","+
              "y"+cleanNumber(row.getCellText(3))+","+
              proj4(
                utm,
                wgs84,
                [
                  cleanNumber(row.getCellText(2)), 
                  cleanNumber(row.getCellText(3))
                ]
              )
            );*/
            /*for (columnIndex = 0; columnIndex < columnsNumber; columnIndex++) {
                cellValue = row.getCellText(columnIndex);
                newRow[gridColumns[columnIndex].key] = cellValue;
            }

            data.push(newRow);*/
        }

              // we can also skip passing the gridColumns use autoGenerateColumns = true, or modify the gridColumns array
        createGrid(data, gridColumns);
      }, function (error) {
          j("#result").text("El archivo de excel es versión antigua, por favor actualice el archivo.");
          j("#result").show(1000);
      });
    }

    if (this.files.length > 0) {
      excelFile = this.files[0];
      if (excelFile.type === "application/vnd.ms-excel" || excelFile.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" || (excelFile.type === "" && (excelFile.name.endsWith("xls") || excelFile.name.endsWith("xlsx")))) {
          fileReader.readAsArrayBuffer(excelFile);
      } else {
          j("#result").text("The format of the file you have selected is not supported. Please select a valid Excel file ('.xls, *.xlsx').");
          j("#result").show(1000);
      }
    }


  })
});
function cleanNumber(number){
  var number = number.replace(/\./g, '');;
  number = number.replace(/\,/g, '.');;
  return parseFloat(number);
}

function createGrid(data, gridColumns) {
  if (j("#grid1").data("igGrid") !== undefined) {
      j("#grid1").igGrid("destroy");
  }

  j("#grid1").igGrid({
      columns: gridColumns,
      autoGenerateColumns: true,
      dataSource: data,
      width: "100%"
  });
}
