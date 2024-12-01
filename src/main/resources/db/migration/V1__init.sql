CREATE TABLE enemy (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  number_of_potions INT NOT NULL,
  hp INT NOT NULL,
  damage INT NOT NULL,
  mana INT NOT NULL,
  skill_level INT NOT NULL,
  skill_id BIGINT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_enemy PRIMARY KEY (id)
);

CREATE TABLE equipment (
  id BIGINT AUTO_INCREMENT NOT NULL,
  gold INT NOT NULL,
  armor_id BIGINT NULL,
  weapon_id BIGINT NULL,
  health_potions INT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_equipment PRIMARY KEY (id)
);

CREATE TABLE skill (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NULL,
  type VARCHAR(255) NOT NULL,
  multiplier FLOAT NOT NULL,
  effect VARCHAR(255) NULL,
  effect_duration INT NOT NULL,
  effect_multiplier FLOAT NOT NULL,
  character_class VARCHAR(255) NULL,
  multiplier_per_level FLOAT NOT NULL,
  effect_duration_per_level INT NOT NULL,
  effect_multiplier_per_level FLOAT NOT NULL,
  mana_cost INT NOT NULL,
  magic_damage BIT(1) NOT NULL,
  gold_cost INT NOT NULL,
  statistic_points_cost INT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_skill PRIMARY KEY (id)
);

CREATE TABLE statistics (
  id BIGINT AUTO_INCREMENT NOT NULL,
  max_hp INT NOT NULL,
  current_hp INT NOT NULL,
  max_mana INT NOT NULL,
  current_mana INT NOT NULL,
  current_xp INT NOT NULL,
  current_level INT NOT NULL,
  strength INT NOT NULL,
  dexterity INT NOT NULL,
  constitution INT NOT NULL,
  intelligence INT NOT NULL,
  free_statistic_points INT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_statistics PRIMARY KEY (id)
);

CREATE TABLE work (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  wage INT NOT NULL,
  work_minutes INT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_work PRIMARY KEY (id)
);

CREATE TABLE fight (
  id BIGINT AUTO_INCREMENT NOT NULL,
  enemy_id BIGINT NULL,
  enemy_current_hp INT NOT NULL,
  enemy_current_mana INT NOT NULL,
  is_active BIT(1) NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_fight PRIMARY KEY (id)
);

CREATE TABLE adventure (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  adventure_time_in_minutes INT NOT NULL,
  enemy_id BIGINT NULL,
  xp_for_adventure INT NOT NULL,
  gold_for_adventure INT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_adventure PRIMARY KEY (id)
);

CREATE TABLE occupation (
  id BIGINT AUTO_INCREMENT NOT NULL,
  finish_time DATETIME NULL,
  adventure_id BIGINT NULL,
  work_id BIGINT NULL,
  fight_id BIGINT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_occupation PRIMARY KEY (id)
);

