package me.ninetyeightping.hcf.team

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import me.ninetyeightping.hcf.HCF
import org.bson.Document
import org.bukkit.entity.Player
import java.util.concurrent.ForkJoinPool

class TeamHandler {

    val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("teams")

    var teams = arrayListOf<Team>()

    init {
        for (document in mongoCollection.find()) teams.add(deserialize(document))
    }

    fun byPlayer(player: Player) : Team {
        return teams.stream().filter { it.members.contains(player.uniqueId.toString()) }.findFirst().orElse(null)
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
        mongoCollection.insertOne(serialize(team))
        saveAndPull()
    }

    fun save(team: Team) {
        mongoCollection.replaceOne(Filters.eq("id", team.id), serialize(team))
        saveAndPull()
    }

    fun deserialize(document: Document) : Team {
        return HCF.instance.gson.fromJson(document.toJson(), Team::class.java)
    }

    fun serialize(team: Team) : Document {
        return Document.parse(team.construct())
    }

}