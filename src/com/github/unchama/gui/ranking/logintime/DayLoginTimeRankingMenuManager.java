/**
 *
 */
package com.github.unchama.gui.ranking.logintime;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import com.github.unchama.gui.ranking.RankingMenuManager;
import com.github.unchama.sql.moduler.RankingTableManager.TimeType;
import com.github.unchama.util.Converter;
import com.github.unchama.util.Util;

/**
 * @author ten_niti
 *
 */
public final class DayLoginTimeRankingMenuManager extends RankingMenuManager {

	@Override
	protected String getLore(double value) {
		return "総接続時間:" + Converter.toTimeString((long)Util.Decimal(value));
	}


	@Override
	public String getInventoryName(Player player) {
		return ChatColor.BLUE + "日間接続時間ﾗﾝｷﾝｸﾞ";
	}

	@Override
	protected TimeType getTimeType() {
		return TimeType.DAY;
	}

}
