$(document).ready(function() {
	
  $(".form-inputs").on("keyup", "input[name=psNumber],input[name=password]", function(event) {
    if (event.key === 'Enter') {
      $(".btn-submit-login").click();
    }
  });

  // submitting form
  $(document).on("click", ".btn-submit-login", function() {
    $("#error").css("visibility", "hidden");
    var username= $("input[name=username]").val();
    var password= $("input[name=password]").val();
    var pattern = /[-!$%^&*()@_+|~=`{}\[\]:";'<>?,.\/]/;
    //Username And Password Validation
    if(username.trim() === "" || password.trim() === ""){
      $("#error").html("Please Enter the credentials").css("visibility", "visible");
      return false;
    } else if (pattern.test(username) === true) {
      $("#error-1").html("Special Characters are not allowed").css("display","inline");
    }
  });
});
