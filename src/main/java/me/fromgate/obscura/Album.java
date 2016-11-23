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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("deprecation")
public class Album {
    private static Obscura plg() {
        return Obscura.instance;
    }

    private static COUtil u() {
        return Obscura.instance.u;
    }

    private static Map<Short, COPhoto> album = new HashMap<Short, COPhoto>();
    private static List<Short> deletedmaps = new ArrayList<Short>();

    public static void init() {
        loadAlbum();
    }

    /*
     * Создание карты, forsnew=true - заставляет создавать именно новую (не используя старые, удаленные карты)
     *
     */
    public static short createMap(BufferedImage image, boolean reusedeleted, String name, boolean showname) {
        if ((!reusedeleted) || deletedmaps.isEmpty() || (deletedmaps.size() == 0)) return createNewMap(image);
        short id = deletedmaps.get(0);
        deletedmaps.remove(0);
        return updateMap(id, image);
    }

    public static boolean isObscuraMap(short id) {
        return album.containsKey(id);
    }

    public static boolean isDeletedMap(short id) {
        return deletedmaps.contains(id);
    }


    public static boolean isOwner(short id, String name) {
        if (!album.containsKey(id)) return false;
        return album.get(id).owner.equalsIgnoreCase(name);
    }

    public static boolean isOwner(short id, Player p) {
        if (!album.containsKey(id)) return false;
        return album.get(id).owner.equalsIgnoreCase(p.getName()) ||
                p.hasPermission("camera-obscura.owner.all");
    }

    public static void setOwner(short id, String newowner) {
        if (!album.containsKey(id)) return;
        album.get(id).owner = newowner;
        saveAlbum();
    }


