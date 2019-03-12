package com.github.synopia.satisfactoratio.container

import com.github.synopia.satisfactoratio.*
import com.github.synopia.satisfactoratio.components.ItemSelectComponent
import com.github.synopia.satisfactoratio.components.ItemSelectProps
import react.*
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction

interface ItemSelectStateProps : RProps {
    var selected: List<Item>
    var amounts: Map<Item, Double>
    var items: List<ItemGroup>
    var configs: List<ConfigTree>
}

interface ItemSelectDispatchProps : RProps {
    var onItemToggled: (Item) -> Unit
    var onAmountChanged: (Item, Double) -> Unit
}

private val mapStateToProps: ItemSelectStateProps.(AppState, ItemSelectProps) -> Unit = { state, props ->
    selected = state.selected
    items = ItemGroups
    amounts = state.requested
    configs = state.configs
}

private val mapDispatchToProps: ItemSelectDispatchProps.((RAction) -> WrapperAction, ItemSelectProps) -> Unit = { dispatch, props ->
    onItemToggled = { item -> dispatch(ToggleSelectedItem(item)) }
    onAmountChanged = { item, amount -> dispatch(SetRequested(item, amount)) }
}

val c: RClass<ItemSelectProps> = rConnect<AppState, RAction, WrapperAction, ItemSelectProps, ItemSelectStateProps, ItemSelectDispatchProps, ItemSelectProps>(
        mapStateToProps, mapDispatchToProps
)(ItemSelectComponent::class.js as RClass<ItemSelectProps>)

class ItemSelectContainer(props: ItemSelectStateProps) : RComponent<ItemSelectStateProps, RState>(props) {
    override fun RBuilder.render() {
        c {
            attrs.amounts = props.amounts
            attrs.items = props.items
            attrs.selected = props.selected
            attrs.configs = props.configs
        }
    }
}

fun RBuilder.itemSelectContainer() = child(ItemSelectContainer::class) {

}