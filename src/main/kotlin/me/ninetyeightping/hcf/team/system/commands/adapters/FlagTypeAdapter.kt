package me.ninetyeightping.hcf.team.system.commands.adapters

import me.ninetyeightping.hcf.team.system.flags.Flag
import me.vaperion.blade.argument.BladeArgument
import me.vaperion.blade.argument.BladeProvider
import me.vaperion.blade.context.BladeContext

class FlagTypeAdapter : BladeProvider<Flag> {


    override fun provide(context: BladeContext, argument: BladeArgument): Flag? {
        val flag = Flag.valueOf(argument.string)

        if (flag == null) {
            return null
        }

        return flag
    }
}