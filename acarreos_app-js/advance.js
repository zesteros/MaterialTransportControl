

/*
* Busca un avance de obra
*/
function searchAdvance(){
	
	j("#advance_map_panel").block({ 
        message: 'Cargando mapa...', 
        css: { border: '1px solid #f6bf26' } 
    }); 

	/*Extrae dinamicamente la obra*/
	var selectedBuilding = j("#buildings_select").val();
	/*
		Extrae dinamicamente las fechas
	*/
	var selectedDates = j("#daterange").val();
	/*
		Limpia el mapa
	*/

	cleanMap();
	/*
		Extrae los tiros con su volumen de esa obra
	*/
	fillMap(selectedBuilding);

	fillTable(selectedBuilding);

	fillChart(selectedBuilding);
	/*
		Dibuja los tiros confome el volumen tirado de rojo a verde 
	*/
	/*
		Muestra el volumen tirado agrupado por material
	*/


}

function fillTable(building){

	advanceTable.clear().draw();

	j("#supplied_material_panel").block({ 
        message: 'Cargando materiales...', 
        css: { border: '1px solid #f6bf26' } 
    }); 

	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    data: { 
	      cmd:"get_material_advance",
	      obra: building
	    },
	    success: function(d){     
	    	if(d.materials){
	    		for(var i = 0; i < d.materials.length; i++){
	    			 d.materials[i].costo =  d.materials[i].costo ?  "$"+ parseFloat(d.materials[i].costo).toFixed(2) : "$0.00";

	    			 d.materials[i].costo_material = d.materials[i].costo_material_x_viaje != "" && d.materials[i].costo_material_x_viaje > 0 ? 
	    			 d.materials[i].costo_material_x_viaje : d.materials[i].costo_material; 
 					 d.materials[i].total_con_material =  d.materials[i].total_con_material_x_viaje != "" &&  d.materials[i].total_con_material_x_viaje > 0 ?
 					  d.materials[i].total_con_material_x_viaje :  d.materials[i].total_con_material;
	    			 var data = [
		               d.materials[i].material,
		               d.materials[i].m3 ? parseFloat(d.materials[i].m3).toFixed(2) : 0,
            		   d.materials[i].limite_presupuestado ? parseFloat(d.materials[i].limite_presupuestado).toFixed(2) : 0,
            		   d.materials[i].porcentaje_completado ? parseFloat(d.materials[i].porcentaje_completado).toFixed(2) + '%' : '0%',
   		               d.materials[i].viajes,
		               d.materials[i].costo_material ? '$'+parseFloat(d.materials[i].costo_material).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,') : '$0.00',
		               d.materials[i].costo_acarreo ? '$'+parseFloat(d.materials[i].costo_acarreo).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,'): '$0.00',
		               d.materials[i].total_con_material ? '$'+parseFloat(d.materials[i].total_con_material).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,'): '$0.00'
		            ];
		            var rowNode = advanceTable.row.add(data).draw().node();
	    		}	    		
	    	}
    		j("#supplied_material_panel").unblock();

	    },
	    error: function(e){
	      console.log(e);
	    }
  	});
}
/*
	Llena el mapa con los tiros de esa obra
*/
function fillMap(selectedBuilding){

	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    data: { 
	      cmd:"get_building_advance",
	      tipo_punto: 2,
	      autorizado: 1,
	      obra: selectedBuilding
	    },
	    success: function(d){     
	      if(!d.data){
	      	swal("No se han podido obtener tiros de la obra "+selectedBuilding);

	      }else{
	      
	      	var initialLocationThrows = new google.maps.LatLng(parseFloat(d.data[0].latitud), parseFloat(d.data[0].longitud));
          	advanceMap.setCenter(initialLocationThrows);
          	advanceMap.setZoom(16);

          	var lowHighLevels = getTopLowM3(selectedBuilding);

	        for(var i = 0; i < d.data.length; i++){
	        	addMarker(d.data[i], lowHighLevels, selectedBuilding);
	        }
	        j("#advance_map_panel").unblock();
	      }
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});
}
/*

Funcion para pintar la grafica
*/
function fillChart(selectedBuilding){
	j("#advance_plot_panel").block({ 
        message: 'Cargando gráfica...', 
        css: { border: '1px solid #f6bf26' } 
    }); 
	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    async:false,
	    data: { 
	      cmd:"get_advance_by_date",
	      obra: selectedBuilding
	    },
	    success: function(d){     
	    	if(d.series){
	    		paintLineBasic('advance_by_date_plot',d.series, 'Avance por fecha', 'Avance de obra ' +selectedBuilding, 'M3', 'Fecha');
	    	}
	    	
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});

  	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    async:false,
	    data: { 
	      cmd:"get_trips_by_date",
	      obra: selectedBuilding
	    },
	    success: function(d){     
	    	if(d.series){
	    		paintLineBasic('trips_by_date_plot',d.series, 'Viajes por fecha', 'Viajes por dia de obra ' +selectedBuilding, 'viajes', 'Fecha');
	    	}
	    	
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});

  	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    async:false,
	    data: { 
	      cmd:"get_trips_by_provider",
	      obra: selectedBuilding
	    },
	    success: function(d){     
	    	if(d.data)
	    		 paintBarChart('trips_by_provider', d.data, 'Viajes por proveedor','VISE', 'PROVEEDOR','VIAJES','viajes','{point.y:.0f}');  	
	    	else 
	    		j('#trips_by_provider').highcharts().destroy();
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});
  	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    async:false,
	    data: { 
	      cmd:"get_money_by_provider",
	      obra: selectedBuilding
	    },
	    success: function(d){     
	    	if(d.data)
	    		 paintBarChart('money_by_provider', d.data, 'Importe por proveedor','VISE', 'PROVEEDOR','Importe','importe','${point.y:,.2f}');
	    	else 
	    		j('#money_by_provider').highcharts().destroy();
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});
  	j("#advance_plot_panel").unblock();

}


