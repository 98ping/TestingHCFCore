package me.ninetyeightping.hcf.team

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.claims.LandBoard
import org.bson.Document
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ForkJoinPool
import kotlin.collections.ArrayList


class TeamHandler {

    val mongoCollection: MongoCollection<Document> = HCF.instance.mongoDatabase.getCollection("teams")

    var teams = arrayListOf<Team>()

    init {
        teams = mongoCollection.find().into(ArrayList()).map { deserialize(it) }.toCollection(ArrayList())
    }

    fun byPlayer(player: Player): Team? {
        return teams.stream().filter { it.members.contains(player.uniqueId) }.findFirst().orElse(null)
    }

    fun byUUID(uuid: UUID): Team? {
        return teams.stream().filter { it.members.contains(uuid) }.findFirst().orElse(null)
    }

    fun byName(name: String): Team? {
        return teams.stream().filter { it.displayName.equals(name, ignoreCase = true) }.findFirst().orElse(null)
    }

    fun exists(name: String): Boolean {
        return teams.stream().filter { it.displayName.equals(name, ignoreCase = true) }.findFirst().orElse(null) != null
    }

    fun pull() {
        teams = mongoCollection.find().into(ArrayList()).map { deserialize(it) }.toCollection(ArrayList())
    }

    fun disbandTeam(team: Team) {
        mongoCollection.deleteOne(Filters.eq("_id", team.id))

        team.claims.forEach {
            HCF.instance.landBoard.claims.remove(it)
        }

        println("Removed team claims for " + team.displayName)

        pull()
    }


    fun createSystemTeam(team: Team) {
        save(team)
    }

    fun addDTRAndMemberToTeam(player: Player, team: Team) {

        team.members.add(player.uniqueId)
        team.dtr = team.calculateMaximumDTR()

        save(team)
    }

    fun createTeam(team: Team) {

        save(team)
    }

    fun save(team: Team) {
        val doc = Document.parse(team.construct())
        doc.remove("_id")

        val query = Document("_id", team.id)
        val finaldoc = Document("\$set", doc)

        mongoCollection.updateOne(query, finaldoc, UpdateOptions().upsert(true))
    }

    fun deserialize(document: Document): Team {
        return HCF.instance.gson.fromJson(document.toJson(), Team::class.java)
    }


}