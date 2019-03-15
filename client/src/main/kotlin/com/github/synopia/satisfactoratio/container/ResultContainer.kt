package com.github.synopia.satisfactoratio.container

import com.github.synopia.satisfactoratio.AppState
import com.github.synopia.satisfactoratio.ConfigTree
import com.github.synopia.satisfactoratio.components.ResultComponent
import com.github.synopia.satisfactoratio.components.ResultProps
import react.*
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction


interface ResultStateProps : RProps {
    var configs: List<ConfigTree>
}

interface ResultDispatchProps : RProps {
}

private val mapStateToProps: ResultStateProps.(AppState, ResultProps) -> Unit = { state, props ->
    configs = state.configs
}

private val mapDispatchToProps: ResultDispatchProps.((RAction) -> WrapperAction, ResultProps) -> Unit = { dispatch, props ->
}

private val c: RClass<ResultProps> = rConnect<AppState, RAction, WrapperAction, ResultProps, ResultStateProps, ResultDispatchProps, ResultProps>(
        mapStateToProps, mapDispatchToProps
)(ResultComponent::class.js as RClass<ResultProps>)

class ResultContainer(props: ResultStateProps) : RComponent<ResultStateProps, RState>(props) {
    override fun RBuilder.render() {
        c {
            attrs.configs = props.configs
        }
    }
}

fun RBuilder.resultContainer() = child(ResultContainer::class) {

}