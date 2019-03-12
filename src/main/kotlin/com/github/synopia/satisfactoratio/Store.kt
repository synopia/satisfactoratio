package com.github.synopia.satisfactoratio

import react.RState
import redux.RAction
import redux.createStore
import redux.rEnhancer

data class AppState(val requested: Map<Item, Double>, val selected: List<Item>, val configs: List<ConfigTree>) : RState

data class ToggleSelectedItem(val item: Item) : RAction
data class SetRequested(val item: Item, val amount: Double) : RAction
data class ConfigTree(val out: Item, val amountInMin: Double, val input: List<ConfigTree>)

fun buildTree(item: Item, amountInMin: Double): ConfigTree {
    val recipe = Recipes.find { it.out == item }
    if (recipe != null) {
        val f = amountInMin / recipe.amount / recipe.ratePerMin
        val i = recipe.ingredient.map {
            buildTree(it.item, it.rateInMin * f)
        }
        return ConfigTree(item, amountInMin, i)
    } else {
        return ConfigTree(item, amountInMin, emptyList())
    }
}

fun appReducer(state: AppState, action: RAction): AppState {
    val newState = when (action) {
        is ToggleSelectedItem -> {
            if (!state.selected.contains(action.item)) {
                state.copy(selected = state.selected + action.item)
            } else if (state.selected.contains(action.item)) {
                state.copy(selected = state.selected - action.item)
            } else {
                state
            }
        }
        is SetRequested -> {
            val map = state.requested + Pair(action.item, action.amount)
            state.copy(requested = map)
        }
        else -> state
    }

    val configs = newState.requested.map { e ->
        val item = e.key
        val amount = e.value

        buildTree(item, amount)
    }

    console.log(configs)

    return newState.copy(configs = configs)
}

val store = createStore(::appReducer, AppState(emptyMap(), emptyList(), emptyList()), rEnhancer())
