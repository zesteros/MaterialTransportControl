


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

var ticketsTable;

var ticketsToPayTable;

var tableSettings = {
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
      return intValM3(a) + intValM3(b);
      }, 0 );
    };
    var changeFormat = function(row, column, backgroundColor, textColor){
      //row.find('th:eq('+column+')').css('background-color', backgroundColor);
      //row.find('th:eq('+column+')').css('color',textColor);
      row.find('th:eq('+column+')').css('font-size', '12px');
    };

    var carryColumn = 13;
    var materialColumn = 14;
    var subtotalColumn = 15;

    var getTotalAndSubtotalOfColumn = function(api, column){
      return ' Total: $'+ numberWithCommas(getSumOfAllPages(api,column).toFixed(2));
    // '$'+getSumOfCurrentPage(api, column).toFixed(2) +

    };

    // Update footer
    changeFormat(j(row), carryColumn, 'yellow', 'black');
    changeFormat(j(row),materialColumn,'yellow', 'black');
    changeFormat(j(row),subtotalColumn,'green','white');
    //j(row).find('th:eq(13)').css('font-size', '15px');


    j( api.column(carryColumn).footer() ).css('backgroung-color',"green");
    j( api.column(carryColumn).footer() ).html(
      getTotalAndSubtotalOfColumn(api, carryColumn)
    );

    j( api.column(materialColumn).footer() ).html(
      getTotalAndSubtotalOfColumn(api, materialColumn)
    );
    j( api.column(subtotalColumn).footer() ).html(
      getTotalAndSubtotalOfColumn(api, subtotalColumn)
    );
    changeFormat(j(row),7,'green','white');
    j( api.column(7).footer() ).html(
      "Total: "+ numberWithCommas(getSumOfM3(api, 7).toFixed(2))+" M3"
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
  "createdCell": function (td, cellData, rowData, row, col) {j(td).css('padding', '2px')}}],
  "buttons": [
    {extend: 'excelHtml5', title: 'ACARREOS VISE',exportOptions: { columns: [0, 1, 2, 3, 4, 5,6,7,8,9,10,11]}},
    {extend: 'csvHtml5', title: 'ACARREOS VISE', exportOptions: { columns: [0, 1, 2, 3, 4, 5,6,7,8,9,10,11]}},
    {extend: 'pdfHtml5', title: 'ACARREOS VISE', orientation: 'landscape', pageSize: 'LEGAL', exportOptions: { columns: [0, 1, 2, 3, 4, 5,6,7,8,9,10,11]},
    customize: function(doc) {
      doc.defaultStyle.fontSize = 16; //<-- set fontsize to 16 instead of 10 
    }
    }
  ],
  "rowCallback": function(row, data, index){
    if(data[5] == '' || data[5] == undefined){
      j(row).css('color', 'red');
      j(row).find('td:eq(3)').find('a').css('color', 'red');
    }
    j(row).find('td:eq(1)').css('font-weight', 'bold');
    j(row).find('td:eq(13)').css('font-weight', 'bold');
    j(row).find('td:eq(14)').css('font-weight', 'bold');
    j(row).find('td:eq(15)').css('font-weight', 'bold');
    //else j(row).css('color', 'green');
  }
};


