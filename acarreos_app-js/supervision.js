var reprintTable;
var ticketsTable;
var syncTable;
var locationsMarkers = [];

var routeMap;

var monthNames = [
    "Enero", "Febrero", "Marzo",
    "Abril", "Mayo", "Junio", "Julio",
    "Agosto", "Septiembre", "Octubre",
    "Noviembre", "Diciembre"
  ];

var weekdays =  [
    "Dom",
    "Lun",
    "Mar",
    "Mié",
    "Jue",
    "Vie",
    "Sáb"
];

var detailsControlClickControl = 0;
var mapUserLocations, heatmap, ticketLocations;
var advanceTable;

j(document).ready(function(){

  j("input").attr("autocomplete", "off"); 

  if (window.location.href.indexOf("cmd=advance") != -1){

    if (j.fn.dataTable.isDataTable( '#advance_table' ) ) {
      advanceTable = j('#advance_table').DataTable();
    }else {
        advanceTable = j("#advance_table").DataTable({
          "order":[],
          "scrollX": true,
          "paging":   true,
          "fixedColumns": true,
          "language": {"url": "/lib/js/datatables.spanish.json"},
          "dom": 'Bfrtip',
          "columnDefs": [{
                      "targets": '_all',
                      "createdCell": function (td, cellData, rowData, row, col) {
                          j(td).css('padding', '5px')
                      }
                  }],
          "buttons": [{
                extend: 'excelHtml5',
                title: 'ACARREOS VISE',
                exportOptions: {
                    columns: [0, 1, 2, 3, 4, 5]
                }
            }, {
                extend: 'csvHtml5',
                title: 'ACARREOS VISE',
                exportOptions: {
                   columns: [0, 1, 2, 3, 4, 5]
                }
            }, {
                extend: 'pdfHtml5',
                title: 'ACARREOS VISE',
                orientation: 'landscape',
                pageSize: 'LEGAL',
                exportOptions: {
                    columns: [0, 1, 2, 3, 4, 5]
                },
                customize: function(doc) {
                  doc.defaultStyle.fontSize = 6; //<-- set fontsize to 16 instead of 10 
                }
            }],
              "rowCallback": function(row, data, index){
                    // if(data[5] == '' || data[5] == undefined){
                    //   j(row).css('color', 'red');
                    //   j(row).find('td:eq(3)').find('a').css('color', 'red');
                    // }
                    // j(row).find('td:eq(1)').css('font-weight', 'bold');
                    //  j(row).find('td:eq(13)').css('font-weight', 'bold');
                    //  j(row).find('td:eq(14)').css('font-weight', 'bold');
                    //  j(row).find('td:eq(15)').css('font-weight', 'bold');
                    // //else j(row).css('color', 'green');
                }
        });

    }
  }

  j("#buildings_select").on('change', function() {
    getProviders();
  });


   j( "#unique_date" ).datepicker({
      defaultDate: "+1w",
      changeMonth: true,
       //minDate: new Date(),
      //numberOfMonths: 2,
      onClose: function( selectedDate ) {
        j("#daterange").val("");
        //j( "#fecha_inicial" ).datepicker( "option", "maxDate", selectedDate );
      }
    });

  j("#ticket_type").multiselect({
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
          user: userSession
        },
        success: function( data ) {
          fillBuildings("#buildings_select", data);
          getProviders();
         
        },
        error: function(e){
        }
    });

  if (j.fn.dataTable.isDataTable( '#tickets-table' ) ) {
    ticketsTable = j('#tickets-table').DataTable();
  }else {
      ticketsTable = j("#tickets-table").DataTable({
       "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
      fixedHeader: true,
    "footerCallback": function ( row, data, start, end, display ) {

        var api = this.api(), data;
        // Remove the formatting to get integer data for summation
        var intVal = function ( i ) {
            return typeof i === 'string' ?
                i.replace(/[\$,]/g, '')*1 :
                typeof i === 'number' ?
                    i : 0;
        };

         var intValM3 = function ( i ) {

            return typeof i === 'string' ?
                i.replace(/ M3/g, '').replace(/[\$,]/g, '')*1 :
                typeof i === 'number' ?
                    i : 0;
        };

        var getSumOfAllPages = function(api, column){
          return api
            .column(column)
            .data()
            .reduce( function (a, b) {
                return intVal(a) + intVal(b);
            }, 0 );
        };

        var getSumOfCurrentPage = function(api, column){
          return  api
            .column(column, { page: 'current'} )
            .data()
            .reduce( function (a, b) {
                return intVal(a) + intVal(b);
            }, 0 );
        };

        var numberWithCommas = function (x) {
            return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }

        var getSumOfM3 = function(api, column){
          return  api
            .column(column)
            .data()
            .reduce( function (a, b) {
              //console.log(a);
                return intValM3(a) + intValM3(b);
            }, 0 );
        };

         var getSumOfCurrentPageM3 = function(api, column){
          return  api
            .column(column, {page:'current'})
            .data()
            .reduce( function (a, b) {
              //console.log(a);
                return intValM3(a) + intValM3(b);
            }, 0 );
        };
        var changeFormat = function(row, column, backgroundColor, textColor){
          //row.find('th:eq('+column+')').css('background-color', backgroundColor);
          //row.find('th:eq('+column+')').css('color',textColor);
          row.find('th:eq('+column+')').css('font-size', '12px');
        };

        var carryColumn = 14;
        var materialColumn = 15;
        var subtotalColumn = 16;

        var getTotalAndSubtotalOfColumn = function(api, column){
          return numberWithCommas(getSumOfAllPages(api,column).toFixed(2));
          // '$'+getSumOfCurrentPage(api, column).toFixed(2) +
          
        };

        // Update footer
       changeFormat(j(row), carryColumn, 'yellow', 'black');
       changeFormat(j(row),materialColumn,'yellow', 'black');
       changeFormat(j(row),subtotalColumn,'green','white');
        //j(row).find('th:eq(13)').css('font-size', '15px');


        j( api.column(carryColumn).footer() ).css('backgroung-color',"green");
        j( api.column(carryColumn).footer() ).html(
             "Total página: $"+numberWithCommas(getSumOfCurrentPage(api, carryColumn).toFixed(2))+
          "\nTotal: $"+getTotalAndSubtotalOfColumn(api, carryColumn)
        );

        j( api.column(materialColumn).footer() ).html(
             "Total página: $"+numberWithCommas(getSumOfCurrentPage(api, materialColumn).toFixed(2))+
          "\nTotal: $"+getTotalAndSubtotalOfColumn(api, materialColumn)
        );
        j( api.column(subtotalColumn).footer() ).html(
           "Total página: $"+numberWithCommas(getSumOfCurrentPage(api, subtotalColumn).toFixed(2))+
          "\nTotal: $"+getTotalAndSubtotalOfColumn(api, subtotalColumn)
        );
        changeFormat(j(row),8,'green','white');
        j( api.column(8).footer() ).html(
            "Total página: "+numberWithCommas(getSumOfCurrentPageM3(api, 8).toFixed(2))+" M3"+"\n\n"+
            "Total: "+ numberWithCommas(getSumOfM3(api, 8).toFixed(2))+" M3"
        );
    },
    "order":[],
    "scrollX": true,
    "paging":   true,
    "fixedColumns": true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
    "dom": 'Bfrtip',
    "columnDefs": [{
                "targets": '_all',
                "createdCell": function (td, cellData, rowData, row, col) {
                    j(td).css('padding', '2px')
                }
            }],
    "buttons": [{
          extend: 'excelHtml5',
          title: 'ACARREOS VISE',
          exportOptions: {
             columns: [1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16,17],
             format: {
                body: function ( data, row, column, node ) {
                    // Strip $ from salary column to make it numeric
                    if(column == 3)//placa trasera
                      return data.substring(data.indexOf('placaT=')+7,data.indexOf('">'));
                    if(column == 7)//volumen
                      return  data.replace( /M3/g, '' );
                    if(column == 12)//distancia
                      return  data.replace( /KM/g, '' );
                    if(column >= 13 && column <= 15)//dinero
                      return data.replace( /[$,]/g, '' );
                    return data;
                }
            }
          }
      }, {
          extend: 'csvHtml5',
          title: 'ACARREOS VISE',
          exportOptions: {
              columns: [1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16,17]
          }
      }, {
          extend: 'pdfHtml5',
          title: 'ACARREOS VISE',
          orientation: 'landscape',
          pageSize: 'LEGAL',
          exportOptions: {
              columns: [1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16,17]
          },
          customize: function(doc) {
            doc.defaultStyle.fontSize = 8; //<-- set fontsize to 16 instead of 10 
          }
      }],
        "rowCallback": function(row, data, index){

              j(row).find('td:eq(2)').css('font-weight', 'bold');
               j(row).find('td:eq(14)').css('font-weight', 'bold');
               j(row).find('td:eq(15)').css('font-weight', 'bold');
               j(row).find('td:eq(16)').css('font-weight', 'bold');

              if(data[17] != 'N/A'){// || data[16] == undefined){
                j(row).css('color', 'white');
                j(row).find('td:eq(4)').find('a').css('color', 'white');
                //j(row).addClass('redClass');
                j('td', row).css('background-color', '#D49A72');
                return;
              }

              if(data[6] == '' || data[6] == undefined){
                //j(row).css('color', 'red');
                //j(row).find('td:eq(3)').find('a').css('color', 'red');
                j('td', row).css('background-color', '#E9CBA5');
                return;
              }
         
              j('td', row).css('background-color', '#9FD59C');
              //else j(row).css('color', 'green');
          }
    });

}

  j('#tickets-table tbody').on('click', 'td.details-control', function () {

        if(detailsControlClickControl>0){
        var tr = j(this).closest('tr');
        var row = ticketsTable.row( tr );

        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            if(row.data()[5] && row.data()[5] != '' ){
              // Open this row
              row.child( formatCarry(row.data(), tr, row) ).show();
              tr.addClass('shown');
            } else {
              swal("Acarreo sin destino");
            }
        }
        detailsControlClickControl = 0;
        } else detailsControlClickControl++;
;
    } );

  reprintTable = j("#reprints-table").DataTable({
    "order":[],
    "scrollX": true,
    "paging":   true,
    "fixedColumns": true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
       "columnDefs": [
    {
      "targets": 0,
      "className": "text-center",
    }]
    });
 /* syncTable = j("#sync-table").DataTable({
    "order":[],
    "scrollX": true,
    "paging":   true,
    "fixedColumns": true,
    "language": {"url": "/lib/js/datatables.spanish.json"},
       "columnDefs": [
    {
      "targets": 0,
      "className": "text-center",
    }]
    });*/

  j('input[name="daterange"]').daterangepicker({
      opens: 'right',
      autoUpdateInput: false,
      ranges: {
       'Ayer': [moment().subtract(1, 'days'), moment().add('days',1)],
       'Últimos 7 días': [moment().subtract(6, 'days'), moment()],
       'Últimos 30 días': [moment().subtract(30, 'days'), moment()],
       'Este mes': [moment().startOf('month'), moment().endOf('month')],
       'Último mes': [moment().subtract(1, 'month').startOf('month'), moment().endOf('month')]
      },
      maxYear: parseInt(moment().format('YYYY'),10),
      locale: {
        format: 'DD/MM/YYYY',
        cancelLabel: 'Cancelar',
        applyLabel: 'Aplicar',
        fromLabel: 'Desde',
        toLabel: 'Hasta',
        customRangeLabel: "Personalizada",
        weekLabel: "S",
        daysOfWeek:weekdays,
        monthNames: monthNames,
        firstDay: 1
      }
  }, 
    function(start, end, label) {
      //  loadTable(start.format('YYYY-MM-DD'), end.add('days',1).format('YYYY-MM-DD'));
      j("#unique_date").val("");
    j('input[name="daterange"]').val(start.format('DD/MM/YYYY')+' - '+end.add('days',1).format('DD/MM/YYYY'));
  }
  );
  //if (window.location.href.indexOf("cmd=inicio&carries=false") != -1){
    j("#sheet_number").autocomplete({
        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            data: {
              q: request.term,
              cmd:"get_sheet_numbers"
            },
            success: function( data ) {
              //j( "#id_material" ).val( "" );
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
          j("#sheet_number").val( ui.item.key);
          return false;
        }

      });
     j("#origin").autocomplete({
        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            data: {
              q: request.term,
              cmd:"get_points_in_tickets"
            },
            success: function( data ) {
              //j( "#id_material" ).val( "" );
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
          j("#origin").val( ui.item.value);
          j("#id_point_origin").val(ui.item.key);
          return false;
        }

      });
       j("#rear_plate").autocomplete({
        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            data: {
              q: request.term,
              cmd:"get_license_plates"
            },
            success: function( data ) {
              //j( "#id_material" ).val( "" );
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
          j("#rear_plate").val( ui.item.key);
          return false;
        }

      });
       //get_material_in_tickets
      j("#material").autocomplete({
        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            data: {
              q: request.term,
              cmd:"get_material_in_tickets"
            },
            success: function( data ) {
              //j( "#id_material" ).val( "" );
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
          j("#material").val( ui.item.value);
          j("#id_material").val( ui.item.key);
          return false;
        }

      });
       j("#user").autocomplete({
        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            data: {
              q: request.term,
              cmd:"get_user_in_tickets"
            },
            success: function( data ) {
              //j( "#id_material" ).val( "" );
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
          j("#user").val( ui.item.value);
          j("#id_user").val( ui.item.key);
          return false;
        }

      });

      j("#username").autocomplete({
        minLength: 1,
        source: function( request, response ) {
          j.ajax({
            url: "/cgi-bin/siloagua/functions.cgi",
            dataType: "json",
            type: "GET",
            data: {
              q: request.term,
              cmd:"get_employee"
            },
            success: function( data ) {
              //j( "#id_material" ).val( "" );
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
          j("#username").val( ui.item.value);
          j("#id_user").val( ui.item.key);
          return false;
        }

      });
  //}
   j('#reprints-table tbody').on('click', 'td.details-control', function () {
        var tr = j(this).closest('tr');
        var row = reprintTable.row( tr );

        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            row.child( format(row.data()) ).show();
            tr.addClass('shown');
        }
    } );

});

