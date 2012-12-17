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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class COWoolSelect {
	
	public static int wand=288; //feather

	public static void setBrushMode(Plugin plg, Player p, boolean brmode){
		if (brmode) p.setMetadata("obscura-brush-mode", new FixedMetadataValue (plg, true));
		else {
			if (p.hasMetadata("obscura-brush-mode")) p.removeMetadata("obscura-brush-mode", plg);
			if (p.hasMetadata("obscura-sel-p1")) p.removeMetadata("obscura-sel-p1", plg);
			if (p.hasMetadata("obscura-sel-p2")) p.removeMetadata("obscura-sel-p2", plg);
		}
	}

	
	public static boolean getBrushMode(Player p){
		return p.hasMetadata("obscura-brush-mode");
	}
	
	public static void setP1(Plugin plg, Player p, Location loc){
		 p.setMetadata("obscura-sel-p1-world", new FixedMetadataValue (plg, loc.getWorld().getName()));
		 p.setMetadata("obscura-sel-p1-x", new FixedMetadataValue (plg, loc.getBlockX()));
		 p.setMetadata("obscura-sel-p1-y", new FixedMetadataValue (plg, loc.getBlockY()));
		 p.setMetadata("obscura-sel-p1-z", new FixedMetadataValue (plg, loc.getBlockZ()));
	}
	
	public static void setP2(Plugin plg, Player p, Location loc){
		 p.setMetadata("obscura-sel-p2-world", new FixedMetadataValue (plg, loc.getWorld().getName()));
		 p.setMetadata("obscura-sel-p2-x", new FixedMetadataValue (plg, loc.getBlockX()));
		 p.setMetadata("obscura-sel-p2-y", new FixedMetadataValue (plg, loc.getBlockY()));
		 p.setMetadata("obscura-sel-p2-z", new FixedMetadataValue (plg, loc.getBlockZ()));
	}

	
	public static Location getP1(Player p){
		if (p.hasMetadata("obscura-sel-p1-world")){
			World w = Bukkit.getServer().getWorld(p.getMetadata("obscura-sel-p1-world").get(0).asString());
			int x = p.getMetadata("obscura-sel-p1-x").get(0).asInt();
			int y = p.getMetadata("obscura-sel-p1-y").get(0).asInt();
			int z = p.getMetadata("obscura-sel-p1-z").get(0).asInt();
			return new Location (w,x,y,z);
		}
			//return (Location) p.getMetadata("obscura-sel-p1").get(0).;
		 return null;
	}
	
	public static Location getP2(Player p){
		if (p.hasMetadata("obscura-sel-p2-world")){
			World w = Bukkit.getServer().getWorld(p.getMetadata("obscura-sel-p2-world").get(0).asString());
			int x = p.getMetadata("obscura-sel-p2-x").get(0).asInt();
			int y = p.getMetadata("obscura-sel-p2-y").get(0).asInt();
			int z = p.getMetadata("obscura-sel-p2-z").get(0).asInt();
			return new Location (w,x,y,z);
		}
		 return null;
	}
	
	public static void clearSelection (Plugin plg, Player p){
		if (p.hasMetadata("obscura-sel-p1-world")) p.removeMetadata("obscura-sel-p1-world", plg);
		if (p.hasMetadata("obscura-sel-p1-x")) p.removeMetadata("obscura-sel-p1-x", plg);
		if (p.hasMetadata("obscura-sel-p1-y")) p.removeMetadata("obscura-sel-p1-y", plg);
		if (p.hasMetadata("obscura-sel-p1-z")) p.removeMetadata("obscura-sel-p1-z", plg);
		if (p.hasMetadata("obscura-sel-p2-world")) p.removeMetadata("obscura-sel-p2-world", plg);
		if (p.hasMetadata("obscura-sel-p2-x")) p.removeMetadata("obscura-sel-p2-x", plg);
		if (p.hasMetadata("obscura-sel-p2-y")) p.removeMetadata("obscura-sel-p2-y", plg);
		if (p.hasMetadata("obscura-sel-p2-z")) p.removeMetadata("obscura-sel-p2-z", plg);	}

	public static boolean isRegionSelected(Player p){
		return (p.hasMetadata("obscura-sel-p1-world"))&&(p.hasMetadata("obscura-sel-p2-world"));
	}
	
	public static boolean isSelectionValid (Player p){
		if (!(p.hasMetadata("obscura-sel-p1-world"))&&(p.hasMetadata("obscura-sel-p2-world"))) return false;
		Location loc1 = getP1(p);
		Location loc2 = getP2(p);
		if ((loc1==null)||(loc2==null)) return false;
		if (!loc1.getWorld().equals(loc2.getWorld())) return false;
		if ((loc1.getBlockX() == loc2.getBlockX())&&
				(loc1.getBlockY() == loc2.getBlockY())&&
				(loc1.getBlockZ() == loc2.getBlockZ())) return false;
		return true;
	}

}
