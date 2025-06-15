package me.ninetyeightping.hcf.players

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.toHCFPlayer
import org.bson.Document
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class HCFPlayerHandler
{

    private val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("hcfplayers")
    private var hcfplayers = mutableMapOf<UUID, HCFPlayer>()

    fun byPlayer(player: Player): HCFPlayer?
    {
        return byUUID(player.uniqueId)
    }

    fun byPlayerName(name: String): HCFPlayer?
    {
        return hcfplayers.values.firstOrNull { it.name.equals(name, true) }
    }

    fun byUUID(uuid: UUID): HCFPlayer?
    {
        return hcfplayers[uuid] ?: fromMongo(uuid)
    }

    private fun fromMongo(uuid: UUID): HCFPlayer?
    {
        return mongoCollection.find(Filters.eq("uuid", uuid)).firstOrNull()?.toHCFPlayer()
    }

    fun createPlayer(hcfplayer: HCFPlayer)
    {
        save(hcfplayer)
    }

    fun cache(player: HCFPlayer)
    {
        hcfplayers[player.uuid] = player
    }

    fun save(hcfplayer: HCFPlayer)
    {
        CompletableFuture.runAsync {
            val doc = Document.parse(hcfplayer.construct())
            doc.remove("_id")

            val query = Document("_id", hcfplayer.uuid)
            val finaldoc = Document("\$set", doc)

            mongoCollection.updateOne(query, finaldoc, UpdateOptions().upsert(true))
        }.thenAccept {
            hcfplayers[hcfplayer.uuid] = hcfplayer
        }
    }
}