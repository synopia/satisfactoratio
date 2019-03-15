package test

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
data class Recipe(val out: Item, val amount: Int, val ratePerMin: Double, val building: Building, val ingredient: List<Ingredient>) {
}
data class Belt(val name: String, val maxSpeed: Double, val image: String)
data class Building(val name: String, val inputs: Int, val power: Double, val image: String)


val MinerMk1 = Building("Miner Mk1", 1, 5.0, "minermk1")
val MinerMk2 = Building("Miner Mk2", 1, 12.0, "minermk2")
val Smelter = Building("Smelter", 1, 4.0, "smelter")
val Foundry = Building("Foundry", 2, 16.0, "foundry")
val Constructor = Building("Constructor", 1, 4.0, "constructor")
val Assembler = Building("Assembler", 2, 15.0, "assembler")
val Manufacturer = Building("Manufacturer", 4, 30.0, "")
val OilRefinery = Building("Oil Refinery", 1, 25.0, "oil_refinery")
val Resources = ItemGroup("Resources")
val IronOre = Item("Iron Ore", "iron_ore", Resources)
val CopperOre = Item("Copper Ore", "copper_ore", Resources)
val Limestone = Item("Limestone", "limestone", Resources)
val Coal = Item("Coal", "coal", Resources)
val CrudeOil = Item("Crude Oil", "crude_oil", Resources)
val CateriumOre = Item("Caterium Ore", "caterium_ore", Resources)
val RawQuartz = Item("Raw Quartz", "raw_quartz", Resources)
val Sulfur = Item("Sulfur", "sulfur", Resources)
val Bauxite = Item("Bauxite", "bauxite", Resources)
val SAMOre = Item("S.A.M. Ore", "sam_ore", Resources)
val Uranium = Item("Uranium", "sam_ore", Resources)

val EstablishedStage = ItemGroup("Established Stage")
val DevelopmentStage = ItemGroup("Development Stage")
val ExpansionStage = ItemGroup("Expansion Stage")

val IronIngot = Item("Iron Ingot", "iron_ingot", EstablishedStage)
val IronPlate = Item("Iron Plate", "iron_plate", EstablishedStage)
val IronRod = Item("Iron Rod", "iron_rod", EstablishedStage)
val CopperIngot = Item("Copper Ingot", "copper_ingot", EstablishedStage)
val Wire = Item("Wire", "wire", EstablishedStage)
val Cable = Item("Cable", "cable", EstablishedStage)
val Concrete = Item("Concrete", "concrete", EstablishedStage)
val Screw = Item("Screw", "screw", EstablishedStage)
val ReinforcedIronPlate = Item("Reinforced Iron Plate", "reinforced_iron_plate", EstablishedStage)
val Rotor = Item("Rotor", "rotor", EstablishedStage)
val ModularFrame = Item("Modular Frame", "modular_frame", EstablishedStage)

val SteelIngot = Item("Steel Ingot", "steel_ingot", DevelopmentStage)
val SteelPlate = Item("Steel Plate", "steel_plate", DevelopmentStage)
val SteelPipe = Item("Steel Pipe", "steel_pipe", DevelopmentStage)
val ReinforcedSteelPlate = Item("Reinforced Steel Plate", "reinforced_steel_plate", DevelopmentStage)
val Stator = Item("Stator", "stator", DevelopmentStage)
val Motor = Item("Motor", "motor", DevelopmentStage)
val HeavyModularFrame = Item("Heavy Modular Frame", "heavy_modular_frame", DevelopmentStage)

val Plastic = Item("Plastic", "plastic", ExpansionStage)
val Fuel = Item("Fuel", "fuel", ExpansionStage)
val Rubber = Item("Rubber", "rubber", ExpansionStage)
val CircuitBoard = Item("Circuit Board", "circuit_board", ExpansionStage)
val Computer = Item("Computer", "computer", ExpansionStage)

val ItemGroups = listOf(Resources, EstablishedStage, DevelopmentStage, ExpansionStage)

val BeltMk1 = Belt("Belt Mk1", 60.0, "beltmk1")
val BeltMk2 = Belt("Belt Mk2", 120.0, "beltmk2")
val BeltMk3 = Belt("Belt Mk3", 270.0, "beltmk3")

