module ChickenKiller.main {
    requires BotWithUs.api;
    requires BotWithUs.imgui;
    requires BotWithUs.navigation.api;
    requires org.slf4j;
    requires static xapi;

    provides net.botwithus.scripts.Script with botwithus.ChickenKiller;
}