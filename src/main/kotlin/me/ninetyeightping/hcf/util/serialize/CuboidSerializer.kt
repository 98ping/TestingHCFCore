package me.ninetyeightping.hcf.util.serialize

import com.google.gson.*
import me.ninetyeightping.hcf.util.Cuboid
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

object CuboidSerializer : JsonSerializer<Cuboid>, JsonDeserializer<Cuboid> {

    @JvmStatic
    fun serialize(cuboid: Cuboid): JsonObject {
        val json = JsonObject()
        json.addProperty("worldName", cuboid.world.name)
        json.addProperty("lowerX", cuboid.lowerX)
        json.addProperty("lowerY", cuboid.lowerY)
        json.addProperty("lowerZ", cuboid.lowerZ)
        json.addProperty("upperX", cuboid.upperX)
        json.addProperty("upperY", cuboid.upperY)
        json.addProperty("upperZ", cuboid.upperZ)
        return json
    }

    @JvmStatic
    fun deserialize(json: JsonElement): Cuboid {
        val json = json.asJsonObject
        val world = Bukkit.getWorld(json["worldName"].asString) ?: throw IllegalStateException("World ${json["worldName"].asString} is not loaded")

        val lowerCorner = Location(
            world,
            json["lowerX"].asInt.toDouble(),
            json["lowerY"].asInt.toDouble(),
            json["lowerZ"].asInt.toDouble()
        )

        val upperCorner = Location(
            world,
            json["upperX"].asInt.toDouble(),
            json["upperY"].asInt.toDouble(),
            json["upperZ"].asInt.toDouble()
        )

        return Cuboid(lowerCorner, upperCorner)
    }

    override fun serialize(cuboid: Cuboid, type: Type, context: JsonSerializationContext): JsonElement {
        return serialize(cuboid)
    }

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Cuboid {
        return deserialize(json)
    }

}