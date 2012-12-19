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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class COCmd implements CommandExecutor{
	Obscura plg;
	COUtil u;
	COImageCraft ic;

	public COCmd(Obscura plg){
		this.plg = plg;
		this.u = plg.u;
		this.ic = plg.ic;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (sender instanceof Player){
			Player p = (Player) sender;
			if ((args.length>0)&&u.CheckCmdPerm(p, args[0])){
				if (args.length==1) return ExecuteCmd(p, args[0]);
				else if (args.length==2) return ExecuteCmd(p, args[0],args[1]);
				else if (args.length==3) return ExecuteCmd(p, args[0],args[1],args[2]);
				else if (args.length>3){
					String arg2 = args[2];
					for (int i = 3; i<args.length;i++) arg2 = arg2+args[i];
					return ExecuteCmd(p, args[0],args[1],arg2);
				}
			}
		}
		return false;
	}

	public boolean ExecuteCmd (Player p, String cmd){
		if (cmd.equalsIgnoreCase("help")){
			u.PrintHLP(p);
		} else if (cmd.equalsIgnoreCase("id")){
			if ((p.getItemInHand()!=null)&&(p.getItemInHand().getType()==Material.MAP)){
				short id = p.getItemInHand().getDurability();
				String maptype = u.MSGnc("map_regular");
				if (plg.album.isObscuraMap(id)) maptype = u.MSGnc("map_obscura");
				if (plg.album.isDeletedMap(id)) maptype = u.MSGnc("map_deleted");
				u.PrintMSG(p, "msg_mapidtype",id+";"+maptype);

			} else u.PrintMSG(p, "msg_needmapinhand");
		} else if (cmd.equalsIgnoreCase("cfg")){
			u.PrintCfg(p);
		} else if (cmd.equalsIgnoreCase("allowcopy")){
			if (plg.album.isObscuraMap(p.getItemInHand())){
				short id = p.getItemInHand().getDurability();
				if (plg.album.isOwner(id, p)){
					plg.album.setAllowCopy(id, plg.album.getAllowCopy(id));

					if (plg.album.getAllowCopy(id)) u.PrintMSG(p, "msg_copyallowed",id);
					else u.PrintMSG(p, "msg_copyforbidden",id);
				} else u.PrintMSG(p, "msg_acurnotowner",Short.toString(id),'c','4');
			} else u.PrintMSG(p, "msg_acneedmap");

		} else if (cmd.equalsIgnoreCase("camera")){
			ItemStack camera = COCamera.newCamera(plg);//COCamera.setName(new ItemStack (plg.camera_id, plg.camera_data), "Photo Camera");
			if (p.getInventory().addItem(camera).size()>0) {
				Item cameraitem = p.getWorld().dropItemNaturally(p.getLocation(), camera);
				cameraitem.setItemStack(camera);
				u.PrintMSG(p, "msg_cameradropped");
			} u.PrintMSG(p, "msg_camerainventory");

		} else if (cmd.equalsIgnoreCase("paper")){
			ItemStack phpaper = COCamera.newPhotoPaper(plg);
			if (p.getInventory().addItem(phpaper).size()>0) {
				Item phpaperitem = p.getWorld().dropItemNaturally(p.getLocation(), phpaper);
				phpaperitem.setItemStack(phpaper);
				u.PrintMSG(p, "msg_paperdropped");
			} u.PrintMSG(p, "msg_paperinventory");

		} else if (cmd.equalsIgnoreCase("files")){
			File dir = new File (plg.d_images);
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()) ln.add(fn);
			u.printPage(p, ln, 1, "msg_filelist", "msg_footer", true);
		} else if (cmd.equalsIgnoreCase("backgrounds")){
			File dir = new File (plg.d_backgrounds);
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()) ln.add(fn);
			u.printPage(p, ln, 1, "msg_bglist", "msg_footer", true);			
		} else if (cmd.equalsIgnoreCase("list")){
			plg.album.printList(p,p.getName(),1);
		} else if (cmd.equalsIgnoreCase("brush")){
			boolean brushmode = !COWoolSelect.getBrushMode(p);
			COWoolSelect.setBrushMode(plg, p, brushmode);
			u.PrintEnDis (p,"msg_brushmode",brushmode);
		} else if (cmd.equalsIgnoreCase("head")){
			plg.album.developPortrait(p);
		} else if (cmd.equalsIgnoreCase("top")){
			plg.album.developTopHalfPhoto(p);
		} else if (cmd.equalsIgnoreCase("full")){
			plg.album.developPhoto(p);
		} else if (cmd.equalsIgnoreCase("reload")){
			plg.album.loadAlbum();
			plg.loadCfg();
			u.PrintMSG(p, "msg_reloadcfg");
		} else return false;
		return true;
	}


	public boolean ExecuteCmd (Player p, String cmd, String arg){
		if (cmd.equalsIgnoreCase("help")){
			u.PrintHLP(p, arg);
		} else if (cmd.equalsIgnoreCase("allowcopy")){
			if (arg.matches("[0-9]*")){
				short id = Short.parseShort(arg);
				if (plg.album.getAllowCopy(id)) u.PrintMSG(p, "msg_copyallowed",id);
				else u.PrintMSG(p, "msg_copyforbidden",id);
			} else u.PrintMSG(p, "msg_acneedmap");

		} else if (cmd.equalsIgnoreCase("owner")){
			if (plg.album.isObscuraMap(p.getItemInHand())){
				if (!plg.album.isLimitOver(arg)){
					short id = p.getItemInHand().getDurability();
					if (plg.album.isOwner(id, p)){
						plg.album.setOwner(id, arg);
						u.PrintMSG(p, "msg_ownerset",id+";"+arg);
					} else u.PrintMSG(p, "msg_owurnotowner",Short.toString(id),'c','4');
				} else u.PrintMSG(p, "msg_playeroverlimit",arg,'c','4');
			} else u.PrintMSG(p, "msg_acneedmap");

		} else if (cmd.equalsIgnoreCase("files")){
			File dir = new File (plg.d_images);
			int pnum = 1;
			String fmask = "";
			if (arg.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg);
			else fmask = arg;
			
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()){
				if (fmask.isEmpty()) ln.add(fn);
				else if (fn.contains(fmask)) ln.add(fn);
			}
			u.printPage(p, ln, pnum, "msg_filelist", "msg_footer", true);
			
		} else if (cmd.equalsIgnoreCase("backgrounds")){
			File dir = new File (plg.d_backgrounds);
			int pnum = 1;
			String fmask = "";
			if (arg.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg);
			else fmask = arg;
			
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()){
				if (fmask.isEmpty()) ln.add(fn);
				else if (fn.contains(fmask)) ln.add(fn);
			}
			u.printPage(p, ln, pnum, "msg_bglist", "msg_footer", true);
		} else if (cmd.equalsIgnoreCase("list")){
			int pnum = 1;
			String pname = p.getName();
			if (arg.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg);
			else pname = arg;
			plg.album.printList(p,pname, pnum);
		} else if (cmd.equalsIgnoreCase("paper")){
			int amount = 1;
			if (arg.matches("[1-9]+[0-9]*")) amount = Integer.parseInt(arg);
			ItemStack phpaper = COCamera.newPhotoPaper(plg,amount);
			if (p.getInventory().addItem(phpaper).size()>0) {
				Item phpaperitem = p.getWorld().dropItemNaturally(p.getLocation(), phpaper);
				phpaperitem.setItemStack(phpaper);
				u.PrintMSG(p, "msg_paperdropped");
			} else u.PrintMSG(p, "msg_paperinventory");

		} else if (cmd.equalsIgnoreCase("give")){
			short id=-1;
			if (arg.matches("[1-9]+[0-9]*")) id = Short.parseShort(arg);
			if ((id>0)&&(plg.album.isObscuraMap(id))){
				COCamera.giveImageToPlayer(plg, p, id, plg.album.getPictureName(id));
				u.PrintMSG(p, "msg_picturegiven",plg.album.getPictureName(id)+";"+id);
			} else u.PrintMSG(p, "msg_unknownpicid",arg,'c','4');
		} else if (cmd.equalsIgnoreCase("portrait")){
			plg.album.developPortrait(p,p.getName(), arg);
		} else if (cmd.equalsIgnoreCase("paint")){
			if (!COWoolSelect.isRegionSelected(p)) {
				u.PrintMSG(p, "msg_pxlnoselection");
				return false;
			}

			Location loc1 = COWoolSelect.getP1(p);
			Location loc2 = COWoolSelect.getP2(p);

			if (COCamera.isPhotoPaper(plg, p.getItemInHand())&&(p.getItemInHand().getAmount()==1)){
				BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, true,plg.burnpaintedwool);
				if (img == null) {
					u.PrintMSG(p, "msg_checkdimensions");
					return true;
				}


				short mapid = plg.album.addImage(p.getName(), arg, img, false, true);
				if (mapid>=0){
					p.getItemInHand().setType(Material.MAP);
					p.getItemInHand().setDurability(mapid);
					p.setItemInHand(COCamera.setName(p.getItemInHand(), arg));
					
				} else u.PrintMSG(p, "msg_cannotcreatemap");
				u.PrintMSG(p, "msg_newmapcreated",mapid);
			} else u.PrintMSG(p, "msg_needphotopaper");


		} else if (cmd.equalsIgnoreCase("repaint")){
			if (!COWoolSelect.isRegionSelected(p)) {
				u.PrintMSG(p, "msg_pxlnoselection");
				return true;
			}
			Location loc1 = COWoolSelect.getP1(p);
			Location loc2 = COWoolSelect.getP2(p);
			if ((p.getItemInHand()!=null)&&(p.getItemInHand().getType()==Material.MAP)&&
					plg.album.isObscuraMap(p.getItemInHand())){
				short mapid = p.getItemInHand().getDurability();
				if (plg.album.isOwner(mapid, p)){
					BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, true,plg.burnpaintedwool);
					plg.album.updateImage(mapid, p.getName(), arg, img, false);
					p.setItemInHand(COCamera.setName(p.getItemInHand(), arg));
					u.PrintMSG(p, "msg_newmapcreated",mapid);
				} else u.PrintMSG(p, "msg_owurnotowner",Short.toString(mapid),'c','4');
			} else u.PrintMSG(p, "msg_needobscuramapinhand");


		} else if (cmd.equalsIgnoreCase("remove")){
			if (arg.matches("[0-9]*")){
				short id = Short.parseShort(arg);

				if (plg.album.deleteImage(id)) u.PrintMSG(p, "msg_mapremoved",id);
				else u.PrintMSG(p, "msg_mapremovefail",id);

			} else u.PrintMSG(p, "msg_wrongnumber",arg);
		} else if (cmd.equalsIgnoreCase("photo")){
			plg.album.developPhoto(p, arg);
		} else if (cmd.equalsIgnoreCase("image")){
			if (COCamera.isPhotoPaper(plg, p.getItemInHand())&&(p.getItemInHand().getAmount()==1)){
				short mapid = plg.album.addImage(p.getName(), arg, ic.getImageByName(arg), false, true);
				if (mapid>=0){
					p.getItemInHand().setType(Material.MAP);
					p.getItemInHand().setDurability(mapid);
					u.PrintMSG(p, "msg_newmapcreated",mapid);
				} else u.PrintMSG(p, "msg_cannotcreatemap");
			} else u.PrintMSG(p, "msg_needphotopaper");


		} else if (cmd.equalsIgnoreCase("head")){
			plg.album.developPortrait(p,p.getName(),arg);
		} else if (cmd.equalsIgnoreCase("top")){
			plg.album.developTopHalfPhoto(p,p.getName(),arg);
		} else if (cmd.equalsIgnoreCase("full")){
			plg.album.developPhoto(p,p.getName(),arg);
		} else return false;
		return true;
	}

	public boolean ExecuteCmd (Player p, String cmd, String arg1, String arg2){
		if (cmd.equalsIgnoreCase("list")){
			int pnum = 1;
			if (arg2.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg2);
			plg.album.printList(p,arg1, pnum);

		} else if (cmd.equalsIgnoreCase("paint")){
			if (!COWoolSelect.isRegionSelected(p)) {
				u.PrintMSG(p, "msg_pxlnoselection");
				return true;
			}

			if (!arg1.equalsIgnoreCase("center")){
				u.PrintMSG(p, "msg_paintcentercmd","/photo paint center <picture name>");
				return true;
			}

			Location loc1 = COWoolSelect.getP1(p);
			Location loc2 = COWoolSelect.getP2(p);

			if (COCamera.isPhotoPaper(plg, p.getItemInHand())&&(p.getItemInHand().getAmount()==1)){
				BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, false,plg.burnpaintedwool);
				if (img == null) {
					u.PrintMSG(p, "msg_checkdimensions");
					return true;
				}
				short mapid = plg.album.addImage(p.getName(), arg2, img, false, true);
				if (mapid>=0){
					p.getItemInHand().setType(Material.MAP);
					p.getItemInHand().setDurability(mapid);
					p.setItemInHand(COCamera.setName(p.getItemInHand(), arg2));
					u.PrintMSG(p, "msg_newmapcreated",mapid);
				} else u.PrintMSG(p, "msg_cannotcreatemap");
			} else u.PrintMSG(p, "msg_needphotopaper");

		} else if (cmd.equalsIgnoreCase("repaint")){
			if (!COWoolSelect.isRegionSelected(p)) {
				u.PrintMSG(p, "msg_pxlnoselection");
				return true;
			}
			if (!arg1.equalsIgnoreCase("center")){
				u.PrintMSG(p, "msg_paintcentercmd","/photo repaint center <picture name>");
				return true;
			}
			Location loc1 = COWoolSelect.getP1(p);
			Location loc2 = COWoolSelect.getP2(p);
			if ((p.getItemInHand()!=null)&&(p.getItemInHand().getType()==Material.MAP)&&
					plg.album.isObscuraMap(p.getItemInHand())){

				short mapid = p.getItemInHand().getDurability();

				if (plg.album.isOwner(mapid, p)){
					BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, false,plg.burnpaintedwool);
					if ((img.getWidth()>=plg.minpixelart)&&((img.getHeight()>=plg.minpixelart))){
						plg.album.updateImage(mapid, p.getName(), arg2, img, false);
						p.setItemInHand(COCamera.setName(p.getItemInHand(), arg2));
						u.PrintMSG(p, "msg_newmapcreated",mapid);
					} else u.PrintMSG(p, "msg_cannotcreatemap"); 
				} else u.PrintMSG(p, "msg_owurnotowner",Short.toString(mapid),'c','4');
			} else u.PrintMSG(p, "msg_needobscuramapinhand");



		} else if (cmd.equalsIgnoreCase("owner")){
			if (arg1.matches("[0-9]*")){
				short id = Short.parseShort(arg1);
				if (plg.album.isOwner(id, p)){
					plg.album.setOwner(id, arg2);
					u.PrintMSG(p, "msg_ownerset",id+";"+arg2);
				} else u.PrintMSG(p, "msg_owurnotowner",Short.toString(id),'c','4');
			} else u.PrintMSG(p, "msg_acneedmap");

		} else if (cmd.equalsIgnoreCase("files")){
			File dir = new File (plg.d_images);
			int pnum = 1;
			if (arg2.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg2);
			
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()){
				if (arg1.isEmpty()) ln.add(fn);
				else if (fn.contains(arg1)) ln.add(fn);
			}
			u.printPage(p, ln, pnum, "msg_filelist", "msg_footer", true);
			
		} else if (cmd.equalsIgnoreCase("backgrounds")){
			File dir = new File (plg.d_backgrounds);
			int pnum = 1;
			if (arg2.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg2);
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()){
				if (arg1.isEmpty()) ln.add(fn);
				else if (fn.contains(arg1)) ln.add(fn);
			}
			u.printPage(p, ln, pnum, "msg_bglist", "msg_footer", true);
		} else if (cmd.equalsIgnoreCase("download")){
			String fn = arg1;
			if (!fn.endsWith(".png")) fn = fn+".png";
			File f = new File (plg.getDataFolder()+File.separator+"images"+File.separator+fn);
			BufferedImage img = ic.getImageByURL(arg2);
			try {
				ImageIO.write(img, "png", f);
				u.PrintMSG(p, "msg_imgsaved",fn);
			} catch (IOException e) {
				u.PrintMSG(p, "msg_imgsavefail");
			}
		} else if (cmd.equalsIgnoreCase("rename")){
			if (arg1.matches("[0-9]*")) {
				short id = Short.parseShort(arg1);
				if (plg.album.isObscuraMap(id)){
					if (plg.album.isOwner(id, p)){
						plg.album.setPictureName(id, arg2);
						u.PrintMSG(p, "msg_renamed", arg1+";"+arg2);
					} else u.PrintMSG(p, "msg_owurnotowner",Short.toString(id),'c','4');		
				} else u.PrintMSG(p, "msg_unknownpicid", arg1,'c','4');
			} else u.PrintMSG(p, "msg_wrongnumber", arg1,'c','4');
			
		} else if (cmd.equalsIgnoreCase("head")){
			plg.album.developPortrait(p,p.getName(),arg1,arg2);
		} else if (cmd.equalsIgnoreCase("top")){
			plg.album.developTopHalfPhoto(p,p.getName(),arg1,arg2);
		} else if (cmd.equalsIgnoreCase("full")){
			plg.album.developPhoto(p,p.getName(),arg1,arg2);
		} else return false;
		return true;

	}



}
