var taskList;

// Helps finding objects with unique fields
function findUnique(arr, predicate) {
    var found = {};
    arr.forEach(d => {
      found[predicate(d)] = d;
    })
    return Object.keys(found).map(key => found[key])
}

// Trasposes rows with columns in a table
function trasposeTable() {
    $(function() {
        var t = $('tbody').eq(0);
        var r = t.find('tr');
        var cols= r.length;
        var rows= r.eq(0).find('td').length;
        var cell, next, tem, i = 0;
        var tb= $('<tbody id="tasks"></tbody>');
    
        $('tbody').remove();
        while(i<rows){
            cell= 0;
            tem= $('<tr></tr>');
            while(cell<cols){
                next= r.eq(cell++).find('td').eq(0);
                tem.append(next);
            }
            tb.append(tem);
            ++i;
        }
        $('#tasktable').append(tb);
        $('#tasktable').show();
    })
}

// Updates the Task Table
function updateTable() {

    // Finding unique operators
    var uniqueOperators = findUnique(taskList, t => t.operatorId)

    // Sorting operators from the busiest to the less busy for avoiding a visual bug.
    for (var i in uniqueOperators) {
        var operatorId = uniqueOperators[i].operatorId;
        uniqueOperators[i].operatorTaskList = taskList.filter(t => t.operatorId == operatorId)
    }
    uniqueOperators.sort(function (a, b ) {
        return b.operatorTaskList.length - a.operatorTaskList.length
    });
    

    var tableHeaderString = "";
    // Printing operator names
    for (var i in uniqueOperators) {
        var operatorId = uniqueOperators[i].operatorId;
        tableHeaderString = tableHeaderString + "<th id=\""+uniqueOperators[i].operatorId+"\">" + uniqueOperators[i].operatorName + " " + uniqueOperators[i].operatorSurname + "</th>";
    }
    document.getElementById("names").innerHTML = tableHeaderString;
    document.getElementById("tasks").innerHTML = "";

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
        url: 'http://localhost:+'sessionExchange'+/api/all',
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
        }
      });
    setTimeout(pollService, 1000);
  }());