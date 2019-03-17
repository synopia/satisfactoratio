package com.github.synopia.satisfactoratio

import test.Item
import test.Recipes

data class ConfigOption(val id: String, val requestBuildingCount: Int? = null)
class ConfigRequest(val reqMap: Map<Item, Double>, val selected: List<Item>, val options: Map<String, ConfigOption> = emptyMap()) {
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
                val tree = buildTree(item, rateInMin)
                tree.calculate(options)
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

    fun buildTree(item: Item, rateInMin: Double, id: String = "0"): ConfigTree {
        val recipe = Recipes.find { it.out == item }
        return if (recipe == null) {
            ConfigTree(id, item, rateInMin, null, false, emptyList())
        } else if (id == "0" || !selected.contains(item)) {
            val f = rateInMin / recipe.ratePerMin
            val i = recipe.ingredient.mapIndexed { index, ingredient ->
                buildTree(ingredient.item, ingredient.rateInMin * f, "$id-$index")
            }
            ConfigTree(id, item, rateInMin, recipe, false, i)
        } else {
            ConfigTree(id, item, rateInMin, recipe, selected.contains(item), emptyList())
        }
    }

}