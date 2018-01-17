package com.salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="locations")
    private List<String> salvoLocations = new ArrayList<>();

    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayer, List<String> salvoLocations, int turn){
        this.gamePlayer = gamePlayer;
        this.salvoLocations = salvoLocations;
        this.turn = turn;
    }

    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    @JsonIgnore
    public GamePlayer getGamePlayer(){
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }
}
