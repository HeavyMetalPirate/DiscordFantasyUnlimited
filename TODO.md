###########################################################
# Known Issues / TODOs
###########################################################

- Battle:
  - Status effects do not expire as planned
  - Not all types of status effects currently in effect (dots, hots, stat/skill de/buffs, ...)
    - new Status effect type "damage reduction/amplification", "healing reduction/amplification" as well as "received" reduction and amplification for either
    - new Status effect for armor reduction
  - Damage calculation still very basic, almost no calculation depending on stats and/or battle skills
  - current battle in global state does not get updated
  - AoE attacks still hit already defeated targets
  - if incapacitated, there's still a selected action for the affected participant
- Content:
  - Yeah... Travel, Quests, Trading, Dialogue, ... lots of stuff left to do
  - Also wiki style pages
  - And obviously, real game entities like races, classes, items and so on
- Navigation:
  - Left bar for navigating game content ("current location", "character details/stats/attribute allocation", "inventory", "equipment" ...)
  - Right bar: when no selected character, show a "select character" link that basically navigates to /game