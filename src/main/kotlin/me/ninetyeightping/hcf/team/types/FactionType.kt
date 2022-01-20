package me.ninetyeightping.hcf.team.types

import lombok.AllArgsConstructor

@AllArgsConstructor
enum class FactionType(val id: String, val displayName: String) {

    SYSTEM("systemfaction", "System Faction"),
    PLAYER("playerfaction", "Player Faction")



}