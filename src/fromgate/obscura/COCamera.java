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

	public static boolean isBlockIsPartOfCamera(Obscura plg, Block b){
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

	public static boolean isClickedButtonIsLens (Obscura plg, Block button){
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


	public static boolean isCameraInHand (Obscura plg, Player p){
		return ((p.getItemInHand()!=null)&&
				(p.getItemInHand().getTypeId()==plg.camera_id)&&
				(p.getInventory().getItemInHand().getDurability() == plg.camera_data));
	}

	public static boolean isCamera (Obscura plg, ItemStack item){
		return ((item != null)&&
				(item.getTypeId()==plg.camera_id)&&
				(item.getDurability()==plg.camera_data));
	}

	public static boolean isPhotoPaper (Obscura plg, ItemStack item){
		return ((item != null)&&
				(item.getTypeId()==plg.photopaper_id)&&
				(item.getDurability()==plg.photopaper_data));
	}

	public static ItemStack newPhotoPaper (Obscura plg){
		return newPhotoPaper(plg,1);
	}

	public static ItemStack newPhotoPaper (Obscura plg,int amount){
		ItemStack item = setName (new ItemStack (plg.photopaper_id,amount, plg.photopaper_data), plg.u.MSGnc("msg_photopaper"));
		item.setDurability(plg.photopaper_data);
		return item;
	}

	public static void updateInventoryItems (Obscura plg, Inventory inv){
		if ((inv.getSize()>0)&&inv.contains(Material.MAP)){
			ItemStack[] items = inv.getContents();
			for (ItemStack item : items){
				if (item == null) continue;
				if (!((item.getType()==Material.MAP)||isPhotoPaper(plg,item)||isCamera(plg,item))) continue;
				updateItemName(plg, item);
			}
		}
	}

		public static void updateItemName (Obscura plg, ItemStack item){
			updateMapName (plg,item);
			updateCameraName (plg,item); 
			updatePhotoPaperName (plg,item); 
		}

		public static String getName (ItemStack item){
			ItemMeta im = item.getItemMeta();
			if (im.hasDisplayName()) return im.getDisplayName(); 
			return "";
		}

		public static void updateMapName (Obscura plg, ItemStack item){
			if ((item==null)||(item.getType()!=Material.MAP)) return;
			short id = item.getDurability();
			String name_album = plg.album.getPictureName(id);
			String name_item = getName (item);
			if (!name_album.equals(name_item)) setName (item, name_album);
		}

		public static void updateCameraName (Obscura plg, ItemStack item){
			if (!isCamera(plg, item)) return;
			String name_camera = plg.u.MSGnc("msg_photocamera");
			String name_item = getName (item);
			if (!name_camera.equals(name_item)) setName (item, name_camera);
		}

		public static void updatePhotoPaperName (Obscura plg, ItemStack item){
			if (!isCamera(plg, item)) return;
			String name_paper = plg.u.MSGnc("msg_photopaper");
			String name_item = getName (item);
			if (!name_paper.equals(name_item)) setName (item, name_paper);
		}



		public static ItemStack newCamera (Obscura plg){
			ItemStack item = setName (new ItemStack (plg.camera_id,plg.camera_data), plg.u.MSGnc("msg_photocamera"));
			item.setDurability(plg.camera_data);
			return item;
		}

		public static ItemStack newImageItem (Obscura plg, short mapid, String image_name){
			ItemStack photo = setName (new ItemStack (Material.MAP),image_name); 
			photo.setDurability(mapid);
			return photo;
		}

		public static void giveImageToPlayer(Obscura plg, Player p, short mapid, String image_name){
			ItemStack photo = newImageItem (plg, mapid, image_name);
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

		public static void initRecipes(Obscura plg){
			ShapedRecipe camera = new ShapedRecipe (newCamera(plg));
			camera.shape("B  ","WRW","WGW");
			camera.setIngredient('B', Material.STONE_BUTTON);
			camera.setIngredient('W', Material.IRON_INGOT);
			//camera.setIngredient('W', Material.WOOD);
			camera.setIngredient('R', Material.DIODE);
			camera.setIngredient('G', Material.DIAMOND);
			//camera.setIngredient('G', Material.GLASS);
			plg.getServer().addRecipe(camera);

			ItemStack paperstack = newPhotoPaper (plg,3);
			ShapedRecipe paper = new ShapedRecipe (paperstack);
			paper.shape("III","RGB","PPP");
			paper.setIngredient('I', Material.INK_SACK);
			paper.setIngredient('R', Material.INK_SACK,1);
			paper.setIngredient('G', Material.INK_SACK,2);
			paper.setIngredient('B', Material.INK_SACK,4);
			paper.setIngredient('P', Material.PAPER);
			plg.getServer().addRecipe(paper);
		}


		public static boolean inventoryContains(Player p, ItemStack item) {
			return inventoryContains (p.getInventory(), item);
		}

		public static boolean inventoryContains(Inventory inv, ItemStack item) {
			int amount = 0;
			for (ItemStack stack : inv.getContents()) {
				if (stack == null) continue;
				if ((stack.getType() == item.getType()) && (stack.getDurability() == item.getDurability())) amount += stack.getAmount();
			}
			return (amount >= item.getAmount());
		}

		public static boolean inventoryContainsPicture (Obscura plg, Inventory inv) {
			for (ItemStack stack : inv.getContents()) {
				if (stack == null) continue;
				if (plg.album.isObscuraMap(stack)) return true;
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

		public static boolean setupEconomy(Obscura plg){
			RegisteredServiceProvider<Economy> economyProvider = plg.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				plg.economy = economyProvider.getProvider();
			}
			return (plg.economy != null);
		}
		
		

	}
