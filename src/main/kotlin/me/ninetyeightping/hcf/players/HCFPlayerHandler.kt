package me.ninetyeightping.hcf.players

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import org.bson.Document
import org.bukkit.entity.Player
import java.util.concurrent.ForkJoinPool

class HCFPlayerHandler {

    val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("hcfplayers")

    var hcfplayers = arrayListOf<HCFPlayer>()

    init {
        for (document in mongoCollection.find()) hcfplayers.add(serialize(document))
    }

    fun byPlayer(player: Player) : HCFPlayer {
        return hcfplayers.stream().filter { it.uuid.equals(player.uniqueId.toString()) }.findFirst().orElse(null)
    }

    fun pull() {
        ForkJoinPool.commonPool().execute {
            hcfplayers.clear()
            for (document in mongoCollection.find()) hcfplayers.add(serialize(document))
        }
    }

    fun createPlayer(hcfplayer: HCFPlayer) {
        mongoCollection.insertOne(deserialize(hcfplayer))
        pull()
    }

    fun save(hcfplayer: HCFPlayer) {
        mongoCollection.replaceOne(Filters.eq("uuid", hcfplayer.uuid), deserialize(hcfplayer))
        pull()
    }

    fun serialize(document: Document) : HCFPlayer {
        return HCF.instance.gson.fromJson(document.toJson(), HCFPlayer::class.java)
    }

    fun deserialize(hcfplayer: HCFPlayer) : Document {
        return Document.parse(hcfplayer.construct())
    }

}