package com.github.unchama.player;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.unchama.event.GiganticPlayerAvailableEvent;
import com.github.unchama.gigantic.Gigantic;
import com.github.unchama.gigantic.PlayerManager;
import com.github.unchama.player.achievement.AchievementManager;
import com.github.unchama.player.build.BuildLevelManager;
import com.github.unchama.player.build.BuildManager;
import com.github.unchama.player.buildskill.BuildSkillManager;
import com.github.unchama.player.dimensionalinventory.DimensionalInventoryManager;
import com.github.unchama.player.donate.DonateDataManager;
import com.github.unchama.player.exp.ExpManager;
import com.github.unchama.player.fishing.FishingManager;
import com.github.unchama.player.fishinglevel.FishingLevelManager;
import com.github.unchama.player.fly.FlyManager;
import com.github.unchama.player.gacha.PlayerGachaManager;
import com.github.unchama.player.gachastack.GachaStackManager;
import com.github.unchama.player.gigantic.GiganticManager;
import com.github.unchama.player.gravity.GravityManager;
import com.github.unchama.player.gui.GuiStatusManager;
import com.github.unchama.player.home.HomeManager;
import com.github.unchama.player.home.HomeProtectManager;
import com.github.unchama.player.huntinglevel.HuntingLevelManager;
import com.github.unchama.player.huntingpoint.HuntingPointManager;
import com.github.unchama.player.mana.ManaManager;
import com.github.unchama.player.menu.PlayerMenuManager;
import com.github.unchama.player.mineblock.MineBlockManager;
import com.github.unchama.player.mineblock.SkillBreakBlockManager;
import com.github.unchama.player.minestack.MineStackManager;
import com.github.unchama.player.moduler.DataManager;
import com.github.unchama.player.moduler.Finalizable;
import com.github.unchama.player.moduler.Initializable;
import com.github.unchama.player.moduler.UsingSql;
import com.github.unchama.player.point.GiganticPointManager;
import com.github.unchama.player.point.UnchamaPointManager;
import com.github.unchama.player.presentbox.PresentBoxManager;
import com.github.unchama.player.region.RegionManager;
import com.github.unchama.player.seichilevel.SeichiLevelManager;
import com.github.unchama.player.seichiskill.SkillEffectManager;
import com.github.unchama.player.seichiskill.active.CondensationManager;
import com.github.unchama.player.seichiskill.active.ExplosionManager;
import com.github.unchama.player.seichiskill.active.FairyAegisManager;
import com.github.unchama.player.seichiskill.active.MagicDriveManager;
import com.github.unchama.player.seichiskill.active.RuinFieldManager;
import com.github.unchama.player.seichiskill.passive.manarecovery.ManaRecoveryManager;
import com.github.unchama.player.seichiskill.passive.mineboost.MineBoostManager;
import com.github.unchama.player.seichiskill.passive.securebreak.SecureBreakManager;
import com.github.unchama.player.seichiskill.passive.skywalk.SkyWalkManager;
import com.github.unchama.player.settings.PlayerSettingsManager;
import com.github.unchama.player.sidebar.SideBarManager;
import com.github.unchama.player.time.PlayerTimeManager;
import com.github.unchama.player.toolpouch.ToolPouchManager;
import com.github.unchama.sql.Sql;
import com.github.unchama.util.ClassUtil;
import com.github.unchama.util.Converter;

/**各プレイヤーにデータを保存したい時はここにマネージャーを追加します．
 *
 * @author tar0ss
 *
 */
public class GiganticPlayer {

	public static enum ManagerType {
		/**
		 * Managerを追加するときはここに書く．
		 */
		SIDEBAR(SideBarManager.class),
		GIGANTIC(GiganticManager.class),
		SETTINGS(PlayerSettingsManager.class),
		GUISTATUS(GuiStatusManager.class),
		MINEBLOCK(MineBlockManager.class),
		SEICHILEVLE(SeichiLevelManager.class),
		MANA(ManaManager.class),
		MANARECOVERY(ManaRecoveryManager.class),
		MENU(PlayerMenuManager.class),
		BUILD(BuildManager.class),
		PLAYERGACHA(PlayerGachaManager.class),
		MINEBOOST(MineBoostManager.class),
		MINESTACK(MineStackManager.class),
		TOOLPOUCH(ToolPouchManager.class),
		EXPLOSION(ExplosionManager.class),
		MAGICDRIVE(MagicDriveManager.class),
		CONDENSATION(CondensationManager.class),
		RUINFIELD(RuinFieldManager.class),
		FAIRYAEGIS(FairyAegisManager.class),
		GRAVITY(GravityManager.class),
		SECUREBREAK(SecureBreakManager.class),
		SKYWALK(SkyWalkManager.class),
		FLY(FlyManager.class),
		REGION(RegionManager.class),
		PLAYERTIME(PlayerTimeManager.class),
		HUNTINGPOINT(HuntingPointManager.class),
		HUNTINGLEVEL(HuntingLevelManager.class),
		BUILDLEVEL(BuildLevelManager.class),
		BUILDSKILL(BuildSkillManager.class),
		DIMENSIONALINVENTORY(DimensionalInventoryManager.class),
		PRESENTBOX(PresentBoxManager.class),
		HOMEPROTECT(HomeProtectManager.class),
		HOME(HomeManager.class),
		DONATEDATA(DonateDataManager.class),
		GACHASTACK(GachaStackManager.class),
		FISHINGLEVEL(FishingLevelManager.class),
		FISHING(FishingManager.class),
		EFFECT(SkillEffectManager.class),
		UNCHAMAPOINT(UnchamaPointManager.class),
		GIGANTICPOINT(GiganticPointManager.class),
		ACHIEVEMENT(AchievementManager.class),
		SKILLBREAKBLOCK(SkillBreakBlockManager.class),
		EXP(ExpManager.class),
		;

