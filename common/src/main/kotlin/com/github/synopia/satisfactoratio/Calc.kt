package com.github.synopia.satisfactoratio

import test.*


data class ConfigResponse(val trees: List<ConfigTree>, val totalPower: Double)
data class RecipeConfig(val recipe: Recipe?, val purity: Purity?)
data class ConfigTree(val id: String, val out: Item, var rateInMin: Double, var recipe: Recipe? = null, val isGrouped: Boolean = false, val input: List<ConfigTree> = emptyList()) {
    var buildingCount: Int = 0
    var buildingPercent: Int = 0
    var group: ConfigTree? = null
    var parent: ConfigTree? = null
    var power: Double = 0.0
    var totalPower: Double = 0.0
    var buildCountRequested: Boolean = false
    var availableOptions = emptyList<RecipeConfig>()
    var purity: Purity = Purity.Normal

    override fun toString(): String {
        return "$id: ${recipe?.out ?: "-"} $buildingCount $buildingPercent "
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