function fillSheetNumbers(id, data){

}



function setHeatMap(){
  var userId = j("#id_user").val();

  if(!userId){
    alert("Introduce un usuario a buscar"); 
    return;
  }

  if(heatmap) {
    heatmap.setMap(null);
    heatmap = null;
  }

  var heatCoordinates = addUserLocationsToMap(mapUserLocations, userId);

  var gradient = [
        'rgba(0, 255, 255, 0)',
        'rgba(0, 255, 255, 1)',
        'rgba(0, 191, 255, 1)',
        'rgba(0, 127, 255, 1)',
        'rgba(0, 63, 255, 1)',
        'rgba(0, 0, 255, 1)',
        'rgba(0, 0, 223, 1)',
        'rgba(0, 0, 191, 1)',
        'rgba(0, 0, 159, 1)',
        'rgba(0, 0, 127, 1)',
        'rgba(63, 0, 91, 1)',
        'rgba(127, 0, 63, 1)',
        'rgba(191, 0, 31, 1)',
        'rgba(255, 0, 0, 1)'
      ];

  if(heatCoordinates.length > 0){

    heatmap = new google.maps.visualization.HeatmapLayer({
      data: heatCoordinates,
      map: mapUserLocations
    });
    //heatmap.set('gradient', gradient);
     heatmap.set('radius',  50);
  } else {
    alert("No se encontraron ubicaciones del usuario "+j("#username").val());
  }
}

