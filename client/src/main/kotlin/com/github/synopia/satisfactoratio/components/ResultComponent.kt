package com.github.synopia.satisfactoratio.components

import com.github.synopia.satisfactoratio.ConfigTree
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.li
import react.dom.p
import react.dom.ul
import kotlin.math.roundToInt

interface ResultProps : RProps {
    var configs: List<ConfigTree>
}
class ResultComponent(props: ResultProps) : RComponent<ResultProps, RState>(props) {
    var id: Int = 0
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
                id = 0
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
            resultLine(id++, configTree.out, configTree.amountInMin, configTree.recipe, configTree.buildingCount, configTree.buildingPercent, configTree.power)
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
