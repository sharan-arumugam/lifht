$(document).ready(function() {
	$(document).on("click", "#admin-actions-toggle", function() {
    if ($(".admin-dropdown").is(":visible")) {
      $("#admin-actions-toggle").find("span").attr("class", 'glyphicon glyphicon-menu-down');
    } else {
      $("#admin-actions-toggle").find("span").attr("class", 'glyphicon glyphicon-menu-up');
    }
    $(".admin-dropdown").toggle();
  });
  /** password **/
	$('#changePassword').on('submit', function(submitEvent) {
				let currentPass = $("#currentPassword").val();
				let confirm_pass = $("#confirmPassword").val();
				let newPassword = $("#newPassword").val();
				if (confirm_pass !== newPassword) {
					$(".pass-error").show();
					$(".pass-error .alert").html("Confirm password failed. Please check.");
					return false;
				} else if (newPassword.length < 5) {
					$(".pass-error").show();
					$(".pass-error .alert").html("Password must contain minimum five characters.");
					return false;
				}
				$(".pass-error").hide();
				$(".pass-error .alert").html("");
        let encode_Current =  currentPass = btoa(btoa(currentPass));
        var encode_newPass = btoa(btoa(newPassword));
        $.ajax({
            method: "POST",
            url: "change",
            data: JSON.stringify({
		            	currentPass: btoa(encode_Current),
		            	newPass: btoa(encode_newPass)
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
