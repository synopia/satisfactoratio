package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.Belt
import com.github.synopia.satisfactoratio.ConfigTree
import com.github.synopia.satisfactoratio.findBelt
import kotlinx.html.title
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import kotlin.math.roundToInt

interface ResultProps : RProps {
    var configs: List<ConfigTree>
    var maxBelt: Belt
}

class ResultComponent(props: ResultProps) : RComponent<ResultProps, RState>(props) {
    override fun RBuilder.render() {
        div("panel") {
            div("panel-header") {
                div("panel-title h5") {
                    +"Build chains"
                }
            }
            div("panel-body") {
                p {
                    +"Total Power: ${formatNumber(props.configs.sumByDouble { it.totalPower })}MW"
                }
                props.configs.forEach { config ->
                    ul {
                        renderTree(config)
                    }
                }
            }
            div("panel-footer") {
            }
        }
    }

    fun RBuilder.renderTree(configTree: ConfigTree) {
        li {
            val belt = findBelt(configTree.amountInMin, props.maxBelt)
            val beltCount = configTree.amountInMin / belt.maxSpeed
            val recipe = configTree.recipe
            +"${formatNumber(configTree.amountInMin)}/min"
            img(src = "images/${configTree.out.image}") {
                attrs {
                    width = "32px"
                    height = "32px"
                    title = configTree.out.name
                }
            }
            +"(${formatNumber(beltCount)}x"
            img(src = "images/${belt.image}") {
                attrs {
                    width = "32px"
                    height = "32px"
                    title = belt.name
                }
            }

            if (recipe != null) {
                +", ${formatNumber(configTree.buildingCount)}x"
                img(src = "images/${recipe.building.image}") {
                    attrs {
                        width = "32px"
                        height = "32px"
                        title = recipe.building.name
                    }
                }
                +"${configTree.buildingPercent}% = ${formatNumber(configTree.power)}MW"
            }
            +")"
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
