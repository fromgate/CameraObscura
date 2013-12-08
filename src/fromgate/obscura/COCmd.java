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

import org.bukkit.GameMode;
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
    COAlbum album;
    COUtil u;
    ImageCraft ic;

    public COCmd(Obscura plg){
        this.plg = plg;
        this.album = plg.album;
        this.u = plg.u;
        this.ic = plg.ic;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if ((args.length>0)&&u.checkCmdPerm(p, args[0])){
                if (args.length==1) return ExecuteCmd(p, args[0]);
                else if (args.length==2) return ExecuteCmd(p, args[0],args[1]);
                else if (args.length==3) return ExecuteCmd(p, args[0],args[1],args[2]);
                else if (args.length>3){
                    if (args[0].equalsIgnoreCase("files")){
                        COCamera.printFileList(p, args[1],args[2],args[3]);
                        return true;
                    } else {
                        String arg2 = args[2];
                        for (int i = 3; i<args.length;i++) arg2 = arg2+" "+args[i];
                        return ExecuteCmd(p, args[0],args[1],arg2);
                    }
                }
            }
        }
        return false;
    }

    public boolean ExecuteCmd (Player p, String cmd){
        if (cmd.equalsIgnoreCase("help")){
            u.PrintHlpList(p, 1, 10);
        } else if (cmd.equalsIgnoreCase("id")){
            if ((p.getItemInHand()!=null)&&(p.getItemInHand().getType()==Material.MAP)){
                short id = p.getItemInHand().getDurability();
                String maptype = u.getMSGnc("map_regular");
                if (album.isObscuraMap(id)) maptype = u.getMSGnc("map_obscura");
                if (album.isDeletedMap(id)) maptype = u.getMSGnc("map_deleted");
                u.printMSG(p, "msg_mapidtype",id,maptype);
            } else u.printMSG(p, "msg_needmapinhand");
        } else if (cmd.equalsIgnoreCase("cfg")){
            u.PrintCfg(p);
        } else if (cmd.equalsIgnoreCase("allowcopy")){
            if (album.isObscuraMap(p.getItemInHand())){
                short id = p.getItemInHand().getDurability();
                if (album.isOwner(id, p)){
                    album.setAllowCopy(id, album.getAllowCopy(id));

                    if (album.getAllowCopy(id)) u.printMSG(p, "msg_copyallowed",id);
                    else u.printMSG(p, "msg_copyforbidden",id);
                } else u.printMSG(p, "msg_acurnotowner",'c','4',Short.toString(id));
            } else u.printMSG(p, "msg_acneedmap");

        } else if (cmd.equalsIgnoreCase("showname")){
            if (album.isObscuraMap(p.getItemInHand())){
                short id = p.getItemInHand().getDurability();
                if (album.isOwner(id, p)){

                    album.setShowName(id, !album.isNameShown(id));

                    if (album.isNameShown(id)) u.printMSG(p, "msg_willshowname",id);
                    else u.printMSG(p, "msg_willnotshowname",id);

                    plg.rh.forceUpdate(id);

                } else u.printMSG(p, "msg_acurnotowner",'c','4',id);
            } else u.printMSG(p, "msg_acneedmap");


        } else if (cmd.equalsIgnoreCase("camera")){
            ItemStack camera = COCamera.newCamera();//COCamera.setName(new ItemStack (plg.camera_id, plg.camera_data), "Photo Camera");
            if (p.getInventory().addItem(camera).size()>0) {
                Item cameraitem = p.getWorld().dropItemNaturally(p.getLocation(), camera);
                cameraitem.setItemStack(camera);
                u.printMSG(p, "msg_cameradropped");
            } else u.printMSG(p, "msg_camerainventory");

        } else if (cmd.equalsIgnoreCase("paper")){
            ItemStack phpaper = COCamera.newPhotoPaper();
            if (p.getInventory().addItem(phpaper).size()>0) {
                Item phpaperitem = p.getWorld().dropItemNaturally(p.getLocation(), phpaper);
                phpaperitem.setItemStack(phpaper);
                u.printMSG(p, "msg_paperdropped");
            } else u.printMSG(p, "msg_paperinventory");

        } else if (cmd.equalsIgnoreCase("files")){
            COCamera.printFileList(p, "");
        } else if (cmd.equalsIgnoreCase("backgrounds")){
            File dir = new File (plg.d_backgrounds);
            List<String> ln = new ArrayList<String>();
            for (String fn : dir.list()) ln.add(fn);
            u.printPage(p, ln, 1, "msg_bglist", "msg_footer", true);			
        } else if (cmd.equalsIgnoreCase("list")){
            album.printList(p,p.getName(),1);
        } else if (cmd.equalsIgnoreCase("brush")){
            boolean brushmode = !WoolSelect.getBrushMode(p);
            WoolSelect.setBrushMode(p, brushmode);
            u.printEnDis (p,"msg_brushmode",brushmode);
        } else if (cmd.equalsIgnoreCase("head")){
            album.developPortrait(p);
        } else if (cmd.equalsIgnoreCase("top")){
            album.developTopHalfPhoto(p);
        } else if (cmd.equalsIgnoreCase("full")){
            album.developPhoto(p);
        } else if (cmd.equalsIgnoreCase("reload")){
            album.loadAlbum();
            plg.loadCfg();
            plg.rh.clearHistory();
            u.printMSG(p, "msg_reloadcfg");
        } else if (cmd.equalsIgnoreCase("rst")){
            plg.rh.clearHistory();
            u.printMSG(p, "msg_rstall");
        } else return false;
        return true;
    }


    public boolean ExecuteCmd (Player p, String cmd, String arg){
        if (cmd.equalsIgnoreCase("help")){
            int pnum = 1;
            if (u.isInteger(arg)) pnum = Integer.parseInt(arg);
            u.PrintHlpList(p, pnum, 10);
        } else if (cmd.equalsIgnoreCase("rst")){
            plg.rh.clearHistory(arg);
            u.printMSG(p, "msg_rstplayer",arg);
        } else if (cmd.equalsIgnoreCase("rename")){
            return ExecuteCmd (p, cmd, "",arg);
        } else if (cmd.equalsIgnoreCase("allowcopy")){
            if (arg.matches("[0-9]*")){
                short id = Short.parseShort(arg);
                if (album.getAllowCopy(id)) u.printMSG(p, "msg_copyallowed",id);
                else u.printMSG(p, "msg_copyforbidden",id);
            } else u.printMSG(p, "msg_acneedmap");


        } else if (cmd.equalsIgnoreCase("showname")){
            if (arg.matches("[0-9]*")){
                short id = Short.parseShort(arg);
                if (album.isOwner(id, p)){
                    album.setShowName(id, album.isNameShown(id));
                    if (album.getAllowCopy(id)) u.printMSG(p, "msg_willshowname",id);
                    else u.printMSG(p, "msg_willnotshowname",id);
                    plg.rh.forceUpdate(id);
                } else u.printMSG(p, "msg_acurnotowner",'c','4',id);
            } else u.printMSG(p, "msg_acneedmap");

        } else if (cmd.equalsIgnoreCase("owner")){
            if (album.isObscuraMap(p.getItemInHand())){
                if (!album.isLimitOver(arg)){
                    short id = p.getItemInHand().getDurability();
                    if (album.isOwner(id, p)){
                        album.setOwner(id, arg);
                        u.printMSG(p, "msg_ownerset",id,arg);
                    } else u.printMSG(p, "msg_owurnotowner",'c','4',id);
                } else u.printMSG(p, "msg_playeroverlimit",'c','4',arg);
            } else u.printMSG(p, "msg_acneedmap");

        } else if (cmd.equalsIgnoreCase("files")){
            COCamera.printFileList(p, arg);
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
            album.printList(p,pname, pnum);
        } else if (cmd.equalsIgnoreCase("paper")){
            int amount = 1;
            if (arg.matches("[1-9]+[0-9]*")) amount = Integer.parseInt(arg);
            ItemStack phpaper = COCamera.newPhotoPaper(amount);
            if (p.getInventory().addItem(phpaper).size()>0) {
                Item phpaperitem = p.getWorld().dropItemNaturally(p.getLocation(), phpaper);
                phpaperitem.setItemStack(phpaper);
                u.printMSG(p, "msg_paperdropped");
            } else u.printMSG(p, "msg_paperinventory");

        } else if (cmd.equalsIgnoreCase("give")){
            short id=-1;
            if (u.isInteger(arg)) id = Short.parseShort(arg);
            if ((id>=0)&&(album.isObscuraMap(id))){
                COCamera.giveImageToPlayer(p, id, album.getPictureName(id));
                u.printMSG(p, "msg_picturegiven",album.getPictureName(id),id);
            } else u.printMSG(p, "msg_unknownpicid",'c','4',arg);
        } else if (cmd.equalsIgnoreCase("portrait")){
            album.developPortrait(p,p.getName(), arg);
        } else if (cmd.equalsIgnoreCase("paint")){
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return false;
            }

            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);

            if (COCamera.isPhotoPaper(p.getItemInHand())&&(p.getItemInHand().getAmount()==1)){
                BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, true,plg.burnpaintedwool);
                if (img == null) {
                    u.printMSG(p, "msg_checkdimensions");
                    return true;
                }

                short mapid = album.addImage(p.getName(), arg, img, false, true);
                if (mapid>=0){
                    p.getItemInHand().setType(Material.MAP);
                    p.getItemInHand().setDurability(mapid);
                    p.setItemInHand(COCamera.setName(p.getItemInHand(), arg));

                } else u.printMSG(p, "msg_cannotcreatemap");
                u.printMSG(p, "msg_newmapcreated",mapid);
            } else u.printMSG(p, "msg_needphotopaper");


        } else if (cmd.equalsIgnoreCase("repaint")){
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return true;
            }
            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);
            if ((p.getItemInHand()!=null)&&(p.getItemInHand().getType()==Material.MAP)&&
                    album.isObscuraMap(p.getItemInHand())){
                short mapid = p.getItemInHand().getDurability();
                if (album.isOwner(mapid, p)){
                    BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, true,plg.burnpaintedwool);
                    album.updateImage(mapid, p.getName(), arg, img, false);
                    p.setItemInHand(COCamera.setName(p.getItemInHand(), arg));
                    u.printMSG(p, "msg_newmapcreated",mapid);
                } else u.printMSG(p, "msg_owurnotowner",'c','4',mapid);
            } else u.printMSG(p, "msg_needobscuramapinhand");


        } else if (cmd.equalsIgnoreCase("remove")){
            if (arg.matches("[0-9]*")){
                short id = Short.parseShort(arg);
                if (album.deleteImage(id)) {
                    u.printMSG(p, "msg_mapremoved",id);
                    COCamera.updateInventoryItems(p.getInventory());
                }
                else u.printMSG(p, "msg_mapremovefail",id);

            } else u.printMSG(p, "msg_wrongnumber",arg);
        } else if (cmd.equalsIgnoreCase("photo")){
            album.developPhoto(p, arg);
        } else if (cmd.equalsIgnoreCase("image")){

            return mapFromImageFile(p,arg,"");

            /*
		    if (ItemUtil.hasItemInInventory(p, plg.photopaper)){
		        ItemUtil.removeItemInInventory(p.getInventory(), plg.photopaper);
				short mapid = album.addImage(p.getName(), arg, ic.getImageByName(p, arg), false, true);
				if (mapid>=0){
				    ItemUtil.giveItemOrDrop(p, new ItemStack (Material.MAP,1,mapid));
					u.printMSG(p, "msg_newmapcreated",mapid);
				} else u.printMSG(p, "msg_cannotcreatemap");
			} else u.printMSG(p, "msg_needphotopaper"); */

        } else if (cmd.equalsIgnoreCase("head")){
            album.developPortrait(p,p.getName(),arg);
        } else if (cmd.equalsIgnoreCase("top")){
            album.developTopHalfPhoto(p,p.getName(),arg);
        } else if (cmd.equalsIgnoreCase("full")){
            album.developPhoto(p,p.getName(),arg);
        } else return false;
        return true;
    }

    public boolean ExecuteCmd (Player p, String cmd, String arg1, String arg2){
        if (cmd.equalsIgnoreCase("list")){
            int pnum = 1;
            if (arg2.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg2);
            album.printList(p,arg1, pnum);
        } else if (cmd.equalsIgnoreCase("image")){
            return mapFromImageFile(p,arg1,arg2);
        } else if (cmd.equalsIgnoreCase("paint")){
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return true;
            }

            if (!arg1.equalsIgnoreCase("center")){
                u.printMSG(p, "msg_paintcentercmd","/photo paint center <picture name>");
                return true;
            }

            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);

            if (COCamera.isPhotoPaper(p.getItemInHand())&&(p.getItemInHand().getAmount()==1)){
                BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, false,plg.burnpaintedwool);
                if (img == null) {
                    u.printMSG(p, "msg_checkdimensions");
                    return true;
                }
                short mapid = album.addImage(p.getName(), arg2, img, false, true);
                if (mapid>=0){
                    p.getItemInHand().setType(Material.MAP);
                    p.getItemInHand().setDurability(mapid);
                    p.setItemInHand(COCamera.setName(p.getItemInHand(), arg2));
                    u.printMSG(p, "msg_newmapcreated",mapid);
                } else u.printMSG(p, "msg_cannotcreatemap");
            } else u.printMSG(p, "msg_needphotopaper");

        } else if (cmd.equalsIgnoreCase("repaint")){
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return true;
            }
            if (!arg1.equalsIgnoreCase("center")){
                u.printMSG(p, "msg_paintcentercmd","/photo repaint center <picture name>");
                return true;
            }
            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);
            if ((p.getItemInHand()!=null)&&(p.getItemInHand().getType()==Material.MAP)&&
                    album.isObscuraMap(p.getItemInHand())){

                short mapid = p.getItemInHand().getDurability();

                if (album.isOwner(mapid, p)){
                    BufferedImage img = ic.createPixelArt2D(p,loc1, loc2, false,plg.burnpaintedwool);
                    if ((img.getWidth()>=plg.minpixelart)&&((img.getHeight()>=plg.minpixelart))){
                        album.updateImage(mapid, p.getName(), arg2, img, false);
                        p.setItemInHand(COCamera.setName(p.getItemInHand(), arg2));
                        u.printMSG(p, "msg_newmapcreated",mapid);
                    } else u.printMSG(p, "msg_cannotcreatemap"); 
                } else u.printMSG(p, "msg_owurnotowner",'c','4',mapid);
            } else u.printMSG(p, "msg_needobscuramapinhand");



        } else if (cmd.equalsIgnoreCase("owner")){
            if (arg1.matches("[0-9]*")){
                short id = Short.parseShort(arg1);
                if (album.isOwner(id, p)){
                    album.setOwner(id, arg2);
                    u.printMSG(p, "msg_ownerset",id,arg2);
                } else u.printMSG(p, "msg_owurnotowner",'c','4',id);
            } else u.printMSG(p, "msg_acneedmap");

        } else if (cmd.equalsIgnoreCase("files")){
            COCamera.printFileList(p, arg1,arg2);
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
                u.printMSG(p, "msg_imgsaved",fn);
            } catch (IOException e) {
                u.printMSG(p, "msg_imgsavefail");
            }
        } else if (cmd.equalsIgnoreCase("rename")){
            String txt = arg2;
            short id = -1;
            if (u.isIntegerGZ(arg1)) id = Short.parseShort(arg1);
            else if (album.isObscuraMap(p.getItemInHand())) {
                id = p.getItemInHand().getDurability();
                if (!arg1.isEmpty()) txt = arg1+" "+txt;
            }
            if ((id>0)&&album.isObscuraMap(id)){
                if (album.isOwner(id, p)){
                    album.setPictureName(id, txt);
                    u.printMSG(p, "msg_renamed", id,txt);
                    plg.rh.forceUpdate(id);
                    COCamera.updateInventoryItems(p.getInventory());
                } else u.printMSG(p, "msg_owurnotowner",'c','4',id);		
            } else u.printMSG(p, "msg_acneedmap", 'c');
        } else if (cmd.equalsIgnoreCase("head")){
            album.developPortrait(p,p.getName(),arg1,arg2);
        } else if (cmd.equalsIgnoreCase("top")){
            album.developTopHalfPhoto(p,p.getName(),arg1,arg2);
        } else if (cmd.equalsIgnoreCase("full")){
            album.developPhoto(p,p.getName(),arg1,arg2);
        } else return false;
        return true;

    }

    public boolean mapFromImageFile(Player p, String arg1, String arg2){
        String dimension = arg2.isEmpty() ? "1x1" : arg2;
        int mx = -1;
        int my = -1;
        if (dimension.equalsIgnoreCase("1x1")||dimension.equalsIgnoreCase("resize")) {
            mx=1;
            my=1;
        } else if (dimension.equalsIgnoreCase("auto")){
            mx=-1;
            my=-1;
        } else {
            String[] xy = dimension.split("x", 2);
            if (xy.length!=2) return u.returnMSG(true, p, "msg_wrongdimension",arg2);
            if (!u.isInteger(xy[0],xy[1])) return u.returnMSG(true, p, "msg_wrongdimension",arg2);
            if (!p.hasPermission("camera-obscura.image.large")) u.returnMSG(true, p, "msg_largeimageperm",'c');
            mx = Integer.parseInt(xy[0]);
            my = Integer.parseInt(xy[1]);

            double k = getRecalcMultiplier (mx,my);
            mx = (int) (mx*k);
            my = (int) (my*k);

        }
        List<BufferedImage> list = ic.getImageByName(p, arg1, mx, my);
        if (list == null || list.isEmpty()) u.returnMSG(true, p, "msg_failedtobuildimages",arg1);
        int amount = list.size();
        StringBuilder sb = new StringBuilder();

        if (p.getGameMode() == GameMode.CREATIVE || ItemUtil.hasItemInInventory(p, plg.photopaper,amount)){
            for (int i = 0; i <list.size(); i++){
                BufferedImage img = list.get(i);
                short mapid = album.addImage(p.getName(), arg1+"["+(i+1)+"/"+amount+"]", img, false, true);
                if (mapid>=0){
                    ItemUtil.giveItemOrDrop(p, new ItemStack (Material.MAP,1,mapid));
                    if (i==0) sb.append(Integer.toString(mapid));
                    else sb.append(",").append(Integer.toString(mapid));
                } else u.returnMSG(true,p, "msg_cannotcreatemap");
                if (p.getGameMode()!= GameMode.CREATIVE) ItemUtil.removeItemInInventory(p.getInventory(), plg.photopaper,amount);
            }
            u.printMSG(p, "msg_newmapscreated",amount,sb.toString());
            COCamera.updateInventoryItems(p.getInventory());
        } else u.printMSG(p, "msg_needphotopapers",amount);

        return true;
    }


    private double getRecalcMultiplier(int mx, int my){
        double k1 = Math.max(1, plg.max_width)/mx;    // 10/6
        double k2 = Math.max(1, plg.max_height)/my;
        if (k1>1&&k2>1) return 1;
        double k = k1<1 ? k1 : 1;
        return k2<k ? k2 : k;
    }


}
