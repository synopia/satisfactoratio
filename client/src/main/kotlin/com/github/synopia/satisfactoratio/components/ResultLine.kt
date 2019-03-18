package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.div
import react.dom.i
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
        belt = findBelt(props.configLine.rateInMin / props.configLine.parentBuildings(), BeltMk2)
    }

    override fun componentWillReceiveProps(nextProps: ResultLineProps) {
        setState {
            belt = findBelt(props.configLine.rateInMin / props.configLine.parentBuildings(), BeltMk2)
        }
    }

    override fun RBuilder.render() {
        val belt = state.belt
        val beltCount = props.configLine.rateInMin / belt.maxSpeed / props.configLine.parentBuildings()
        val recipe = props.configLine.recipe
        +"${formatNumber(props.configLine.rateInMin)}/min"
        button(classes = "btn-icon icon-${props.configLine.out.image} tooltip tooltip-bottom") {
            attrs["data-tooltip"] = props.configLine.out.name
        }
        if (props.configLine.isGrouped) {
            +"${props.configLine.groupPercent()}%"
        }
        +"(${formatNumber(beltCount)}x"
        div("popover popover-bottom") {
            button(classes = "btn-icon icon-${belt.image}") {
                attrs["data-tooltip"] = belt.name
            }
            popupContainer {

                Belts.forEach { b ->
                    div("column col-6") {
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
                    }
                    div("column col-6") {
                        val parent = if (b == BeltMk2) {
                            "${props.configLine.parentBuildings()}"
                        } else ""
                        +"${formatNumber(props.configLine.rateInMin / b.maxSpeed / props.configLine.parentBuildings())}x $parent"
                    }
                }

            }
        }
        if (recipe != null) {
            +", ${props.configLine.buildingCount}x"
            div("popover popover-bottom") {
                button(classes = "btn-icon icon-${recipe.building.image} tooltip tooltip-bottom") {
                    attrs["data-tooltip"] = recipe.building.name
                }
                popupContainer {
                    div("column col-3") {
                        button(classes = "btn btn-primary btn-action") {
                            i(classes = "icon icon-arrow-up") {}
                            attrs {
                                onClickFunction = {
                                    store.dispatch(SetOptionBuildings(props.configLine.id, props.configLine.buildingCount + 1))
                                }
                            }
                        }
                    }
                    div("column col-3") {
                        button(classes = "btn btn-primary btn-action") {
                            i(classes = "icon icon-arrow-down") {}
                            attrs {
                                onClickFunction = {
                                    store.dispatch(SetOptionBuildings(props.configLine.id, props.configLine.buildingCount - 1))
                                }
                            }
                        }
                    }
                    div("column col-6") {
                        val onOff = if (!props.configLine.buildCountRequested) "btn-primary" else ""
                        button(classes = "btn $onOff") {
                            i(classes = "icon icon-refresh") {}
                            attrs {
                                onClickFunction = {
                                    store.dispatch(SetOptionBuildings(props.configLine.id, null))
                                }
                            }
                        }
                    }
                    props.configLine.availableOptions.forEach { opt ->
                        val recipe = opt.recipe
                        if (recipe != null) {
                            div("column col-4") {
                                val onOff = if (props.configLine.recipe == recipe) "btn-primary" else ""
                                button(classes = "btn $onOff") {
                                    i(classes = "icon icon-${opt.recipe.building.image}") {
                                        attrs {
                                            onClickFunction = {
                                                store.dispatch(SetOptionRecipe(props.configLine.id, opt.recipe))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val purity = opt.purity
                        if (purity != null) {
                            div("column col-4") {
                                val onOff = if (props.configLine.purity == purity) "btn-primary" else ""
                                button(classes = "btn $onOff") {
                                    i(classes = "icon icon-minermk1") {
                                        attrs {
                                            onClickFunction = {
                                                store.dispatch(SetOptionPurity(props.configLine.id, purity))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            +"${props.configLine.buildingPercent}% = ${formatNumber(props.configLine.power)}MW"
        }
        +")"

    }

    fun formatNumber(v: Double): String {
        return ((v * 1000).roundToInt() / 1000.0).toString()
    }

    fun RBuilder.popupContainer(block: RBuilder.() -> Unit) {
        div("popover-container") {
            div("card") {
                div("container") {
                    div("columns") {
                        block()
                    }
                }
            }
        }
    }
}


fun RBuilder.resultLine(configLine: ConfigTree) = child(ResultLine::class) {
    attrs {
        this.configLine = configLine
    }
}