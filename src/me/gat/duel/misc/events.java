package me.gat.duel.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.gat.duel.Main;

public class events implements Listener {
	@SuppressWarnings("unused")
	private Main plugin;
	
	public events(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		final Player plr = e.getPlayer();
		duel.cancelDuel(plr);
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		final Player plr = e.getPlayer();
		duel.cancelDuel(plr);
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity().getKiller() instanceof Player) {
			Player plr = e.getEntity();
			Player killer = plr.getKiller();
			Player vs = duel.getPlayerVersus(plr);
			if(killer == vs && killer != plr) {
				duel.win(killer);
			}
		}
	}
}
