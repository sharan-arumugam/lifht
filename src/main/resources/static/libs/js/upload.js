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
      success: function(xml, textStatus, xhr) {
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
      url: "/io/import/swipe-data",
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
