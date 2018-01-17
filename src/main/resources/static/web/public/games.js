/*eslint-env browser*/
/*eslint "no-console": "off"*/
/*global $*/
/*!
 * mustache.js - Logic-less {{mustache}} templates with JavaScript
 * http://github.com/janl/mustache.js
 */

/*global Mustache: true*/

var gamesArray = [];
var gameList = $("#gamesTable-rows");
var gamesObj = {
    "games": []
};
var gameplayersArray = [];
var playersObj = {
    "players":[

    ]
};
var leaderBoard = $("#leaderTable-rows");

$(function () {
    $.getJSON( "/api/games", function (data){
        gamesArray = data["games"];
        var games = gamesObj.games;
        fillGamesObj(gamesArray);
        fillGameList(games);
        getListOfGamePlayers(gamesArray);
        getPlayersObj(gameplayersArray);
        fillTemplate();

        document.getElementById("log-in").addEventListener("click", manageLogin);
        document.getElementById("log-out").addEventListener("click", manageLogOut);
        document.getElementById("sign-up").addEventListener("click", manageSignUp);

        if(data.player){
           $("#userinfo").hide();
           $("#log-in").hide();
           $("#sign-up").hide();
           $("#loggedinuserinfo").show();
           $("#logout").show();
           $(".btn").show();
           $("#create-game").show();
           getCurrentUserName();
           addGameLinkToButton(games, data.player.id);
           document.getElementById("create-game").addEventListener("click", manageCreateGame);
           manageJoinGame();
         }

    });
});

function manageJoinGame(){
    $(".join").click(function(){
        var gameId = $(this).attr("data-gameid");

        $.post("/api/game/" + gameId + "/players")
            .done(function(response){
                var gamePlayerId = response.gameplayerid;
                var url = "/web/game.html?gp=" + gamePlayerId;
                window.location.replace(url);
            })
            .fail(function(){
                alert("Error!");
            })
    })
}

function manageCreateGame(){
     $.post("/api/games")
            .done(function(response) {
                console.log(response.gameplayerid);
                var gameplayerid = response.gameplayerid;
                var url = "/web/game.html?gp=" + gameplayerid;
                window.location.replace(url);

            })
            .fail(function(){
                alert("Error!");
            });
}

function addGameLinkToButton(objArray, someId){

    for (var i in objArray){
        var gameId = objArray[i].id;
        var firstPlayerId = objArray[i].players[0].id;
        if(objArray[i].players.length > 1){
            var secondPlayerId = objArray[i].players[1].id;

        if (someId == firstPlayerId){

           var gamePlayerId = objArray[i].players[0].gameplayerid;
            $("#gamesTable-rows tr").find("[data-gameid='" + gameId + "']").append("<a href='/web/game.html?gp=" + gamePlayerId + "'>Play</a>");
        } else if (someId == secondPlayerId){

             var gamePlayerId = objArray[i].players[1].gameplayerid;
             $("#gamesTable-rows tr").find("[data-gameid='" + gameId + "']").append("<a href='/web/game.html?gp=" + gamePlayerId + "'>Play</a>");
        }
        } else{
            if(someId != firstPlayerId){
                $("#gamesTable-rows tr").find("[data-gameid='" + gameId + "']").append("<a>Join</a>").attr("class", "join");
            } else {
                var gamePlayerId = objArray[i].players[0].gameplayerid;
                $("#gamesTable-rows tr").find("[data-gameid='" + gameId + "']").append("<a href='/web/game.html?gp=" + gamePlayerId + "'>Play</a>");
            }
        }
    }
}

function fillGameList(objArray){
    for (var i in objArray) {
        var newTr = $("<tr></tr>");
        var newCrDateTd = $("<td></td>").text(objArray[i].created);
        newTr.append(newCrDateTd);
        var newPlayer1Td = $("<td></td>").text(objArray[i].players[0].name);
        newTr.append(newPlayer1Td);
        var newPlayer2Td = $("<td></td>");
        newTr.append(newPlayer2Td);
        if(objArray[i].players.length > 1){
            newPlayer2Td.text(objArray[i].players[1].name);
        }
        var newBtnTd = $("<td class='btnTd'></td>");
        var newBtn = $("<button type='button' class='btn' style='display: none'></button>");
        newBtn.attr("data-gameid", objArray[i].id);
        newBtnTd.append(newBtn);
        newTr.append(newBtnTd);
        gameList.append(newTr);
    }
}

function fillGamesObj(objArray){
    for (var i in objArray){
        var innerGame = {};
        var gamePlayersForGame = objArray[i].gamePlayers;
        innerGame.id = objArray[i].id;
        innerGame.created = objArray[i].created;
        innerGame.players = fillInnerGamePlayers(gamePlayersForGame);
        gamesObj.games.push(innerGame);
    }
}

