package com.salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    private Date finishDate;
    private double score;

    public Score() {
    }

    public Score(Date finishDate, double score, Game game, Player player){
        this.finishDate = finishDate;
        this.score = score;
        this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

