package com.github.synopia.satisfactoratio

data class Item(
        val name: String,
        val image: String,
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


fun buildTree(item: Item, amountInMin: Double, map: MutableMap<Item, ConfigTree>) {
    val recipe = Recipes.find { it.out == item }
    val tree = if (recipe != null) {
        val f = amountInMin / recipe.amount / recipe.ratePerMin
        val i = recipe.ingredient.map {
            buildTreeRec(it.item, it.rateInMin * f, map)
        }
        ConfigTree(item, amountInMin, i)
    } else {
        ConfigTree(item, amountInMin, emptyList())
    }
    map[item] = tree
}

private fun buildTreeRec(item: Item, amountInMin: Double, map: MutableMap<Item, ConfigTree>): ConfigTree {
    if (map.containsKey(item)) {
        val tree = ConfigTree(item, amountInMin + map[item]!!.amountInMin, emptyList())
        map[item] = tree
        return tree
    } else {
        val recipe = Recipes.find { it.out == item }
        if (recipe != null) {
            val f = amountInMin / recipe.amount / recipe.ratePerMin
            val i = recipe.ingredient.map {
                buildTreeRec(it.item, it.rateInMin * f, map)
            }
            return ConfigTree(item, amountInMin, i)
        } else {
            return ConfigTree(item, amountInMin, emptyList())
        }
    }
}


val Resources = ItemGroup("Resources")
val IronOre = Item("Iron Ore", "iron_ore.png", Resources)
val CopperOre = Item("Copper Ore", "copper_ore.png", Resources)
val Limestone = Item("Limestone", "limestone.png", Resources)
val Coal = Item("Coal", "coal.png", Resources)
val CrudeOil = Item("Crude Oil", "crude_oil.png", Resources)
val CateriumOre = Item("Caterium Ore", "caterium_ore.png", Resources)
val RawQuartz = Item("Raw Quartz", "raw_quartz.png", Resources)
val Sulfur = Item("Sulfur", "sulfur.png", Resources)
val Bauxite = Item("Bauxite", "bauxite.png", Resources)
val SAMOre = Item("S.A.M. Ore", "sam_ore.png", Resources)
val Uranium = Item("Uranium", "sam_ore.png", Resources)

val EstablishedStage = ItemGroup("Established Stage")
val DevelopmentStage = ItemGroup("Development Stage")
val ExpansionStage = ItemGroup("Expansion Stage")

val IronIngot = Item("Iron Ingot", "iron_ingot.png", EstablishedStage)
val IronPlate = Item("Iron Plate", "iron_plate.png", EstablishedStage)
val IronRod = Item("Iron Rod", "iron_rod.png", EstablishedStage)
val CopperIngot = Item("Copper Ingot", "copper_ingot.png", EstablishedStage)
val Wire = Item("Wire", "wire.png", EstablishedStage)
val Cable = Item("Cable", "cable.png", EstablishedStage)
val Concrete = Item("Concrete", "concrete.png", EstablishedStage)
val Screw = Item("Screw", "screw.png", EstablishedStage)
val ReinforcedIronPlate = Item("Reinforced Iron Plate", "reinforced_iron_plate.png", EstablishedStage)
val Rotor = Item("Rotor", "rotor.png", EstablishedStage)
val ModularFrame = Item("Modular Frame", "modular_frame.png", EstablishedStage)

val SteelIngot = Item("Steel Ingot", "steel_ingot.png", DevelopmentStage)
val SteelPlate = Item("Steel Plate", "steel_plate.png", DevelopmentStage)
val SteelPipe = Item("Steel Pipe", "steel_pipe.png", DevelopmentStage)
val ReinforcedSteelPlate = Item("Reinforced Steel Plate", "reinforced_steel_plate.png", DevelopmentStage)
val Stator = Item("Stator", "stator.png", DevelopmentStage)
val Motor = Item("Motor", "motor.png", DevelopmentStage)
val HeavyModularFrame = Item("Heavy Modular Frame", "heavy_modular_frame.png", DevelopmentStage)

val Plastic = Item("Plastic", "plastic.png", ExpansionStage)
val Fuel = Item("Fuel", "fuel.png", ExpansionStage)
val Rubber = Item("Rubber", "rubber.png", ExpansionStage)
val CircuitBoard = Item("Circuit Board", "circuit_board.png", ExpansionStage)
val Computer = Item("Computer", "computer.png", ExpansionStage)

val ItemGroups = listOf(Resources, EstablishedStage, DevelopmentStage, ExpansionStage)

val Recipes = listOf(
        Recipe(IronIngot, 1, 30.0, listOf(Ingredient(IronOre, 1, 30.0))),
        Recipe(CopperIngot, 1, 30.0, listOf(Ingredient(CopperOre, 1, 30.0))),
        Recipe(IronPlate, 1, 15.0, listOf(Ingredient(IronIngot, 2, 30.0))),
        Recipe(IronRod, 1, 15.0, listOf(Ingredient(IronIngot, 1, 15.0))),
        Recipe(Wire, 3, 45.0, listOf(Ingredient(CopperIngot, 1, 15.0))),
        Recipe(Cable, 1, 15.0, listOf(Ingredient(Wire, 2, 30.0))),
        Recipe(Concrete, 1, 60.0, listOf(Ingredient(Limestone, 3, 180.0))),
        Recipe(Screw, 6, 90.0, listOf(Ingredient(IronRod, 1, 15.0))),
        Recipe(SteelPlate, 1, 15.0, listOf(Ingredient(SteelIngot, 2, 30.0))),
        Recipe(SteelPipe, 1, 15.0, listOf(Ingredient(SteelIngot, 1, 15.0))),
        Recipe(ReinforcedIronPlate, 1, 5.0, listOf(Ingredient(IronPlate, 4, 20.0), Ingredient(Screw, 24, 120.0))),
        Recipe(Rotor, 1, 6.0, listOf(Ingredient(IronRod, 3, 18.0), Ingredient(Screw, 22, 132.0))),
        Recipe(ModularFrame, 1, 4.0, listOf(Ingredient(ReinforcedIronPlate, 3, 12.0), Ingredient(IronRod, 6, 24.0))),
        Recipe(ReinforcedSteelPlate, 1, 4.0, listOf(Ingredient(SteelPlate, 6, 24.0), Ingredient(Screw, 36, 144.0))),
        Recipe(Stator, 1, 6.0, listOf(Ingredient(SteelPipe, 3, 18.0), Ingredient(Wire, 10, 60.0))),
        Recipe(Motor, 1, 5.0, listOf(Ingredient(Rotor, 2, 10.0), Ingredient(Stator, 2, 10.0))),
        Recipe(CircuitBoard, 1, 5.0, listOf(Ingredient(Wire, 12, 60.0), Ingredient(Plastic, 6, 30.0))),
        Recipe(Rubber, 4, 30.0, listOf(Ingredient(CrudeOil, 4, 30.0))),
        Recipe(Fuel, 5, 37.5, listOf(Ingredient(CrudeOil, 8, 60.0))),
        Recipe(Plastic, 3, 22.5, listOf(Ingredient(CrudeOil, 4, 30.0))),
        Recipe(HeavyModularFrame, 1, 2.0, listOf(Ingredient(ModularFrame, 5, 10.0), Ingredient(SteelPipe, 15, 30.0), Ingredient(ReinforcedSteelPlate, 5, 10.0), Ingredient(Screw, 60, 120.0))),
        Recipe(Computer, 1, 1.875, listOf(Ingredient(CircuitBoard, 5, 9.375), Ingredient(Cable, 12, 22.5), Ingredient(Plastic, 18, 33.75), Ingredient(Screw, 60, 112.5)))
)