val Belts = listOf(BeltMk1, BeltMk2, BeltMk3)
val Recipes = listOf(
        Recipe(IronOre, 1, 60.0, MinerMk1, emptyList()),
        Recipe(CopperOre, 1, 60.0, MinerMk1, emptyList()),
        Recipe(Coal, 1, 60.0, MinerMk1, emptyList()),
        Recipe(Limestone, 1, 60.0, MinerMk1, emptyList()),
        Recipe(IronIngot, 1, 30.0, Smelter, listOf(Ingredient(IronOre, 1, 30.0))),
        Recipe(CopperIngot, 1, 30.0, Smelter, listOf(Ingredient(CopperOre, 1, 30.0))),
        Recipe(SteelIngot, 2, 30.0, Foundry, listOf(Ingredient(IronOre, 3, 45.0), Ingredient(Coal, 3, 45.0))),
        Recipe(IronPlate, 1, 15.0, Constructor, listOf(Ingredient(IronIngot, 2, 30.0))),
        Recipe(IronRod, 1, 15.0, Constructor, listOf(Ingredient(IronIngot, 1, 15.0))),
        Recipe(Wire, 3, 45.0, Constructor, listOf(Ingredient(CopperIngot, 1, 15.0))),
        Recipe(Cable, 1, 15.0, Constructor, listOf(Ingredient(Wire, 2, 30.0))),
        Recipe(Concrete, 1, 15.0, Constructor, listOf(Ingredient(Limestone, 3, 45.0))),
        Recipe(Screw, 6, 90.0, Constructor, listOf(Ingredient(IronRod, 1, 15.0))),
        Recipe(SteelPlate, 1, 15.0, Constructor, listOf(Ingredient(SteelIngot, 2, 30.0))),
        Recipe(SteelPipe, 1, 15.0, Constructor, listOf(Ingredient(SteelIngot, 1, 15.0))),
        Recipe(ReinforcedIronPlate, 1, 5.0, Assembler, listOf(Ingredient(IronPlate, 4, 20.0), Ingredient(Screw, 24, 120.0))),
        Recipe(Rotor, 1, 6.0, Assembler, listOf(Ingredient(IronRod, 3, 18.0), Ingredient(Screw, 22, 132.0))),
        Recipe(ModularFrame, 1, 4.0, Assembler, listOf(Ingredient(ReinforcedIronPlate, 3, 12.0), Ingredient(IronRod, 6, 24.0))),
        Recipe(ReinforcedSteelPlate, 1, 4.0, Assembler, listOf(Ingredient(SteelPlate, 6, 24.0), Ingredient(Screw, 36, 144.0))),
        Recipe(Stator, 1, 6.0, Assembler, listOf(Ingredient(SteelPipe, 3, 18.0), Ingredient(Wire, 10, 60.0))),
        Recipe(Motor, 1, 5.0, Assembler, listOf(Ingredient(Rotor, 2, 10.0), Ingredient(Stator, 2, 10.0))),
        Recipe(CircuitBoard, 1, 5.0, Assembler, listOf(Ingredient(Wire, 12, 60.0), Ingredient(Plastic, 6, 30.0))),
        Recipe(Rubber, 4, 30.0, OilRefinery, listOf(Ingredient(CrudeOil, 4, 30.0))),
        Recipe(Fuel, 5, 37.5, OilRefinery, listOf(Ingredient(CrudeOil, 8, 60.0))),
        Recipe(Plastic, 3, 22.5, OilRefinery, listOf(Ingredient(CrudeOil, 4, 30.0))),
        Recipe(HeavyModularFrame, 1, 2.0, Manufacturer, listOf(Ingredient(ModularFrame, 5, 10.0), Ingredient(SteelPipe, 15, 30.0), Ingredient(ReinforcedSteelPlate, 5, 10.0), Ingredient(Screw, 60, 120.0))),
        Recipe(Computer, 1, 1.875, Manufacturer, listOf(Ingredient(CircuitBoard, 5, 9.375), Ingredient(Cable, 12, 22.5), Ingredient(Plastic, 18, 33.75), Ingredient(Screw, 60, 112.5)))
)