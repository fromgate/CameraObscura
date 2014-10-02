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
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Button;
import org.bukkit.plugin.RegisteredServiceProvider;


public class COCamera {
    private static Obscura plg(){
        return Obscura.instance;
    }
    
    
	public static boolean isBlockIsPartOfCamera(Block b){
		if (!((b.getType()==Material.FENCE)||(b.getType()==Material.NOTE_BLOCK)||(b.getType()==Material.STONE_BUTTON))) return false;
		Block noteblock;
		if (b.getType()==Material.FENCE) {
			noteblock = b.getRelative(BlockFace.UP);
		} else if (b.getType()==Material.NOTE_BLOCK){
			noteblock = b;
		} else if ((b.getType()==Material.STONE_BUTTON)&&(b.getState().getData() instanceof Button)){
			Button btn = (Button) b.getState().getData();
			noteblock = b.getRelative(btn.getAttachedFace());
		} else return false;
		return isNoteBlockIsCamera (noteblock); 
	} 

	public static boolean isClickedButtonIsLens (Block button){
		if (button.getState().getData() instanceof Button){
			Button btn = (Button) button.getState().getData();
			Block b = button.getRelative(btn.getAttachedFace());
			return isNoteBlockIsCamera (b);
		}
		return false;
	}

	public static boolean isNoteBlockIsCamera (Block b){
		return (b.getType()==Material.NOTE_BLOCK)&&
				(b.getRelative(BlockFace.DOWN).getType()==Material.FENCE)&&
				isButtonPlacedOnTheNoteBlock(b);
	}

	public static boolean isButtonPlacedOnTheNoteBlock (Block b){
		if (b.getType() != Material.NOTE_BLOCK) return false;
		if (b.getRelative(BlockFace.NORTH).getType()==Material.STONE_BUTTON) return true;
		if (b.getRelative(BlockFace.SOUTH).getType()==Material.STONE_BUTTON) return true;
		if (b.getRelative(BlockFace.EAST).getType()==Material.STONE_BUTTON) return true;
		if (b.getRelative(BlockFace.WEST).getType()==Material.STONE_BUTTON) return true;
		return false;
	}

	public static Block getLensFromTripodCamera (Block block){
		if ((block.getType() != Material.NOTE_BLOCK)&&(block.getType() != Material.FENCE)) return null;
		Block b = block;
		if (block.getType()==Material.FENCE) b = block.getRelative(BlockFace.UP);
		if (isButtonConnectedToBlock (b.getRelative(BlockFace.NORTH),b)) return b.getRelative(BlockFace.NORTH);
		if (isButtonConnectedToBlock (b.getRelative(BlockFace.SOUTH),b)) return b.getRelative(BlockFace.SOUTH);
		if (isButtonConnectedToBlock (b.getRelative(BlockFace.WEST),b)) return b.getRelative(BlockFace.WEST);
		if (isButtonConnectedToBlock (b.getRelative(BlockFace.EAST),b)) return b.getRelative(BlockFace.EAST);
		return null;
	}

	public static boolean isButtonConnectedToBlock (Block button, Block block){
		if ((button!=null)&&(button.getType()==Material.STONE_BUTTON)&&
				(button.getState().getData() instanceof Button)){
			Button btn = (Button) button.getState().getData();
			return button.getRelative(btn.getAttachedFace()).equals(block);
		}

		return false;
	}


	public static boolean isCameraInHand (Player p){
		if (p.getItemInHand()==null) return false;
		return isCamera (p.getItemInHand());
	}

	public static boolean isCamera (ItemStack item){
	    return ItemUtil.compareItemStr(item, plg().camera);
	}

	public static boolean isPhotoPaper (ItemStack item){
	    return ItemUtil.compareItemStr(item, plg().photopaper);
	}

	public static ItemStack newPhotoPaper (){
		return newPhotoPaper(1);
	}

	// теперь это только для карт!
	public static void updateInventoryItems (Inventory inv){
		try{
			if ((inv == null)||(inv.getSize()==0)||(!inv.contains(Material.MAP))) return;
			ItemStack[] items = inv.getContents();
			for (ItemStack item : items){
				if (item == null) continue;
				if (!((item.getType()==Material.MAP)||isPhotoPaper(item)||isCamera(item))) continue;
				updateItemName(item);
			}
		} catch (Exception e){
		}
	}

	
	public static void updateItemName (ItemStack item){
		updateMapName (item);
	}

	public static String getName (ItemStack item){
		ItemMeta im = item.getItemMeta();
		if (im.hasDisplayName()) return im.getDisplayName(); 
		return "";
	}

	public static void updateMapName (ItemStack item){
		if ((item==null)||(item.getType()!=Material.MAP)) return;
		short id = item.getDurability();
		String name_album = plg().album.getPictureName(id);
        // don't rename the item as the map may be used by another plugin
        if (name_album.isEmpty()) return;
		String name_item = getName (item);
		if (!name_album.equals(name_item)) setName (item, name_album);
	}

	public static ItemStack newCamera (){
	    ItemStack item = ItemUtil.parseItemStack(plg().camera);
	    item.setAmount(1);
		return item;
	}
	
	
	public static ItemStack newPhotoPaper (int amount){
	    ItemStack item = ItemUtil.parseItemStack(plg().photopaper);
	    item.setAmount(amount);
		return item;
	}

