package com.github.synopia.satisfactoratio

data class Item(
        val name: String,
        val group: ItemGroup
) {
    init {
        group.items += this
        all += this
    }

    companion object {
        val all = mutableListOf<Item>()
    }
}

data class ItemGroup(val name: String) {
    val items = mutableListOf<Item>()
}

data class Ingredient(val item: Item, val amount: Int, val rateInMin: Double)
data class Recipe(val out: Item, val amount: Int, val ratePerMin: Double, val ingredient: List<Ingredient>)

val Ores = ItemGroup("Ore")
val CrudeOil = Item("Crude Oil", Ores)
val IronOre = Item("Iron Ore", Ores)
val CopperOre = Item("Copper Ore", Ores)
val Coal = Item("Coal", Ores)
val Limestone = Item("Limestone", Ores)

val T1 = ItemGroup("T1")
val T2 = ItemGroup("T2")
val T3 = ItemGroup("T3")
val T4 = ItemGroup("T4")
val T5 = ItemGroup("T5")
val IronIngot = Item("Iron Ingot", T1)
val CopperIngot = Item("Copper Ingot", T1)
val IronPlate = Item("Iron Plate", T1)
val IronRod = Item("Iron Rod", T1)
val Wire = Item("Wire", T1)
val Cable = Item("Cable", T1)
val Concrete = Item("Concrete", T1)
val Screw = Item("Screw", T1)
val SteelPlate = Item("Steel Plate", T4)
val SteelPipe = Item("Steel Pipe", T4)
val ReinforcedIronPlate = Item("Reinforced Iron Plate", T2)
val Rotor = Item("Rotor", T2)
val ModulerFrame = Item("Moduler Frame", T4)
val ReinforcedSteelPlate = Item("Reinforced Steel Plate", T4)
val Stator = Item("Stator", T4)
val Motor = Item("Motor", T3)
val CircuitBoard = Item("Circuit Board", T5)
val Rubber = Item("Rubber", T5)
val Fuel = Item("Fuel", T5)
val Plastic = Item("Plastic", T5)
val HeavyModularFrame = Item("Heavy Modular Frame", T5)
val Computer = Item("Computer", T5)

val ItemGroups = listOf(Ores, T1, T2, T3, T4, T5)

val Recipes = listOf(
        Recipe(IronIngot, 1, 30.0, listOf(Ingredient(IronOre, 1, 30.0))),
        Recipe(CopperIngot, 1, 30.0, listOf(Ingredient(CopperOre, 1, 30.0))),
        Recipe(IronPlate, 1, 15.0, listOf(Ingredient(IronIngot, 2, 30.0))),
        Recipe(IronRod, 1, 15.0, listOf(Ingredient(IronIngot, 1, 15.0))),
        Recipe(Wire, 3, 45.0, listOf(Ingredient(CopperIngot, 1, 15.0))),
        Recipe(Cable, 1, 15.0, listOf(Ingredient(Wire, 2, 30.0))),
        Recipe(Concrete, 1, 60.0, listOf(Ingredient(Limestone, 3, 180.0))),
        Recipe(Screw, 6, 90.0, listOf(Ingredient(IronRod, 1, 15.0))),
        Recipe(ReinforcedIronPlate, 1, 5.0, listOf(Ingredient(IronPlate, 4, 20.0), Ingredient(Screw, 24, 120.0))),
        Recipe(Rotor, 1, 5.0, listOf(Ingredient(IronRod, 3, 18.0), Ingredient(Screw, 22, 132.0))),
        Recipe(ModulerFrame, 1, 4.0, listOf(Ingredient(ReinforcedIronPlate, 3, 12.0), Ingredient(IronRod, 6, 24.0)))
)