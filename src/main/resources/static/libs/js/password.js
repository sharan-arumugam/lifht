$(document).ready(function() {
  /** password **/
	$('#changePassword').on('submit', function(submitEvent) {
        var currentPass = btoa(btoa($("#currentPassword").val()));
        var newPass = btoa(btoa($("#newPassword").val()));
        $.ajax({
            method: "POST",
            url: "change",
            data: JSON.stringify({
		            	currentPass: btoa(currentPass),
		            	newPass: btoa(newPass)
            		}),
            contentType:"application/json; charset=utf-8",
            dataType:"json",
            success: function(response) {
	            alert(response);
            }
          });
        submitEvent.preventDefault();
    });
  /** password end **/
});