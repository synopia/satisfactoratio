package com.github.synopia.satisfactoratio.components

import kotlinx.css.Display
import kotlinx.css.Float
import kotlinx.css.em
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.i
import react.dom.input
import react.dom.span
import styled.css
import styled.styledDiv
import styled.styledInput
import test.Item

interface ItemProps : RProps {
    var item: Item
    var selected: Boolean
    var amount: Double

    var onItemSelected: (Boolean) -> Unit
    var onAmountChanged: (Double) -> Unit
}

class ItemComponent(props: ItemProps) : RComponent<ItemProps, RState>(props) {
    override fun RBuilder.render() {
        styledDiv {
            attrs {
                classes = setOf("column", "col-2")
            }
            css {
                display = Display.inlineBlock
                float = Float.left
            }
            span(classes = "icon is-large") {
                i("icon-${props.item.image}") {
                    attrs["title"] = props.item.name
                }
            }
            input(InputType.checkBox) {
                attrs {
                    checked = props.selected
                    onClickFunction = {
                        props.onItemSelected(!props.selected)
                    }
                }
            }
            styledInput(InputType.number) {
                css {
                    width = 3.em
                }
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