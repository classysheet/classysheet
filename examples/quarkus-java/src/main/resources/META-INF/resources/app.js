
$(document).ready(function () {
  $("#writeExcelButton").click(function () {
    writeExcel();
  });
  $("#readExcelButton").click(function () {
    readExcel();
  });

  setupAjax();
  fetchDemoData();
});

function setupAjax() {
  // $.ajaxSetup({
  //   headers: {
  //     'Content-Type': 'application/json,multipart/form-data',
  //     'Accept': 'application/json,text/plain',
  //   }
  // });

  // Extend jQuery to support $.put() and $.delete()
  jQuery.each(["put", "delete"], function (i, method) {
    jQuery[method] = function (url, data, callback, type) {
      if (jQuery.isFunction(data)) {
        type = type || callback;
        callback = data;
        data = undefined;
      }
      return jQuery.ajax({
        url: url,
        type: method,
        dataType: type,
        data: data,
        success: callback
      });
    };
  });
}

function fetchDemoData() {
  $.get("/classysheet/demo-data", function (schedule) {
    let $demo = $("#demo");
    $demo.empty();
    $demo.html("<p>The schedule has " + schedule.employees.length + " employees and "
        + schedule.shifts.length + " shifts.</p>");
  }).fail(function (xhr, ajaxOptions, thrownError) {
    showError("Get demo data failed.", xhr);
  });
}

function writeExcel() {
  $.post("/classysheet/write-excel", function (data) {
    fetchDemoData();
  }).fail(function (xhr, ajaxOptions, thrownError) {
      showError("Write Excel failed.", xhr);
    },
    "text");
}

function readExcel() {
  var file = $("#readExcelFile")[0].files[0];
  if (!file) {
    alert("Please select a file.");
    return;
  }
  var formData = new FormData();
  formData.append("file", file);
  $.ajax({
    url: "/classysheet/read-excel",
    type: "POST",
    data: formData,
    processData: false,
    contentType: false,
    success: function (response) {
      fetchDemoData();
    },
    error: function (xhr, status, error) {
      showError("Read Excel failed.", xhr);
    }
  });
  // $.post("/classysheet/read-excel", function (data) {
  //   fetchDemoData();
  // }).fail(function (xhr, ajaxOptions, thrownError) {
  //     showError("Read Excel failed.", xhr);
  //   },
  //   "text");
}


function showError(title, xhr) {
  var serverErrorMessage = !xhr.responseJSON ? `${xhr.status}: ${xhr.statusText}` : xhr.responseJSON.message;
  var serverErrorCode = !xhr.responseJSON ? `unknown` : xhr.responseJSON.code;
  var serverErrorId = !xhr.responseJSON ? `----` : xhr.responseJSON.id;
  var serverErrorDetails = !xhr.responseJSON ? `no details provided` : xhr.responseJSON.details;

  if (xhr.responseJSON && !serverErrorMessage) {
    serverErrorMessage = JSON.stringify(xhr.responseJSON);
    serverErrorCode = xhr.statusText + '(' + xhr.status + ')';
    serverErrorId = `----`;
  }

  console.error(title + "\n" + serverErrorMessage + " : " + serverErrorDetails);
  const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 50rem"/>`)
      .append($(`<div class="toast-header bg-danger">
                 <strong class="me-auto text-dark">Error</strong>
                 <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
               </div>`))
      .append($(`<div class="toast-body"/>`)
          .append($(`<p/>`).text(title))
          .append($(`<pre/>`)
              .append($(`<code/>`).text(serverErrorMessage + "\n\nCode: " + serverErrorCode + "\nError id: " + serverErrorId))
          )
      );
  $("#notificationPanel").append(notification);
  notification.toast({delay: 30000});
  notification.toast('show');
}