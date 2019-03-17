package com.github.synopia.satisfactoratio

import test.Belt
import test.Belts
import test.Item
import test.Recipe
import kotlin.math.pow


data class ConfigResponse(val trees: List<ConfigTree>, val totalPower: Double)
data class ConfigTree(val id: String, val out: Item, val rateInMin: Double, val recipe: Recipe? = null, val isGrouped: Boolean = false, val input: List<ConfigTree> = emptyList()) {
    var buildingCount: Int = 0
    var buildingPercent: Int = 0
    var group: ConfigTree? = null
    var parent: ConfigTree? = null
    var power: Double = 0.0
    var totalPower: Double = 0.0
    var buildCountRequested: Boolean = false
    init {
//        calculate()
    }

    fun calculate(options: Map<String, ConfigOption>) {
        if (recipe != null) {
            val option = options[id]
            val count = rateInMin / recipe.ratePerMin
            if (option?.requestBuildingCount != null) {
                buildingCount = option.requestBuildingCount
                val percent = count / buildingCount * 100
                buildingPercent = percent.toInt()
                buildCountRequested = true
            } else {
                buildingCount = count.toInt()
                val fraction = count - buildingCount
                var percent = 100
                if (fraction >= 0.01) {
                    percent = (100 * count / (count.toInt() + 1)).toInt()
                    buildingCount = count.toInt() + 1
                }
                buildingPercent = percent
                buildCountRequested = false
            }
            if (!isGrouped) {
                power = buildingCount * recipe.building.power * (buildingPercent / 100.0).pow(1.6)
            } else {
                power = 0.0
            }
        } else {
            buildingCount = 0
            buildingPercent = 0
            power = 0.0
        }
        input.forEach {
            it.calculate(options)
        }
        totalPower = input.sumByDouble { it.totalPower } + power
    }

    override fun toString(): String {
        return "$id: ${recipe?.out ?: "-"} $buildingCount $buildingPercent "
    }


    fun calcGroupPercent(map: Map<Item, ConfigTree>) {
        val group = map[out]
        if (group != null) {
            this.group = group
        }
        input.forEach {
            it.calcGroupPercent(map)
            it.parent = this
        }
    }

    fun groupPercent() = (this.rateInMin / (group?.rateInMin ?: rateInMin) * 100).toInt()
    fun parentBuildings() = (this.parent?.buildingCount) ?: 1
}


fun findBelt(speed: Double, maxBelt: Belt): Belt {
    Belts.forEach { belt ->
        if (belt.maxSpeed <= maxBelt.maxSpeed) {
            if (speed <= belt.maxSpeed) {
                return belt
            }
        }
    }
    return maxBelt
}

