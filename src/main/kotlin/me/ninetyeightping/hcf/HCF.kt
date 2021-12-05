package me.ninetyeightping.hcf

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.players.listeners.HCFPlayerListener
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.comands.GenericTeamCommands
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.bukkit.BukkitCommandHandler
import java.util.concurrent.ForkJoinPool

class HCF : JavaPlugin() {

    companion object {
        lateinit var instance: HCF
    }

    lateinit var mongoDatabase: MongoDatabase;
    lateinit var mongoClient: MongoClient

    var gson: Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create();

    lateinit var teamHandler: TeamHandler
    lateinit var hcfPlayerHandler: HCFPlayerHandler


    override fun onEnable() {
        saveDefaultConfig()
        instance = this

        mongoClient = MongoClient(MongoClientURI(config.getString("uri")))
        mongoDatabase = mongoClient.getDatabase("HCFCoreTest")

        teamHandler = TeamHandler()
        hcfPlayerHandler = HCFPlayerHandler()


        server.pluginManager.registerEvents(HCFPlayerListener(), this)

        //commands
        val commandHandler = BukkitCommandHandler.create(this)

        commandHandler.register(GenericTeamCommands())


    }
}