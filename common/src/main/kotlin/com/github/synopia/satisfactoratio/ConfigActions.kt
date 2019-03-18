package com.github.synopia.satisfactoratio

import test.Item
import test.Purity
import test.Recipe
import test.Recipes
import kotlin.math.max
import kotlin.math.pow

data class ConfigOptions(val recipe: Recipe?, val requestedBuildings: Int?, val requestedPurity: Purity?) {
}

interface ConfigAction {
    fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {}
    fun visitAfter(tree: ConfigTree, options: ConfigOptions?) {}

    fun apply(tree: ConfigTree, options: Map<String, ConfigOptions>) {
        visitBefore(tree, options[tree.id])
        tree.input.forEach {
            apply(it, options)
        }
        visitAfter(tree, options[tree.id])
    }
}

class SetParents(val map: MutableMap<Item, ConfigTree>) : ConfigAction {
    var stack = emptyList<ConfigTree>()
    var first = true
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        if (stack.isEmpty() && !first) {
            map[tree.out] = tree
        }
        first = false
        stack += tree
    }

    override fun visitAfter(tree: ConfigTree, options: ConfigOptions?) {
        val last = stack.last()
        stack = stack.dropLast(1)

        tree.parent = if (stack.isNotEmpty()) stack.last() else null
    }
}


class SetGroups(val map: Map<Item, ConfigTree>) : ConfigAction {
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        if (tree.isGrouped) {
            tree.group = map[tree.out]
        }
    }
}

class ExtractAvailableOptions : ConfigAction {
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        val recipes = Recipes.filter { it.out == tree.out }
        tree.availableOptions = emptyList()
        val hasPurity = recipes.firstOrNull { it.building.hasPurity } != null
        if (hasPurity) {
            tree.availableOptions += Purity.values().map { RecipeConfig(null, it) }
            tree.purity = options?.requestedPurity ?: Purity.Normal
        }
        tree.availableOptions += recipes.map { RecipeConfig(it, null) }
    }
}

class ApplyBuildings : ConfigAction {
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        if (options?.requestedBuildings != null) {
            tree.buildingCount = options.requestedBuildings
            tree.buildCountRequested = true
        } else {
            val count = tree.rateInMin / tree.purity.factor / tree.recipe!!.ratePerMin
            if (count - count.toInt() > 0.01) {
                tree.buildingCount = count.toInt() + 1
            } else {
                tree.buildingCount = count.toInt()
            }
        }
    }
}

data class Buildings(val tree: ConfigTree, val count: Int, val requested: Boolean) {
    fun snapCount(): Int {
        if (count == 1) {
            return 1
        }
        var c = count
        while (true) {
            val isPartOfTwo = c % 2 == 0
            if (isPartOfTwo) {
                return c
            }
            c++
        }
    }
}

class RoundBuildings() : ConfigAction {
    var stack = emptyList<Buildings>()
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        stack += Buildings(tree, tree.buildingCount, tree.buildCountRequested)
    }

    override fun visitAfter(tree: ConfigTree, options: ConfigOptions?) {
        val last = stack.last()
        stack = stack.dropLast(1)
        if (!tree.buildCountRequested) {
            if (tree.input.isNotEmpty()) {
                val min = tree.input.map { it.buildingCount }.min()!!
                tree.buildingCount = max(min, tree.buildingCount)
            } else {
                tree.buildingCount = last.snapCount()
            }
        }
//        val parent = tree.parent
//        if( parent!=null && !parent.buildCountRequested) {
//            var parentCount = parent.buildingCount
//            val count = tree.buildingCount
//            if( parentCount-count>=1 ) {
//                while (!(parentCount % count == 0 || count % parentCount == 0)) {
//                    parentCount++
//                }
//            } else if( count-parentCount>=1) {
//                while (!(parentCount % count == 0 || count % parentCount == 0)) {
//                    parentCount--
//                }
//            }
//            parent.buildingCount = parentCount
//        }
    }
}

class CalculateBuildings : ConfigAction {
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        if (tree.recipe != null) {
            val count = tree.rateInMin / tree.purity.factor / tree.recipe!!.ratePerMin
            val percent = count / tree.buildingCount * 100
            tree.buildingPercent = percent.toInt()
        } else {
            tree.buildingCount = 0
            tree.buildingPercent = 0
        }
    }
}

/*
-   1
-   12
-   123
-   234
-   34
-   456
-   456
-   56
-   6

 */

class CalculatePower() : ConfigAction {
    override fun visitBefore(tree: ConfigTree, options: ConfigOptions?) {
        if (tree.recipe != null && !tree.isGrouped) {
            tree.power = tree.buildingCount * tree.recipe!!.building.power * (tree.buildingPercent / 100.0).pow(1.6)
        } else {
            tree.power = 0.0
        }
    }
}

class CalculateTotalPower : ConfigAction {
    override fun visitAfter(tree: ConfigTree, options: ConfigOptions?) {
        tree.totalPower = tree.input.sumByDouble { it.totalPower } + tree.power
    }
}