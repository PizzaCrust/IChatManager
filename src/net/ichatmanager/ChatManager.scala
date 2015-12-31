package net.ichatmanager

import java.io.{IOException, File}
import java.util

import net.ichatmanager.cmd.CommandHandler
import net.ichatmanager.data.DataConfiguration
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.{ConsoleCommandSender, CommandSender, Command}
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin

/**
  * Represents the plugin.
  * @author PizzaCrust
  */
class ChatManager extends JavaPlugin with Listener {
  var dataYaml: YamlConfiguration = null
  var data: DataConfiguration = null

  /**
    * Gets called when plugin is enabled
    */
  override def onEnable(){
    // DECLARE FILES
      val configurationFolder: File = new File(this.getDataFolder, "iChatManager")
      val configurationFile: File = new File(configurationFolder, "preferences.yml")
      val dataconfigFile: File = new File(configurationFolder, "data.yml")
      val configuration: YamlConfiguration = new YamlConfiguration
      val datayaml: YamlConfiguration = new YamlConfiguration
      val dataConfiguration: DataConfiguration = new DataConfiguration(dataconfigFile, datayaml)
    dataYaml = datayaml
    data = dataConfiguration
    // SETUP CONFIGURATION
    configuration.set("defaultChatColor", true)
    configuration.set("chatColorPermission", "chatmanager.chatcoloroverride")
    // CHECK IF FILE EXISTS AND LOAD CONFIG
    if(!configurationFolder.exists()){
      configurationFolder.mkdir()
      try {
        configurationFile.createNewFile()
        dataconfigFile.createNewFile()
      } catch {
        case e: IOException => e.printStackTrace();
      }
      try{
        configuration.save(configurationFile)
      } catch {
        case e: IOException => e.printStackTrace();
      }
      try {
        configuration.load(configurationFile)
        datayaml.load(dataconfigFile)
      } catch {
        case e: Exception => e.printStackTrace();
      }
    } else {
      try {
        configuration.load(configurationFile)
        datayaml.load(dataconfigFile)
      } catch {
        case e: Exception => e.printStackTrace();
      }
    }
    // REGISTER LISTENER
    Bukkit.getPluginManager.registerEvents(this, this)
    // TELL USER LOADING IS DONE
    getLogger.info("iChatManager has loaded!")
  }

  override def onDisable() {
    getLogger.info("iChatManager has been disabled!")
  }

  override def onCommand(sender: CommandSender, cmd: Command, label: String, args: Array[String]): Boolean = {
    val handler: CommandHandler = new CommandHandler(sender, cmd, args, this.getDataFolder)
    if(handler.handle()){
      true
    }
    false
  }

  @EventHandler
  def onPlayerChat(e: PlayerChatEvent) {
    if (dataYaml.get(".mainchannel") == null){
      return
    }
    e.setCancelled(true)
    val mainChannelName: String = dataYaml.getString(e.getPlayer().getUniqueId() + ".mainchannel")
    val playersInMainChannel: util.ArrayList[Player] = dataYaml.getString(mainChannelName + ".players").asInstanceOf[util.ArrayList[Player]]
    val listeners: util.ArrayList[Player] = new util.ArrayList[Player]()
    for (player: Player <- Bukkit.getOnlinePlayers) {
       if(!(dataYaml.get(player.getUniqueId + ".viewchannels") == null)){
         val channels: util.ArrayList[Player] = dataYaml.get(player.getUniqueId + ".viewchannels").asInstanceOf[util.ArrayList[Player]]
         for (name: String <- channels){
           if(name.equals(mainChannelName)){
             listeners.add(player)
           }
         }
       }
    }
    val everyone: util.ArrayList[Player] = new util.ArrayList[Player]()
    everyone.addAll(playersInMainChannel)
    everyone.addAll(listeners)
    for (player : Player <- everyone) {
      player.sendMessage("<" + player.getDisplayName + "> " + e.getMessage)
    }
  }
}
