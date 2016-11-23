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
package me.fromgate.obscura;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
 * 0.2.1
 * - Немного улучшил работу с командами
 * - Опция на сокрытие названия в рамке
 * - Проппорциональное изменения размера
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
    boolean versionCheck = true;
    String language = "english";
    boolean languageSave = false;

    private String folderAlbum = "album";
    private String folderImages = "images";
    private String folderBackgrounds = "backgrounds";
    private String folderSkins = "skins";


    String brush = "FEATHER";
    String photopaper = "Photo_paper&1&2&3$339";
    String camera = "Photo_camera&1&2&3$347";
    int maxWidth = 7; //896
    int maxHeight = 5; //640
    boolean keepAspectRation = true;
    String emptySpaceColor = "#000000";
    boolean reuseDeleted = true;
    boolean useRecipes = true;
    boolean dropObscura = true; //будет ли камера на штативе выбрасывать фотоаппарат при разрушении?
    boolean blockSbuttonPlace = true;
    int minPixelart = 16;
    String defaultBackground = "default"; // "random" - для случайного, реализовать
    int picsPerOwner = 15;
    boolean burnPaintedWool = false;
    boolean personalFolders = false;
    boolean autocreatePersonalFolder = false;

    float focus1 = 2.0f;
    float focus2 = 3.8f;

    String steveSkin = "steve.png";
    String skinUrl = "http://s3.amazonaws.com/MinecraftSkins/";

    //Настройка отображения заголовка
    boolean defaultShowName = true;
    boolean hideNameInFrames = true;
    String fontName = "Serif";
    int fontSize = 9;
    boolean stroke = true;
    String nameColor = "#000000";
    String strokeColor = "#FFFFFF";
    int nameX = 1;
    int nameY = 122;


    String dirImages;
    String dirAlbum;
    String dirSkins;
    String dirBackgrounds;

    public static Obscura instance;

    COUtil u;
    COCmd cmd;
    COListener l;
    protected Economy economy = null;
    boolean vault_eco = false;


    @Override
    public void onEnable() {
        loadCfg();
        saveCfg();
        instance = this;
        dirImages = getDataFolder() + File.separator + folderImages + File.separator;
        dirAlbum = getDataFolder() + File.separator + folderAlbum + File.separator;
        dirSkins = getDataFolder() + File.separator + folderSkins + File.separator;
        dirBackgrounds = getDataFolder() + File.separator + folderBackgrounds + File.separator;
        WoolSelect.init(this);
        ItemUtil.init(this);
        File dir = new File(dirImages);
        if (!dir.exists()) dir.mkdirs();
        dir = new File(dirAlbum);
        if (!dir.exists()) dir.mkdirs();
        dir = new File(dirBackgrounds);
        if (!dir.exists()) dir.mkdirs();
        dir = new File(dirSkins);
        if (!dir.exists()) dir.mkdirs();
        u = new COUtil(this, versionCheck, languageSave, language, "camera-obscura", "CameraObscura", "photo", "&3[CO]&f ");
        Album.init();
        l = new COListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(l, this);
        cmd = new COCmd(this);
        getCommand("photo").setExecutor(cmd);
        if (useRecipes) COCamera.initRecipes();
        vault_eco = COCamera.setupEconomy();
        if (!vault_eco) u.log("Failed to init Vault/Economy services. Economic features will be disabled");
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        }
    }


    public void saveCfg() {
        getConfig().set("general.check-updates", versionCheck);
        getConfig().set("general.language", language);
        getConfig().set("general.language-save", languageSave);
        getConfig().set("items.use-recipes", useRecipes);
        getConfig().set("items.obscura-drop", dropObscura);
        getConfig().set("pictures.reuse-deleted-maps", reuseDeleted);
        getConfig().set("pictures.minimal-pixel-art-size", minPixelart);
        getConfig().set("pictures.default-background", defaultBackground);
        getConfig().set("pictures.pictures-per-owner", picsPerOwner);
        getConfig().set("pictures.burn-pixel-art-wool", burnPaintedWool);
        getConfig().set("pictures.default-skin", steveSkin);
        getConfig().set("pictures.skin-url", skinUrl);
        getConfig().set("pictures.personal-folders.enable", personalFolders);
        getConfig().set("pictures.personal-folders.auto-create", autocreatePersonalFolder);
        getConfig().set("pictures.muti-map.max-width", maxWidth);
        getConfig().set("pictures.muti-map.max-height", maxHeight);
        getConfig().set("pictures.muti-map.keep-aspect-ratio", keepAspectRation);
        getConfig().set("pictures.muti-map.empty-space-color", emptySpaceColor);
        getConfig().set("pictures.hide-image-hint-in-frame", hideNameInFrames);
        getConfig().set("picture-name.show-at-new-pictures", defaultShowName);
        getConfig().set("picture-name.x", nameX);
        getConfig().set("picture-name.y", nameY);
        getConfig().set("picture-name.font-name", fontName);
        getConfig().set("picture-name.font-size", fontSize);
        getConfig().set("picture-name.font-color", nameColor);
        getConfig().set("picture-name.stroke", stroke);
        getConfig().set("picture-name.stroke-color", strokeColor);
        saveConfig();
    }

    public void loadCfg() {
        versionCheck = getConfig().getBoolean("general.check-updates", true);
        language = getConfig().getString("general.language", "english");
        languageSave = getConfig().getBoolean("general.language-save", false);
        maxWidth = getConfig().getInt("pictures.muti-map.max-width", maxWidth);
        maxHeight = getConfig().getInt("pictures.muti-map.max-height", maxHeight);
        keepAspectRation = getConfig().getBoolean("pictures.muti-map.keep-aspect-ratio", false);
        emptySpaceColor = getConfig().getString("pictures.muti-map.empty-space-color", "#000000");
        useRecipes = getConfig().getBoolean("items.use-recipes", true);
        hideNameInFrames = getConfig().getBoolean("pictures.hide-image-hint-in-frame", true);
        dropObscura = getConfig().getBoolean("items.obscura-drop", true);
        reuseDeleted = getConfig().getBoolean("pictures.reuse-deleted-maps", true);
        minPixelart = getConfig().getInt("pictures.minimal-pixel-art-size", 16);
        defaultBackground = getConfig().getString("pictures.default-background", "default");
        picsPerOwner = getConfig().getInt("pictures.pictures-per-owner", 15);
        burnPaintedWool = getConfig().getBoolean("pictures.burn-pixel-art-wool", true);
        steveSkin = getConfig().getString("pictures.default-skin", "default_skin.png");
        skinUrl = getConfig().getString("pictures.skin-url", "http://s3.amazonaws.com/MinecraftSkins/");
        defaultShowName = getConfig().getBoolean("picture-name.show-at-new-pictures", false);
        personalFolders = getConfig().getBoolean("pictures.personal-folders.enable", false);
        autocreatePersonalFolder = getConfig().getBoolean("pictures.personal-folders.auto-create", false);
        nameX = getConfig().getInt("picture-name.x", 1);
        nameY = getConfig().getInt("picture-name.y", 122);
        fontName = getConfig().getString("picture-name.font-name", "Serif");
        fontSize = getConfig().getInt("picture-name.font-size", 9);
        nameColor = getConfig().getString("picture-name.font-color", "#000000");
        stroke = getConfig().getBoolean("picture-name.stroke", true);
        strokeColor = getConfig().getString("picture-name.stroke-color", "#FFFFFF");
    }

}
