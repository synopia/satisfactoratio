package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.Item
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.input
import react.dom.p

interface ItemProps : RProps {
    var item: Item
    var selected: Boolean
    var amount: Double

    var onItemSelected: (Boolean) -> Unit
    var onAmountChanged: (Double) -> Unit
}

class ItemComponent(props: ItemProps) : RComponent<ItemProps, RState>(props) {
    override fun RBuilder.render() {
        div {
            p { +props.item.name }
            input(InputType.checkBox) {
                attrs {
                    checked = props.selected
                    onClickFunction = {
                        props.onItemSelected(!props.selected)
                    }
                }
            }
            input(InputType.number) {
                attrs {
                    value = props.amount.toString()
                    onChangeFunction = {
                        val target = it.target as HTMLInputElement
                        val number = target.valueAsNumber
                        props.onAmountChanged(number)
                    }
                }
            }
        }
    }
}

fun RBuilder.item(item: Item, selected: Boolean = false, amount: Double = 0.0, onItemSelected: (Boolean) -> Unit, onAmountChanged: (Double) -> Unit) = child(ItemComponent::class) {
    attrs.item = item
    attrs.amount = amount
    attrs.selected = selected
    attrs.onAmountChanged = onAmountChanged
    attrs.onItemSelected = onItemSelected
}