package com.github.synopia.satisfactoratio

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import test.*

infix fun ConfigTree.shouldBeTree(tree: ConfigTree) {
    this.out shouldBe tree.out
    this.amountInMin shouldBe tree.amountInMin
    this.buildingCount shouldBe tree.buildingCount
    this.buildingPercent shouldBe tree.buildingPercent
    this.input.size shouldBe tree.input.size
    this.input.forEachIndexed { index, _ ->
        this.input[index] shouldBeTree tree.input[index]
    }
}

class ConfigBuilder(val out: Item, val amountInMin: Double, val count: Double, val percent: Int) {
    var children: List<ConfigBuilder> = emptyList()

    fun add(out: Item, amountInMin: Double, count: Double, percent: Int, init: ConfigBuilder.() -> Unit = {}): ConfigBuilder {
        val child = ConfigBuilder(out, amountInMin, count, percent)
        child.init()
        children += child
        return child
    }

    fun build(): ConfigTree {
        val configTree = ConfigTree(out, amountInMin, input = children.map { it.build() })
        configTree.buildingCount = count
        configTree.buildingPercent = percent
        return configTree
    }
}


fun config(out: Item, amountInMin: Double, count: Double, percent: Int, init: ConfigBuilder.() -> Unit): ConfigTree {
    val root = ConfigBuilder(out, amountInMin, count, percent)
    root.init()
    return root.build()
}


class CalcTests : StringSpec({

    "testOreNormal" {
        buildTree(IronPlate, 15.0, emptyList()) shouldBeTree
                config(IronPlate, 15.0, 1.0, 100) {
                    add(IronIngot, 30.0, 1.0, 100) {
                        add(IronOre, 30.0, 1.0, 50)
                    }
                }
    }
    "testLimestoneNormal" {
        buildTree(Concrete, 15.0, emptyList()) shouldBeTree
                config(Concrete, 15.0, 1.0, 100) {
                    add(Limestone, 45.0, 1.0, 75)
                }
    }

    "testWireNormal" {
        buildTree(Wire, 45.0, emptyList()) shouldBeTree
                config(Wire, 45.0, 1.0, 100) {
                    add(CopperIngot, 15.0, 1.0, 50) {
                        add(CopperOre, 15.0, 1.0, 25)
                    }
                }
    }

    "testRotorReinforcedIronPlate" {
        val selected = listOf(ReinforcedIronPlate, Rotor, IronIngot)
        val map = mutableMapOf<Item, Double>()
        collectItems(ReinforcedIronPlate, 3.0, map, selected)
        collectItems(Rotor, 2.0, map, selected)
        map shouldBe mapOf(ReinforcedIronPlate to 3.0, Rotor to 2.0, IronIngot to 49.333333333333336)

    }
    "testCollectNothing" {
        val selected = listOf(IronPlate)
        val map = mutableMapOf<Item, Double>()
        collectItems(IronPlate, 10.0, map, selected)
        map shouldBe mapOf(IronPlate to 10.0)
    }

    "testCollectSingleItem" {
        val selected = listOf(IronIngot, IronPlate)
        val map = mutableMapOf<Item, Double>()
        collectItems(IronPlate, 10.0, map, selected)
        map shouldBe mapOf(IronPlate to 10.0, IronIngot to 20.0)
    }

    "testCollectDoubleItem" {
        val selected = listOf(IronIngot, IronRod, IronPlate)
        val map = mutableMapOf<Item, Double>()
        collectItems(IronPlate, 10.0, map, selected)
        collectItems(IronRod, 10.0, map, selected)
        map shouldBe mapOf(IronPlate to 10.0, IronRod to 10.0, IronIngot to 30.0)
    }
})
