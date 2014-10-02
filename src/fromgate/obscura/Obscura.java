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
import java.util.Arrays;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Изменения:
 * 0.1.0
 * - выпуск плагина
 *  * 0.1.1
 * - апдейт до 1.4.5 и более поздних
 * 0.1.2
 * - Фикс установки кнопки на нотные блоки (с шифтом)
 * - Отображение названия картинки на карте (настраивается щрифт, цвет, обводка)
 * - Автосмена названия итемов (карты, фотоаппараты, фотобумага)
 * 
 * 0.1.2/3
 * - Исправлена ситуация с несохранением настроек отображения карты
 * - Исправлена ошибка с названием фотокамеры (обзывалась фотобумагой)
 * - Исправлена ошибка при которой только Опы могли пользоваться стационарной камерой
 * 
 * 0.1.3
 * - Смена пермишенов: camera-obscura.image - /photo image
 *                     camera-obscura.files - /photo files
 *                     camera-obscura.files.autocreate
 *                     camera-obscura.files.all
 * - команда /photo files теперь поддерживает ещё и выбор персональной директории [p:имя]
 * 
 * 0.2.0
 * - Добавлена возможность "нарезки" больших файлов
 * - Новый пермишен: camera-obscura.image.large - дает возможность создавать "большие картины"
 * - При подгрузке большого файла на одну карту - он меняет размер
 * - Изменен подход к "уникальным" предметам. Теперь они определяются на имя, а не по нестандартному значению data 
 * 
 * TODO
 * - Авто переделка удаленных карт в фотобумагу? Автодроп из рамок удаленных картин?  
 * - Возможность перерисовывать/перефотографировать все карты при наличии пермишена
 * - HD-скины
 * - Отключить стаканье фотобумаги
 * - персональные директори для картинок
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
 * camera-obscura.image - /photo image
 * camera-obscura.files - /photo files
 * camera-obscura.files.autocreate
 * camera-obscura.files.all
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

	public static Obscura instance;
	
	String brush = "FEATHER";
	String photopaper = "Photo_paper&1&2&3$339";
	String camera = "Photo_camera&1&2&3$347";
	int max_width =  7; //896
	int max_height = 5; //640 
	
	
	boolean reusedeleted = true;
	boolean use_recipes = true;
	boolean obscura_drop = true; //будет ли камера на штативе выбрасывать фотоаппарат при разрушении?
	boolean block_sbutton_place = true;
	int minpixelart = 16;
	String default_background = "default"; // "random" - для случайного, реализовать
	int picsperowner = 15;
	boolean burnpaintedwool = false;
	boolean personalfolders = false;
	boolean pf_autocreate = false;

	float focus_1 = 2.0f;
	float focus_2 = 3.8f;

	String steve = "steve.png";
	String skinurl = "http://s3.amazonaws.com/MinecraftSkins/";

	//Настройка отображения заголовка
	boolean default_showname=true;
	String font_name = "Serif";
	int font_size = 9;
	boolean stroke = true;
	String name_color = "#000000";
	String stroke_color = "#FFFFFF";
    List<Short> reserved_maps = Arrays.asList((short)1963, (short)1964);;
	int name_x = 1;
	int name_y = 122;
	
	boolean useOldPalette = false;




	protected Economy economy = null;
	boolean vault_eco = false;

	String d_images;
	String d_album; 
	String d_skins;
	String d_backgrounds;


	COUtil u;
	ImageCraft ic;
	COAlbum album;
	COCmd cmd;
	COListener l;
	RenderHistory rh;



	@Override
	public void onEnable() {
		loadCfg();
		saveCfg();
		instance = this;
		d_images = getDataFolder()+File.separator+dir_images+File.separator;
		d_album = getDataFolder()+File.separator+dir_album+File.separator;
		d_skins = getDataFolder()+File.separator+dir_skins+File.separator;
		d_backgrounds = getDataFolder()+File.separator+dir_backgrounds+File.separator;
		WoolSelect.init(this);
		ItemUtil.init(this);
		Palette.init(useOldPalette);
		
		File dir = new File (d_images);
		if (!dir.exists()) dir.mkdirs();
		dir = new File (d_album);
		if (!dir.exists()) dir.mkdirs();
		dir = new File (d_backgrounds);
		if (!dir.exists()) dir.mkdirs();
		dir = new File (d_skins);
		if (!dir.exists()) dir.mkdirs();

		
		u = new COUtil (this, version_check, language_save, language, "camera-obscura", "CameraObscura", "photo", "&3[CO]&f ");
		rh = new RenderHistory ();
		ic = new ImageCraft (this);
		album = new COAlbum (this);
		l = new COListener (this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(l, this);
		cmd = new COCmd (this);
		getCommand("photo").setExecutor(cmd);
		if (use_recipes) COCamera.initRecipes();
		vault_eco = COCamera.setupEconomy();
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
		getConfig().set("items.use-recipes", use_recipes);
		getConfig().set("items.obscura-drop", obscura_drop);
		getConfig().set("pictures.reuse-deleted-maps", reusedeleted );
		getConfig().set("pictures.minimal-pixel-art-size",minpixelart);
		getConfig().set("pictures.default-background",default_background);
		getConfig().set("pictures.pictures-per-owner",picsperowner);
		getConfig().set("pictures.burn-pixel-art-wool", burnpaintedwool);
		getConfig().set("pictures.default-skin",steve);
		getConfig().set("pictures.skin-url",skinurl);
		getConfig().set("pictures.personal-folders.enable",personalfolders);
		getConfig().set("pictures.personal-folders.auto-create",pf_autocreate);
		getConfig().set("pictures.muti-map.max-width", max_width);
		getConfig().set("pictures.muti-map.max-height", max_height);
		getConfig().set("pictures.use-old-pallete", useOldPalette);
		getConfig().set("picture-name.show-at-new-pictures",default_showname);
		getConfig().set("picture-name.x",name_x);
		getConfig().set("picture-name.y",name_y);
		getConfig().set("picture-name.font-name",font_name);
		getConfig().set("picture-name.font-size",font_size);
		getConfig().set("picture-name.font-color",name_color);
		getConfig().set("picture-name.stroke",stroke);
		getConfig().set("picture-name.stroke-color",stroke_color);
		saveConfig();
	}

    public void loadCfg(){
		version_check = getConfig().getBoolean("general.check-updates",true);
		language = getConfig().getString("general.language","english");
		language_save = getConfig().getBoolean("general.language-save",false);
		max_width=getConfig().getInt("pictures.muti-map.max-width", max_width);
		max_height= getConfig().getInt("pictures.muti-map.max-height",max_height);
		useOldPalette=getConfig().getBoolean("pictures.use-old-pallete", useOldPalette);
        use_recipes = getConfig().getBoolean ("items.use-recipes", true);
		obscura_drop = getConfig().getBoolean ("items.obscura-drop", true);
		reusedeleted  = getConfig().getBoolean ("pictures.reuse-deleted-maps", true);
		minpixelart = getConfig().getInt("pictures.minimal-pixel-art-size",16);
		default_background = getConfig().getString("pictures.default-background","default");
		picsperowner = getConfig().getInt("pictures.pictures-per-owner",15);
		burnpaintedwool   = getConfig().getBoolean ("pictures.burn-pixel-art-wool", true);
		steve = getConfig().getString("pictures.default-skin","default_skin.png");
		skinurl = getConfig().getString("pictures.skin-url","http://s3.amazonaws.com/MinecraftSkins/");
		default_showname=getConfig().getBoolean("picture-name.show-at-new-pictures",false);
		personalfolders=getConfig().getBoolean("pictures.personal-folders.enable",false);
		pf_autocreate=getConfig().getBoolean("pictures.personal-folders.auto-create",false);
		name_x = getConfig().getInt("picture-name.x",1);
		name_y = getConfig().getInt("picture-name.y",122);
		font_name = getConfig().getString("picture-name.font-name","Serif");
		font_size = getConfig().getInt("picture-name.font-size",9);
		name_color = getConfig().getString("picture-name.font-color","#000000");
		stroke =getConfig().getBoolean("picture-name.stroke",true);
		stroke_color = getConfig().getString("picture-name.stroke-color","#FFFFFF");
        reserved_maps = getConfig().getShortList("reserved-maps");
	}

}
