package me.ninetyeightping.hcf.team

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import me.ninetyeightping.hcf.HCF
import org.bson.Document
import org.bukkit.entity.Player
import java.util.concurrent.ForkJoinPool
import javax.print.Doc

class TeamHandler {

    val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("teams")

    var teams = arrayListOf<Team>()

    init {
        for (document in mongoCollection.find()) teams.add(deserialize(document))
    }

    fun byPlayer(player: Player) : Team? {
        return teams.stream().filter { it.members.contains(player.uniqueId.toString()) }.findFirst().orElse(null)
    }


    fun byName(name: String) : Team? {
        return teams.stream().filter { it.displayName.equals(name, ignoreCase = true) }.findFirst().orElse(null)
    }

    fun exists(name: String) : Boolean {
        return teams.stream().filter { it.displayName.equals(name, ignoreCase = true) }.findFirst().orElse(null) != null
    }

    fun saveAndPull() {
        ForkJoinPool.commonPool().execute {
            teams.stream().filter { it.needsSave }.forEach {
                it.save()
            }
            teams.clear()
            for (document in mongoCollection.find()) teams.add(deserialize(document))
        }
    }

    fun createTeam(team: Team) {
        save(team)
        saveAndPull()
    }

    fun save(team: Team) {
        val doc = Document.parse(team.construct())
        doc.remove("_id")

        val query = Document("_id", team.id)
        val finaldoc = Document("\$set", doc)

        mongoCollection.updateOne(query, finaldoc, UpdateOptions().upsert(true))
        saveAndPull()
    }

    fun deserialize(document: Document) : Team {
        return HCF.instance.gson.fromJson(document.toJson(), Team::class.java)
    }


}