/*eslint-env browser*/
/*eslint "no-console": "off"*/
/*global $*/
var gridColumnShips = $("#headers1");
var gridRowShips = $("#table-rows1");
var gridColumnSalvoes = $("#headers2");
var gridRowSalvoes = $("#table-rows2")
var gamePlayerIndicator = $("#gamePlayerIndicator");
var gameData;
var enemy;
var you;

$(function () {

    var gamePlayerId = getPlayerId();
    document.getElementById("gameview-log-out").addEventListener("click", manageLogOut);
    document.getElementById("back-to-games").addEventListener("click", function(){window.location.replace("public/games.html");});

    createShipOrSalvoesLocationsTable(gridColumnShips, gridRowShips, "data-shipLocation",);
    createShipOrSalvoesLocationsTable(gridColumnSalvoes, gridRowSalvoes, "data-salvoLocation");

    $.getJSON("http://localhost:8080/api/game_view/" + gamePlayerId, function (data){
    gameData = data;
    document.getElementById("place-ships").addEventListener("click", manageAddingShips);

    if(data.ships.length != 0){
        $("#place-ships").hide();
    }

    if(!data.gamePlayers){
        $("#tables").hide();
        alert("You have no access to this game!");
        window.location.replace("public/games.html");
        return;
    }

   if(data.gamePlayers.length > 1){
        if(gamePlayerId == data.gamePlayers[0].id){
                var gamePlayer1 = data.gamePlayers[0].player.username;
                var gamePlayer2 = data.gamePlayers[1].player.username;
                enemy = 1;
                you = 0;
            } else {
                var gamePlayer1 = data.gamePlayers[1].player.username;
                var gamePlayer2 = data.gamePlayers[0].player.username;
                enemy = 0;
                you = 1;
            }

            gamePlayerIndicator.text(gamePlayer1 + " (you) v. " + gamePlayer2);

            colorShipLocation(data, enemy);
            colorSalvoLocation(data, you);

    } else if(data.gamePlayers.length = 1) {
        gamePlayerIndicator.text(data.gamePlayers[0].player.username + " (you)");
        colorShipLocation(data, 0);

    } else {
        gamePlayerIndicator.text("No players");
    }

    }).fail(function(){
        alert("Wrong user ID!");
         window.location.replace("public/games.html");
    });

});

function getPlayerId() {
  var locationSearch = window.location.search.split("").splice(1).join("");;
  var paramAndValArray = locationSearch.split("=");
  var gamePlayerId = paramAndValArray[1];

  return gamePlayerId;
}

function createShipOrSalvoesLocationsTable(domElementColumn, domElementRow, attrName ) {
    var alphabetArray = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    for (var i = 0; i<10; i++){
        var newTh = $("<th scope='col'></th>");
        newTh.text(i+1);
        domElementColumn.append(newTh);
        var newRow = $("<tr></tr>");
        domElementRow.append(newRow);
        for (var j = 0; j<11; j++){
                    var newTd = $("<td></td>");

            if(j == 0){
                  newTd.text(alphabetArray[i]);
                  newRow.append(newTd);

            }else{
                    newTd.text("");
                    newRow.append(newTd);
                    var newTdId = "";
                    newTdId += (alphabetArray[i] + j);
                    newTd.attr(attrName, newTdId);
            }
        }
    }
}
function colorShipLocation(data, gamePlayerIndex){
     for (var n=0; n < data.ships.length; n++){

     var shipLocationsArray = data.ships[n].locations;

        for (var x = 0; x <shipLocationsArray.length; x++){
            var shipLocN = shipLocationsArray[x];
            $('[data-shipLocation = "' + shipLocN + '"]').addClass("ship");
        }
     }
     for (n=0; n < data.gamePlayers[gamePlayerIndex].salvoes.length; n++){
        var hitLocationsArray = data.gamePlayers[gamePlayerIndex].salvoes[n].salvoLocations;

        var hitTurn = data.gamePlayers[gamePlayerIndex].salvoes[n].turn;

        for (x = 0; x <hitLocationsArray.length; x++){
             var hitLocN = hitLocationsArray[x];

             $('[data-shipLocation = "' + hitLocN + '"]').addClass("hit").text(hitTurn);
         }
     }
}

function colorSalvoLocation(data, gamePlayerIndex){
    for (var n=0; n < data.gamePlayers[gamePlayerIndex].salvoes.length; n++){

         var salvoLocationsArray = data.gamePlayers[gamePlayerIndex].salvoes[n].salvoLocations;
         var salvoTurn = data.gamePlayers[gamePlayerIndex].salvoes[n].turn;

            for (var x = 0; x <salvoLocationsArray.length; x++){
                var salvoLocN = salvoLocationsArray[x];

                $('[data-salvoLocation = "' + salvoLocN + '"]').addClass("fired").text(salvoTurn);
            }
         }
}
function manageLogOut(){
        $.post("/api/logout")
                    .done(function() {
                        window.location.replace("public/games.html");
                        document.getElementById('username').value = "";
                        document.getElementById('password').value = "";
                        $("#userinfo").show();
                        $("#log-in").show();
                        $("#sign-up").show();
                        $("#loggedinuserinfo").hide();
                        $("#logout").hide();
                    })
                    .fail(function(){console.log("error")});
}
function manageAddingShips(){
    var gamePlayerId = getPlayerId();
    console.log(gamePlayerId);

//    var ships = [
//        {"type": "Aircraft Carrier",
//         "locations": [],
//         "length": 5
//        },
//        {"type": "Battleship",
//         "locations": [],
//         "length": 4
//        },
//        {"type": "Submarine",
//          "locations": [],
//          "length": 3
//        },
//        {"type": "Destroyer",
//          "locations": [],
//          "length": 3
//        },
//        {"type": "Patrol Boat",
//          "locations": [],
//          "length": 2
//        }
//    ];

    var jsonArray = [ { "type": "destroyer", "locations": ["A1", "B1", "C1"] },
                       { "type": "patrol boat", "locations": ["H5", "H6"] }];
//    if (gameData.ships.length == 0){
//        alert("No ships placed for this player.");
//    } else
//    if (gameData.ships.length == 0){
//        $(gameData.ships).each(function(i, ship) {
//            jsonArray.push(ship);
//        });
      $.post({
      url: "/api/games/players/" + gamePlayerId + "/ships",
      data: JSON.stringify(jsonArray),
      dataType: "text",
      contentType: "application/json"
    })
        .done(function(response){
            alert("Ships added!" + response);
            location.reload();
            $("#place-ships").hide();
        })
        .fail(function(){
            alert("Failed to add ships.");
        })
//    }
}