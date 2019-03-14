package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.Belt
import com.github.synopia.satisfactoratio.ConfigTree
import com.github.synopia.satisfactoratio.Item
import com.github.synopia.satisfactoratio.ItemGroup
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div

interface ItemSelectProps : RProps {
    var selected: List<Item>
    var amounts: Map<Item, Double>
    var items: List<ItemGroup>
    var configs: List<ConfigTree>
    var maxBelt: Belt

    var onItemToggled: (Item) -> Unit
    var onAmountChanged: (Item, Double) -> Unit
}

class ItemSelectComponent(props: ItemSelectProps) : RComponent<ItemSelectProps, RState>(props) {
    override fun RBuilder.render() {
        div("panel") {
            div("panel-header") {
                div("panel-title h5") {
                    +"Select Items"
                }
            }
            div("panel-body") {
                props.items.forEach { group ->
                    div("tile tile-centered") {
                        div("tile-content") {
                            div("tile-title text-bold") {
                                +group.name
                            }
                            div("columns") {
                                group.items.forEach { item ->
                                    item(item, props.selected.contains(item), props.amounts[item] ?: 0.0, {
                                        props.onItemToggled(item)
                                    }, { amount ->
                                        props.onAmountChanged(item, amount)
                                    })
                                }
                            }
                        }
                    }
                }
            }
            div("panel-footer") {
            }
        }
    }
}
