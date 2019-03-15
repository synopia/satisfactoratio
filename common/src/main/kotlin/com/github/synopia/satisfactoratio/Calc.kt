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

fun buildTree(item: Item, amountInMin: Double, map: MutableMap<Item, ConfigTree>) {
    val recipe = Recipes.find { it.out == item }
    val totalAmount = amountInMin + (map[item]?.amountInMin ?: 0.0)
    val tree = if (recipe != null) {
        val f = totalAmount / recipe.ratePerMin
        val i = recipe.ingredient.map {
            buildTreeRec(it.item, it.rateInMin * f, map)
        }
        ConfigTree(item, totalAmount, recipe, i)
    } else {
        ConfigTree(item, totalAmount, null, emptyList())
    }
    map[item] = tree
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

fun buildTreeRec(item: Item, amountInMin: Double, map: MutableMap<Item, ConfigTree>): ConfigTree {
    val recipe = Recipes.find { it.out == item }
    if (map.containsKey(item)) {
        val tree = ConfigTree(item, amountInMin + map[item]!!.amountInMin, recipe)
        map[item] = tree
        return ConfigTree(item, amountInMin)
    } else {
        if (recipe != null) {
            val f = amountInMin / recipe.ratePerMin
            val i = recipe.ingredient.map {
                buildTreeRec(it.item, it.rateInMin * f, map)
            }
            return ConfigTree(item, amountInMin, recipe, i)
        } else {
            return ConfigTree(item, amountInMin, null)
        }
    }
}