$(document).ready(function() {
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
	// $(document).on("click", "#admin-actions-toggle", function() {
  //   if ($(".admin-dropdown").is(":visible")) {
  //     $("#admin-actions-toggle").find("span").attr("class", 'glyphicon glyphicon-menu-down');
  //   } else {
  //     $("#admin-actions-toggle").find("span").attr("class", 'glyphicon glyphicon-menu-up');
  //   }
  //   $(".admin-dropdown").toggle();
  // });
  /** password **/
	$('#changePassword').on('submit', function(e) {
				e.preventDefault();
				$(".pass-success").hide();
				$(".pass-error").hide();
				$(".alert").html("");
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
        let encode_Current =  currentPass = btoa(btoa(currentPass));
        var encode_newPass = btoa(btoa(newPassword));
        $.ajax({
            method: "POST",
            url: "api/changepassword",
            data: JSON.stringify({
		            	currentPass: btoa(encode_Current),
		            	newPass: btoa(encode_newPass)
            		}),
            contentType:"application/json; charset=utf-8",
            success: function(xml, textStatus, xhr) {
							$("#currentPassword").val('');
							$("#confirmPassword").val('');
							$("#newPassword").val('');
							$(".pass-success").show();
							$(".alert-success").html("Password changed successfully")
            },
						error: function() {
							$(".pass-error").show();
							$(".pass-error .alert").html("Current Password is not matching.");
						}
        })
    });
  /** password end **/

});
