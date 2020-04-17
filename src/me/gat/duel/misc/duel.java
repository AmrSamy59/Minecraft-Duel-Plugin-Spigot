package me.gat.duel.misc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.gat.duel.Main;

public class duel {
	public static Map<Player, Map<Player, String>> reqs = new HashMap<>();
	public static Map<Player, Player> duels = new HashMap<>();
	@SuppressWarnings("unused")
	private static Main plugin;
	
	public duel(Main plugin) {
		duel.plugin = plugin;
	}
	public static void sendDuelRequest(Player sender, Player target) {
		Map<Player, String> playerReqs;
		if(!(reqs.containsKey((Player) target))) {
			playerReqs = new HashMap<>();
			reqs.put((Player) target, playerReqs);
		}
		else {
			playerReqs = reqs.get((Player) target);
		}
		if(isPlayerInDuel(sender)) {
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.no_action")));
		}
		if(isPlayerInDuel(target)) {
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.target_in_duel").replace("<plr>", target.getDisplayName())));
		}
		else if(playerReqs.containsKey((Player) sender) || (!(sender instanceof Player) && getPlayerVersus(sender) != sender)) {
			sender.sendMessage(plugin.getConfig().getString("messages.already_sent_req"));
		}
		else {
			reqs.get((Player) target).put((Player) sender, (String) sender.getDisplayName());
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.on_req_sent").replace("<plr>", target.getDisplayName())));
			target.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.on_req_receive").replace("<plr>", sender.getDisplayName())));
		}
	}
	public static boolean rejectDuelRequest(Player rejecter, Player sender) {
		if((!(sender instanceof Player)) || (sender == null))
		{
			rejecter.sendMessage("Syntax: /reject <player_name>");
			return false;
		}
		if(isPlayerInDuel(rejecter)) {
			rejecter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.no_action")));
			return false;
		}
		if(isPlayerInDuel(sender)) {
			rejecter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.target_in_duel").replace("<plr>", sender.getDisplayName())));
		}
		if(!(reqs.containsKey((Player) rejecter))) {
			final Map<Player, String> playerReqsTemp = new HashMap<>();
			reqs.put((Player) rejecter, playerReqsTemp);
			rejecter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.req_error_noreq")));
			return false;
		}
		Map<Player, String> playerReqs = reqs.get((Player) rejecter);
		if(!(playerReqs.containsKey((Player) sender))) {
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.req_error_notreceived").replace("<plr>", sender.getDisplayName())));
			return false;
		}
		else {
			playerReqs.remove((Player) sender);
			rejecter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.on_req_reject").replace("<plr>", sender.getDisplayName())));
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.on_req_rejected").replace("<plr>", rejecter.getDisplayName())));
			return true;
		}
	}
	public static boolean acceptDuelRequest(Player accepter, Player sender) {
		if((!(sender instanceof Player)) || (sender == null))
		{
			accepter.sendMessage("Syntax: /accept <player_name>");
			return false;
		}
		if(isPlayerInDuel(accepter)) {
			accepter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.no_action")));
			return false;
		}
		if(isPlayerInDuel(sender)) {
			accepter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.target_in_duel").replace("<plr>", accepter.getDisplayName())));
		}
		if(!(reqs.containsKey((Player) accepter))) {
			final Map<Player, String> playerReqsTemp = new HashMap<>();
			reqs.put((Player) accepter, playerReqsTemp);
			accepter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.req_error_noreq")));
			return false;
		}
		Map<Player, String> playerReqs = reqs.get((Player) accepter);
		if(!(playerReqs.containsKey((Player) sender))) {
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.req_error_notreceived").replace("<plr>", sender.getDisplayName())));
			return false;
		}
		else {
			playerReqs.remove((Player) sender);
			duels.put((Player) sender, (Player) accepter);
			accepter.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.duel_start").replace("<plr>", sender.getDisplayName())));
			sender.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.duel_start").replace("<plr>", accepter.getDisplayName())));
			sender.teleport(accepter);
			return true;
		}
	}

	public static Player getPlayerVersus(Player plr) {
		 for(Map.Entry<Player, Player> entry : duels.entrySet()) {
			Player p1 = entry.getKey();
			Player p2 = entry.getValue();
			if(plr == p1) {
				return p2;
			}
			else if (plr == p2) {
				return p1;
			}
		 }
		 return plr;
	}
	public static boolean isPlayerInDuel(Player plr) {
		Player cvs = getPlayerVersus(plr);
		if(cvs instanceof Player) {
			if (cvs == plr) {
				return false;
			}
			else if(cvs != plr) {
				return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
	public static String getRequests(Player plr) {
		String returned = "";
		for(Map.Entry<Player, Map<Player, String>> entry : reqs.entrySet()) {
			Player cp = entry.getKey();
			Map<Player, String> map = entry.getValue();
			if(plr == cp) {
				for(Map.Entry<Player, String> entryy : map.entrySet()) {
					String name = entryy.getValue();
					returned = returned == "" ? returned + name : returned + ", " + name;
				 }
				break;
			}
		 }
		 return returned;
	}
	public static void cancelDuel(Player sender, boolean... reqsOnly) {
		Player target = getPlayerVersus(sender);
		boolean ronly = reqsOnly.length > 0 ? reqsOnly[0] : false;
		if(ronly == false) {
			duels.remove((Player) sender);
			duels.remove((Player) target);
		}
		if(reqs.containsKey((Player) target)) {
			Map<Player, String> playerReqs = reqs.get((Player) target);
			playerReqs.remove((Player) sender);
			reqs.remove((Player) sender);
		}
	}
	public static void win(Player winner) {
		Player loser = getPlayerVersus(winner);
		if(winner instanceof Player && loser instanceof Player) {
			if(winner != loser) {
				winner.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.on_win")));
				loser.sendMessage(misc.formatMessage(plugin.getConfig().getString("messages.on_lose")));
			}
		}
		cancelDuel(winner);
		cancelDuel(loser);
	}
}
