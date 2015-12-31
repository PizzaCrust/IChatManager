package net.ichatmanager.data

import java.io.{IOException, File}

import org.bukkit.configuration.file.YamlConfiguration

/**
  * A configuration that saves data in YAML.
  * @author PizzaCrust
  */
class DataConfiguration(dataSaveTo: File, configuration: YamlConfiguration) {
  def save() {
    try{
      configuration.save(dataSaveTo)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def setAndSave(key: String, value: Object) {
    configuration.set(key, value);
    save();
  }

  def getConfiguration: YamlConfiguration ={
    configuration
  }
}
