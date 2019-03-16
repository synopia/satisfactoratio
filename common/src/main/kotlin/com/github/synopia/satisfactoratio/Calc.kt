package com.github.synopia.satisfactoratio

import test.*
import kotlin.math.pow

class ConfigRequest(val reqMap: Map<Item, Double>, val selected: List<Item>) {
    val map = mutableMapOf<Item, Double>()

    fun build(): ConfigResponse {
        reqMap.forEach { e ->
            val item = e.key
            val amount = e.value
            if (amount > 0.0) {
                collectItems(item, amount)
            }
        }
        val map2 = mutableMapOf<Item, ConfigTree>()
        val trees = map.map { e ->
            val item = e.key
            val rateInMin = e.value
            if (rateInMin > 0.0) {
                val tree = buildTree(item, rateInMin, selected)
                map2[item] = tree
                tree
            } else {
                null
            }
        }.filterNotNull().map { e ->
            e.calcGroupPercent(map2)
            e
        }
        val totalPower = trees.sumByDouble { it.totalPower }
        return ConfigResponse(trees, totalPower)

    }

    fun collectItems(item: Item, rateInMin: Double) {
        if (selected.contains(item)) {
            map[item] = (map[item] ?: 0.0) + rateInMin
        }
        val recipe = Recipes.find { it.out == item }
        if (recipe != null) {
            val f = rateInMin / recipe.ratePerMin
            recipe.ingredient.forEach {
                collectItems(it.item, it.rateInMin * f)
            }
        }
    }

    fun buildTree(item: Item, rateInMin: Double, selected: List<Item>, id: String = "0"): ConfigTree {
        val recipe = Recipes.find { it.out == item }
        return if (recipe == null) {
            ConfigTree(id, item, rateInMin, null, false, emptyList())
        } else if (id == "0" || !selected.contains(item)) {
            val f = rateInMin / recipe.ratePerMin
            val i = recipe.ingredient.mapIndexed { index, ingredient ->
                buildTree(ingredient.item, ingredient.rateInMin * f, selected, "$id-$index")
            }
            ConfigTree(id, item, rateInMin, recipe, false, i)
        } else {
            ConfigTree(id, item, rateInMin, recipe, selected.contains(item), emptyList())
        }
    }

}

data class ConfigResponse(val trees: List<ConfigTree>, val totalPower: Double)
data class ConfigTree(val id: String, val out: Item, val rateInMin: Double, val recipe: Recipe? = null, val isGrouped: Boolean = false, val input: List<ConfigTree> = emptyList()) {
    var buildingCount: Int
    var buildingPercent: Int
    var group: ConfigTree? = null
    var parent: ConfigTree? = null
    val power: Double
    val totalPower: Double

    init {
        if (recipe != null) {
            val count = rateInMin / recipe.ratePerMin
            buildingCount = count.toInt()
            val fraction = count - buildingCount
            var percent = 100
            if (fraction >= 0.01) {
                percent = (100 * count / (count.toInt() + 1)).toInt()
                buildingCount = count.toInt() + 1
            }
            buildingPercent = percent
            if (!isGrouped) {
                power = buildingCount * recipe.building.power * (percent / 100.0).pow(1.6)
            } else {
                power = 0.0
            }
        } else {
            buildingCount = 0
            buildingPercent = 0
            power = 0.0
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

