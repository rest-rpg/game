package com.rest_rpg.game.adventure.model;

import com.rest_rpg.game.enemy.model.Enemy;
import com.rest_rpg.game.occupation.Occupation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(name = Adventure.ADVENTURE_BASIC,
        attributeNodes = {
                @NamedAttributeNode("enemy")
        }
)
@NamedEntityGraph(name = Adventure.ADVENTURE_DETAILS,
        attributeNodes = {
                @NamedAttributeNode(value = "enemy", subgraph = "enemyBasic")
        },
        subgraphs = {
                @NamedSubgraph(name = "enemyBasic", attributeNodes = {
                        @NamedAttributeNode("skill")
                })
        }
)
public class Adventure {

    public static final String ADVENTURE_BASIC = "ADVENTURE_BASIC_GRAPH";
    public static final String ADVENTURE_DETAILS = "ADVENTURE_DETAILS_GRAPH";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String name;

    private int adventureTimeInMinutes;

    private int xpForAdventure;

    private int goldForAdventure;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "enemy_id")
    private Enemy enemy;

    @OneToMany(mappedBy = "adventure", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Occupation> occupation = new HashSet<>();

    private boolean deleted;

    public static Adventure of(@NotNull @Valid AdventureCreateRequestDto dto, @NotNull Enemy enemy) {
        return Adventure.builder()
                .name(dto.getName())
                .adventureTimeInMinutes(dto.getAdventureLengthInMinutes())
                .xpForAdventure(dto.getXpForAdventure())
                .goldForAdventure(dto.getGoldForAdventure())
                .enemy(enemy)
                .build();
    }

    public void modify(@NotNull @Valid AdventureCreateRequestDto dto, @NotNull Enemy enemy) {
        setName(dto.getName());
        setAdventureTimeInMinutes(dto.getAdventureLengthInMinutes());
        setXpForAdventure(dto.getXpForAdventure());
        setGoldForAdventure(dto.getGoldForAdventure());
        setEnemy(enemy);
    }
}
