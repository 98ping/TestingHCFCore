package me.ninetyeightping.hcf

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import io.github.thatkawaiisam.assemble.Assemble
import io.github.thatkawaiisam.assemble.AssembleStyle
import javafx.scene.shape.Arc
import me.ninetyeightping.hcf.board.AssembleBoard
import me.ninetyeightping.hcf.elevator.listeners.ElevatorCreateListener
import me.ninetyeightping.hcf.events.koth.commands.KothGenericCommands
import me.ninetyeightping.hcf.events.koth.types.KothHandler
import me.ninetyeightping.hcf.events.sotw.SOTWHandler
import me.ninetyeightping.hcf.events.sotw.commands.SOTWCommands
import me.ninetyeightping.hcf.events.sotw.listeners.SOTWDamageListener
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.commands.EconomyCommands
import me.ninetyeightping.hcf.players.listeners.HCFPlayerListener
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import me.ninetyeightping.hcf.pvpclass.types.Archer
import me.ninetyeightping.hcf.pvpclass.types.Bard
import me.ninetyeightping.hcf.pvpclass.types.tasks.ArcherCheckTask
import me.ninetyeightping.hcf.pvpclass.types.tasks.BardCheckTask
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.claims.LandBoard
import me.ninetyeightping.hcf.team.claims.listener.ClaimListener
import me.ninetyeightping.hcf.team.claims.listener.LandBoardListener
import me.ninetyeightping.hcf.team.comands.GenericTeamCommands
import me.ninetyeightping.hcf.team.comands.adapter.HCFPlayerAdapter
import me.ninetyeightping.hcf.team.dtr.DTRUpdateTask
import me.ninetyeightping.hcf.team.system.claims.listeners.SystemTeamClaimListener
import me.ninetyeightping.hcf.team.system.commands.SystemTeamCommands
import me.ninetyeightping.hcf.team.system.commands.adapters.FlagTypeAdapter
import me.ninetyeightping.hcf.team.system.flags.Flag
import me.ninetyeightping.hcf.timers.listeners.GenericTimerListener
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.serialize.CuboidSerializer
import me.ninetyeightping.hcf.util.serialize.LocationSerializer
import me.ninetyeightping.hcf.util.serialize.StatisticSerializer
import me.vaperion.blade.Blade
import me.vaperion.blade.argument.BladeProvider
import me.vaperion.blade.bindings.impl.BukkitBindings
import me.vaperion.blade.container.impl.BukkitCommandContainer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


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
        .registerTypeAdapter(StatisticEntry::class.java, StatisticSerializer)
        .create();

    lateinit var sotwHandler: SOTWHandler
    lateinit var kothHandler: KothHandler
    lateinit var landBoard: LandBoard

    //need this for initialization of landboard. Beans dont work when it depends on itself
    lateinit var teamHandler: TeamHandler


    override fun onEnable() {
        saveDefaultConfig()
        instance = this

        mongoClient = MongoClient(MongoClientURI(config.getString("uri")))
        mongoDatabase = mongoClient.getDatabase("HCFCoreTest")

        sotwHandler = SOTWHandler()
        kothHandler = KothHandler()
        teamHandler = TeamHandler()
        landBoard = LandBoard()

        val assemble = Assemble(this, AssembleBoard())
        assemble.ticks = 2
        assemble.assembleStyle = AssembleStyle.VIPER

        startPvPClassTasks()
        registerEvents()
        registerCommands()


        (DTRUpdateTask()).runTaskTimer(this, 0L, 20L)

    }

    override fun onDisable() {
        Bard.listOfBards.stream().map { Bukkit.getPlayer(it) }.filter(Objects::nonNull).forEach { Bard.onRemoval(it) }
    }

    fun registerEvents() {
        server.pluginManager.registerEvents(HCFPlayerListener(), this)
        server.pluginManager.registerEvents(ClaimListener(), this)
        server.pluginManager.registerEvents(LandBoardListener(), this)
        server.pluginManager.registerEvents(GenericTimerListener(), this)
        server.pluginManager.registerEvents(SystemTeamClaimListener(), this)
        server.pluginManager.registerEvents(ElevatorCreateListener(), this)
        server.pluginManager.registerEvents(SOTWDamageListener(), this)

        server.pluginManager.registerEvents(Bard, this)
        server.pluginManager.registerEvents(Archer, this)
    }

    fun registerCommands() {
        //commands
        Blade.of().fallbackPrefix("HCF")
            .containerCreator(BukkitCommandContainer.CREATOR)
            .bind(Flag::class.java, FlagTypeAdapter())
            .bind(HCFPlayer::class.java, HCFPlayerAdapter())
            .binding(BukkitBindings()).build()
            .register(GenericTeamCommands())
            .register(EconomyCommands())
            .register(SOTWCommands())
            .register(SystemTeamCommands())
            .register(KothGenericCommands())

    }

    fun startPvPClassTasks() {
        Bard.loadItems()
        (BardCheckTask()).runTaskTimer(this, 0L, 20L)

        Archer.loadItems()
        (ArcherCheckTask()).runTaskTimer(this, 0L, 20L)
    }



}