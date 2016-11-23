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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class COCmd implements CommandExecutor {
    Obscura plg;
    COUtil u;

    public COCmd(Obscura plg) {
        this.plg = plg;
        this.u = plg.u;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if ((args.length > 0) && u.checkCmdPerm(p, args[0])) {
                if (args.length == 1) return ExecuteCmd(p, args[0]);
                else if (args.length == 2) return ExecuteCmd(p, args[0], args[1]);
                else if (args.length == 3) return ExecuteCmd(p, args[0], args[1], args[2]);
                else if (args.length > 3) {
                    if (args[0].equalsIgnoreCase("files")) {
                        COCamera.printFileList(p, args[1], args[2], args[3]);
                        return true;
                    } else {
                        String arg2 = args[2];
                        for (int i = 3; i < args.length; i++) arg2 = arg2 + " " + args[i];
                        return ExecuteCmd(p, args[0], args[1], arg2);
                    }
                }
            }
        }
        return false;
    }

    public boolean ExecuteCmd(Player p, String cmd) {
        if (cmd.equalsIgnoreCase("help")) {
            u.PrintHlpList(p, 1, 10);
        } else if (cmd.equalsIgnoreCase("id")) {
            if ((p.getItemInHand() != null) && (p.getItemInHand().getType() == Material.MAP)) {
                short id = p.getItemInHand().getDurability();
                String maptype = u.getMSGnc("map_regular");
                if (Album.isObscuraMap(id)) maptype = u.getMSGnc("map_obscura");
                if (Album.isDeletedMap(id)) maptype = u.getMSGnc("map_deleted");
                u.printMSG(p, "msg_mapidtype", id, maptype);
            } else u.printMSG(p, "msg_needmapinhand");
        } else if (cmd.equalsIgnoreCase("cfg")) {
            u.PrintCfg(p);
        } else if (cmd.equalsIgnoreCase("rotate")) {
            return removeImage(p, "");
        } else if (cmd.equalsIgnoreCase("rotate")) {
            return setRotionAllowed(p, "");
        } else if (cmd.equalsIgnoreCase("allowcopy")) {
            return setAllowCopy(p, "");
        } else if (cmd.equalsIgnoreCase("showname")) {
            return setShowName(p, "");
        } else if (cmd.equalsIgnoreCase("camera")) {
            ItemStack camera = COCamera.newCamera();//COCamera.setName(new ItemStack (plg.camera_id, plg.camera_data), "Photo Camera");
            if (p.getInventory().addItem(camera).size() > 0) {
                Item cameraitem = p.getWorld().dropItemNaturally(p.getLocation(), camera);
                cameraitem.setItemStack(camera);
                u.printMSG(p, "msg_cameradropped");
            } else u.printMSG(p, "msg_camerainventory");

        } else if (cmd.equalsIgnoreCase("paper")) {
            ItemStack phpaper = COCamera.newPhotoPaper();
            if (p.getInventory().addItem(phpaper).size() > 0) {
                Item phpaperitem = p.getWorld().dropItemNaturally(p.getLocation(), phpaper);
                phpaperitem.setItemStack(phpaper);
                u.printMSG(p, "msg_paperdropped");
            } else u.printMSG(p, "msg_paperinventory");

        } else if (cmd.equalsIgnoreCase("files")) {
            COCamera.printFileList(p, "");
        } else if (cmd.equalsIgnoreCase("backgrounds")) {
            File dir = new File(plg.dirBackgrounds);
            List<String> ln = new ArrayList<String>();
            for (String fn : dir.list()) ln.add(fn);
            u.printPage(p, ln, 1, "msg_bglist", "msg_footer", true);
        } else if (cmd.equalsIgnoreCase("list")) {
            Album.printList(p, p.getName(), 1);
        } else if (cmd.equalsIgnoreCase("brush")) {
            boolean brushmode = !WoolSelect.getBrushMode(p);
            WoolSelect.setBrushMode(p, brushmode);
            u.printEnDis(p, "msg_brushmode", brushmode);
        } else if (cmd.equalsIgnoreCase("head")) {
            Album.developPortrait(p);
        } else if (cmd.equalsIgnoreCase("top")) {
            Album.developTopHalfPhoto(p);
        } else if (cmd.equalsIgnoreCase("full")) {
            Album.developPhoto(p);
        } else if (cmd.equalsIgnoreCase("reload")) {
            Album.loadAlbum();
            plg.loadCfg();
            RenderHistory.clearHistory();
            u.printMSG(p, "msg_reloadcfg");
        } else if (cmd.equalsIgnoreCase("rst")) {
            RenderHistory.clearHistory();
            u.printMSG(p, "msg_rstall");
        } else return false;
        return true;
    }


    public boolean ExecuteCmd(Player p, String cmd, String arg) {
        if (cmd.equalsIgnoreCase("help")) {
            int pnum = 1;
            if (u.isInteger(arg)) pnum = Integer.parseInt(arg);
            u.PrintHlpList(p, pnum, 10);
        } else if (cmd.equalsIgnoreCase("rst")) {
            RenderHistory.clearHistory(arg);
            u.printMSG(p, "msg_rstplayer", arg);
        } else if (cmd.equalsIgnoreCase("rename")) {
            return ExecuteCmd(p, cmd, "", arg);
        } else if (cmd.equalsIgnoreCase("allowcopy")) {
            return setAllowCopy(p, arg);
        } else if (cmd.equalsIgnoreCase("rotate")) {
            return setRotionAllowed(p, arg);
        } else if (cmd.equalsIgnoreCase("showname")) {
            return setShowName(p, arg);
        } else if (cmd.equalsIgnoreCase("owner")) {
            if (Album.isObscuraMap(p.getItemInHand())) {
                if (!Album.isLimitOver(arg)) {
                    short id = p.getItemInHand().getDurability();
                    if (Album.isOwner(id, p)) {
                        Album.setOwner(id, arg);
                        u.printMSG(p, "msg_ownerset", id, arg);
                    } else u.printMSG(p, "msg_owurnotowner", 'c', '4', id);
                } else u.printMSG(p, "msg_playeroverlimit", 'c', '4', arg);
            } else u.printMSG(p, "msg_acneedmap");

        } else if (cmd.equalsIgnoreCase("files")) {
            COCamera.printFileList(p, arg);
        } else if (cmd.equalsIgnoreCase("backgrounds")) {
            File dir = new File(plg.dirBackgrounds);
            int pnum = 1;
            String fmask = "";
            if (arg.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg);
            else fmask = arg;
            List<String> ln = new ArrayList<String>();
            for (String fn : dir.list()) {
                if (fmask.isEmpty()) ln.add(fn);
                else if (fn.contains(fmask)) ln.add(fn);
            }
            u.printPage(p, ln, pnum, "msg_bglist", "msg_footer", true);
        } else if (cmd.equalsIgnoreCase("list")) {
            int pnum = 1;
            String pname = p.getName();
            if (arg.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg);
            else pname = arg;
            Album.printList(p, pname, pnum);
        } else if (cmd.equalsIgnoreCase("paper")) {
            int amount = 1;
            if (arg.matches("[1-9]+[0-9]*")) amount = Integer.parseInt(arg);
            ItemStack phpaper = COCamera.newPhotoPaper(amount);
            if (p.getInventory().addItem(phpaper).size() > 0) {
                Item phpaperitem = p.getWorld().dropItemNaturally(p.getLocation(), phpaper);
                phpaperitem.setItemStack(phpaper);
                u.printMSG(p, "msg_paperdropped");
            } else u.printMSG(p, "msg_paperinventory");

        } else if (cmd.equalsIgnoreCase("give")) {
            short id = -1;
            if (u.isInteger(arg)) id = Short.parseShort(arg);
            if ((id >= 0) && (Album.isObscuraMap(id))) {
                COCamera.giveImageToPlayer(p, id, Album.getPictureName(id));
                u.printMSG(p, "msg_picturegiven", Album.getPictureName(id), id);
            } else u.printMSG(p, "msg_unknownpicid", 'c', '4', arg);
        } else if (cmd.equalsIgnoreCase("portrait")) {
            Album.developPortrait(p, p.getName(), arg);
        } else if (cmd.equalsIgnoreCase("paint")) {
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return false;
            }

            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);

            if (COCamera.isPhotoPaper(p.getItemInHand()) && (p.getItemInHand().getAmount() == 1)) {
                BufferedImage img = ImageCraft.createPixelArt2D(p, loc1, loc2, true, plg.burnPaintedWool);
                if (img == null) {
                    u.printMSG(p, "msg_checkdimensions");
                    return true;
                }

                short mapid = Album.addImage(p.getName(), arg, img, false, true);
                if (mapid >= 0) {
                    p.getItemInHand().setType(Material.MAP);
                    p.getItemInHand().setDurability(mapid);
                    p.setItemInHand(COCamera.setName(p.getItemInHand(), arg));

                } else u.printMSG(p, "msg_cannotcreatemap");
                u.printMSG(p, "msg_newmapcreated", mapid);
            } else u.printMSG(p, "msg_needphotopaper");


        } else if (cmd.equalsIgnoreCase("repaint")) {
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return true;
            }
            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);
            if ((p.getItemInHand() != null) && (p.getItemInHand().getType() == Material.MAP) &&
                    Album.isObscuraMap(p.getItemInHand())) {
                short mapid = p.getItemInHand().getDurability();
                if (Album.isOwner(mapid, p)) {
                    BufferedImage img = ImageCraft.createPixelArt2D(p, loc1, loc2, true, plg.burnPaintedWool);
                    Album.updateImage(mapid, p.getName(), arg, img, false);
                    p.setItemInHand(COCamera.setName(p.getItemInHand(), arg));
                    u.printMSG(p, "msg_newmapcreated", mapid);
                } else u.printMSG(p, "msg_owurnotowner", 'c', '4', mapid);
            } else u.printMSG(p, "msg_needobscuramapinhand");


        } else if (cmd.equalsIgnoreCase("remove")) {
            return removeImage(p, arg);
        } else if (cmd.equalsIgnoreCase("photo")) {
            Album.developPhoto(p, arg);
        } else if (cmd.equalsIgnoreCase("image")) {
            return mapFromImageFile(p, arg, "");
        } else if (cmd.equalsIgnoreCase("head")) {
            Album.developPortrait(p, p.getName(), arg);
        } else if (cmd.equalsIgnoreCase("top")) {
            Album.developTopHalfPhoto(p, p.getName(), arg);
        } else if (cmd.equalsIgnoreCase("full")) {
            Album.developPhoto(p, p.getName(), arg);
        } else return false;
        return true;
    }

    public boolean ExecuteCmd(Player p, String cmd, String arg1, String arg2) {
        if (cmd.equalsIgnoreCase("list")) {
            int pnum = 1;
            if (arg2.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg2);
            Album.printList(p, arg1, pnum);
        } else if (cmd.equalsIgnoreCase("image")) {
            return mapFromImageFile(p, arg1, arg2);
        } else if (cmd.equalsIgnoreCase("paint")) {
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return true;
            }

            if (!arg1.equalsIgnoreCase("center")) {
                u.printMSG(p, "msg_paintcentercmd", "/photo paint center <picture name>");
                return true;
            }

            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);

            if (COCamera.isPhotoPaper(p.getItemInHand()) && (p.getItemInHand().getAmount() == 1)) {
                BufferedImage img = ImageCraft.createPixelArt2D(p, loc1, loc2, false, plg.burnPaintedWool);
                if (img == null) u.returnMSG(true, p, "msg_checkdimensions");
                short mapid = Album.addImage(p.getName(), arg2, img, false, true);
                if (mapid >= 0) {
                    p.getItemInHand().setType(Material.MAP);
                    p.getItemInHand().setDurability(mapid);
                    p.setItemInHand(COCamera.setName(p.getItemInHand(), arg2));
                    u.printMSG(p, "msg_newmapcreated", mapid);
                } else u.printMSG(p, "msg_cannotcreatemap");
            } else u.printMSG(p, "msg_needphotopaper");

        } else if (cmd.equalsIgnoreCase("repaint")) {
            if (!WoolSelect.isRegionSelected(p)) {
                u.printMSG(p, "msg_pxlnoselection");
                return true;
            }
            if (!arg1.equalsIgnoreCase("center")) {
                u.printMSG(p, "msg_paintcentercmd", "/photo repaint center <picture name>");
                return true;
            }
            Location loc1 = WoolSelect.getP1(p);
            Location loc2 = WoolSelect.getP2(p);
            if ((p.getItemInHand() != null) && (p.getItemInHand().getType() == Material.MAP) &&
                    Album.isObscuraMap(p.getItemInHand())) {

                short mapid = p.getItemInHand().getDurability();

                if (Album.isOwner(mapid, p)) {
                    BufferedImage img = ImageCraft.createPixelArt2D(p, loc1, loc2, false, plg.burnPaintedWool);
                    if ((img.getWidth() >= plg.minPixelart) && ((img.getHeight() >= plg.minPixelart))) {
                        Album.updateImage(mapid, p.getName(), arg2, img, false);
                        p.setItemInHand(COCamera.setName(p.getItemInHand(), arg2));
                        u.printMSG(p, "msg_newmapcreated", mapid);
                    } else u.printMSG(p, "msg_cannotcreatemap");
                } else u.printMSG(p, "msg_owurnotowner", 'c', '4', mapid);
            } else u.printMSG(p, "msg_needobscuramapinhand");


        } else if (cmd.equalsIgnoreCase("owner")) {
            if (arg1.matches("[0-9]*")) {
                short id = Short.parseShort(arg1);
                if (Album.isOwner(id, p)) {
                    Album.setOwner(id, arg2);
                    u.printMSG(p, "msg_ownerset", id, arg2);
                } else u.printMSG(p, "msg_owurnotowner", 'c', '4', id);
            } else u.printMSG(p, "msg_acneedmap");

        } else if (cmd.equalsIgnoreCase("files")) {
            COCamera.printFileList(p, arg1, arg2);
        } else if (cmd.equalsIgnoreCase("backgrounds")) {
            File dir = new File(plg.dirBackgrounds);
            int pnum = 1;
            if (arg2.matches("[1-9]+[1-9]*")) pnum = Integer.parseInt(arg2);
            List<String> ln = new ArrayList<String>();
            for (String fn : dir.list()) {
                if (arg1.isEmpty()) ln.add(fn);
                else if (fn.contains(arg1)) ln.add(fn);
            }
            u.printPage(p, ln, pnum, "msg_bglist", "msg_footer", true);
        } else if (cmd.equalsIgnoreCase("download")) {
            String fn = arg1;
            if (!fn.endsWith(".png")) fn = fn + ".png";
            File f = new File(plg.getDataFolder() + File.separator + "images" + File.separator + fn);
            BufferedImage img = ImageCraft.getImageByURL(arg2);
            try {
                ImageIO.write(img, "png", f);
                u.printMSG(p, "msg_imgsaved", fn);
            } catch (IOException e) {
                u.printMSG(p, "msg_imgsavefail");
            }
        } else if (cmd.equalsIgnoreCase("rename")) {
            String txt = arg2;
            short id = -1;
            if (u.isIntegerGZ(arg1)) id = Short.parseShort(arg1);
            else if (Album.isObscuraMap(p.getItemInHand())) {
                id = p.getItemInHand().getDurability();
                if (!arg1.isEmpty()) txt = arg1 + " " + txt;
            }
            if ((id > 0) && Album.isObscuraMap(id)) {
                if (Album.isOwner(id, p)) {
                    Album.setPictureName(id, txt);
                    u.printMSG(p, "msg_renamed", id, txt);
                    RenderHistory.forceUpdate(id);
                    COCamera.updateInventoryItems(p.getInventory());
                } else u.printMSG(p, "msg_owurnotowner", 'c', '4', id);
            } else u.printMSG(p, "msg_acneedmap", 'c');
        } else if (cmd.equalsIgnoreCase("head")) {
            Album.developPortrait(p, p.getName(), arg1, arg2);
        } else if (cmd.equalsIgnoreCase("top")) {
            Album.developTopHalfPhoto(p, p.getName(), arg1, arg2);
        } else if (cmd.equalsIgnoreCase("full")) {
            Album.developPhoto(p, p.getName(), arg1, arg2);
        } else return false;
        return true;

    }

    public boolean mapFromImageFile(Player p, String arg1, String arg2) {
        String dimension = arg2.isEmpty() ? "1x1" : arg2;
        int mx = -1;
        int my = -1;
        if (dimension.equalsIgnoreCase("1x1") || dimension.equalsIgnoreCase("resize")) {
            mx = 1;
            my = 1;
        } else if (dimension.equalsIgnoreCase("auto")) {
            mx = -1;
            my = -1;
        } else {
            String[] xy = dimension.split("x", 2);
            if (xy.length != 2) return u.returnMSG(true, p, "msg_wrongdimension", arg2);
            if (!u.isInteger(xy[0], xy[1])) return u.returnMSG(true, p, "msg_wrongdimension", arg2);
            if (!p.hasPermission("camera-obscura.image.large")) u.returnMSG(true, p, "msg_largeimageperm", 'c');
            mx = Integer.parseInt(xy[0]);
            my = Integer.parseInt(xy[1]);
        }
        List<BufferedImage> list = ImageCraft.getImageByName(p, arg1, mx, my);
        if (list == null || list.isEmpty()) u.returnMSG(true, p, "msg_failedtobuildimages", arg1);
        int amount = list.size();
        StringBuilder sb = new StringBuilder();

        if (p.getGameMode() == GameMode.CREATIVE || ItemUtil.hasItemInInventory(p, plg.photopaper, amount)) {
            for (int i = 0; i < list.size(); i++) {
                BufferedImage img = list.get(i);
                short mapid = Album.addImage(p.getName(), arg1 + "[" + (i + 1) + "/" + amount + "]", img, false, true);
                if (mapid >= 0) {
                    ItemUtil.giveItemOrDrop(p, new ItemStack(Material.MAP, 1, mapid));
                    if (i == 0) sb.append(Integer.toString(mapid));
                    else sb.append(",").append(Integer.toString(mapid));
                } else u.returnMSG(true, p, "msg_cannotcreatemap");
                if (p.getGameMode() != GameMode.CREATIVE)
                    ItemUtil.removeItemInInventory(p.getInventory(), plg.photopaper, amount);
            }
            u.printMSG(p, "msg_newmapscreated", amount, sb.toString());
            COCamera.updateInventoryItems(p.getInventory());
        } else u.printMSG(p, "msg_needphotopapers", amount);

        return true;
    }


    /**
     * @param player
     * @param strId
     * @return Id value of map in hand or strId (if it was typed by player)
     */
    private short getId(Player player, String strId) {
        short id = -1;
        if (strId.isEmpty()) {
            if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.MAP))
                id = player.getItemInHand().getDurability();
        } else if (u.isInteger(strId)) id = Short.parseShort(strId);
        return id;
    }


    private boolean setRotionAllowed(Player p, String strId) {
        short id = getId(p, strId);
        if (!Album.isOwner(id, p)) u.returnMSG(true, p, "msg_acurnotowner", 'c', '4', id);
        Album.setRotationAllowed(id, !Album.isRotationAllowed(id));
        u.printMSG(p, "msg_rotationallowed", id, Album.isRotationAllowed(id));
        return true;
    }

    private boolean setAllowCopy(Player p, String strId) {
        short id = getId(p, strId);
        if (id < 0)
            return u.returnMSG(true, p, "msg_acneedmap"); //u.returnMSG(false, p, "msg_mustholdortypeid",'c');
        if (!Album.isOwner(id, p)) u.returnMSG(true, p, "msg_acurnotowner", 'c', '4', id);
        Album.setAllowCopy(id, Album.getAllowCopy(id));
        if (Album.getAllowCopy(id)) u.printMSG(p, "msg_copyallowed", id);
        else u.printMSG(p, "msg_copyforbidden", id);
        return true;
    }

    private boolean setShowName(Player p, String strId) {
        short id = getId(p, strId);
        if (id < 0)
            return u.returnMSG(true, p, "msg_acneedmap"); //u.returnMSG(false, p, "msg_mustholdortypeid",'c');
        if (!Album.isOwner(id, p)) u.returnMSG(true, p, "msg_acurnotowner", 'c', '4', id);
        Album.setShowName(id, !Album.isNameShown(id));
        if (Album.isNameShown(id)) u.printMSG(p, "msg_willshowname", id);
        else u.printMSG(p, "msg_willnotshowname", id);
        RenderHistory.forceUpdate(id);
        return true;
    }

    private boolean removeImage(Player p, String strId) {
        short id = getId(p, strId);
        if (id < 0) return u.returnMSG(true, p, "msg_acneedmap"); //u.returnMSG(false, p, "msg_mustholdortypeid",'c');
        if (!Album.isOwner(id, p)) u.returnMSG(true, p, "msg_acurnotowner", 'c', '4', id);
        if (!Album.deleteImage(id)) u.returnMSG(true, p, "msg_mapremovefail", id);
        u.printMSG(p, "msg_mapremoved", id);
        COCamera.updateInventoryItems(p.getInventory());
        return true;
    }


}
