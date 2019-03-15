package com.github.synopia.satisfactoratio

import react.RState
import redux.RAction
import redux.createStore
import redux.rEnhancer
import test.Item

data class AppState(val requested: Map<Item, Double>, val selected: List<Item>, val configs: List<ConfigTree>) : RState {
    fun addItem(item: Item): AppState {
        return copy(selected = selected + item)
    }

    fun removeItem(item: Item): AppState {
        return copy(selected = selected - item, requested = requested.filterNot { it.key == item })
    }
}

data class ToggleSelectedItem(val item: Item) : RAction
data class SetRequested(val item: Item, val amount: Double) : RAction

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
        else -> state
    }

    val map = newState.selected.associateWith { ConfigTree(it, 0.0) }.toMutableMap()

    newState.requested.forEach { e ->
        val item = e.key
        val amount = e.value
        if (amount > 0.0) {
            buildTree(item, amount, map)
        }
    }

    val configs = map.values.toList().map {
        if (it.input.isEmpty()) {
            buildTreeRec(it.out, it.amountInMin, mutableMapOf())
        } else {
            it
        }
    }

    return newState.copy(configs = configs)
}

val store = createStore(::appReducer, AppState(emptyMap(), emptyList(), emptyList()), rEnhancer())
