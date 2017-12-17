package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Type("gamePlayerStats")
public class GamePlayerStats {
    @Id
    private String id;
    private boolean ai;
    private Faction faction;
    private byte color;
    private byte team;
    private byte startSpot;
    private Float beforeMean;
    private Float beforeDeviation;
    private Float afterMean;
    private Float afterDeviation;
    private byte score;
    @Nullable
    private OffsetDateTime scoreTime;

    @Relationship("game")
    private Game game;

    @Relationship("player")
    private Player player;
}