j(document).ready(function(){

  j('#scan_tickets').tagsinput();

  var scanTicketsInternal = j("#scan_tickets").tagsinput('input');

  scanTicketsInternal.focus();

  scanTicketsInternal.on('change paste input', function(){

    var sheetNumber =  scanTicketsInternal.val();
    console.log(sheetNumber);
  
    if((sheetNumber.length+1)% 14 == 0 ){
      var existInTable = false;


      ticketsToPayTable
        .column(2)
        .data()
        .each( function ( value, index ) {
          if(value==sheetNumber){
            existInTable = true;

            swal({
              title: "Boleto repetido",
              text:"El boleto con no. de folio: "+sheetNumber+" ya se capturó.",
              type: "warning",
              showCancelButton: false,
              confirmButtonClass: "btn-danger",
              confirmButtonText: "Aceptar",
              closeOnConfirm: true
            },
            function(){
              scanTicketsInternal.focus();
            });
          }
        });
      if(!existInTable){
        if(addTicketToTable(sheetNumber)){
          j('#scan_tickets').tagsinput('add', sheetNumber);
          scanTicketsInternal.val("");
        } else {
           swal({
              title: "Boleto inexistente",
              text:"El ticket con no. de folio "+sheetNumber+" no existe o fue dado de baja.",
              type: "warning",
              showCancelButton: false,
              confirmButtonClass: "btn-danger",
              confirmButtonText: "Aceptar",
              closeOnConfirm: true
            },
            function(){
              scanTicketsInternal.focus();
            });
          j('#scan_tickets').tagsinput('remove', sheetNumber);


        }
      }
      return;
    }

  });

  j.ajax({
        url: "/cgi-bin/acarreos_app/functions.cgi",
        dataType: "json",
        type: "POST",
        data: {
          cmd:"get_all_active_buildings"
        },
        success: function( data ) {
          fillBuildings("#buildings_select", data);
         
        },
        error: function(e){
        }
    });

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
    j('input[name="daterange"]').val(start.format('DD/MM/YYYY')+' - '+end.add('days',1).format('DD/MM/YYYY'));
  }
  );


  if (j.fn.dataTable.isDataTable( '#tickets-table' ) ) {
    ticketsTable = j('#tickets-table').DataTable();
  } else {
    ticketsTable = j("#tickets-table").DataTable(tableSettings);
  }
  if (j.fn.dataTable.isDataTable( '#tickets-table-to-pay' ) ) {
    ticketsToPayTable = j('#tickets-table-to-pay').DataTable();
  } else {
    ticketsToPayTable = j("#tickets-table-to-pay").DataTable(tableSettings);
  }
  scanTicketsInternal.focus();
});

function addTicketToTable(sheetNumber){
  
   // ticketsToPayTable
   //  .clear()
   //  .draw();

  var existInDatabase = false;


  j.ajax({
    url: "/cgi-bin/acarreos_app/functions.cgi",
    dataType: "json",
    type: "POST",
    async: false,
    data: {
      cmd:"get_carry_tickets_by_sheet_number",
      sheet_number: sheetNumber
    },
    success: function( data ) {

      if(data.tickets != false){
        if(data.tickets.length > 0){

          var ticket = data.tickets[0];
            /*
  "fecha_salida":"$$d{'fecha_salida'}",
      "hora_salida":"$$d{'hora_salida'}",
      "folio":"$$d{'folio'}",
      "plates":"$$d{'plates'}",
      "origen":"$$d{'origen'}",
      "destino":"$$d{'destino'}",
      "material":"$$d{'material'}",
      "m3":$$d{'m3'},
      "elaboro":"$$d{'elaboro'}",
      "fecha_entrega":"$$d{'fecha_entrega'}",
      "hora_entrega":"$$d{'hora_entrega'}",
      "recibio":"$$d{'recibio'}",
      "tiempo":"$$d{'tiempo'}",
      "distancia":"$$d{'distancia'}",
      "trips":"$$d{'trips'}",
      "building_origin":"$$d{'building_origin'}",
      "building_destiny":"$$d{'building_destiny'}",
      "exit_date":"$$d{'exit_date'}",
      "id_point_origin":$$d{'id_point_origin'},
      "id_material":$$d{'id_material'},
      "importe_acarreo":"$$d{'importe_acarreo'}",
      "importe_material":"$$d{'importe_material'}"
            */
          var rowNode = ticketsToPayTable.row.add([
            "",
            ticket.fecha_salida +" "+ticket.hora_salida,
            ticket.folio,
            ticket.plates,
            ticket.origen,
            ticket.destino,
            ticket.material,
            parseFloat(ticket.m3).toFixed(2) +" M3",
            ticket.elaboro,
            ticket.fecha_entrega +" " +ticket.hora_entrega,
            ticket.recibio,
            ticket.tiempo,
            ticket.distancia,
            formatNumber(ticket.importe_acarreo?ticket.importe_acarreo:"0"),
            formatNumber(ticket.importe_material?ticket.importe_material:"0"),
            formatNumber(parseFloat(ticket.importe_acarreo?ticket.importe_acarreo:"0")+parseFloat(ticket.importe_material?ticket.importe_material:"0"))
          ])
          .draw()
          .node();
          existInDatabase = true;
          }
        } else {
          existInDatabase = false;
        }
       
      },
      error: function(e){
        console.log(e);
        existInDatabase = false;
      }
  });
    

    return existInDatabase;
}

function formatNumber(num) {
  num = parseFloat(num).toFixed(2);
  return "$"+num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
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

