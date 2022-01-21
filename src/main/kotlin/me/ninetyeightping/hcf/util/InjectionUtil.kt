package me.ninetyeightping.hcf.util

import me.ninetyeightping.hcf.team.TeamHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


object InjectionUtil {


    var context: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext(InjectionConfig::class.java)


    @JvmStatic
    fun <T> get(tClass: Class<T>): T {
        return context.getBean(tClass)
    }


}