package me.ninetyeightping.hcf.events.koth.types

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import me.ninetyeightping.hcf.HCF
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.ForkJoinPool

class KothHandler {

    val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("koths")

    var koths = arrayListOf<Koth>()

    init {
        for (document in mongoCollection.find()) koths.add(deserialize(document))
    }


    fun byName(name: String) : Koth? {
        return koths.stream().filter { it.name.equals(name, ignoreCase = true) }.findFirst().orElse(null)
    }

    fun getFirstActiveKoth() : Koth? {
        return koths.stream().filter { it.isActive }.findFirst().orElse(null)
    }

    fun serverHasActiveKoth() : Boolean {
        return getFirstActiveKoth() != null
    }

    fun exists(name: String) : Boolean {
        return koths.stream().filter { it.name.equals(name, ignoreCase = true) }.findFirst().orElse(null) != null
    }

    fun saveAndPull() {
        ForkJoinPool.commonPool().execute {
            koths.clear()
            for (document in mongoCollection.find()) koths.add(deserialize(document))
        }
    }

    fun playerIsStandingInKothRegion(koth: Koth) : Boolean {
        for (player in Bukkit.getOnlinePlayers()) {
            if (koth.constructCuboid().contains(player.location)) {
                return true
            }
        }
        return false
    }

    fun createKoth(koth: Koth) {
        save(koth)
        saveAndPull()
    }

    fun save(koth: Koth) {
        val doc = Document.parse(HCF.instance.gson.toJson(koth))
        doc.remove("_id")

        val query = Document("_id", koth.name.toLowerCase())
        val finaldoc = Document("\$set", doc)

        mongoCollection.updateOne(query, finaldoc, UpdateOptions().upsert(true))
        saveAndPull()
    }

    fun deserialize(document: Document) : Koth {
        return HCF.instance.gson.fromJson(document.toJson(), Koth::class.java)
    }
}