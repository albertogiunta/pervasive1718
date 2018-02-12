var taskList

// Helps finding objects with unique fields
function findUnique(arr, predicate) {
    var found = {};
    arr.forEach(d => {
      found[predicate(d)] = d;
    });
    return Object.keys(found).map(key => found[key]); 
}

// Trasposes rows with columns in a table
function trasposeTable() {
    $("tbody").each(function() {
        var $this = $(this);
        var newrows = [];
        $this.find("tr").each(function(){
            var i = 0;
            $(this).find("td").each(function(){
                i++;
                if(newrows[i] === undefined) { newrows[i] = $("<tr></tr>"); }
                newrows[i].append($(this));
            });
        });
        $this.find("tr").remove();
        $.each(newrows, function(){
            $this.append(this);
        });
    });
}

// Updates the Task Table
function updateTable() {

    // Finding unique operators
    var uniqueOperators = findUnique(taskList, t => t.operatorId);

    // Sorting operators from the busiest to the less busy for avoiding a visual bug.
    for (var i in uniqueOperators) {
        var operatorId = uniqueOperators[i].operatorId;
        uniqueOperators[i].operatorTaskList = taskList.filter(t => t.operatorId == operatorId).sort(function (a, b) {
            return (a.priority).localeCompare(b.priority)
        })
    }
    uniqueOperators.sort(function (a, b ) {
        return b.operatorTaskList.length - a.operatorTaskList.length
    })
    

    var tableHeaderString = "";
    // Printing operator names
    for (var i in uniqueOperators) {
        var operatorId = uniqueOperators[i].operatorId;
        tableHeaderString = tableHeaderString + "<th id=\""+uniqueOperators[i].operatorId+"\">" + uniqueOperators[i].operatorName + " " + uniqueOperators[i].operatorSurname + "</th>";
    };
    document.getElementById("names").innerHTML = tableHeaderString
    document.getElementById("tasks").innerHTML = ""

    // Writing all operator's tasks, one operator per time, IN ROW    
    for (var i in uniqueOperators) {
        var tableBodyString = "<tr>";
        var operatorId = uniqueOperators[i].operatorId;
        
        for (var j in uniqueOperators[i].operatorTaskList) {
            tableBodyString = tableBodyString + "<td>" + uniqueOperators[i].operatorTaskList[j].name+ "</td>";
        }

        tableBodyString = tableBodyString + "</tr>";
        document.getElementById("tasks").innerHTML = document.getElementById("tasks").innerHTML + tableBodyString
    }

    // Flip rows with columns
    trasposeTable()
}

(function pollService() {
    $.ajax({
        url: 'http://localhost:4567/api/all',
        type: "GET",
        contentType: "application/json",
        dataType: 'json',
        crossDomain: true,        
        headers: {
            "Access-Control-Allow-Origin" : "*"
       },
        success: function(data) {
            taskList = data;
            updateTable()
        },
        error: function(error){
            console.log(error)
        }
      });
    setTimeout(pollService, 1000);
  }());