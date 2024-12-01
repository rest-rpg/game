package com.rest_rpg.game.character.model;

public enum CharacterArtwork {

    HUMAN_MALE_1("human_male_1.jpg"),
    HUMAN_MALE_2("human_male_2.jpg"),
    HUMAN_MALE_3("human_male_3.jpg"),
    HUMAN_FEMALE_1("human_female_1.jpg"),
    HUMAN_FEMALE_2("human_female_2.jpg"),
    ELF_MALE_1("elf_male_1.jpg"),
    ELF_MALE_2("elf_male_2.jpg"),
    ELF_MALE_3("elf_male_3.jpg"),
    ELF_FEMALE_1("elf_female_1.jpg"),
    ELF_FEMALE_2("elf_female_2.jpg"),
    DWARF_MALE_1("dwarf_male_1.jpg"),
    DWARF_MALE_2("dwarf_male_2.jpg"),
    DWARF_MALE_3("dwarf_male_3.jpg"),
    DWARF_FEMALE_1("dwarf_female_1.jpg"),
    DWARF_FEMALE_2("dwarf_female_2.jpg"),
    DWARF_FEMALE_3("dwarf_female_3.jpg");

    private final String artworkName;

    CharacterArtwork(String artworkName) {
        this.artworkName = artworkName;
    }

    public String getArtworkName() {
        return artworkName;
    }
}
