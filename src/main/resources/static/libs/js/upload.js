$(document).ready(function() {
  /** upload **/
  var form = document.forms.namedItem("fileinfo");
  form.addEventListener('submit', function(ev) {

    var oOutput = document.querySelector("div"),
        oData = new FormData(form);

    var oReq = new XMLHttpRequest();
    oReq.open("POST", "/io/import/head-count", true);
    oReq.onload = function(oEvent) {
      if (oReq.status == 200) {
        oOutput.innerHTML = "Uploaded!";
      } else {
        oOutput.innerHTML = "Error " + oReq.status + " occurred when trying to upload your file.<br \/>";
      }
    };

    oReq.send(oData);
    ev.preventDefault();
  }, false);
  /** upload end **/
});