package me.ninetyeightping.hcf

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import io.github.thatkawaiisam.assemble.Assemble
import io.github.thatkawaiisam.assemble.AssembleStyle
import me.ninetyeightping.hcf.board.AssembleBoard
import me.ninetyeightping.hcf.events.sotw.SOTWHandler
import me.ninetyeightping.hcf.events.sotw.commands.SOTWCommands
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.players.commands.EconomyCommands
import me.ninetyeightping.hcf.players.listeners.HCFPlayerListener
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.claims.LandBoard
import me.ninetyeightping.hcf.team.claims.listener.ClaimListener
import me.ninetyeightping.hcf.team.claims.listener.LandBoardListener
import me.ninetyeightping.hcf.team.comands.GenericTeamCommands
import me.ninetyeightping.hcf.team.system.claims.listeners.SystemTeamClaimListener
import me.ninetyeightping.hcf.team.system.commands.SystemTeamCommands
import me.ninetyeightping.hcf.timers.TimerHandler
import me.ninetyeightping.hcf.timers.listeners.GenericTimerListener
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.serialize.CuboidSerializer
import me.ninetyeightping.hcf.util.serialize.LocationSerializer
import me.vaperion.blade.Blade
import me.vaperion.blade.bindings.impl.BukkitBindings
import me.vaperion.blade.container.impl.BukkitCommandContainer
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

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
        .registerTypeAdapter(Cuboid::class.java, CuboidSerializer)
        .registerTypeAdapter(Location::class.java, LocationSerializer)
        .create();

    lateinit var teamHandler: TeamHandler
    lateinit var hcfPlayerHandler: HCFPlayerHandler
    lateinit var timerHandler: TimerHandler
    lateinit var sotwHandler: SOTWHandler

    lateinit var landBoard: LandBoard


    override fun onEnable() {
        saveDefaultConfig()
        instance = this

        mongoClient = MongoClient(MongoClientURI(config.getString("uri")))
        mongoDatabase = mongoClient.getDatabase("HCFCoreTest")

        teamHandler = TeamHandler()
        hcfPlayerHandler = HCFPlayerHandler()
        timerHandler = TimerHandler()
        sotwHandler = SOTWHandler()

        landBoard = LandBoard()

        val assemble = Assemble(this, AssembleBoard())
        assemble.ticks = 2
        assemble.assembleStyle = AssembleStyle.VIPER

        server.pluginManager.registerEvents(HCFPlayerListener(), this)
        server.pluginManager.registerEvents(ClaimListener(), this)
        server.pluginManager.registerEvents(LandBoardListener(), this)
        server.pluginManager.registerEvents(GenericTimerListener(), this)
        server.pluginManager.registerEvents(SystemTeamClaimListener(), this)

        //commands
        Blade.of().fallbackPrefix("HCF")
            .containerCreator(BukkitCommandContainer.CREATOR)
            .binding(BukkitBindings()).build()
            .register(GenericTeamCommands()).register(EconomyCommands()).register(SOTWCommands()).register(SystemTeamCommands())


    }
}