function fillInnerGamePlayers(objArray){
        var innerGamePlayers = [];
        for (var i in objArray){
                var innerObj = {};
                innerObj.gameplayerid = objArray[i].id;
                innerObj.id = objArray[i].player.id;
                innerObj.name = objArray[i].player.username;
                innerGamePlayers.push(innerObj);
        }
    return innerGamePlayers;
}

function getListOfGamePlayers(objArray) {

    for (var i in objArray) {
        gameplayer1 = objArray[i].gamePlayers[0];
        gameplayersArray.push(gameplayer1);
        if (objArray[i].gamePlayers.length > 1){
            gameplayer2 = objArray[i].gamePlayers[1];
            gameplayersArray.push(gameplayer2);
        }
    } return gameplayersArray;
}

function getPlayersObj(objArray){
      var playerList = [];

      for (var i in objArray){
        if(playerList.includes(objArray[i].player.username) == false) {
            playerList.push(objArray[i].player.username);
        }
      }
      for (var i in playerList){
        var innerPlayer = {};
        innerPlayer.username = playerList[i];
        innerPlayer.totalScore = getTotalScore(gameplayersArray, playerList[i]);
        innerPlayer.totalWins = getTotalWins(gameplayersArray, playerList[i]);
        innerPlayer.totalLosts = getTotalLosts(gameplayersArray, playerList[i]);
        innerPlayer.totalTied = getTotalTied(gameplayersArray, playerList[i]);
        playersObj["players"].push(innerPlayer);
      }

}

function getTotalScore(objArray, playerName){
       var totalScore = 0;
       for (var i in objArray){
       if(playerName == objArray[i].player.username && objArray[i].score != null){
        totalScore += objArray[i].score.score;
       }
      }
        return totalScore;
}

function getTotalWins (objArray, playerName){
        var totalWins = 0;
        for (var i in objArray){
        if(playerName == objArray[i].player.username && objArray[i].score != null && objArray[i].score.score == 1){
             totalWins += 1;
        }
     }
        return totalWins;
}

function getTotalLosts (objArray, playerName){
        var totalLosts = 0;
        for (var i in objArray){
            if(playerName == objArray[i].player.username && objArray[i].score != null && objArray[i].score.score == 0){
                     totalLosts += 1;
                }
             }
                return totalLosts;
}

function getTotalTied (objArray, playerName){
        var totalTied = 0;
        for (var i in objArray){
            if(playerName == objArray[i].player.username && objArray[i].score != null && objArray[i].score.score == 0.5){
                     totalTied += 1;
                }
             }
                return totalTied;
}

function fillTemplate() {

    var template = $("#leaderBoard-template").html();
    var output = Mustache.render(template, playersObj);

    leaderBoard.html(output);
}

function manageLogin(){
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;

        if (username == null || username == "undefined" || username.length < 2){
            alert ("Please enter a valid Username.")
        }
        else if (password.length < 2) {
            alert("Please enter a valid password.");
            return;
        } else {
            $.post("/api/login", { username: username, password: password })
            .done(function() {
                $("#userinfo").hide();
                $("#log-in").hide();
                $("#sign-up").hide();
                $("#loggedinuserinfo").show();
                $("#logout").show();
                $(".btn").show();
                $("#create-game").show();
                getCurrentUserName();
                location.reload();

            })
            .fail(function(){console.log("error: wrong username or password.")});
        }
}

function getCurrentUserName(){
    $.get("/api/games", function(data){
        var currentUserName = data.player.name;
        console.log(currentUserName);
        $("#loggedInUserName").text(currentUserName);
    })
}

function manageLogOut(){
        $.post("/api/logout")
                    .done(function() {
                        document.getElementById('username').value = "";
                        document.getElementById('password').value = "";
                        $("#userinfo").show();
                        $("#log-in").show();
                        $("#sign-up").show();
                        $("#loggedinuserinfo").hide();
                        $("#logout").hide();
                        $(".btn").hide();
                        $("#create-game").hide();
                    })
                    .fail(function(){console.log("error")});
}

function manageSignUp(){
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    if (username == null || username == "undefined" || username.length < 2){
        alert ("Please enter a valid Username.")
    }
        else if (password.length < 2) {
        alert("Please enter a password of at least 2 characters.");
        return;
        }  else {
                 $.post("/api/players", { username: username, password: password })
                    .done(function(){
                        console.log("success")
                        manageLogin();
                    })
                    .fail(function(){
                        console.log("error");
                        alert("The Username already exists");
                    })
            }
}
//function checkIfLoggedIn(){
//    var isLoggedIn;
//    $.getJSON( "/api/games", function (data){
//        if (data.player){
//            isLoggedIn = true;
//        } else {
//            isLoggedIn = false;
//        }
//
//    } return isLoggedIn;
//}




