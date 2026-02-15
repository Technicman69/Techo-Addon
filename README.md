Primary goal of this mod was to implement a better substitute for apugli's `custom_footstep` power, but I plan to expand with new powers, actions and conditions when I come up with some cool ideas.

I plan to make some of it a part of Apoli for Origins 1.20.1 later on :>

# Power Types

## Replace Sound Emission

Replaces a sound emissed by power holder with a different sound.

Type ID: `techo:replace_sound_emission`

### Fields
| Field           | Type          | Default    | Description                                                                                                                                                                                                                                                                                                                     |
|-----------------|---------------|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `sounds`        | Object        |            | An object with `"key": "value"` pairs that determine which sound (`"key"`) will be replaced with a new sound (`"value"`). The "value" can be either a **String**, a **Weighted Sound** or an **Array** of **Weighted Sounds** Supports regex matching (with referencing captured groups in replacement) and resourcepack sounds |
| `replace`       | Boolean       | `true`     | Whether the correctly matched sound should replace the old one, or play along with it. If `false`, both old and new sound will be played                                                                                                                                                                                        |
| `entity_action` | Entity Action | *optional* | If specified, this action will be executed on the player upon successfully replacing (or matching) sound                                                                                                                                                                                                                        |
| `priority`      | Integer       | `0`        | Determines the application priority of the power. If several powers match the sound only the highest `priority` with `replace` set to `true` will replace sound and powers with higher `priority` but with `replace` set to `false` will play their sound along                                                                 |
### Examples
```json
{
    "type": "techo:replace_sound_emission",
    "sounds": {
        "minecraft:entity.player.burp": "empty",
        "entity.generic.eat": "minecraft:empty"
    }
}
```
This example will replace `entity.player.burp` and `entity.generic.eat` sound events with `empty`, which will mute every eating sound coming from the power holder (`minecraft` namespace can be omitted).

```json
{
    "type": "techo:replace_sound_emission",
    "sounds": {
        "block.([a-z|0-9|-|_]*).step": "block.$1.break"
    },
    "condition": {
        "type": "apoli:on_block",
        "inverted": true,
        "block_condition": {
            "type": "apoli:and",
            "conditions": [
                {
                    "type": "apoli:in_tag",
                    "tag": "minecraft:wool"
                }
            ]
        }
    }
}
```
This example will replace any stepping sound coming from the power holder with a breaking sound used by the same block that was stepped upon (unless that block is a wool).

### History
| Version | Change                                                                                                       |
|---------|--------------------------------------------------------------------------------------------------------------|
| 1.0.0   | Added the power                                                                                              |
| 1.1.0   | Added new fields: `replace`, `entity_action`, `priority`. Values in `sounds` now accepts **Weighted Sounds** |


# Entity Actions Types

## Play Sound

Plays a sound either at the entity's position or coming from the entity.

Type ID: `techo:play_sound`

!!! Note

The value of the volume field is used to multiply the base distance of the sound event, which is 16 blocks (1.0).


### Fields
| Field           | Type       | Default                  | Description                                                                                                                                                                                                                                                                 |
|-----------------|------------|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `sound`         | Identifier |                          | The ID of the sound to play.                                                                                                                                                                                                                                                |
| `category`      | String     | *optional*               | If specified, this specifies the category and options the sound falls under. Otherwise, uses the category specified in the entity that invoked this action. Accepts "master", "music", "record", "weather", "block", "hostile", "neutral", "players", "ambient" or "voice". |
| `volume`        | Float      | `1.0`                    | The volume of the sound.                                                                                                                                                                                                                                                    |
| `pitch`         | Float      | `1.0`                    | The pitch of the sound.                                                                                                                                                                                                                                                     |
| `follow_entity` | Boolean    | `false`                  | Whether the sound should be heard coming from entity's position when it moves.                                                                                                                                                                                              |
| `global`        | Boolean    | value of `follow_entity` | Whether all players in the dimension should register this sound as playing even if outside of its range. If `false` player won't hear it even if they come in the range. <br/>**Note**: This does **not** make all the players hear the sound just as loud.                 |

### Examples

```
"entity_action": {
    "type": "techo:play_sound",
    "sound": "music_disc.pigstep",
    "follow_entity": true
}
```
This example will play the pigstep music disc that will be heard by all players in the dimension it was played if they are close enough to the entity that played this sound.

```
"entity_action": {
    "type": "techo:play_sound",
    "sound": "block.end_portal.spawn",
    "volume": 1e9001
}
```
This example will play the sound of creating end portal all around the dimension with all the players hearing it just as loud (`1e9001` yields special float value: `Infinity`).

### History
| Version | Change                                                                                                       |
|---------|--------------------------------------------------------------------------------------------------------------|
| 1.0.0   | Added the action                                                                                             |

# Data Types

## Weighted Sound

An **Object** representing a sound that can have volume and pitch. If in an **Array** it can also have weight that determines how likely it is to be chosen.

Used in **Replace Sound Emission** to provide a sound replacement.

### Fields

| Field    | Type             | Default | Description                                                                                                                                                         |
|----------|------------------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`     | Identifier       |         | The ID of the sound to play. Can contain backreferences (`$1`, `$2` etc.) of the regex groups matched                                                               |
| `volume` | Float            | `1.0`   | The volume of the sound.                                                                                                                                            |
| `pitch`  | Float            | `1.0`   | The pitch of the sound.                                                                                                                                             |
| `weight` | Positive Integer | `1`     | The weight of the sound. The chance of the sound to be picked is determined by dividing the weight to the sum of all weights (`weight / sumOfAllWeights = chance`). |

### Examples

```json
[
    {
        "id": "entity.villager.yes",
        "weight": 1
    },
    {
        "id": "entity.villager.no",
        "weight": 1
    }
]
```
This example will return either `entity.villager.yes` or `entity.villager.no` with the same probability for both.


### History
| Version | Change              |
|---------|---------------------|
| 1.1.0   | Added the data type |

# Credits
- Chris The Big! - for inspiring me to do this mod in the first place (he needed this functionality in one of his datapacks)
- Robin - for giving the idea for power's syntax
- Overgrown - for giving the idea for weighted sound list (TBA)
- `apoli:replace_loot_table` power from Apoli mod - for letting me borrow its code