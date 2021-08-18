package com.justinsimonelli.brdges.data.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}