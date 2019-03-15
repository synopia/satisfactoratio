package com.github.synopia.satisfactoratio

import test.*
import kotlin.math.pow

data class ConfigTree(val out: Item, val amountInMin: Double, val recipe: Recipe? = null, val input: List<ConfigTree> = emptyList()) {
    var buildingCount: Double
    var buildingPercent: Int
    val power: Double
    val totalPower: Double

    init {
        if (recipe != null) {
            var count = amountInMin / recipe.ratePerMin
            val fraction = count - count.toInt()
            var percent = 100
            if (fraction >= 0.01) {
                percent = (100 * count / (count.toInt() + 1)).toInt()
                count = count.toInt() + 1.0
            }
            buildingCount = count
            buildingPercent = percent
            power = count * recipe.building.power * (percent / 100.0).pow(1.6)
        } else {
            buildingCount = 0.0
            buildingPercent = 0
            power = 0.0
        }
        totalPower = input.sumByDouble { it.totalPower } + power
    }
}

fun collectItems(item: Item, rateInMin: Double, map: MutableMap<Item, Double>, selected: List<Item>) {
    if (selected.contains(item)) {
        map[item] = (map[item] ?: 0.0) + rateInMin
    }
    val recipe = Recipes.find { it.out == item }
    if (recipe != null) {
        val f = rateInMin / recipe.ratePerMin
        recipe.ingredient.forEach {
            collectItems(it.item, it.rateInMin * f, map, selected)
        }
    }
}

fun buildTree(item: Item, rateInMin: Double, selected: List<Item>, root: Boolean = true): ConfigTree {
    val recipe = Recipes.find { it.out == item }
    return if (recipe != null && (root || !selected.contains(item))) {
        val f = rateInMin / recipe.ratePerMin
        val i = recipe.ingredient.map {
            buildTree(it.item, it.rateInMin * f, selected, false)
        }
        ConfigTree(item, rateInMin, recipe, i)
    } else {
        ConfigTree(item, rateInMin, null, emptyList())
    }
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