function getTopLowM3(selectedBuilding){

	var lowHighLevels;

	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    async:false,
	    data: { 
	      cmd:"get_low_and_high_volume",
	      obra: selectedBuilding
	    },
	    success: function(d){     
	    	lowHighLevels = d.levels;
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});

  	return lowHighLevels;
}

function getColorByM3(m3, lowHighLevels){
	if((m3 == 0 && lowHighLevels.low == 0 && lowHighLevels.top == 0) || !m3){
		return ["#27F53D", "#03A403"];//verde

	}
	if(m3 >= lowHighLevels.top){
		return ["#F52727","#B90303"];//rojo

	}
	if(m3 >= lowHighLevels.low && m3 < lowHighLevels.top){
		return ["#F5EF27","#ABB903"];//amarillo
	}
	if(m3 < lowHighLevels.low){
		return ["#27F53D", "#03A403"];//verde
	}
}

function addMarker(point, levels, selectedBuilding){

	var map = advanceMap;

	var myLatlng = new google.maps.LatLng(point.latitud, point.longitud);

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

	var icons = iconThrow;


	var throwColors = ['#11C1CF','#33F0FF'];
	var bankColors = ['#11CF9E', '#27F5C0'];
	//var bankColors = 
	var circle = new google.maps.Circle({
		strokeColor: getColorByM3(point.m3_total,levels)[1],
		strokeOpacity: (point.m3_total <= 0 ? 0.1 : 0.8),
		strokeWeight: 2,
		fillColor: getColorByM3(point.m3_total,levels)[0],
		fillOpacity: (point.m3_total <= 0 ? 0.1 : 0.5),
		map: map,
		center: {lat:point.latitud, lng: point.longitud},
		radius: 20
	});

	var infowindow = new google.maps.InfoWindow({
		content: point.cadenamiento
	});

	if(point.m3_total > 0){

		circle.addListener('click', function() {
			var contentString = getMaterialsDroppedByPoint(point.id_punto, selectedBuilding);
			if(contentString != ''){
				var infowindow = new google.maps.InfoWindow({
					content: contentString
				});
				infowindow.setPosition(circle.getCenter());
		    	infowindow.open(advanceMap);
	    	}
		});
	}

	// marker.addListener('dragend', function() {
	// 	circle.setCenter(marker.getPosition());

	// 	setTimeout(function(){changeLocation(marker.getPosition(), id_punto);},800);

	// });

	

	var pointObject = new Object();
	  //pointObject.marker = marker;
	  pointObject.circle = circle;
	  pointObject.infowindow = infowindow;
	  pointObject.latlng = myLatlng;
	  pointObject.point = point;

	advanceMapMarkers.push(pointObject);
	

}

function getMaterialsDroppedByPoint(idPoint, selectedBuilding){


	var content = '';

	j.ajax({
	    type: 'POST',
	    url: '/cgi-bin/acarreos_app/functions.cgi',
	    dataType: 'json',
	    async:false,
	    data: { 
	      cmd:"get_dropped_materials_by_point",
	      obra: selectedBuilding,
	      id_point:idPoint
	    },
	    success: function(d){     
	    	if(d.dropped_materials){

	    		for(var i = 0; i < d.dropped_materials.length; i++){
	    			content+=
	    				'<tr>'+
	    					'<td> '+d.dropped_materials[i].destino+' </td>'+
	    					'<td> '+d.dropped_materials[i].material+' </td>'+
	    					'<td> '+parseFloat(d.dropped_materials[i].m3).toFixed(2)+'</td>'+
	    				'</tr>';
	    		}
	    	} else {
	    		swal("No hay materiales tirados en este cadenamiento.");
	    	}
	    },
	    error: function(e){
	      console.log(e);
	    }
  	});



  	var table = content != '' ?
		'<table cellpadding="10" border="1" cellspacing="10">'+
			'<thead>'+
				'<tr>'+
					'<th>Cadenamiento</th>'+
					'<th>Material</th>'+
					'<th>M3</th>'+
				'</tr>'+
			'</thead>'+
			'<tbody>'+
				content+
			'</tbody>'+
		'</table>' : '';

  	return table;
}


function cleanMap(){
	for(var i = 0; i < advanceMapMarkers.length; i++){
		//advanceMapMarkers[i].marker.setMap(null);
		advanceMapMarkers[i].circle.setMap(null);
	}
	advanceMapMarkers = [];
}