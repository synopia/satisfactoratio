package com.github.synopia.satisfactoratio


import com.github.synopia.satisfactoratio.container.itemSelectContainer
import com.github.synopia.satisfactoratio.container.resultContainer
import react.dom.div
import react.dom.render
import react.redux.provider
import kotlin.browser.document

@JsModule("lz-string")
external object LZString {
    fun compressToBase64(s: String): String
    fun decompressFromBase64(s: String): String
}


fun main(args: Array<String>) {
    render(document.getElementById("root")) {
        provider(store) {
            div("tile is-ancestor is-parent") {
                itemSelectContainer()
                div("tile is-child box") {
                    resultContainer()
                }
            }
        }
    }
}