		private Class<? extends DataManager> managerClass;

		ManagerType(Class<? extends DataManager> managerClass) {
			this.managerClass = managerClass;
		}

		public Class<? extends DataManager> getManagerClass() {
			return managerClass;
		}

	}

	Gigantic plugin = Gigantic.plugin;
	Sql sql = Gigantic.sql;

	public final String name;
	public final UUID uuid;
	private GiganticStatus gs;
	// Player型は突然消えることがあるため保持しない

	private LinkedHashMap<Class<? extends DataManager>, DataManager> managermap = new LinkedHashMap<Class<? extends DataManager>, DataManager>();

	public GiganticPlayer(Player player) {
		this.name = Converter.getName(player);
		this.uuid = player.getUniqueId();
		this.setStatus(GiganticStatus.LODING);
		try {
			for (ManagerType mt : ManagerType.values()) {
				this.managermap.put(mt.getManagerClass(), mt.getManagerClass().getConstructor(GiganticPlayer.class)
						.newInstance(this));
			}

			//Sqlを使用しないクラスに関してloadedFlagをtrueに変更
			for (Class<? extends DataManager> mc : this.managermap.keySet()) {
				if (!ClassUtil.isImplemented(mc, UsingSql.class)) {
					mc.getMethod("setLoaded", Boolean.class).invoke(this.managermap.get(mc), true);
				}
			}
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to create new Instance of player:" + this.name);
			e.printStackTrace();
			this.setStatus(GiganticStatus.ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends DataManager> T getManager(Class<T> type) {
		return (T) managermap.get(type);
	}

	public boolean isloaded() {
		try {
			for (Class<? extends DataManager> mc : this.managermap.keySet()) {
				if (ClassUtil.isImplemented(mc, UsingSql.class)) {
					boolean loaded = (Boolean) mc.getMethod("isLoaded").invoke(this.managermap.get(mc));
					if (loaded == false) {
						return false;
					}
				}
			}
			return true;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException e) {
			plugin.getLogger().warning("Failed to check \"isloaded\" of player:" + this.name);
			e.printStackTrace();
			return false;
		}

	}

	public boolean isOffline() {
		return Gigantic.plugin.getServer().getPlayer(uuid) == null;
	}

	public void init() {
		Player player = PlayerManager.getPlayer(this);
		this.setStatus(GiganticStatus.INITIALIZE);

		player.sendMessage(ChatColor.GREEN
				+ "データ更新中");

		try {
			for (Class<? extends DataManager> mc : this.managermap.keySet()) {
				if (ClassUtil.isImplemented(mc, Initializable.class)) {
					mc.getMethod("init").invoke(this.managermap.get(mc));
				}
			}
			Bukkit.getServer().getPluginManager().callEvent(new GiganticPlayerAvailableEvent(this));
			this.setStatus(GiganticStatus.AVAILABLE);
			player.sendMessage(ChatColor.GREEN
					+ "ロードが完了しました");
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException e) {
			plugin.getLogger().warning("Failed to run init() of player:" + this.name);
			e.printStackTrace();
			this.setStatus(GiganticStatus.ERROR);
		}

	}

	public void fin() {
		this.setStatus(GiganticStatus.FINALIZE);
		try {
			for (Class<? extends DataManager> mc : this.managermap.keySet()) {
				if (ClassUtil.isImplemented(mc, Finalizable.class)) {
					mc.getMethod("fin").invoke(this.managermap.get(mc));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | NullPointerException e) {
			plugin.getLogger().warning("Failed to run fin() of player:" + this.name);
			e.printStackTrace();
			this.setStatus(GiganticStatus.ERROR);
		}
	}

	/**プレイヤーデータを保存します．
	 * このメソッドをプレイヤーのログアウト時に呼び出す場合は，loginflagをfalseにしてください．
	 * 定期セーブ時に呼び出す場合はloginflagをtrueにしてください．
	 *
	 * @param loginflag:
	 */
	public void save(boolean loginflag) {
		if (!loginflag) {
			this.setStatus(GiganticStatus.SAVING);
		}
		for (Class<? extends DataManager> mc : this.managermap.keySet()) {
			if (ClassUtil.isImplemented(mc, UsingSql.class)) {
				try {
					mc.getMethod("save", Boolean.class).invoke(this.managermap.get(mc), loginflag);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| SecurityException | NullPointerException e) {
					plugin.getLogger().warning("Failed to save data of player:" + this.name);
					e.printStackTrace();
					this.setStatus(GiganticStatus.ERROR);
				}
			}
		}
	}

	/**プレイヤーデータのステータスをセットします．
	 *
	 * @param gs
	 */
	private void setStatus(GiganticStatus gs) {
		this.gs = gs;
	}

	/**プレイヤーデータのステータスを取得します．
	 *
	 * @return ステータス
	 */
	public GiganticStatus getStatus() {
		return this.gs;
	}
}
