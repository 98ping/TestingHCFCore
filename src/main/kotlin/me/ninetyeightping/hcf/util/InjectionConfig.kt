package me.ninetyeightping.hcf.util

import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.team.TeamHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(value = ["me.ninetyeightping.hcf"])
open class InjectionConfig {


    var teamHandler : TeamHandler = TeamHandler()
    var hcfPlayerHandler = HCFPlayerHandler()


    @Bean
    open fun teamHandler() : TeamHandler {
        return teamHandler
    }

    @Bean
    open fun hcfplayerHandler() : HCFPlayerHandler {
        return hcfPlayerHandler
    }
}