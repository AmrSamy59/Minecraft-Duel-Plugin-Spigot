package me.gat.duel;

import org.bukkit.plugin.java.JavaPlugin;

import me.gat.duel.commands.commands;
import me.gat.duel.misc.duel;
import me.gat.duel.misc.events;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		new commands(this);
		new events(this);
		new duel(this);
	}
}