function addUserLocationsToMap(map, userId){

  var locations = [];
  j.ajax({
    url: "/cgi-bin/acarreos_app/functions.cgi",
    dataType: "json",
    type: "POST",
    async:false,
    data: {
      cmd:"get_user_locations",
      id_user:userId
    },
    success: function( data ) {
      //console.log(data);
      j.each(data.data,function(i,o){
            var coordinates = o.location.split(",");
            var lat =  parseFloat(coordinates[0]);
            var lon = parseFloat(coordinates[1]);
            var text = o.username + " - " + o.super_nomina_id;
            //addMarker(text, map, lat, lon);
            locations.push(new google.maps.LatLng(lat, lon));
      });
      drawSyncsTable(data.data);
      map.setCenter(locations[locations.length-1]);

    
    },
    error: function(e){
    }
  });
  return locations;

}

function drawSyncsTable(data){
   var table;

  if ( j.fn.dataTable.isDataTable( '#sync-table' ) ) {
      table = j('#sync-table').DataTable();
  }
  else {
      table = j('#sync-table').DataTable( {
          "language": {"url": "/lib/js/datatables.spanish.json"},
      } );
  }

  table
    .clear()
    .draw();

  //j("#sync-table tbody tr").remove();
  for(var i = 0; i < data.length; i++){

    var rowNode = table
    .row.add(
      [
        data[i].username +" - "+ data[i].super_nomina_id,
        data[i].fecha+ " "+data[i].hora,

        '<a href="#" onclick="moveMapTo(\''+data[i].location+'\',\''+data[i].fecha+ ' '+data[i].hora+'\')">'+data[i].location+'</a>',
        data[i].imei
      ] )
    .draw()
    .node();
  }
  
}


