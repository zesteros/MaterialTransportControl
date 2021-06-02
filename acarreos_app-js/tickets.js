
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

j(document).ready(function(){


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

	j("#tickets-table").DataTable({
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
});