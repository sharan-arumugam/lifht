$(document).ready(function() {
  var login = "admin";
  var psNumber = [];
  var staffName = [];
  var nonchecked = 0;
  var allStaff = [];
  var allpsNumber = [];
  var psNumber_hard = sessionPsNumber;
  var tableoptions = {
    columnDefs: [
        {
            targets: [ 0, 1, 2 ],
            className: 'mdl-data-table__cell--non-numeric'
        }
    ],
    lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]] // Will change depend on the count
  };
  
  console.log("sessionPsNumber: "+sessionPsNumber);

  // Calender
  var startDate = new Date();
  var FromEndDate = new Date();
  $(".from-date").datepicker({
     format: 'dd/mm/yyyy'
 }).on('changeDate', function(selected){
     startDate = new Date(selected.date.valueOf());
     startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
     $(".to-date").datepicker('setStartDate', startDate);
 });
 $(".to-date").datepicker({
    format: 'dd/mm/yyyy'
}).on('changeDate', function(selected){
      FromEndDate = new Date(selected.date.valueOf());
      FromEndDate.setDate(FromEndDate.getDate(new Date(selected.date.valueOf())));
      $(".from-date").datepicker('setEndDate', FromEndDate);
  });

  /***** Admin *****/

  // Generate Emplyee list from API for Admin only
  // Mock data
  if (document.body.getAttribute('data-page') === "ADMIN") {
    var html = '';
    $.ajax({
      method: "GET",
      url: "api/all",
      success: function(response) {
        response.map((val) => {
          allStaff.push(val.psName);
          allpsNumber.push(val.psNumber);
          html += "<div class='staff-each staff-checkbox'><input id='"+val.psNumber+"' type='checkbox' name='' value='"+val.psNumber+"' /><label for='"+val.psNumber+"'>"+val.psName+"</label></div>";
        });
        $(".staff-list").append(html);
      },
      error: function(err) {
        // console.log(err);
      }
    });
  }

  // Staff Dropdown

  $(document).on("focus", "input[name=user-select]", function() {
    $(".staff-list").css("display", "block");
  });

  // Restrict typing in select user. Will replace this by autocomplete
  $(document).on("keydown", "input[name=user-select]", function(e) {
    return false;
  });
  $(document).on("click", "body", function(e) {
    var staffList = $(".user-dropdown");
    if (!staffList.is(e.target) && staffList.has(e.target).length === 0) {
      $(".staff-list").css("display", "none");
    }
  });

  // Select All
  $(document).on("click", "#checkbox-all", function() {
    $(".staff-checkbox").find("input:checkbox").prop('checked', this.checked);
    if ($(this).prop("checked") === true) {
      psNumber = [];
      staffName = [];
      $("input[name=user-select]").val('Selected All');
    } else {
      psNumber = [];
      staffName = [];
      $("input[name=user-select]").val('');
    }
  });

  // Select user by user for Admin
  $(document).on("click", ".staff-checkbox input:checkbox", function() {
    if ($(this).prop("checked") === false) {
      $("#checkbox-all").prop("checked", false);
      var psval = $(this).val();
      var psindex = psNumber.indexOf(psval);
      if (psindex !== -1) {
        psNumber.splice(psindex, 1);
      }
      var name = $(this).siblings('label').text();
      var nameindex = staffName.indexOf(name);
      if (nameindex !== -1) {
        staffName.splice(nameindex, 1);
      }
    }
    $(".staff-checkbox input:checkbox").each(function(i, ele) {
      if ($(ele).prop("checked") === true && !psNumber.includes($(ele).val())) {
        psNumber.push($(ele).val());
        staffName.push($(ele).siblings('label').text());
        $("#checkbox-all").prop("checked", true);
      }

      // Uncheck select all
      $(".staff-checkbox input:checkbox").each(function(i, ele) {
        if ($(ele).prop("checked") === false) {
          $("#checkbox-all").prop("checked", false);
        }
      });
    });
    if ($("#checkbox-all").prop("checked") === true) {
      psNumber = [];
      staffName = [];
      $("input[name=user-select]").val('Selected All');
    } else {
      $("input[name=user-select]").val(staffName.join(','));
    }
    $("input[name=user-select]").siblings('.error').css('visibility', 'hidden');
  });

  // Submit form Admin

  $(document).on("click", "#submit-admin-form", function() {
    var table = $('#result-table').DataTable();
    table.destroy();
    var staff = $("input[name=user-select]").val();
    var fromDateForm = $("#from-date-admin").val();
    var fromDate = '';
    var toDate = '';
    var toDateForm = $("#to-date-admin").val();
    var clear = true;
    var data = {};
    var api = '';
    var callType = '';
    var query_psList = [];
    // Check Staff selected list
    if (staff.trim().length > 0) {
      $("input[name=user-select]").siblings('.error').css('visibility', 'hidden');
    } else {
      $("input[name=user-select]").siblings('.error').css('visibility', 'visible');
      clear = false;
    }

    // Chacking dates
    if (fromDateForm.trim().length > 0) {
      $("#from-date-admin").parent().siblings('.error').css('visibility', 'hidden');
      fromDate = fromDateForm.split("/");
      fromDate = new Date(fromDate[2], fromDate[1] - 1, fromDate[0]);
      // fromDate = fromDate.getTime();
      fromDate = fromDate.toISOString();
    } else {
      $("#from-date-admin").parent().siblings('.error').css('visibility', 'visible');
      clear = false;
    }
    if (toDateForm.trim().length > 0) {
      $("#to-date-admin").parent().siblings('.error').css('visibility', 'hidden');
      toDate = toDateForm.split("/");
      toDate = new Date(toDate[2], toDate[1] - 1, toDate[0]);
      // toDate = toDate.getTime();
      toDate = toDate.toISOString();
    } else {
      $("#to-date-admin").parent().siblings('.error').css('visibility', 'visible');
      clear = false;
    }
    if (clear) {
      var chartY = [];
      $(".report").css('display', 'block');
      $(".report-title-admin").html("Compliance Report from "+ fromDateForm +" to " + toDateForm);
      if (staff === 'Selected All') {
        callType = 'multi-ps-multi-date';
        api = "api/swipe/range-multi-ps";
        data = {
          psNumberList: allpsNumber,
          fromDate,
          toDate,
        }
        query_psList = allpsNumber;
      } else if (staff.split(",").length === 1) {
        callType = 'single-ps-multi-date';
        api = "api/swipe/range-single-ps";
        data = {
          psNumber: psNumber.join(),
          fromDate,
          toDate,
        }
        query_psList = psNumber;
      } else if (staff.split(",").length > 1) {
        callType = 'multi-ps-multi-date';
        api = "api/swipe/range-multi-ps";
        data = {
          psNumberList: psNumber,
          fromDate,
          toDate,
        }
        query_psList = psNumber;
      }
      var dateRange = fromDateForm +" to " + toDateForm;
      // Generate excel url
      var query_fromdate = fromDateForm.replace(/\//g, '-');
      var query_toDate = toDateForm.replace(/\//g, '-');
      var excel_url = "io/export/range-multi-ps?fromDate="+query_fromdate+"&toDate="+query_toDate+"&psNumberList="+query_psList.join(",");
      $(".download-xl").attr("href", excel_url);
      $.ajax({
        method: "POST",
        data: JSON.stringify(data),
        url: api,
        contentType:"application/json; charset=utf-8",
        dataType:"json",
        success: function(response) {
          var tableHtml = '';
          var tableHtml = structureTable(response, callType);
          $("#result-table tbody").html(tableHtml);
          $('#result-table').DataTable(tableoptions);
        }
      });
    } else {
      return false;
    }
  });

  function structureTable(response, type) {
    var date = [];
    var name = [];
    var inTime = [];
    var chartType='';
    var tableHtml = '';
    if (type === 'single-ps-multi-date') {
      response.map((val) => {
        console.log(val);
        date.push(val.swipeDate);
        name = val.employee.psName;
        inTime.push(val.durationString);
        tableHtml += "<tr>/n<td>"+val.employee.psName+"</td><td>"+val.swipeDate+"</td><td>"+val.durationString+"</td><td>"+val.complianceString+"</td><td>"+val.filoString+"</td><td><button data-psNumber='"+val.employee.psNumber+"' class='btn btn-default btn-link' data-toggle='modal' data-target='#admin-detail'></button></td></tr>";
      });
      chartType = 'line';
      createChart(chartType,date,name,inTime);
    } else if (type === 'multi-ps-multi-date') {
      response.map((val) => {
        console.log(val);
        date = val.dateRange;
        name.push(val.employee.psName);
        inTime.push(val.durationString);
        tableHtml += "<tr>/n<td>"+val.employee.psName+"</td><td>"+val.dateRange+"</td><td>"+val.durationString+"</td><td>"+val.complianceString+"</td><td>"+val.filoString+"</td><td><button data-psNumber='"+val.employee.psNumber+"' class='btn btn-default btn-link' data-toggle='modal' data-target='#admin-detail'></button></td></tr>";
      });
        chartType = "column";
        createChart(chartType,name,date,inTime);
    }
    return tableHtml;
  }


  /****** End Admin ******/

  /***Staff Page***/
  if (document.body.getAttribute("data-page") === "staff") {
    $(document).on("click", "#submit-staff-form", function() {
      $('#result-table-single').css("display", "none");
      $('#result-table-range').css("display", "none");
      var table1 = $('#result-table-single').DataTable();
      var table2 = $('#result-table-range').DataTable();
      $(".report").css("display","none");
      table1.destroy();
      table2.destroy();
      var fromDateForm = $("#from-date-staff").val();
      var toDateForm = $("#to-date-staff").val();
      var fromDate = '';
      var toDate = '';
      var clear = true;
      var psName = '';
      var callType = '';
      // Chacking dates
      if (fromDateForm.trim().length > 0) {
        $("#from-date-staff").parent().siblings('.error').css('visibility', 'hidden');
        fromDate = fromDateForm.split("/");
        fromDate = new Date(fromDate[2], fromDate[1] - 1, fromDate[0]);
        var form_fromDate = fromDate.toISOString();
      } else {
        $("#from-date-staff").parent().siblings('.error').css('visibility', 'visible');
        clear = false;
      }
      if (toDateForm.trim().length > 0) {
        $("#to-date-staff").parent().siblings('.error').css('visibility', 'hidden');
        toDate = toDateForm.split("/");
        toDate = new Date(toDate[2], toDate[1] - 1, toDate[0]);
        var form_toDate = toDate.toISOString();
      } else {
        $("#to-date-staff").parent().siblings('.error').css('visibility', 'visible');
        clear = false;
      }
      var data = '';
      var url = '';
      if (form_fromDate === form_toDate) {
        data = {
          psNumber: psNumber_hard,
          date: form_toDate
        };
        url = "api/swipe/date-single-ps";
        callType = "single";
      }
      else {
        data = {
          psNumber: psNumber_hard,
          fromDate: form_fromDate,
          toDate: form_toDate
        };
        url = "api/swipe/range-single-ps";
        callType = "range";
      }
      if (clear) {
        $.ajax({
          method : "POST",
          url,
          data : JSON.stringify(data),
          contentType : "application/json",
          success:function(response){
            var tableHtml = '';
            var date= '';
            var date1=[];
            var inTime=[];
            var swipeIn = [];
            var name = '';
            if (callType == "single") {
              response.map((val)=>{
                //swipeIn = swipeIn.push(val.swipIn);
                inTime.push(val.durationString);
                date = val.swipeDateString;
              tableHtml += "<tr><td>"+date+"</td><td>"+val.swipeIn+"</td><td>"+val.swipeOut+"</td><td>"+val.durationString+"</td></tr>";
            });
            $("#result-table-single").css("display", "block");
            $("#result-table-single tbody").html(tableHtml);
            $("#result-table-single").DataTable();
            } else {
              response.map((val)=>{
                date1.push(val.swipeDate);
                inTime.push(val.durationString);
                name = val.employee.psName;
                date = val.swipeDateString;
              tableHtml += "<tr>/n<td>"+date+"</td><td>"+val.durationString+"</td><td>"+val.complianceString+"</td><td><button data-psNumber='"+val.employee.psNumber+"' class='btn btn-default btn-link' data-toggle='modal' data-target='#admin-detail'></button></td></tr>";
            });
            $("#result-table-range").css("display", "block");
            $("#result-table-range tbody").html(tableHtml);
            $("#result-table-range").DataTable();
            }

            $(".report").css("display","block");
            createChart('line',date1,name,inTime);
            createChart('line',swipeIn,date,inTime);
          },
          error:function(err){
            console.log(err);
          }
        });
      }
    });
  }
  /***End Staff Page***/
});

function createChart(type,date,name,inTime){
  Highcharts.chart('container', {
    chart: {
        type: type
    },
    title: {
        text: 'Staff Compliance Chart'
    },
    xAxis: {
        categories: date,
        title: {
            text: 'Date'
        }
    },
    yAxis: {
        title: {
            text: 'Time spent in Floor (Hrs)'
        }
    },
    plotOptions: {
        line: {
            dataLabels: {
                enabled: false
            },
            enableMouseTracking: true
        }
    },
    series: [{
        name: name,
        data: convertstrtotime(inTime)
    }]
  });
}
function convertstrtotime(timeStr) {
  var arr = [];
  timeStr.map((val) => {
    var str = [];
    str = val.split(":");
    str.splice(-1,1);
    str = str.join(".");
    str = parseFloat(str);
    arr.push(str);
  });
  return arr;
}

var a = 10;
function test() {
  var a = 20;
  function a() {
    console.log(hi);
  }
  console.log(typeof a);
}