    public static boolean isObscuraMap(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.MAP) return false;
        return album.containsKey(item.getDurability());
    }

    public static void setPictureName(short id, String name) {
        if (album.containsKey(id)) {
            album.get(id).name = name;
            saveAlbum();
        }
    }

    public static String getPictureName(short id) {
        if (album.containsKey(id)) return album.get(id).name;
        else if (deletedmaps.contains(id))
            return ChatColor.stripColor(u().getMSG("msg_removedimage", Short.toString(id)));
        return "";
    }


    public static void setAllowCopy(short id, boolean allowcopy) {
        if (album.containsKey(id)) {
            album.get(id).allowcopy = allowcopy;
            saveAlbum();
        }

    }

    public static boolean getAllowCopy(short id) {
        if (album.containsKey(id))
            return album.get(id).allowcopy;
        return false;
    }

    /*
     * Обновляем карту (заново навешиваем на неё рендерер)
     *
     */
    public static short updateMap(short id, BufferedImage image) {
        MapView map = Bukkit.getServer().getMap(id);
        map.setCenterX(Integer.MAX_VALUE);
        map.setCenterZ(Integer.MAX_VALUE);
        for (MapRenderer r : map.getRenderers())
            map.removeRenderer(r);
        //map.getRenderers().clear();
        CORenderer mr = new CORenderer(plg(), image);
        mr.initialize(map);
        map.addRenderer(mr);
        RenderHistory.forceUpdate(map.getId());
        return map.getId();
    }


    /*
     *  Создаем новую карту (новый ид)
     */
    public static short createNewMap(BufferedImage image) {
        MapView map = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));
        map.setCenterX(Integer.MAX_VALUE);
        map.setCenterZ(Integer.MAX_VALUE);
        CORenderer mr = new CORenderer(plg(), image);
        mr.initialize(map);
        map.getRenderers().clear();
        map.addRenderer(mr);
        RenderHistory.forceUpdate(map.getId());
        return map.getId();
    }


    public static boolean deleteImage(Short id) {
        if (album.containsKey(id)) {
            album.remove(id);
            MapView map = Bukkit.getServer().getMap(id);
            for (MapRenderer mr : map.getRenderers())
                if (mr instanceof CORenderer) map.removeRenderer(mr);
            deletedmaps.add(id);
            RenderHistory.forceUpdate(id);
            saveAlbum();
            return true;
        }
        return false;
    }


    public static short developPhoto(Player p) {
        return developPhoto(p, p.getName(), p.getName(), false, plg().reuseDeleted);
    }

    public static short developPhoto(Player p, String target_name) {
        return developPhoto(p, p.getName(), target_name, false, plg().reuseDeleted);
    }

    public static short developPhoto(Player p, String target_name, String background) {
        return developPhoto(p, target_name, background, false, plg().reuseDeleted);
    }

    public static short developPhoto(Player p, String owner, String target_name, String background) {
        return developPhoto(p, owner, target_name, background, false, plg().reuseDeleted);
    }

    public static short developPhoto(Player p, String owner, String target, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.createPhoto(target), p, target, allowcopy, reusedeleted);
    }

    public static short developPhoto(Player p, String owner, String target, String background, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.createPhoto(target, background), p, target, allowcopy, reusedeleted);
    }

    public static short developPainting(Player p, String filename, boolean fullsize, boolean allowcopy, boolean reusedeleted) {
        return developPainting(p, filename, filename, fullsize, false, plg().reuseDeleted);
    }

    public static short developPainting(Player p, String filename, String image_name, boolean fullsize, boolean allowcopy, boolean reusedeleted) {
        return addImage(p.getName(), image_name, ImageCraft.getResizedImageByName(filename, fullsize), allowcopy, reusedeleted);
    }


    public static short developAnyImage(BufferedImage img, Player p, String image_name, boolean allowcopy, boolean reusedeleted) {
        if (img != null) {
            if (COCamera.isPhotoPaper(p.getItemInHand()) && (p.getItemInHand().getAmount() == 1)) {
                if (!isLimitOver(p)) {
                    short mapid = addImage(p.getName(), image_name, img, allowcopy, reusedeleted);
                    if (mapid >= 0) {
                        p.getItemInHand().setType(Material.MAP);
                        p.getItemInHand().setDurability(mapid);
                        COCamera.setName(p.getItemInHand(), image_name);
                        u().printMSG(p, "msg_newmapcreated", mapid);
                    } else u().printMSG(p, "msg_cannotaddphoto");
                    return mapid;
                } else u().printMSG(p, "msg_overlimit", 'c');
            } else u().printMSG(p, "msg_needphotopaper");
        } else {
            u().printMSG(p, "msg_cannotcreatemap");
        }
        return -1;
    }

    public static short takePicture(BufferedImage img, Player p, String image_name, boolean allowcopy, boolean reusedeleted) {
        if (img == null) return -1;
        if (!COCamera.isCameraInHand(p)) return -1;
        if (COCamera.inventoryContainsPaper(p.getInventory())) {
            short mapid = addImage(p.getName(), image_name, img, allowcopy, reusedeleted);
            if (mapid > 0) {
                final short mpid = mapid;
                final String img_name = image_name;
                final Player tp = p;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plg(), new Runnable() {
                    public void run() {
                        tp.getInventory().removeItem(COCamera.newPhotoPaper());
                        COCamera.giveImageToPlayer(tp, mpid, img_name);
                        tp.getWorld().playSound(tp.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1.0f, 1.5f);
                        tp.getWorld().playSound(tp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                        u().printMSG(tp, "msg_newmapcreated", mpid);
                    }
                }, 1);
                return mapid;
            } else u().printMSG(p, "msg_cannotaddphoto");
        } else u().printMSG(p, "msg_phpapernotfound");
        return -1;
    }

    public static short takePicturePortrait(Player p, String model, String background) {
        return takePicture(ImageCraft.createPortrait(model, background), p, model, false, plg().reuseDeleted);
    }

    public static short takePicturePortrait(Player p, String model) {
        return takePicture(ImageCraft.createPortrait(model), p, model, false, plg().reuseDeleted);
    }

    public static short takePictureTopHalf(Player p, String model, String background) {
        return takePicture(ImageCraft.createTopHalfPhoto(model, background), p, model, false, plg().reuseDeleted);
    }

    public static short takePictureTopHalf(Player p, String model) {
        return takePicture(ImageCraft.createTopHalfPhoto(model), p, model, false, plg().reuseDeleted);
    }

    public static short takePicturePhoto(Player p, String model, String background) {
        return takePicture(ImageCraft.createPhoto(model, background), p, model, false, plg().reuseDeleted);
    }

    public static short takePicturePhoto(Player p, String model) {
        return takePicture(ImageCraft.createPhoto(model), p, model, false, plg().reuseDeleted);
    }

    public static short developTopHalfPhoto(Player p) {
        return developTopHalfPhoto(p, p.getName(), p.getName());
    }

    public static short developTopHalfPhoto(Player p, String owner, String target_name) {
        return developTopHalfPhoto(p, owner, target_name, false, plg().reuseDeleted);
    }

    public static short developTopHalfPhoto(Player p, String owner, String target, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.createTopHalfPhoto(target), p, target, allowcopy, reusedeleted);
    }


    public static short developTopHalfPhoto(Player p, String owner, String target_name, String background) {
        return developTopHalfPhoto(p, owner, target_name, background, false, plg().reuseDeleted);
    }

    public static short developTopHalfPhoto(Player p, String owner, String target, String background, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.createTopHalfPhoto(target, background), p, target, allowcopy, reusedeleted);
    }


    public static short developPortrait(Player p) {
        return developPortrait(p, p.getName(), p.getName());
    }

    public static short developPortrait(Player p, String owner, String target_name) {
        return developPortrait(p, owner, target_name, false, plg().reuseDeleted);
    }

    public static short developPortrait(Player p, String owner, String target_name, String background) {
        return developPortrait(p, owner, target_name, background, false, plg().reuseDeleted);
    }

    public static short developPortrait(Player p, String owner, String target, String background, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.createPortrait(target, background), p, target, allowcopy, reusedeleted);
    }

    public static short developPortrait(Player p, String owner, String target, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.createPortrait(target), p, target, allowcopy, reusedeleted);
    }

    public static short developImage(Player p, String filename, String imagename, boolean allowcopy, boolean reusedeleted) {
        return developAnyImage(ImageCraft.getImageByName(p, filename, true), p, imagename, allowcopy, reusedeleted);
    }


    public static short addImage(String owner, String name, BufferedImage image, boolean allowcopy, boolean reusedeleted) {
        if (image == null) return -1;
        Short id = createMap(image, reusedeleted, name, plg().defaultShowName);
        album.put(id, new COPhoto(owner, name, allowcopy));
        saveMapSource(id, image);
        saveAlbum();
        return id;
    }

    public static short updateImage(short id, String owner, String name, BufferedImage image, boolean allowcopy) {
        if (image == null) return -1;
        updateMap(id, image);
        album.put(id, new COPhoto(owner, name, allowcopy));

        saveMapSource(id, image);
        saveAlbum();
        return id;
    }


    public static void saveMapSource(short id, BufferedImage image) {
        if ((id >= 0) && (image != null)) {
            try {
                File f = new File(plg().getDataFolder() + File.separator + "album" + File.separator + Short.toString(id) + ".png");
                if (f.exists()) f.delete();
                ImageIO.write(image, "png", f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadMapSource(short id, String name, boolean showname) {
        if (id < 0) return;
        BufferedImage image = null;
        File f = new File(plg().getDataFolder() + File.separator + "album" + File.separator + Short.toString(id) + ".png");
        if (!f.exists()) return;
        try {
            image = ImageIO.read(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateMap(id, image);
    }


    public static void saveAlbum() {
        try {
            File f = new File(plg().getDataFolder() + File.separator + "album.yml");
            if (f.exists()) f.delete();
            if (album.size() > 0) {
                f.createNewFile();
                YamlConfiguration cfg = new YamlConfiguration();
                for (short id : album.keySet()) {
                    COPhoto ph = album.get(id);
                    cfg.set(Short.toString(id) + ".owner", ph.owner);
                    cfg.set(Short.toString(id) + ".name", ph.name);
                    cfg.set(Short.toString(id) + ".allow-copy", ph.allowcopy);
                    cfg.set(Short.toString(id) + ".show-name", ph.showname);
                    cfg.set(Short.toString(id) + ".allow-rotation", ph.allowrotate);
                }
                if (deletedmaps.size() > 0)
                    cfg.set("deleted-maps", deletedmaps);
                cfg.save(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAlbum() {
        try {
            File f = new File(plg().getDataFolder() + File.separator + "album.yml");
            if (f.exists()) {
                album.clear();
                YamlConfiguration cfg = new YamlConfiguration();
                cfg.load(f);
                for (String str_id : cfg.getKeys(false)) {
                    if (str_id.matches("[0-9]*")) {
                        short id = Short.parseShort(str_id);
                        String name = cfg.getString(str_id + ".name");
                        boolean showname = cfg.getBoolean(str_id + ".show-name", plg().defaultShowName);
                        boolean allowrotate = cfg.getBoolean(str_id + ".allow-rotation", false);
                        loadMapSource(id, name, showname);
                        album.put(id, new COPhoto(cfg.getString(str_id + ".owner", "unknown"),
                                name,
                                cfg.getBoolean(str_id + ".allow-copy", false),
                                showname, allowrotate));
                    } else if (str_id.equalsIgnoreCase("deleted-maps")) {
                        List<String> strlist = new ArrayList<String>();
                        deletedmaps.clear();
                        strlist = cfg.getStringList("deleted-maps");
                        for (String str : strlist)
                            if (str.matches("[0-9]*")) deletedmaps.add(Short.parseShort(str));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public static void printList(Player p, String owner, int pnum) {
        if (album.size() > 0) {
            List<String> ln = new ArrayList<String>();
            for (short id : album.keySet()) {
                COPhoto ph = album.get(id);

                if (ph.owner.equalsIgnoreCase(owner))
                    ln.add("&a" + id + " [&2" + ph.owner + "&a] : &e" + ph.name + " &a(" + u().EnDis(u().getMSGnc("msg_allowcopy"), ph.allowcopy) +
                            "&a, " + u().EnDis(u().getMSGnc("msg_displayname"), ph.showname) + "&a)");
            }

            u().printPage(p, ln, pnum, "msg_albumlist", "msg_footer", true);
        } else u().printMSG(p, "msg_albumempty", '6');
        u().printMSG(p, "msg_albumtotal", album.size(), deletedmaps.size());
    }

    public static int getPictureCount() {
        return album.size();
    }

    public static int getDeletedCount() {
        return deletedmaps.size();
    }


    public static boolean isRotationAllowed(short id) {
        if (!album.containsKey(id)) return true;
        return album.get(id).allowrotate;
    }

    public static boolean setRotationAllowed(short id, boolean allowed) {
        if (!album.containsKey(id)) return true;
        album.get(id).allowrotate = allowed;
        return true;
    }

    public static boolean isCopyAllowed(short id) {
        if (!album.containsKey(id)) return true;
        return album.get(id).allowcopy;
    }

    public static boolean isNameShown(short id) {
        if (!album.containsKey(id)) return plg().defaultShowName;
        return album.get(id).showname;
    }

    public static void setShowName(short id, boolean showname) {
        if (album.containsKey(id)) {
            album.get(id).showname = showname;
            saveAlbum();
        }
    }


    public static boolean isLimitOver(Player p) {
        if (p.hasPermission("camera-obscura.owner.limit")) return false;
        return (plg().picsPerOwner <= getOwnerCount(p.getName()));
    }

    public static boolean isLimitOver(String pname) {
        Player tp = Bukkit.getPlayerExact(pname);
        if ((tp != null) && tp.hasPermission("camera-obscura.owner.limit")) return false;
        return (plg().picsPerOwner <= getOwnerCount(pname));
    }

    public static int getOwnerCount(String name) {
        int count = 0;
        if (album.size() > 0) {
            for (Short id : album.keySet())
                if (album.get(id).owner.equalsIgnoreCase(name)) count++;
        }
        return count;
    }


}