function moveMapTo(coordinates,date){

  if(locationsMarkers){
    if(locationsMarkers.length>0){
      for(var i = 0; i < locationsMarkers.length; i++){
        locationsMarkers[i].setMap(null);
      }
    }
  }

  var coordinates = coordinates.split(",");
  var lat =  parseFloat(coordinates[0]);
  var lon = parseFloat(coordinates[1]);

  var latlng = new google.maps.LatLng(lat, lon);

  mapUserLocations.setCenter(latlng);

  var marker = new google.maps.Marker({
    position: latlng,
    title:coordinates,
    label:date
  });

// To add the marker to the map, call setMap();
  marker.setMap(mapUserLocations);
  locationsMarkers.push(marker);

}

function addMarker(name, map, latitude, longitude){//, radius, id_punto){


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

  var marker = new google.maps.Marker({
      position: myLatlng, 
      map: map, 
      label:name,
      title:name,
      draggable:true,

  });
  marker.addListener('click', function() {
    infowindow.open(map, marker);
  });

  var contentString = '<h4><b>'+name+'</b> <br>'+latitude+', '+longitude+'</h4>';//+'Cambiar radio <button>+</button><button>-</button>';
  var infowindow = new google.maps.InfoWindow({
    content: contentString
  });


}

function showInMap(latlng){
   var url = "https://maps.google.com/?q=" + latlng;
   window.open(url);
}

