package net.ichatmanager.cmd

import java.io.File
import java.util

import net.ichatmanager.data.DataConfiguration
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.{ConsoleCommandSender, Command, CommandSender}
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

/**
  * Handles commands
  */
class CommandHandler(sender: CommandSender, cmd: Command, args: Array[String], dataFolder: File) {
  def handle(): Boolean ={
    /** POSSIBLY NEEDED VALUES **/
    val configurationFolder: File = new File(dataFolder, "iChatManager")
    val configurationFile: File = new File(configurationFolder, "preferences.yml")
    val dataconfigFile: File = new File(configurationFolder, "data.yml")
    val configuration: YamlConfiguration = new YamlConfiguration
    val datayaml: YamlConfiguration = new YamlConfiguration
    val dataConfiguration: DataConfiguration = new DataConfiguration(dataconfigFile, datayaml)
    val dataYaml: YamlConfiguration = datayaml
    val data = dataConfiguration

    /** COMMANDS **/
    if(cmd.getName.equalsIgnoreCase("addchannel")){
    if(!sender.hasPermission("chatmanager.manage")){
    sender.sendMessage(ChatColor.RED + "Insufficent permissions!")
    true
  }
    if(args.length < 1){
    sender.sendMessage(ChatColor.GOLD + "Usage: /addchannel <channelname>")
    true
  }
    if(args.length > 1){
    sender.sendMessage(ChatColor.GOLD + "Usage: /addchannel <channelname>")
    true
  }
    if(sender.isInstanceOf[ConsoleCommandSender]){
    sender.sendMessage(ChatColor.GOLD + "Your entity is incompatible with this command.")
    true
  }
    val channelName: String = args(0)
    if(!(dataYaml.getString(channelName + ".verified") == null)){
    sender.sendMessage(ChatColor.GOLD + "Sorry, that channel name has already been taken.")
    true
  }
    val player: Player = sender.asInstanceOf[Player]
    data.setAndSave(channelName + ".verified", "yes")
    val players: util.ArrayList[Player] = new util.ArrayList[Player]()
    players.add(player)
    data.setAndSave(channelName + ".players", players)
    var channelNames: util.ArrayList[String] = new util.ArrayList[String]()
    if (!(dataYaml.getString(player.getUniqueId + ".viewchannels") == null)){
    channelNames = dataYaml.get(player.getUniqueId + ".viewchannels").asInstanceOf[util.ArrayList[String]]
  }
    channelNames.add(channelName)
    data.setAndSave(player.getUniqueId + ".mainchannel", channelName)
    data.setAndSave(player.getUniqueId + ".viewchannels", channelNames)
    sender.sendMessage(ChatColor.GOLD + "Your channel called " + channelName + " has been created!")
    true
  }
    if(cmd.getName.equalsIgnoreCase("seechannel")){
    if(!sender.hasPermission("chatmanager.personal")){
    sender.sendMessage(ChatColor.RED + "Insufficent permissions!")
    true
  }
    if(args.length < 1){
    sender.sendMessage(ChatColor.GOLD + "Usage: /seechannel <channelname>")
    true
  }
    if(args.length > 1){
    sender.sendMessage(ChatColor.GOLD + "Usage: /seechannel <channelname>")
    true
  }
    if(sender.isInstanceOf[ConsoleCommandSender]){
    sender.sendMessage(ChatColor.GOLD + "Your entity is incompatible with this command.")
    true
  }
    if(dataYaml.getString(args(0) + ".verified") == null){
    sender.sendMessage(ChatColor.GOLD + "That channel could not be found!")
    true
  }
    val player: Player = sender.asInstanceOf[Player]
    if(dataYaml.get(player.getUniqueId + ".viewchannels") == null){
    val channels: util.ArrayList[String] =  new util.ArrayList[String]
    channels.add(args(0))
    sender.sendMessage("Channel " + args(0) + " have been added to your subscriptions.")
    true
  }
    val channels: util.ArrayList[String] = dataYaml.get(player.getUniqueId + ".viewchannels").asInstanceOf[util.ArrayList[String]]
    for (channelName: String <- channels){
    if(channelName.equals(args(0))){
    sender.sendMessage(ChatColor.GOLD + "Sorry, that channel is already in your subscriptions.")
    true
  }
  }
    channels.add(args(0))
    sender.sendMessage("Channel " + args(0) + " have been added to your subscriptions.")
    true
  }
    if(cmd.getName.equalsIgnoreCase("subscribedchannels")){
    if(!sender.hasPermission("chatmanager.personal")){
    sender.sendMessage(ChatColor.RED + "Insufficent permissions!")
    true
  }
    if(sender.isInstanceOf[ConsoleCommandSender]){
    sender.sendMessage(ChatColor.GOLD + "Your entity is incompatible with this command.")
    true
  }
    val player: Player = sender.asInstanceOf[Player]
    if(dataYaml.get(player.getUniqueId + ".viewchannels") == null && dataYaml.get(player.getUniqueId + ".mainchannel") == null){
    sender.sendMessage(ChatColor.GOLD + "You are not subscribed to any channels!")
    true
  }
    if(dataYaml.get(player.getUniqueId + ".mainchannel") == null){
    sender.sendMessage(ChatColor.RED + "You have not set a main channel yet!")
  } else {
    sender.sendMessage(ChatColor.GOLD + "Your current main channel is channel " + dataYaml.getString(player.getUniqueId + ".mainchannel"))
  }
    if(dataYaml.get(player.getUniqueId + ".viewchannels") == null){
    sender.sendMessage(ChatColor.RED + "You have not subscribed to any view channels yet!")
  } else {
    val viewChannels: util.ArrayList[String] = dataYaml.get(player.getUniqueId + ".viewchannels").asInstanceOf[util.ArrayList[String]]
    val builder: StringBuilder = new StringBuilder
    for (name: String <- viewChannels){
    builder.append(name + ", ")
  }
    val subscriptions: String = "Channels: " + builder.toString
    sender.sendMessage(subscriptions)
  }
    true
  }
    if(cmd.getName.equalsIgnoreCase("joinchannel")){
    if(!sender.hasPermission("chatmanager.personal")){
    sender.sendMessage(ChatColor.RED + "Insufficent permissions!")
    true
  }
    if(sender.isInstanceOf[ConsoleCommandSender]){
    sender.sendMessage(ChatColor.RED + "Incompatible entity!")
    true
  }
    if(args.length < 1){
    sender.sendMessage(ChatColor.GOLD + "Usage: /joinchannel <channelname>")
    true
  }
    if(args.length > 1){
    sender.sendMessage(ChatColor.GOLD + "Usage: /joinchannel <channelname>")
    true
  }
    val player: Player = sender.asInstanceOf[Player]
    if(dataYaml.get(args(0) + ".verified") == null) {
    sender.sendMessage(ChatColor.GOLD + "That channel could not be found!")
    true
  }
    data.setAndSave(player.getUniqueId + ".mainchannel", args(0))
    sender.sendMessage(ChatColor.GOLD + "You have joined channel " + args(0) + " successfully!")
    true
  }
    false
  }
}
