package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.ConfigTree
import com.github.synopia.satisfactoratio.findBelt
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.br
import react.dom.button
import react.dom.div
import test.Belt
import test.BeltMk2
import test.Belts
import kotlin.math.roundToInt

interface ResultLineProps : RProps {
    var configLine: ConfigTree
}

interface ResultLineState : RState {
    var belt: Belt
}

class ResultLine(props: ResultLineProps) : RComponent<ResultLineProps, ResultLineState>(props) {
    override fun ResultLineState.init(props: ResultLineProps) {
        belt = findBelt(props.configLine.rateInMin / props.configLine.parentBuildings, BeltMk2)
    }

    override fun componentWillReceiveProps(nextProps: ResultLineProps) {
        setState {
            belt = findBelt(props.configLine.rateInMin / props.configLine.parentBuildings, BeltMk2)
        }
    }

    override fun RBuilder.render() {
        val belt = state.belt
        val beltCount = props.configLine.rateInMin / belt.maxSpeed / props.configLine.parentBuildings
        val recipe = props.configLine.recipe
        +"${formatNumber(props.configLine.rateInMin)}/min"
        button(classes = "btn-icon icon-${props.configLine.out.image} tooltip tooltip-bottom") {
            attrs["data-tooltip"] = props.configLine.out.name
        }
        if (props.configLine.isGrouped) {
            +"${props.configLine.groupPercent}%"
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
            +", ${props.configLine.buildingCount}x"
            button(classes = "btn-icon icon-${recipe.building.image} tooltip tooltip-bottom") {
                attrs["data-tooltip"] = recipe.building.name
            }
            +"${props.configLine.buildingPercent}% = ${formatNumber(props.configLine.power)}MW"
        }
        +")"

    }

    fun formatNumber(v: Double): String {
        return ((v * 1000).roundToInt() / 1000.0).toString()
    }
}


fun RBuilder.resultLine(configLine: ConfigTree) = child(ResultLine::class) {
    attrs {
        this.configLine = configLine
    }
}