package me.ninetyeightping.hcf.players

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import me.ninetyeightping.hcf.HCF
import org.bson.Document
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ForkJoinPool

class HCFPlayerHandler {

    val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("hcfplayers")

    var hcfplayers = arrayListOf<HCFPlayer>()

    init {
        for (document in mongoCollection.find()) hcfplayers.add(serialize(document))
    }


    fun byPlayer(player: Player) : HCFPlayer? {
        return hcfplayers.stream().filter { it.uuid == player.uniqueId.toString() }.findFirst().orElse(null)
    }

    fun byUUID(uuid: UUID) : HCFPlayer? {
        return hcfplayers.stream().filter { it.uuid == uuid.toString() }.findFirst().orElse(null)
    }

    fun pull() {
        ForkJoinPool.commonPool().execute {
            hcfplayers.clear()
            for (document in mongoCollection.find()) hcfplayers.add(serialize(document))
        }
    }

    fun createPlayer(hcfplayer: HCFPlayer) {
        save(hcfplayer)
        pull()
    }

    fun save(hcfplayer: HCFPlayer) {
        val doc = Document.parse(hcfplayer.construct())
        doc.remove("_id")

        val query = Document("_id", hcfplayer.uuid)
        val finaldoc = Document("\$set", doc)

        mongoCollection.updateOne(query, finaldoc, UpdateOptions().upsert(true))
        pull()
    }

    fun serialize(document: Document) : HCFPlayer {
        return HCF.instance.gson.fromJson(document.toJson(), HCFPlayer::class.java)
    }


}