package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.ConfigTree
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.p
import test.Item
import test.ItemGroup

interface ItemSelectProps : RProps {
    var selected: List<Item>
    var amounts: Map<Item, Double>
    var items: List<ItemGroup>
    var configs: List<ConfigTree>

    var onItemToggled: (Item) -> Unit
    var onAmountChanged: (Item, Double) -> Unit
}

class ItemSelectComponent(props: ItemSelectProps) : RComponent<ItemSelectProps, RState>(props) {
    override fun RBuilder.render() {
        div("tile is-child is-parent box") {
            p("title") {
                +"Select Items"
            }
            props.items.forEach { group ->
                div("tile is-child box") {
                    p("subtitle") {
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
}
