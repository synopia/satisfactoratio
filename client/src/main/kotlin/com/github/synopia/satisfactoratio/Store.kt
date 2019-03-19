package com.github.synopia.satisfactoratio

import react.RState
import redux.RAction
import redux.createStore
import redux.rEnhancer
import test.Item
import test.Purity
import test.Recipe

data class AppState(val requested: Map<Item, Double>, val selected: List<Item>, val configs: List<ConfigTree>, val options: Map<String, ConfigOptions>) : RState {
    fun addItem(item: Item): AppState {
        return copy(selected = selected + item)
    }

    fun removeItem(item: Item): AppState {
        return copy(selected = selected - item, requested = requested.filterNot { it.key == item })
    }
}

data class ToggleSelectedItem(val item: Item) : RAction
data class SetRequested(val item: Item, val amount: Double) : RAction
data class SetOptionRecipe(val id: String, val recipe: Recipe) : RAction
data class SetOptionBuildings(val id: String, val count: Int?) : RAction
data class SetOptionPurity(val id: String, val purity: Purity) : RAction
fun appReducer(state: AppState, action: RAction): AppState {
    val newState = when (action) {
        is ToggleSelectedItem -> {
            if (!state.selected.contains(action.item)) {
                state.addItem(action.item)
            } else if (state.selected.contains(action.item)) {
                state.removeItem(action.item)
            } else {
                state
            }
        }
        is SetRequested -> {
            val map = state.requested + Pair(action.item, action.amount)
            val selected = if (!state.selected.contains(action.item)) state.selected + action.item else state.selected
            state.copy(requested = map, selected = selected)
        }
        is SetOptionRecipe -> {
            val options = state.options.filterNot { it.key.startsWith(action.id) }
            val opt = ConfigOptions(action.recipe, null, null)
            state.copy(options = options + Pair(action.id, opt))
        }
        is SetOptionBuildings -> {
            val opt = getOption(state, action.id)
            state.copy(options = state.options + Pair(action.id, opt.copy(requestedBuildings = action.count)))
        }
        is SetOptionPurity -> {
            val opt = getOption(state, action.id)
            state.copy(options = state.options + Pair(action.id, opt.copy(requestedPurity = action.purity)))
        }
        else -> state
    }

    val config = ConfigRequest(newState.requested, newState.selected, newState.options, false)
    val res = config.build()

    return newState.copy(configs = res.trees)
}

private fun getOption(state: AppState, id: String): ConfigOptions {
    return state.options.getOrElse(id) { ConfigOptions(null, null, null) }
}

val store = createStore(::appReducer, AppState(emptyMap(), emptyList(), emptyList(), emptyMap()), rEnhancer())
