package com.github.synopia.satisfactoratio

import test.Item
import test.Recipe
import test.Recipes


class ConfigRequest(val reqMap: Map<Item, Double>, val selected: List<Item>, val options: Map<String, ConfigOptions> = emptyMap()) {
    val map = mutableMapOf<Item, Double>()

    fun build(): ConfigResponse {
        reqMap.forEach { e ->
            val item = e.key
            val amount = e.value
            if (amount > 0.0) {
                collectItems(item, amount)
            }
        }
        val trees = map.map { e ->
            val item = e.key
            val rateInMin = e.value
            if (rateInMin > 0.0) {
                val tree = buildTree(item, rateInMin)
                tree
            } else {
                null
            }
        }.filterNotNull()
        trees.forEach { it.calculatePassOne(options) }
        trees.forEach { it.calculatePassTwo(options) }
        val totalPower = trees.sumByDouble { it.totalPower }
        return ConfigResponse(trees, totalPower)

    }

    fun collectItems(item: Item, rateInMin: Double, id: String = "0") {
        if (selected.contains(item)) {
            map[item] = (map[item] ?: 0.0) + rateInMin
        }
        val recipe = findRecipe(item, id)
        if (recipe != null) {
            val f = rateInMin / recipe.ratePerMin
            recipe.ingredient.forEachIndexed { index, ingredient ->
                collectItems(ingredient.item, ingredient.rateInMin * f, "$id-$index")
            }
        }
    }

    fun buildTree(item: Item, rateInMin: Double, id: String = "0"): ConfigTree {
        val recipe = findRecipe(item, id)
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

    private fun findRecipe(item: Item, id: String): Recipe? {
        val recipes = Recipes.filter { it.out == item }
        if (recipes.isEmpty()) {
            return null
        } else if (recipes.size == 1) {
            return recipes[0]
        } else {
            val req = options[id]?.recipe
            if (req != null) {
                return req
            } else {
                return recipes[0]
            }
        }

    }

}