package me.ninetyeightping.hcf.util.serialize

import com.google.gson.*
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

object StatisticSerializer : JsonDeserializer<StatisticEntry>, JsonSerializer<StatisticEntry> {
    override fun serialize(src: StatisticEntry, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return toJson(src) as JsonElement
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): StatisticEntry? {
        return fromJson(json)
    }

    fun toJson(stat: StatisticEntry?): JsonObject? {
        if (stat == null) {
            return null
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("kills", stat.kills)
        jsonObject.addProperty("deaths", stat.deaths)
        jsonObject.addProperty("killstreak", stat.killstreak)
        jsonObject.addProperty("kothCaptures", stat.kothCaptures)

        return jsonObject
    }

    fun fromJson(jsonElement: JsonElement?): StatisticEntry? {
        if (jsonElement == null || !jsonElement.isJsonObject) {
            return null
        }

        val jsonObject = jsonElement.asJsonObject
        val kills = jsonObject.get("kills").asInt
        val deaths = jsonObject.get("deaths").asInt
        val killstreak = jsonObject.get("killstreak").asInt
        val kothCaptures = jsonObject.get("kothCaptures").asInt

        return StatisticEntry(kills, killstreak, deaths, kothCaptures)
    }
}