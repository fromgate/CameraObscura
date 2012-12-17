/*  
 *  CameraObscura, Minecraft bukkit plugin
 *  (c)2012, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/camera-obscura/
 *    
 *  This file is part of NoobProtector.
 *  
 *  CameraObscura is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CameraObscura is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CameraObscura.  If not, see <http://www.gnorg/licenses/>.
 * 
 */
package fromgate.obscura;

import java.io.File;
import java.io.IOException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * TODO
 * 
 * - Авто переделка удаленных карт в фотобумагу?   
 * - Авто переименование карт после смены название (и соответственно - удаления!)
 * - Возможность перерисовывать/перефотографировать все карты при наличии пермишена
 * 
 */

/* 
 * Permissions:
 * camera-obscura.owner.all - owner of all pictures (maps)
 * camera-obscura.config
 * camera-obscura.givecamera
 * camera-obscura.givepaper
 * camera-obscura.pixelart
 * camera-obscura.remove
 * camera-obscura.rename
 * camera-obscura.allowcopy
 * camera-obscura.owner
 * camera-obscura.photo
 * camera-obscura.repaint
 * camera-obscura.handy.use
 * camera-obscura.tripod-camera.use
 * camera-obscura.set-sign
 * camera-obscura.tripod-camera.build
 * camera-obscura.fireproof-wool
 * 
 */

public class Obscura extends JavaPlugin {
	//Конфигурация
	boolean version_check = true;
	String language = "english";
	boolean language_save = false;
	
	private String dir_album="album";
	private String dir_images="images";
	private String dir_backgrounds = "backgrounds";
	private String dir_skins = "skins";
	
	/////////////////////////////////////////////////
	int brush_id = Material.FEATHER.getId();
	int photopaper_id = 339;    //395 - empty_map, 339 - paper
	short photopaper_data = 1;    //395 - empty_map, 339 - paper
	int camera_id = 347;     //по умолчанию, фотоаппарат - это часы с data = 1
	short camera_data = 1;
	boolean reusedeleted = true;
	boolean use_recipes = true;
	boolean obscura_drop = true; //будет ли камера на штативе выбрасывать фотоаппарат при разрушении?
	int minpixelart = 16;
	String default_background = "default"; // "random" - для случайного, реализовать
	int picsperowner = 15;
	boolean burnpaintedwool = false;
	
	float focus_1 = 2.0f;
	float focus_2 = 3.8f;
	
	String steeve = "default_skin.png";
	String skinurl = "http://s3.amazonaws.com/MinecraftSkins/";
	
	protected Economy economy = null;
	boolean vault_eco = false;
	
	String d_images;
	String d_album; 
	String d_skins;
	String d_backgrounds;
	
	
	COUtil u;
	COImageCraft ic;
	COAlbum album;
	COCmd cmd;
	COListener l;
	CORenderHistory rh;
	
	
	
	@Override
	public void onEnable() {
		loadCfg();
		saveCfg();
		
		d_images = getDataFolder()+File.separator+dir_images+File.separator;
		d_album = getDataFolder()+File.separator+dir_album+File.separator;
		d_skins = getDataFolder()+File.separator+dir_skins+File.separator;
		d_backgrounds = getDataFolder()+File.separator+dir_backgrounds+File.separator;
		
		File dir = new File (d_images);
		if (!dir.exists()) dir.mkdirs();
		dir = new File (d_album);
		if (!dir.exists()) dir.mkdirs();
		dir = new File (d_backgrounds);
		if (!dir.exists()) dir.mkdirs();
		dir = new File (d_skins);
		if (!dir.exists()) dir.mkdirs();
		
		u = new COUtil (this, version_check, language_save, language, "camera-obscura", "CameraObscura", "photo", "&3[CO]&f ");
		rh = new CORenderHistory ();
		ic = new COImageCraft (this);
		album = new COAlbum (this);
		l = new COListener (this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(l, this);
		cmd = new COCmd (this);
		getCommand("photo").setExecutor(cmd);
		if (use_recipes) COCamera.initRecipes(this);
		vault_eco = COCamera.setupEconomy(this);
		if (!vault_eco) u.log("Connection to Vault failed!");
		
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}
	}
	
	
	public void saveCfg(){
		 getConfig().set("general.check-updates",version_check);
		 getConfig().set("general.language",language);
		 getConfig().set("general.language-save",language_save);
		 getConfig().set("items.brush-id",brush_id);
		 getConfig().set("items.photo-paper.id",photopaper_id);
		 getConfig().set("items.photo-paper.data",photopaper_data);
		 getConfig().set("items.photo-camera.id",camera_id);
		 getConfig().set("items.photo-camera.data",camera_data);
		 getConfig().set("items.use-recipes", use_recipes);
		 getConfig().set("items.obscura-drop", obscura_drop);
		 getConfig().set("pictures.reuse-deleted-maps", reusedeleted );
		 getConfig().set("pictures.minimal-pixel-art-size",minpixelart);
		 getConfig().set("pictures.default-background",default_background);
		 getConfig().set("pictures.pictures-per-owner",picsperowner);
		 getConfig().set("pictures.burn-pixel-art-wool", burnpaintedwool);
		 getConfig().set("pictures.default-skin",steeve);
		 getConfig().set("pictures.skin-url",skinurl);
		 saveConfig();
	}
	
	public void loadCfg(){
		version_check = getConfig().getBoolean("general.check-updates",true);
		language = getConfig().getString("general.language","english");
		language_save = getConfig().getBoolean("general.language-save",false);
		brush_id = getConfig().getInt("items.brush-id",Material.FEATHER.getId());
		photopaper_id = getConfig().getInt("items.photo-paper.id",339);
		photopaper_data = (short) getConfig().getInt("items.photo-paper.data",1);
		camera_id = getConfig().getInt("items.photo-camera.id",347);
		camera_data = (short) getConfig().getInt("items.photo-camera.data",1);
		use_recipes = getConfig().getBoolean ("items.use-recipes", true);
		obscura_drop = getConfig().getBoolean ("items.obscura-drop", true);
		reusedeleted  = getConfig().getBoolean ("pictures.reuse-deleted-maps", true);
		minpixelart = getConfig().getInt("pictures.minimal-pixel-art-size",16);
		default_background = getConfig().getString("pictures.default-background","default");
		picsperowner = getConfig().getInt("pictures.pictures-per-owner",15);
		burnpaintedwool   = getConfig().getBoolean ("pictures.burn-pixel-art-wool", true);
		steeve = getConfig().getString("pictures.default-skin","default_skin.png");
		skinurl = getConfig().getString("pictures.skin-url","http://s3.amazonaws.com/MinecraftSkins/");
	}
	
	
	
	

	
	
}
