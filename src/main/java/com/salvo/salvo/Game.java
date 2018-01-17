package com.salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy="game", fetch= FetchType.EAGER)
    Set<GamePlayer> gamePlayers;
    @JsonIgnore
    @OneToMany(mappedBy="game", fetch= FetchType.EAGER)
    Set<Score> scores;

    private Date creationDate;

    public Game(){
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Game(Date creationDate){
        this.creationDate = creationDate;
    }

    public long getID(){
        return id;
    }

    public String getCreationDate(){
        return creationDate.toString();
    }

    public void setCreationDate(Date creationDate){
        this.creationDate = creationDate;
    }
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }
    public void addScore(Score score){
        score.setGame(this);
        scores.add(score);
    }
    public Set<Score> getScores(){
        return scores;
    }
}
