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

import org.bukkit.entity.Player;

public class COUtil extends FGUtilCore {


	Obscura plg;

	public COUtil(Obscura plugin, boolean vcheck, boolean savelng, String language, String devbukkitname, String version_name, String plgcmd, String px){
	    super (plugin, savelng, language, plgcmd, devbukkitname);
	    this.initUpdateChecker(version_name, "48542", "a2a7b26dd4dc9bc496c80de4b49e87cb42e34ae3", devbukkitname, vcheck);
		this.plg = plugin;
		FillMSG();
		InitCmd();

		if (savelng) this.SaveMSG();
	}

	public void PrintCfg(Player p){
		printMsg(p, "&6&l"+des.getName()+" v"+des.getVersion()+" &r&6| "+getMSG("cfg_configuration",'6'));
		printEnDis (p, "cfg_vcheck",plg.version_check);
		printMSG (p, "cfg_language",plg.language);
		printEnDis (p, "cfg_lngsave",plg.language_save);
		/*printMSG (p, "cfg_camera",plg.camera_id,plg.camera_data);
		printMSG (p, "cfg_photopaper",plg.photopaper_id,plg.photopaper_data);
		printMSG (p, "cfg_brushid",plg.brush_id);*/
		printEnDis (p, "cfg_recipes",plg.use_recipes);
		printEnDis (p, "cfg_lensdrop",plg.obscura_drop);
		printMSG (p, "cfg_minpixelart",plg.minpixelart);
		printMSG (p, "cfg_defbackground",plg.default_background);
		printMSG (p, "cfg_totalmaps",plg.album.getPictureCount(),plg.album.getDeletedCount());
		printMSG (p, "cfg_personalfolders",EnDis(plg.personalfolders),EnDis(plg.pf_autocreate));
	}



	public void InitCmd(){
		cmds.clear();
		cmdlist = "";
		addCmd("help", "config","hlp_thishelp","&3/photo help [command]",'b');        
		addCmd("camera", "givecamera","cmd_givecamera","&3/photo camera",'b');        
		addCmd("paper", "givepaper","cmd_givepaper","&3/photo paper [amount]",'b');        
		addCmd("brush", "pixelart","cmd_brush","&3/photo brush",'b');                       
		addCmd("list", "config","cmd_list","&3/photo list [page] [name mask]",'b');        
		addCmd("files", "files","cmd_files","&3/photo files [page] [filename mask] [p:playername]",'b');
		addCmd("backgrounds", "config","cmd_backgrounds","&3/photo backgrounds [page] [filename mask]",'b');
		addCmd("download", "config","cmd_download","&3/photo download <name> <url>",'b');	 	
		addCmd("remove", "remove","cmd_remove","&3/photo remove <id>",'b');               
		addCmd("rename", "rename","cmd_rename","&3/photo rename [id] <new name>",'b');    
		addCmd("allowcopy", "allowcopy","cmd_allowcopy","&3/photo allowcopy [id]",'b');
		addCmd("showname", "showname","cmd_showname","&3/photo showname [id]",'b');
		addCmd("owner", "owner","cmd_owner","&3/photo owner [id] <new owner>",'b');       
		addCmd("head", "photo","cmd_headshot","&3/photo head [player] [background]",'b');              
		addCmd("top", "photo","cmd_tophalf","&3/photo top [player] [background]",'b');                  
		addCmd("full", "photo","cmd_fulllength","&3/photo full [player] [background]",'b');             
		addCmd("image", "image","cmd_image","&3/photo image [filename]",'b');               
		addCmd("paint", "pixelart","cmd_paint","&3/photo paint {center} <name>",'b');     
		addCmd("repaint", "repaint","cmd_repaint","&3/photo repaint {center} <name>",'b');
		addCmd("id", "id","cmd_id","&3/photo id",'b');        
		addCmd("give", "give","cmd_givemap","&3/photo give <picture id>",'b');
		addCmd("reload", "config","cmd_reload","&3/photo reload",'b');
		addCmd("rst", "config","cmd_rst","&3/photo rst [player]",'b');
		addCmd("cfg", "config","cmd_cfg","&3/photo cfg",'b');
	}

