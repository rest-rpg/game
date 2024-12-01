package com.rest_rpg.game.character.model;

import com.rest_rpg.game.character.model.dto.CharacterCreateRequestDto;
import com.rest_rpg.game.character_skill.CharacterSkill;
import com.rest_rpg.game.equipment.Equipment;
import com.rest_rpg.game.item.ItemService;
import com.rest_rpg.game.item.model.Item;
import com.rest_rpg.game.occupation.Occupation;
import com.rest_rpg.game.skill.model.Skill;
import com.rest_rpg.game.statistics.Statistics;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.model.CharacterClass;
import org.openapitools.model.CharacterRace;
import org.openapitools.model.CharacterSex;
import org.openapitools.model.CharacterStatus;
import org.openapitools.model.ItemType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "character_table")
@NamedEntityGraph(name = Character.CHARACTER_BASIC,
        attributeNodes = {
                @NamedAttributeNode("statistics"),
                @NamedAttributeNode(value = "occupation", subgraph = "occupation-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "occupation-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("adventure"),
                                @NamedAttributeNode("work")
                        }
                )
        }
)
@NamedEntityGraph(name = Character.CHARACTER_TEST,
        attributeNodes = {
                @NamedAttributeNode("statistics"),
                @NamedAttributeNode(value = "skills", subgraph = "skills-subgraph"),
                @NamedAttributeNode(value = "equipment", subgraph = "equipment-subgraph"),
                @NamedAttributeNode(value = "occupation", subgraph = "occupation-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "occupation-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "adventure", subgraph = "adventure-subgraph"),
                                @NamedAttributeNode("work"),
                                @NamedAttributeNode("fight")
                        }
                ),
                @NamedSubgraph(
                        name = "adventure-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("enemy")
                        }
                ),
                @NamedSubgraph(
                        name = "equipment-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("armor"),
                                @NamedAttributeNode("weapon")
                        }
                ),
                @NamedSubgraph(
                        name = "skills-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("skill")
                        }
                )
        }
)
@NamedEntityGraph(name = Character.CHARACTER_FIGHT,
        attributeNodes = {
                @NamedAttributeNode("statistics"),
                @NamedAttributeNode(value = "occupation", subgraph = "occupation-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "occupation-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "fight", subgraph = "fight-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "fight-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("fightEffects"),
                                @NamedAttributeNode(value = "enemy", subgraph = "skill-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "skill-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("skill")
                        }
                )
        }
)
@NamedEntityGraph(name = Character.CHARACTER_FIGHT_ACTION,
        attributeNodes = {
                @NamedAttributeNode("statistics"),
                @NamedAttributeNode(value = "occupation", subgraph = "occupation-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "occupation-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "fight", subgraph = "fight-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "fight-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("fightEffects"),
                                @NamedAttributeNode(value = "enemy", subgraph = "enemy-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "enemy-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("skill"),
                                @NamedAttributeNode("strategyElements")
                        }
                )
        }
)
@NamedEntityGraph(name = Character.CHARACTER_FIGHT_LITE,
        attributeNodes = {
                @NamedAttributeNode("statistics"),
                @NamedAttributeNode(value = "occupation", subgraph = "occupation-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "occupation-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("fight")
                        }
                )
        }
)
@NamedEntityGraph(name = Character.CHARACTER_SKILLS,
        attributeNodes = {
                @NamedAttributeNode(value = "skills", subgraph = "skills-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "skills-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("skill")
                        }
                )
        }
)
public class Character {

    public static final String CHARACTER_BASIC = "CHARACTER_BASIC_GRAPH";
    public static final String CHARACTER_DETAILS = "CHARACTER_DETAILS_GRAPH";
    public static final String CHARACTER_FIGHT = "CHARACTER_FIGHT_GRAPH";
    public static final String CHARACTER_FIGHT_ACTION = "CHARACTER_FIGHT_ACTION_GRAPH";
    public static final String CHARACTER_FIGHT_LITE = "CHARACTER_FIGHT_LITE_GRAPH";
    public static final String CHARACTER_SKILLS = "CHARACTER_SKILLS_GRAPH";
    public static final String CHARACTER_TEST = "CHARACTER_TEST_GRAPH";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CharacterRace race;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CharacterSex sex;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CharacterStatus status = CharacterStatus.IDLE;

    @Nullable
    @Enumerated(EnumType.STRING)
    private CharacterArtwork artwork;

    private long userId;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CharacterSkill> skills = new HashSet<>();

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "statistics_id", referencedColumnName = "id")
    private Statistics statistics;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "occupation_id", referencedColumnName = "id")
    private Occupation occupation;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private Equipment equipment;

    private boolean deleted;

    public static Character createCharacter(@NotNull @Valid CharacterCreateRequestDto dto, long userId) {
        var character = Character.builder()
                .name(dto.getName())
                .characterClass(dto.getCharacterClass())
                .artwork(dto.getArtwork())
                .race(dto.getRace())
                .sex(dto.getSex())
                .skills(new HashSet<>())
                .equipment(Equipment.init())
                .occupation(Occupation.init())
                .statistics(Statistics.init())
                .status(CharacterStatus.IDLE)
                .userId(userId)
                .build();

        character.getOccupation().setCharacter(character);
        character.getStatistics().setCharacter(character);
        character.getEquipment().setCharacter(character);

        return character;
    }

    public void learnNewSkill(@NotNull Skill skill) {
        var currentSkill = skills.stream().filter(s -> s.getSkill().getId().equals(skill.getId())).findFirst();
        currentSkill.ifPresentOrElse(CharacterSkill::upgrade, () -> {
            var characterSkill = CharacterSkill.newSkill(skill, this);
            if (skills == null) {
                skills = new HashSet<>();
            }
            skills.add(characterSkill);
        });
        equipment.spendGold(skill.getSkillTraining().getGoldCost());
        statistics.useStatisticPoints(skill.getSkillTraining().getStatisticPointsCost());
    }

    public void buyItem(@NotNull Item item) {
        equipment.spendGold(item.getPrice());
        if (item.getType() == ItemType.ARMOR) {
            equipment.setArmor(item);
        } else {
            equipment.setWeapon(item);
        }
    }

    public void buyPotion() {
        equipment.spendGold(ItemService.POTION_PRICE);
        equipment.addPotion(1);
    }

    public void usePotion() {
        equipment.usePotion();
        statistics.heal(ItemService.POTION_HEAL_PERCENT);
    }
}
