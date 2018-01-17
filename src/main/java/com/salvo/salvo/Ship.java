package com.salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="locations")
    private List<String> locations = new ArrayList<>();

    public Ship() {
    }

    public Ship(String type, GamePlayer gamePlayer, List<String> locations){
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    @JsonIgnore
    public GamePlayer getGamePlayer(){
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