function format ( d ) {
      var table = '<h4><b>Folio:</b> '+d[3]+'</h3>'+
      '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'+
      '<thead><tr><th>Fecha impresión</th><th>Ubicación</th></thead>';

      +'<tbody>';
      // `d` is the original data object for the row
      j.ajax({
            url: "/cgi-bin/acarreos_app/functions.cgi",
            dataType: "json",
            type: "POST",
            async:false,
            data: {
              sheet_number:d[3],
              cmd:"get_reprints"
            },
            success: function( data ) {
              j.each(data.reprints, function(i,o){
                table+='<tr>'+
                    '<td>'+o.fecha+' '+o.hora+'</td>'+
                    '<td><a href="" onclick="showInMap(\''+o.lat+','+o.lon+'\')">Mostrar en mapa</a></td>'+
                '</tr>';
              });
            },
            error: function(e){
            }
          });
      table+='</tbody></table>';
    return table;
    
}


function initCarryMap() {
        var directionsService = new google.maps.DirectionsService();
        var directionsRenderer = new google.maps.DirectionsRenderer();
        var map = new google.maps.Map(document.getElementById('map-carry'), {
          zoom: 7,
          center: {lat: 41.85, lng: -87.65}
        });
        directionsRenderer.setMap(map);

        var onChangeHandler = function() {
          calculateAndDisplayRoute(directionsService, directionsRenderer);
        };
        document.getElementById('start').addEventListener('change', onChangeHandler);
        document.getElementById('end').addEventListener('change', onChangeHandler);
      }

function calculateAndDisplayRoute(directionsService, directionsRenderer) {
  directionsService.route(
      {
        origin: {query: document.getElementById('start').value},
        destination: {query: document.getElementById('end').value},
        travelMode: 'DRIVING'
      },
      function(response, status) {
        if (status === 'OK') {
          directionsRenderer.setDirections(response);
        } else {
          window.alert('Directions request failed due to ' + status);
        }
      });
}



