package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.ConfigTree
import com.github.synopia.satisfactoratio.Item
import com.github.synopia.satisfactoratio.ItemGroup
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h3
import react.dom.li
import react.dom.ul

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
        props.items.forEach { group ->
            h3 { +group.name }
            div {
                group.items.forEach { item ->
                    item(item, props.selected.contains(item), props.amounts[item] ?: 0.0, {
                        props.onItemToggled(item)
                    }, { amount ->
                        props.onAmountChanged(item, amount)
                    })
                }
            }
        }
        props.configs.forEach { config ->
            ul {
                renderTree(config)
            }
        }
    }

    fun RBuilder.renderTree(configTree: ConfigTree) {
        li {
            +"${configTree.amountInMin}/min ${configTree.out.name}"
            if (configTree.input.isNotEmpty()) {
                ul {
                    configTree.input.forEach {
                        renderTree(it)
                    }
                }
            }
        }
    }
}

fun RBuilder.itemSelect(items: List<ItemGroup>, amounts: Map<Item, Double>, selected: List<Item>, onItemToggled: (Item) -> Unit, onAmountChanged: (Item, Double) -> Unit) = child(ItemSelectComponent::class) {
    attrs.amounts = amounts
    attrs.items = items
    attrs.selected = selected
    attrs.onAmountChanged = onAmountChanged
    attrs.onItemToggled = onItemToggled
}