	public void FillMSG(){
		addMSG("cmd_givecamera", "%1% - gives a camera to you");
		addMSG("cmd_givepaper", "%1% - gives photographic paper to you");
		addMSG("cmd_givemap", "%1% - gives a picture (map) to you");
		addMSG("cmd_remove", "%1% - remove picture (map) with defined id");
		addMSG("cmd_list", "%1% - show list of pictures (maps)");
		addMSG("cmd_backgrounds", "%1% - show list of background images");
		addMSG("cmd_files", "%1% - show image list");
		addMSG("cmd_download", "%1% - download new image from the net");
		addMSG("сmd_remove", "%1% - remove picture with defined id");
		addMSG("cmd_rename", "%1% - name picture (holding in your hand)");
		addMSG("cmd_allowcopy", "%1% - toggle copy mode for defined picture");
		addMSG("cmd_showname", "%1% - toggle displaying name mode for defined picture");
		addMSG("cmd_owner", "%1% - redefine owner of picture with defined id");
		addMSG("cmd_headshot", "%1% - take a headshot photo");
		addMSG("cmd_tophalf", "%1% - take a tophalf photo");
		addMSG("cmd_fulllength", "%1% - take a fullenghth photo");
		addMSG("cmd_image", "%1% - create image from the png-file");
		addMSG("cmd_brush", "%1% - toggle brush mode"); 
		addMSG("cmd_paint", "%1% - create pixelart picture");
		addMSG("cmd_repaint", "%1% - repaint picture holding in hand with new pixelart"); 
		addMSG("cmd_id", "%1% - show info about picture (map) in hand"); 
		addMSG("cmd_reload", "%1% - reload plugin configuration");
		addMSG("cmd_rst", "%1% - reset rendered information (repaint pictures)");
		addMSG("cmd_cfg", "%1% - display plugin configuration");
		addMSG("msg_wrongdimension", "Wrong large image dimensions defined %1%. You must use syntax [width]x[height] (for example 5x7), or keywords: \"auto\" or \"resize\"");
		addMSG("msg_newmapcreated", "New picture created! (map id: %1%)");
		addMSG("msg_newmapscreated", "Create %1% pictures(map ids: %2%)");
		addMSG("msg_cannotaddphoto", "Can not create new photo!");
		addMSG("msg_needphotopaper", "You must hold one sheet of photopaper to develop a new photo");
		addMSG("msg_needphotopapers", "You must hold %1% sheet of photopaper to create new pictures");
		addMSG("msg_phpapernotfound", "You need a photopaper to take a picture!");
		addMSG("msg_cameradropped", "New camera created and dropped near you!");
		addMSG("msg_camerainventory", "New camera added to your inventory!");
		addMSG("msg_picturegiven", "Picture %1% (#%2%) given to you!");
		addMSG("msg_paperdropped", "New photopaper created and dropped near you!");
		addMSG("msg_paperinventory", "New photopaper added to your inventory!");
		addMSG("msg_brushmode", "Brush-mode");
		addMSG("msg_reloadcfg", "Configuration reloaded");
		addMSG("msg_pxlnoselection", "You need to select two coners of picture (point #1 - top left corner, point #2 - down right corner)");
		addMSG("msg_mapremoved", "Picture (map id %1%) was removed!");
		addMSG("msg_mapremovefail", "Failed to remove picture (map id %1%)!");
		addMSG("msg_wrongnumber", "%1% - is not a number!");
		addMSG("msg_pxlnoselection", "You need to select picture using the brush");
		addMSG("msg_selectp1", "Point #1 selected");
		addMSG("msg_selectp2", "Point #2 selected");
		addMSG("msg_albumtotal", "Total maps: %1%, deleted: %2%");
		addMSG("msg_albumlist", "Picture list (map id [owner] : name):");
		addMSG("msg_filelist", "Image list (files):");
		addMSG("msg_bglist", "Backgrounds list (files):");
		addMSG("msg_footer", "Page: [%1% / %2%]");
		addMSG("msg_albumnoownermap", "%1% owns no pictures");
		addMSG("msg_photocamera", "Photo camera");
		addMSG("msg_photopaper", "Photo paper");
		addMSG("msg_imgsaved", "Image downloaded to file %1%");
		addMSG("msg_imgsavefail", "Failed to save downloaded image to file %1%");
		addMSG("msg_unknownpicid", "Unknown picture (map) id: %1%");
		addMSG("msg_renamed", "Picture (#%1%) renamed to: %2%");
		addMSG("msg_copyallowed", "Copying of picture #%1% is allowed now!");
		addMSG("msg_copyforbidden", "Copying of picture #%1% is forbidden now!");
		addMSG("msg_acurnotowner", "You have not enough permissions to edit picture parameters");
		addMSG("msg_acneedmap", "You need to hold map item in your hands or define picture number at command");
		addMSG("msg_ownerset", "Owner of picture #%1% is set to %2%");
		addMSG("msg_owurnotowner", "You have not enought permissions to change owner of this picture");
		addMSG("msg_paintcentercmd", "You need to use command %1% to paint centered picture");
		addMSG("msg_youpaid", "You paid %1% for photography! (Balance: %2%)");
		addMSG("msg_youreceived", "You received %1% from %2% for photography! (Balance: %3%)");
		addMSG("msg_youhavenotmoney", "You have not enough money!");
		addMSG("msg_overlimit", "Your personal picture limit is over!");
		addMSG("msg_playeroverlimit", "Picture limit of player %1% is over!");
		addMSG("msg_needobscuramapinhand", "You must hold picture (map) in your hands");
		addMSG("map_regular", "Regular");
		addMSG("map_obscura", "Picture");
		addMSG("map_deleted", "Deleted");
		addMSG("msg_mapidtype", "Map id: %1%. Type: %2%");
		addMSG("msg_checkdimensions", "Cannot create picture. Please check image sizes.");
		addMSG("msg_cannotcreatemap", "Something wrong. Picture was not created....");
		addMSG("cfg_configuration", "Configuration");
		addMSG("cfg_vcheck", "Check updates"); 
		addMSG("cfg_language", "Language: %1%");
		addMSG("cfg_lngsave", "Save language-file");
		addMSG("cfg_camera", "Photo camera item: %1%:%2%");
		addMSG("cfg_photopaper", "Photo paper item: %1%:%2%");
		addMSG("cfg_brushid", "Photo brush item id: %1%");
		addMSG("cfg_recipes", "Use custom recipes");
		addMSG("cfg_lensdrop", "Lens drop (for tripod camera)");
		addMSG("cfg_minpixelart", "Minimal size of pixel-art: %1%x%1%");
		addMSG("cfg_defbackground", "Default background image: %1%");
		addMSG("cfg_totalmaps", "Total pictures: %1% Deleted: %2%");
		addMSG("cfg_personalfolders", "Personal folders: %1% Autocreate (after join): %2%");
		addMSG("msg_willshowname", "Name of picture #%1% now will be displayed at the canvas");
		addMSG("msg_willnotshowname", "Displaying the name of picture #%1% is now disabled");
		addMSG("msg_allowcopy", "allow copy");
		addMSG("msg_displayname", "display name");
		addMSG("msg_removedimage", "Removed image (#%1%)");
		addMSG("msg_rstplayer", "All images will be repainted for player %1%");
		addMSG("msg_rstall", "All images will be repainted!");
		addMSG("msg_dirnotexist", "Directory %1% is not exists");
		
		//addMSG("", "");
		
	}

}
