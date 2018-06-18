$(document).ready(function() {
  /** upload head count**/
  $("#head-count").submit(function(e) {
    e.preventDefault();
    $(".head-count-ack-msg").html('').removeClass('alert-success alert-danger').css("display", "none");
    var form = document.forms.namedItem("head-count");
    var oData = new FormData(form);
    $.ajax({
      method: "POST",
      url: "/io/import/head-count",
      data: oData,
      contentType: false,
      processData: false,
      success: function(data, textStatus, xhr) {
        if (xhr.status === 202) {
          $(".head-count-ack-msg").css("display", "block").html("File Uploaded Successfully.").addClass('alert-success');
        } else {
          $(".head-count-ack-msg").css("display", "block").html("Something went wrong. Please try again.").addClass('alert-danger');
        }
        $(".head-count-ack-msg").fadeOut(10000, function() {
          $(".head-count-ack-msg").removeClass('alert-success alert-danger')
        });
      },
      error: function(err) {
        console.log(err);
      }
    })
  });

  /**** Upload swipe ****/
  $("#swipe-data").submit(function(e) {
    e.preventDefault();
    $(".swipe-data-ack-msg").html('').removeClass('alert-success alert-danger').css("display", "none");
    var form = document.forms.namedItem("swipe-data");
    var oData = new FormData(form);
    $.ajax({
      method: "POST",
      url: "/io/import/swipe-data" + "?send-mail="+ $('#sendMail').is(':checked'),
      data: oData,
      contentType: false,
      processData: false,
      success: function(xml, textStatus, xhr) {
        if (xhr.status === 202) {
          $(".swipe-data-ack-msg").css("display", "block").html("File Uploaded Successfully.").addClass('alert-success');
        } else {
          $(".swipe-data-ack-msg").css("display", "block").html("Something went wrong. Please try again.").addClass('alert-danger');
        }
        $(".swipe-data-ack-msg").fadeOut(60000, function() {
          $(".swipe-data-ack-msg").removeClass('alert-success alert-danger')
        });
      },
      error: function(err) {
        console.log(err);
      }
    })
  });
  
  /**** Upload exclusion ****/
  $("#exclusion").submit(function(e) {
    e.preventDefault();
    $(".exclusion-data-ack-msg").html('').removeClass('alert-success alert-danger').css("display", "none");
    var form = document.forms.namedItem("exclusion");
    var oData = new FormData(form);
    $.ajax({
      method: "POST",
      url: "/io/import/exclusion",
      data: oData,
      contentType: false,
      processData: false,
      success: function(xml, textStatus, xhr) {
        if (xhr.status === 202) {
          $(".exclusion-data-ack-msg").css("display", "block").html("File Uploaded Successfully.").addClass('alert-success');
        } else {
          $(".exclusion-data-ack-msg").css("display", "block").html("Something went wrong. Please try again.").addClass('alert-danger');
        }
        $(".exclusion-data-ack-msg").fadeOut(60000, function() {
          $(".exclusion-data-ack-msg").removeClass('alert-success alert-danger')
        });
      },
      error: function(err) {
        console.log(err);
      }
    })
  });

  /**** Upload Project allocation ****/
  $("#project-allocation").submit(function(e) {
    e.preventDefault();
    $(".project-all-ack-msg").html('').removeClass('alert-success alert-danger').css("display", "none");
    var form = document.forms.namedItem("project-allocation");
    var oData = new FormData(form);
    console.log(oData);
    $.ajax({
      method: "POST",
      url: "/io/import/project-allocation",
      data: oData,
      contentType: false,
      processData: false,
      xhr: function () {
          var xhr = new window.XMLHttpRequest();
          xhr.upload.addEventListener("progress", function (evt) {
              if (evt.lengthComputable) {
                  var percentComplete = evt.loaded / evt.total;
                  percentComplete = parseInt(percentComplete * 100);
                  $('.myprogress').text(percentComplete + '%');
                  $('.myprogress').css('width', percentComplete + '%');
              }
          }, false);
          return xhr;
      },
      success: function(xml, textStatus, xhr) {
        if (xhr.status === 202) {
          $(".project-all-ack-msg").css("display", "block").html("File Uploaded Successfully.").addClass('alert-success');
        } else {
          $(".project-all-ack-msg").css("display", "block").html("Something went wrong. Please try again.").addClass('alert-danger');
        }
        $(".project-all-ack-msg").fadeOut(60000, function() {
          $(".project-all-ack-msg").removeClass('alert-success alert-danger')
        });
      },
      error: function(err) {
        console.log(err);
      }
    })
  });
  
  /**** Upload Banjo File ****/
  $("#banjo-file").change((event) => {
	  
	  	event.preventDefault();
	  	
	    $(".banjo-ack-msg").html('').removeClass('alert-success alert-danger').css("display", "none");
	    
	    var form = document.forms.namedItem("banjo");
	    var oData = new FormData(form);

	    $.ajax({
	      method: "POST",
	      url: "/io/import/banjo",
	      data: oData,
	      contentType: false,
	      processData: false,
	      success: (json, textStatus, xhr) => {
	    	  if (xhr.status === 200) {

	    		  var items = json;
	    		  

	    		  const replacer = (key, value) => value === null ? '' : value;
	    		  const header = Object.keys(items[0]);
	    		  items.splice(0,1);
	    		  var date = items[3]['Date'];
	    		  var dateSplit = date.split('/');
	    		  
	    		  var fileName = "ODC_Access_LTI_" + dateSplit[0] +'-'+ dateSplit[1] + '-' + '20' + dateSplit[2];

	    		  let csv = items.map(row => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(','));
	    		  
	    		  
	    		  csv.unshift(header.join(','));
	    		  csv = csv.join('\r\n');

	    		  var downloadLink = document.createElement("a");
	    		  var blob = new Blob(["\ufeff", csv]);
	    		  var url = URL.createObjectURL(blob);
	                
	    		  downloadLink.href = url;
	    		  downloadLink.download = fileName + ".csv";
	                
	    		  document.body.appendChild(downloadLink);
	    		  downloadLink.click();
	    		  document.body.removeChild(downloadLink);
	    	  
	    	  } else {
	    		  $(".banjo-ack-msg").css("display", "block").html("Something went wrong. Please try again.").addClass('alert-danger');
	    	  }
	    	  
	    	  $(".banjo-ack-msg").fadeOut(60000, () => $(".banjo-ack-msg").removeClass('alert-success alert-danger'));
	      },
	      error: (err) => $(".banjo-ack-msg").css("display", "block").html("Error - " + err.status).addClass('alert-danger')
	    })
  });
  
  
  /**** Dpownload Resource Tracker File ****/
  $("#resourceTracker").click((event) => {
	  
	  	event.preventDefault();
	  	
	    $(".resource-ack-msg").html('').removeClass('alert-success alert-danger').css("display", "none");
	    
	    var form = document.forms.namedItem("resourceTracker");
	    var oData = new FormData(form);

	    $.ajax({
	      method: "GET",
	      url: "/io/export/resource-tracker",
	      data: oData,
	      contentType: false,
	      processData: false,
	      success: (json, textStatus, xhr) => {
	    	  if (xhr.status === 200) {

	    		  var items = json;
	    		  //console.log(items[0].valueOf().);

	    		  const replacer = (key, value) => value === null ? '' : value;
	    		  const header = Object.keys(items[0]);
	    		  //items.splice(0,1);
	    		  //var date = items[3]['Date'];
	    		  //var dateSplit = date.split('/');
	    		  
	    		  var fileName = "Resource_Tracker";

	    		  let csv = items.map(row => header.map(fieldName => JSON.stringify(row[fieldName], replacer)).join(','));
	    		  
	    		  
	    		  //csv.unshift(header.join(','));
	    		  csv = csv.join('\r\n');

	    		  var downloadLink = document.createElement("a");
	    		  var blob = new Blob(["\ufeff", csv]);
	    		  var url = URL.createObjectURL(blob);
	                
	    		  downloadLink.href = url;
	    		  downloadLink.download = fileName + ".csv";
	                
	    		  document.body.appendChild(downloadLink);
	    		  downloadLink.click();
	    		  document.body.removeChild(downloadLink);
	    	  
	    	  } else {
	    		  $(".resource-ack-msg").css("display", "block").html("Something went wrong. Please try again.").addClass('alert-danger');
	    	  }
	    	  
	    	  $(".resource-ack-msg").fadeOut(60000, () => $(".resource-ack-msg").removeClass('alert-success alert-danger'));
	      },
	      error: (err) => $(".resource-ack-msg").css("display", "block").html("Error - " + err.status).addClass('alert-danger')
	    })
  });
  
  
  $(document).ajaxStart(function() {
    $(".loading").show(); // show the gif image when ajax starts
  }).ajaxStop(function() {
      $(".loading").hide(); // hide the gif image when ajax completes
  });
});

$(document).on("click", "body", function(e) {
  const container = $(".admin-dropdown");
  const mainContainer = $("#admin-actions-toggle");
  if (mainContainer.is(e.target) || mainContainer.has(e.target).length > 0) {
    if (container.css("display") == 'block') {
        container.hide();
      } else {
        container.show();
      }
  } else if (!container.is(e.target) && container.has(e.target).length === 0) {
     container.hide();
  }
});
