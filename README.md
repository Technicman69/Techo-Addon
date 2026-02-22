Primary goal of this mod was to implement a better substitute for apugli's `custom_footstep` power, but I plan to expand with new powers, actions and conditions when I come up with some cool ideas.

I plan to make some of it a part of Apoli for Origins 1.20.1 later on :>

**Features**
1. Power Types
  - Replace Sound Emission (`techo:replace_sound_emission`)
2. Entity Action Types
  - Play Sound (`techo:play_sound`) - adds new fields to original `apoli:play_sound` entity action type.
3. A way to pass Resource power values as uniforms to post-processing shaders (via **Resource Value Provider** data type)

---
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

```json
{
    "type": "techo:replace_sound_emission",
    "sounds": {
        "(.*)": {
            "id": "$1",
            "pitch": 0.5
        }
    }
}
```
This example will make all the sounds emitted by power holder have pitch set to 0.5 making them sound slow and low.

```json
{
    "type": "techo:replace_sound_emission",
    "sounds": {
        "entity.player.(hurt|death)": [
            {
                "id": "minecraft:entity.axolotl.$1",
                "volume": 2,
                "pitch": 0.5,
                "weight": 1
            },
            {
                "id": "minecraft:entity.wither.$1",
                "volume": 2,
                "pitch": 0.5,
                "weight": 1
            }
        ]
    },
    "entity_action": {
        "type": "apoli:execute_command",
        "command": "say bleh XP"
    },
    "priority": 1
}
```
This example will replace power holder's death and hurt sounds with the corresponding sound from axolotl or wither (50% chance for either). The result sounds will be slowed 50% (`"pitch": 0.5`) and will be heard in distance of `32` blocks (`"volume": 2`, so `2 * 16 = 32 blocks`)

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
| `internal`      | Boolean    | `false`                  | Whether only the entity that plays this sound should hear it.                                                                                                                                                                                                               |


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

```
"entity_action": {
    "type": "techo:play_sound",
    "sound": "ambient.cave",
    "internal": true
}
```
This example plays cave ambient only for this entity.
### History
| Version | Change                      |
|---------|-----------------------------|
| 1.0.0   | Added the action            |
| 1.2.0   | Added new field: `internal` |

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

## Resource Value Provider

Provides a scaled value from a **Resource** power (in order to convert an integer resource value to a fraction float).

Can be passed instead of any float value of a uniform vector/matrix in a post shader

### Fields

| Field      | Type       | Default | Description                                                                                                                     |
|------------|------------|---------|---------------------------------------------------------------------------------------------------------------------------------|
| `resource` | Identifier |         | The ID of the resource power from which the uniform value will be set.                                                          |
| `scale`    | Float      | `0.01`  | Can be defined instead of `min` and `max` pair. The value by which the one provided by resource will be multiplied.             |
| `min`      | Float      |         | The value to which the `min` boundary of the resource should be mapped. If defined with `max`, will be used instead of `scale`. |
| `max`      | Float      |         | The value to which the `max` boundary of the resource should be mapped. If defined with `min`, will be used instead of `scale`. |

### Examples
`assets/example/shaders/post/chromatic_aberration.json`

```json
{
  "targets": [
    "swap"
  ],
  "passes": [
    {
      "name": "deconverge",
      "intarget": "minecraft:main",
      "outtarget": "swap",
      "uniforms": [
        {
          "name": "ConvergeX",
          "values": [ 0.0,  0.0,  0.0 ]
        },
        {
          "name": "ConvergeY",
          "values": [  0.0, 0.0,  0.0 ]
        },
        {
          "name": "RadialConvergeX",
          "values": [  {"resource": "example:intensity", "min": 0.9, "max": 1.0},  {"resource": "example:intensity", "min": 1.02, "max": 1.0},  {"resource": "example:intensity", "min": 1.02, "max": 1.0} ]
        },
        {
          "name": "RadialConvergeY",
          "values": [  {"resource": "example:intensity", "min": 0.9, "max": 1.0},  {"resource": "example:intensity", "min": 1.02, "max": 1.0},  {"resource": "example:intensity", "min": 1.02, "max": 1.0} ]
        }
      ]
    },
    {
      "name": "blit",
      "intarget": "swap",
      "outtarget": "minecraft:main"
    }
  ]
}

```
This example will let the `example:intensity` resource power control the intensity of a Chromatic Aberration shader

### History
| Version | Change              |
|---------|---------------------|
| 1.2.0   | Added the data type |

---

# Credits
- Chris The Big! - for inspiring me to do this mod in the first place (he needed this functionality in one of his datapacks)
- Robin - for giving the idea for power's syntax
- Overgrown - for giving the idea for weighted sound list (TBA)
- `apoli:replace_loot_table` power from Apoli mod - for letting me borrow its code