	public static ItemStack newImageItem (short mapid, String image_name){
		ItemStack photo = setName (new ItemStack (Material.MAP),image_name); 
		photo.setDurability(mapid);
		return photo;
	}

	public static void giveImageToPlayer(Player p, short mapid, String image_name){
		ItemStack photo = newImageItem (mapid, image_name);
		if ((p.getInventory().addItem(new ItemStack[] {photo})).size()>0){
			Item photoitem = p.getWorld().dropItemNaturally(p.getLocation(), photo);
			photoitem.setItemStack(photo);
		}
	}
	

	
	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		return item.clone();
	}

	@SuppressWarnings("deprecation")
    public static void initRecipes(){
		ShapedRecipe camera = new ShapedRecipe (newCamera());
		camera.shape("B  ","WRW","WGW");
		camera.setIngredient('B', Material.STONE_BUTTON);
		camera.setIngredient('W', Material.IRON_INGOT);
		camera.setIngredient('R', Material.DIODE);
		camera.setIngredient('G', Material.DIAMOND);
		plg().getServer().addRecipe(camera);

		ItemStack paperstack = newPhotoPaper (3);
		ShapedRecipe paper = new ShapedRecipe (paperstack);
		paper.shape("III","RGB","PPP");
		paper.setIngredient('I', Material.INK_SACK);
		paper.setIngredient('R', Material.INK_SACK,1);
		paper.setIngredient('G', Material.INK_SACK,2);
		paper.setIngredient('B', Material.INK_SACK,4);
		paper.setIngredient('P', Material.PAPER);
		plg().getServer().addRecipe(paper);
	}


	public static boolean inventoryContainsPaper (Inventory inv) {
	   return ItemUtil.hasItemInInventory(inv, plg().photopaper);
	}
	
	public static boolean inventoryContainsCamera (Inventory inv) {
		for (ItemStack stack : inv.getContents()) {
			if (stack == null) continue;
			if (isCamera (stack)) return true;
		}
		return false;
	}

	public static boolean inventoryContainsPicture (Inventory inv) {
		for (ItemStack stack : inv.getContents()) {
			if (stack == null) continue;
			if (plg().album.isObscuraMap(stack)) return true;
		}
		return false;
	}

	public static Sign getObscuraSign(Block b){
		if (b.getType()!=Material.WALL_SIGN) return null;
		if (!(b.getState() instanceof Sign)) return null;
		Sign sign = (Sign) b.getState();
		if (sign.getLine(1).isEmpty()) return null; 
		String type = ChatColor.stripColor(sign.getLine(1));
		if (type.equalsIgnoreCase("[head shot]")||
				type.equalsIgnoreCase("[top half]")||
				type.equalsIgnoreCase("[full length]")||
				type.equalsIgnoreCase("[photo]")) return sign;
		return null;
	}

	public static String getObscuraOwner (Sign sign){
		if (!sign.getLine(0).isEmpty()) return ChatColor.stripColor(sign.getLine(0));
		return "unknown";
	}

	public static int getObscuraFocus (Sign sign){
		if (!sign.getLine(1).isEmpty()) {
			String type = ChatColor.stripColor(sign.getLine(1));
			if (type.equalsIgnoreCase("[head shot]")) return 1;
			else if (type.equalsIgnoreCase("[top half]")) return 2;
			else if (type.equalsIgnoreCase("[full length]")) return 3;
		}
		return 0;
	}

	public static double getObscuraPrice (Sign sign){
		String str = ChatColor.stripColor(sign.getLine(2));
		if (str.matches("[0-9]*.[0-9]*")||str.matches("[0-9]*")) return Double.parseDouble(str);
		return 0.0;
	}

	public static String getObscuraBackground (Sign sign){
		if (!sign.getLine(3).isEmpty()) return ChatColor.stripColor(sign.getLine(3));
		return "default";
	}

	public static boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = plg().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			plg().economy = economyProvider.getProvider();
		}
		return (plg().economy != null);
	}

	public static void printFileList (Player p, String... str){
		String pdir = ""; 
		if (plg().personalfolders) pdir = p.getName()+File.separator;
		int pnum = 1;
		String mask = "";
		if (str.length>0)
			for (int i = 0; i<str.length;i++){
				if (str[i].toLowerCase().startsWith("p:")&&plg().personalfolders){
					if (p.hasPermission("camera-obscura.files.all")) pdir = str[i].substring(2)+File.separator;
				} else if (str[i].matches("[1-9]+[0-9]*")){
					pnum = Integer.valueOf(str[i]);
				} else {
					mask = str[i];
				}
			}
		File dir = new File (plg().d_images+pdir);
		if (dir.exists()){
			
			List<String> ln = new ArrayList<String>();
			for (String fn : dir.list()){
				if (mask.isEmpty()) ln.add(fn);
				else if (fn.contains(mask)) ln.add(fn);
			}
			plg().u.printPage(p, ln, pnum, "msg_filelist", "msg_footer", true);
		} else plg().u.printMSG(p, "msg_dirnotexist",pdir);
	}



}
