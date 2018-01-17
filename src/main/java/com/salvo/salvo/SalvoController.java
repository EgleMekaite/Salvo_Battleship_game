package com.salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private ShipRepository shipRepo;

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer (String username, String password) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Please enter your user name"), HttpStatus.FORBIDDEN);
        }
        List<Player> players = playerRepo.findByUserName(username);
        if (!players.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepo.save(new Player(username, password));
        return new ResponseEntity<>(makeMap("player", player.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)

    public ResponseEntity<Map<String, Object>> createNewGame (Authentication authentication){
        if(authentication == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else{
            Date creationDate = new Date();
            Game game = gameRepo.save(new Game(creationDate));
            Player player = playerRepo.findByUserName(currentUserName(authentication)).get(0);
            LocalDateTime joindate = null;
            GamePlayer gamePlayer = gamePlayerRepo.save(new GamePlayer(joindate.now(), player, game));
            return new ResponseEntity<>(makeMap("gameplayerid", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)

    public ResponseEntity<Map<String, Object>> joinGame (@PathVariable long gameId, Authentication authentication) {
        Game game = gameRepo.findOne(gameId);
        if(authentication == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (game == null) {
            return new ResponseEntity<>(makeMap("error", "There is no such game."), HttpStatus.FORBIDDEN);
        } else {
            Set<GamePlayer> gamePlayers = game.getGamePlayers();
            int size = gamePlayers.size();
            if (size > 1) {
                return new ResponseEntity<>(makeMap("error", "This game is full."), HttpStatus.FORBIDDEN);
            } else {
                Player player = playerRepo.findByUserName(currentUserName(authentication)).get(0);
                LocalDateTime joindate = null;
                GamePlayer gamePlayer = gamePlayerRepo.save(new GamePlayer(joindate.now(), player, game));
                return new ResponseEntity<>(makeMap("gameplayerid", gamePlayer.getId()), HttpStatus.CREATED);
            }
        }
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)

    public ResponseEntity<String> addShip(@PathVariable long gamePlayerId, Authentication authentication, @RequestBody List<Ship> ships){
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        Player player = playerRepo.findByUserName(currentUserName(authentication)).get(0);
        String gpName = gamePlayer.getPlayer().getUserName();
        String currentUserName = authentication.getName();
        if(authentication == null || gamePlayer == null || !currentUserName.equals(gpName)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if(!gamePlayer.getShips().isEmpty()){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            for (Ship ship : ships) {
                ship.setGamePlayer(gamePlayer);
                shipRepo.save(ship);
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @RequestMapping("/games")

    public Map<String, Object> getGamesInfo(Authentication authentication) {

        List<Game> games = gameRepo.findAll();
        Map<String, Object> dto = new LinkedHashMap<>();
        if(authentication != null) {
            Player player = playerRepo.findByUserName(currentUserName(authentication)).get(0);
            dto.put("player", makeCurrentUserDTO(player));
        }
        dto.put("games",games.stream()
                .map(gameX -> makeGameDTO(gameX))
                .collect(toList()));
        return dto;
    }

    private Map<String, Object> makeCurrentUserDTO (Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("name", player.getUserName());
        return dto;
    }
    private Map<String, Object> makeGameDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getID());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream()
            .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(toList())
        );
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> dtogp = new LinkedHashMap<String, Object>();
        dtogp.put("id", gamePlayer.getId());
        dtogp.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        dtogp.put("salvoes", gamePlayer.getSalvoes().stream()
                .map(thisSalvo -> makeSalvoesDTO(thisSalvo))
                .collect(toList())
        );
        dtogp.put("score", gamePlayer.getScore());
        return dtogp;
    }

    private Map<String, Object> makePlayerDTO(Player player){
        Map<String, Object> dtop = new LinkedHashMap<String, Object>();
        dtop.put("id", player.getId());
        dtop.put("username", player.getUserName());
        return dtop;
    }

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer (@PathVariable long gamePlayerId, Authentication authentication){
        String currentName = authentication.getName();

        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

        String gpName = gamePlayer.getPlayer().getUserName();

        if(!currentName.equals(gpName)){
            return new ResponseEntity<>(makeMap("error", "You have no access to this game."), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> gameMap;
        if(gamePlayer != null){
            Game thisGame = gamePlayer.getGame();
            gameMap = makeGameViewDTO(gamePlayer);

        } else {
            gameMap = new LinkedHashMap<>();
            gameMap.put("Error", "No player found!");
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(gameMap, HttpStatus.OK);
    }
    private Map<String, Object> makeGameViewDTO (GamePlayer gamePlayer){
        Game game = gamePlayer.getGame();

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getID());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers().stream()
                .map(thisGamePlayer -> makeGamePlayerDTO(thisGamePlayer))
                .collect(toList())
        );
        dto.put("ships", gamePlayer.getShips().stream()
                .map(thisShip -> makeShipDTO(thisShip))
                .collect(toList())
        );
//        dto.put("salvoes", gamePlayer.getSalvoes().stream()
//                .map(thisSalvo -> makeSalvoesDTO(thisSalvo))
//                .collect(toList())
//        );
        return dto;
    }
    private Map<String, Object> makeSalvoesDTO (Salvo salvo){
        Map<String, Object> dtosalvoes = new LinkedHashMap<>();
        dtosalvoes.put("turn", salvo.getTurn());
        dtosalvoes.put("salvoLocations", salvo.getSalvoLocations());

        return dtosalvoes;
    }


    private Map<String, Object> makeShipDTO(Ship ship){
        Map<String, Object> dtoships = new LinkedHashMap<String, Object>();
        dtoships.put("type", ship.getType());
        dtoships.put("locations", ship.getLocations());

        return dtoships;
    }
}