CREATE TABLE item (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(255) NOT NULL,
  price INT NOT NULL,
  POWER INT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE character_table (
  id BIGINT AUTO_INCREMENT NOT NULL,
  name VARCHAR(255) NOT NULL,
  race VARCHAR(255) NOT NULL,
  character_class VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL,
  artwork VARCHAR(255) NOT NULL,
  sex VARCHAR(255) NOT NULL,
  user_id BIGINT NOT NULL,
  statistics_id BIGINT NOT NULL,
  occupation_id BIGINT NOT NULL,
  equipment_id BIGINT NOT NULL,
  deleted BIT(1) NOT NULL,
  CONSTRAINT pk_character PRIMARY KEY (id)
);

CREATE TABLE character_skill (
  level INT NOT NULL,
  deleted BIT(1) NOT NULL,
  character_id BIGINT NOT NULL,
  skill_id BIGINT NOT NULL,
  CONSTRAINT pk_characterskill PRIMARY KEY (character_id, skill_id)
);

CREATE TABLE strategy_element (
  id BIGINT AUTO_INCREMENT NOT NULL,
  element_event VARCHAR(255) NOT NULL,
  element_action VARCHAR(255) NOT NULL,
  priority INT NOT NULL,
  CONSTRAINT pk_strategyelement PRIMARY KEY (id)
);

CREATE TABLE strategy_element_enemy (
  enemy_id BIGINT NOT NULL,
  strategy_element_id BIGINT NOT NULL,
  CONSTRAINT pk_strategy_element PRIMARY KEY (enemy_id, strategy_element_id)
);

CREATE TABLE fight_effect (
  id BIGINT AUTO_INCREMENT NOT NULL,
  fight_id BIGINT NOT NULL,
  skill_effect VARCHAR(255) NOT NULL,
  duration INT NOT NULL,
  is_player_effect BIT(1) NOT NULL,
  effect_multiplier FLOAT NOT NULL,
  CONSTRAINT pk_fighteffect PRIMARY KEY (id)
);

ALTER TABLE fight
ADD CONSTRAINT FK_FIGHT_ON_ENEMY FOREIGN KEY (enemy_id) REFERENCES enemy (id);

ALTER TABLE adventure
ADD CONSTRAINT FK_ADVENTURE_ON_ENEMY FOREIGN KEY (enemy_id) REFERENCES enemy (id);

ALTER TABLE occupation
ADD CONSTRAINT FK_OCCUPATION_ON_ADVENTURE FOREIGN KEY (adventure_id) REFERENCES adventure (id);

ALTER TABLE occupation
ADD CONSTRAINT FK_OCCUPATION_ON_WORK FOREIGN KEY (work_id) REFERENCES work (id);

ALTER TABLE occupation
ADD CONSTRAINT FK_OCCUPATION_ON_FIGHT FOREIGN KEY (fight_id) REFERENCES fight (id);

ALTER TABLE character_table
ADD CONSTRAINT uc_character_name UNIQUE (name);

ALTER TABLE character_table
ADD CONSTRAINT FK_CHARACTER_ON_EQUIPMENT FOREIGN KEY (equipment_id) REFERENCES equipment (id);

ALTER TABLE character_table
ADD CONSTRAINT FK_CHARACTER_ON_OCCUPATION FOREIGN KEY (occupation_id) REFERENCES occupation (id);

ALTER TABLE character_table
ADD CONSTRAINT FK_CHARACTER_ON_STATISTICS FOREIGN KEY (statistics_id) REFERENCES statistics (id);

ALTER TABLE character_skill
ADD CONSTRAINT FK_CHARACTERSKILL_ON_CHARACTER FOREIGN KEY (character_id) REFERENCES character_table (id);

ALTER TABLE character_skill
ADD CONSTRAINT FK_CHARACTERSKILL_ON_SKILL FOREIGN KEY (skill_id) REFERENCES skill (id);

ALTER TABLE strategy_element_enemy
ADD CONSTRAINT fk_streleenestr_on_enemy FOREIGN KEY (enemy_id) REFERENCES enemy (id);

ALTER TABLE strategy_element_enemy
ADD CONSTRAINT fk_streleenestr_on_strategy_element FOREIGN KEY (strategy_element_id) REFERENCES strategy_element (id);

ALTER TABLE enemy
ADD CONSTRAINT FK_ENEMY_ON_SKILL FOREIGN KEY (skill_id) REFERENCES skill (id);

ALTER TABLE fight_effect
ADD CONSTRAINT FK_FIGHTEFFECT_ON_FIGHT FOREIGN KEY (fight_id) REFERENCES fight (id);

ALTER TABLE equipment
ADD CONSTRAINT FK_EQUIPMENT_ON_ARMOR FOREIGN KEY (armor_id) REFERENCES item (id);

ALTER TABLE equipment
ADD CONSTRAINT FK_EQUIPMENT_ON_WEAPON FOREIGN KEY (weapon_id) REFERENCES item (id);