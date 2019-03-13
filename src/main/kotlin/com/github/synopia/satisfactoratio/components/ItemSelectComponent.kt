package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h3
import react.dom.li
import react.dom.ul
import kotlin.math.roundToInt

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
        div("container") {
            props.items.forEach { group ->
                div("columns") {
                    div("column col-12") {
                        h3 { +group.name }
                    }
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
        props.configs.forEach { config ->
            ul {
                renderTree(config)
            }
        }
    }

    fun RBuilder.renderTree(configTree: ConfigTree) {
        li {
            val belt = findBelt(configTree.amountInMin, props.maxBelt)
            val beltCount = configTree.amountInMin / belt.maxSpeed
            val recipe = configTree.recipe
            val recipeText = if (recipe != null) {
                var count = configTree.amountInMin / recipe.ratePerMin
                val fraction = count - count.toInt()
                var percent = 100
                if (fraction >= 0.001) {
                    percent = (100 * count / (count.toInt() + 1)).toInt()
                    count = count.toInt() + 1.0
                }
                ", ${formatNumber(count)}x ${recipe.building.name} $percent%"
            } else {
                ""
            }
            +"${formatNumber(configTree.amountInMin)}/min ${configTree.out.name} (${formatNumber(beltCount)}x ${belt.name}$recipeText)"
            if (configTree.input.isNotEmpty()) {
                ul {
                    configTree.input.forEach {
                        renderTree(it)
                    }
                }
            }
        }
    }

    fun formatNumber(v: Double): String {
        return ((v * 1000).roundToInt() / 1000.0).toString()
    }

}

fun RBuilder.itemSelect(items: List<ItemGroup>, amounts: Map<Item, Double>, selected: List<Item>, onItemToggled: (Item) -> Unit, onAmountChanged: (Item, Double) -> Unit) = child(ItemSelectComponent::class) {
    attrs.amounts = amounts
    attrs.items = items
    attrs.selected = selected
    attrs.onAmountChanged = onAmountChanged
    attrs.onItemToggled = onItemToggled
}