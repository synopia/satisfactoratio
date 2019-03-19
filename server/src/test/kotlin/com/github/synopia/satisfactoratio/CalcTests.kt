package com.github.synopia.satisfactoratio

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import test.*

infix fun ConfigTree.shouldBeTree(tree: ConfigTree) {
    println("${this} -> $tree")
    this.id shouldBe tree.id
    this.isGrouped shouldBe tree.isGrouped
    this.out shouldBe tree.out
    this.rateInMin shouldBe tree.rateInMin
    this.buildingCount shouldBe tree.buildingCount
    this.buildingPercent shouldBe tree.buildingPercent
    this.input.size shouldBe tree.input.size
    this.input.forEachIndexed { index, _ ->
        this.input[index] shouldBeTree tree.input[index]
        this.input[index].parent shouldBe this
    }
}

class ConfigBuilder(val out: Item, val rateInMin: Double, val count: Int, val percent: Int, val isGrouped: Boolean = false, val groupPercent: Int) {
    var children: List<ConfigBuilder> = emptyList()

    fun add(out: Item, amountInMin: Double, count: Int, percent: Int, isGrouped: Boolean = false, groupPercent: Int = 100, init: ConfigBuilder.() -> Unit = {}): ConfigBuilder {
        val child = ConfigBuilder(out, amountInMin, count, percent, isGrouped, groupPercent)
        child.init()
        children += child
        return child
    }

    fun build(id: String = "0"): ConfigTree {
        val configTree = ConfigTree(id, out, rateInMin, input = children.mapIndexed { index, child -> child.build("$id-$index") }, isGrouped = isGrouped)
        configTree.input.forEach {
            it.parent = configTree
        }
        configTree.buildingCount = count
        configTree.buildingPercent = percent
        return configTree
    }
}


fun config(out: Item, amountInMin: Double, count: Int, percent: Int, id: String = "0", init: ConfigBuilder.() -> Unit): ConfigTree {
    val root = ConfigBuilder(out, amountInMin, count, percent, false, 100)
    root.init()
    return root.build(id)
}

fun buildTestTree(item: Item, rateInMin: Double): ConfigTree {
    val r = ConfigRequest(mapOf(item to rateInMin), listOf(item), emptyMap(), false)
    val tree = r.build()
    return tree.trees.first()
}

class CalcTests : StringSpec({

    "testOreNormal" {
        buildTestTree(IronPlate, 15.0) shouldBeTree
                config(IronPlate, 15.0, 1, 100) {
                    add(IronIngot, 30.0, 1, 100) {
                        add(IronOre, 30.0, 1, 50)
                    }
                }
    }
    "testLimestoneNormal" {
        buildTestTree(Concrete, 15.0) shouldBeTree
                config(Concrete, 15.0, 1, 100) {
                    add(Limestone, 45.0, 1, 75)
                }
    }

    "testWireNormal" {
        buildTestTree(Wire, 45.0) shouldBeTree
                config(Wire, 45.0, 1, 100) {
                    add(CopperIngot, 15.0, 1, 50) {
                        add(CopperOre, 15.0, 1, 25)
                    }
                }
    }

    "testFrame" {
        buildTestTree(ModularFrame, 4.0) shouldBeTree
                config(ModularFrame, 4.0, 1, 100) {
                    add(ReinforcedIronPlate, 12.0, 3, 80) {
                        add(IronPlate, 48.0, 4, 80) {
                            add(IronIngot, 96.0, 4, 80) {
                                add(IronOre, 96.0, 2, 80)
                            }
                        }
                        add(Screw, 288.0, 4, 80) {
                            add(IronRod, 48.0, 4, 80) {
                                add(IronIngot, 48.0, 2, 80) {
                                    add(IronOre, 48.0, 1, 80)
                                }
                            }
                        }
                    }

                    add(IronRod, 24.0, 2, 80) {
                        add(IronIngot, 24.0, 1, 80) {
                            add(IronOre, 24.0, 1, 40)
                        }
                    }

                }
    }

    "testFrameGroupedByIronIngot" {
        val items = listOf(ModularFrame, IronIngot)
        val req = ConfigRequest(mapOf(ModularFrame to 4.0), items, emptyMap(), false)
        val res = req.build()

        res.trees[0] shouldBeTree
                config(ModularFrame, 4.0, 1, 100) {
                    add(ReinforcedIronPlate, 12.0, 3, 80) {
                        add(IronPlate, 48.0, 4, 80) {
                            add(IronIngot, 96.0, 4, 80, true, 57)
                        }

                        add(Screw, 288.0, 4, 80) {
                            add(IronRod, 48.0, 4, 80) {
                                add(IronIngot, 48.0, 2, 80, true, 28)
                            }
                        }
                    }

                    add(IronRod, 24.0, 2, 80) {
                        add(IronIngot, 24.0, 1, 80, true, 14)
                    }
                }
        res.trees[1] shouldBeTree
                config(IronIngot, 168.0, 6, 93, "1") {
                    add(IronOre, 168.0, 3, 93)
                }
    }

    "testFrameGroupedByIronIngotWithBuildingRequest" {
        val items = listOf(ModularFrame, IronIngot)
        val req = ConfigRequest(mapOf(ModularFrame to 4.0), items, mapOf("0" to ConfigOptions(null, 2, null)), false)
        val res = req.build()

        res.trees[0] shouldBeTree
                config(ModularFrame, 4.0, 2, 50) {
                    add(ReinforcedIronPlate, 12.0, 3, 80) {
                        add(IronPlate, 48.0, 4, 80) {
                            add(IronIngot, 96.0, 4, 80, true, 57)
                        }

                        add(Screw, 288.0, 4, 80) {
                            add(IronRod, 48.0, 4, 80) {
                                add(IronIngot, 48.0, 2, 80, true, 28)
                            }
                        }
                    }

                    add(IronRod, 24.0, 2, 80) {
                        add(IronIngot, 24.0, 1, 80, true, 14)
                    }
                }
        res.trees[1] shouldBeTree
                config(IronIngot, 168.0, 6, 93, "1") {
                    add(IronOre, 168.0, 3, 93)
                }
    }

})
