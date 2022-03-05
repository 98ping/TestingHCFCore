package me.ninetyeightping.hcf.team.comands.adapter

import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.util.InjectionUtil
import me.vaperion.blade.argument.BladeArgument
import me.vaperion.blade.argument.BladeProvider
import me.vaperion.blade.context.BladeContext

class HCFPlayerAdapter : BladeProvider<HCFPlayer> {
    override fun provide(context: BladeContext, argument: BladeArgument): HCFPlayer? {
        return InjectionUtil.get(HCFPlayerHandler::class.java).byPlayerName(argument.string)
    }
}