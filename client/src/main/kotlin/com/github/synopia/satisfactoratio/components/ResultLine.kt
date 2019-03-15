package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.findBelt
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.br
import react.dom.button
import react.dom.div
import test.*
import kotlin.math.roundToInt

interface ResultLineProps : RProps {
    var id: Int
    var ratePerMin: Double
    var out: Item
    var recipe: Recipe?
    var buildingCount: Double
    var buildingPercent: Int
    var power: Double
}

interface ResultLineState : RState {
    var belt: Belt
}

class ResultLine(props: ResultLineProps) : RComponent<ResultLineProps, ResultLineState>(props) {
    override fun ResultLineState.init(props: ResultLineProps) {
        belt = findBelt(props.ratePerMin, BeltMk2)
    }

    override fun RBuilder.render() {
        val belt = state.belt
        val beltCount = props.ratePerMin / belt.maxSpeed
        val recipe = props.recipe
        +"${formatNumber(props.ratePerMin)}/min"
        button(classes = "btn-icon icon-${props.out.image} tooltip tooltip-bottom") {
            attrs["data-tooltip"] = props.out.name
        }
        +"(${formatNumber(beltCount)}x"
        div("popover popover-right") {
            button(classes = "btn-icon icon-${belt.image}") {
                attrs["data-tooltip"] = belt.name
            }
            div("popover-container") {
                div("card") {
                    div("card-body") {
                        Belts.forEach { b ->
                            val cls = if (b == belt) "btn-primary" else ""
                            button(classes = "btn $cls") {
                                +b.name
                                attrs {
                                    onClickFunction = {
                                        setState {
                                            this.belt = b
                                        }
                                    }
                                }
                            }
                            br { }
                        }
                    }
                }
            }
        }
        if (recipe != null) {
            +", ${formatNumber(props.buildingCount)}x"
            button(classes = "btn-icon icon-${recipe.building.image} tooltip tooltip-bottom") {
                attrs["data-tooltip"] = recipe.building.name
            }
            +"${props.buildingPercent}% = ${formatNumber(props.power)}MW"
        }
        +")"

    }

    fun formatNumber(v: Double): String {
        return ((v * 1000).roundToInt() / 1000.0).toString()
    }
}


fun RBuilder.resultLine(id: Int, out: Item, ratePerMin: Double, recipe: Recipe?, buildingCount: Double, buildingPercent: Int, power: Double) = child(ResultLine::class) {
    attrs {
        this.id = id
        this.out = out
        this.ratePerMin = ratePerMin
        this.recipe = recipe
        this.buildingCount = buildingCount
        this.buildingPercent = buildingPercent
        this.power = power
    }
}