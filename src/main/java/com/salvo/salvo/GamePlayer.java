package com.salvo.salvo;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch= FetchType.EAGER)
    Set<Ship> ships;

    @OneToMany(mappedBy="gamePlayer", fetch= FetchType.EAGER)
    Set<Salvo> salvoes;

    public GamePlayer(){
    }

    public GamePlayer(LocalDateTime joinDate, Player player, Game game){
        this.joinDate = joinDate;
        this.player = player;
        this.game = game;

    }

    public long getId() {
        return id;
    }
    public LocalDateTime getJoinDate(){
        return joinDate;
    }
    public void setJoinDate(LocalDateTime joinDate){
        this.joinDate = joinDate;
    }
    public Player getPlayer(){
        return player;
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    public Game getGame(){
        return game;
    }
    public void setGame(Game game){
        this.game = game;
    }
    public Set<Ship> getShips() {
        return ships;
    }
    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public Set<Salvo> getSalvoes(){
        return salvoes;
    }
    public void setSalvoes(Set<Salvo> salvoes){
        this.salvoes = salvoes;
    }
    public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Score getScore(){
        return player.getScore(game);
    }

}