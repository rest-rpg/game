package com.rest_rpg.game.equipment;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.item.model.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Min(0)
    private int gold;

    @NotNull
    @OneToOne(mappedBy = "equipment", fetch = FetchType.EAGER)
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item armor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item weapon;

    @Min(0)
    private int healthPotions;

    private boolean deleted;

    public static Equipment init() {
        return Equipment.builder().gold(0).build();
    }

    public void spendGold(int amount) {
        gold -= amount;
    }

    public void earnGold(int amount) {
        gold += amount;
    }

    public void addPotion(int amount) {
        healthPotions += amount;
    }

    public void usePotion() {
        healthPotions--;
    }
}
