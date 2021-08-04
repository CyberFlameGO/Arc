plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
}

bukkit {
    main = "me.notom3ga.arc.bukkit.ArcBukkit"
    apiVersion = "1.17"
    authors = listOf("notOM3GA")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}