function formatCarry ( d, tr, row ) {


      var table = 
      '<style>'+
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      '#map {'+
       ' height: 50%;'+
      '}'+
      /* Optional: Makes the sample page fill the window. */
      'html, body {'+
        'height: 50%;'+
        'margin: 0;'+
        'padding: 0;'+
      '}'+
      '#floating-panel {'+
        'position: absolute;'+
        'top: 10px;'+
        'left: 25%;'+
        'z-index: 5;'+
        'background-color: #fff;'+
        'padding: 5px;'+
        'border: 1px solid #999;'+
        'text-align: center;'+
        'font-family: "Roboto","sans-serif";'+
        'line-height: 30px;'+
        'padding-left: 10px;'+
      '}'+
    '</style>'+

      '<h4><b>Folio:</b> '+d[2]+'</h3>'+
      '<button onclick="showRoute(\''+d[2]+'\')" > Mostrar ruta</button>'
    return table;
}

var toHHMMSS = function (time) {
    var sec_num = parseInt(time, 10); // don't forget the second param
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    if (hours   < 10) {hours   = "0"+hours;}
    if (minutes < 10) {minutes = "0"+minutes;}
    if (seconds < 10) {seconds = "0"+seconds;}
    return hours+':'+minutes+':'+seconds;
}

function showRoute(sheetNumber){
  j.ajax({
    url: "/cgi-bin/acarreos_app/functions.cgi",
    dataType: "json",
    type: "POST",
    async:false,
    data: {
      sheet_number:sheetNumber,
      cmd:"get_throw_and_bank_coordinates"
    },
    success: function( data ) {
      if(data.coordinates != false){
         j('#show_route_map_modal').modal("show");

        var originLatitude = data.coordinates[0].origen_coordenadas.split(",")[0];
        var originLongitude = data.coordinates[0].origen_coordenadas.split(",")[1];

        var destinyLatitude = data.coordinates[0].destino_coordenadas.split(",")[0];
        var destinyLongitude = data.coordinates[0].destino_coordenadas.split(",")[1];

        var originCoordinates = new google.maps.LatLng(parseFloat(originLatitude), parseFloat(originLongitude));
        var destinyCoordinates = new google.maps.LatLng(parseFloat(destinyLatitude), parseFloat(destinyLongitude));

        directionsRenderer.setMap(map);


        directionsService.route(
          {
            origin: originCoordinates,
            destination: destinyCoordinates,
            travelMode: 'DRIVING'
          },
          function(response, status) {
            if (status === 'OK') {
              directionsRenderer.setDirections(response);
              j("#distance").text(parseFloat(response.routes[0].legs[0].distance.value/1000).toFixed(2) +" km");
              j("#time").text(toHHMMSS(parseFloat(response.routes[0].legs[0].duration.value)));
              
            } else {
              window.alert('Directions request failed due to ' + status);
            }
          });



      }
    },
    error: function(e){
    }
  });
}

function getProviders(){
  var obra = j("#buildings_select").val();
 j.ajax({
    url: "/cgi-bin/acarreos_app/functions.cgi",
    dataType: "json",
    type: "POST",
    data: {
      cmd:"get_providers_by_building",
      obra: obra
    },
    success: function( data ) {
      var providers = j("#provider_select");
      providers.empty();
        var selectedProvider = j("#selected_provider").val();
        var allSelected = selectedProvider == "-1" ? "selected" : "";
         providers.append(
              '<option value="-1" '+allSelected+'>Todos los proveedores</option>');
        
        for(var i = 0; i < data.proveedores.length; i++){
          
          var selected = data.proveedores[i].id_proveedor === selectedProvider ? "selected" :"";

          providers.append(
              '<option value=' + data.proveedores[i].id_proveedor +' '+selected+'>'
               + data.proveedores[i].nombre  + '</option>');
        }
        providers.multiselect({
            multiple: false,
            header: "Selecione una opción",
            noneSelectedText: "Selecione una opción",
            selectedList: 1
          }).multiselectfilter();

    },
    error: function(e){
    }
  });

}

