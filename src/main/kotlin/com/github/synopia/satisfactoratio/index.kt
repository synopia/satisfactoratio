package com.github.synopia.satisfactoratio


import com.github.synopia.satisfactoratio.container.itemSelectContainer
import com.github.synopia.satisfactoratio.container.resultContainer
import kotlinext.js.require
import kotlinext.js.requireAll
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
    requireAll(require.context(".", true, js("/\\.css$/")))

    render(document.getElementById("root")) {
        provider(store) {
            div("container") {
                div("columns") {
                    div("column col-6") {
                        itemSelectContainer()
                    }
                    div("column col-6") {
                        resultContainer()
                    }
                }
            }
        }
    }
}
