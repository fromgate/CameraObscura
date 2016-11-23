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
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RenderHistory {
    private static HashMap<String, Set<Short>> rh = new HashMap<String, Set<Short>>();

    public static void clearHistory(Player p) {
        clearHistory(p.getName());
    }

    public static void clearHistory(String pname) {
        if (rh.containsKey(pname)) rh.remove(pname);
    }

    public static void clearHistory() {
        rh.clear();
    }

    public static boolean isRendered(Player p, short map_id) {
        String pn = p.getName();
        if (rh.containsKey(pn)) {
            if (rh.get(pn).contains(map_id)) return true;
        } else rh.put(pn, new HashSet<Short>());
        rh.get(pn).add(map_id);
        return false;
    }

    public static void forceUpdate(short id) {
        for (String pn : rh.keySet())
            if (rh.get(pn).contains(id)) rh.get(pn).remove(id);
    }


    /*
     * Send UnRendered map / SpigotFix
     */
    @SuppressWarnings("deprecation")
    public static void sendMap(Player p, short id) {
        if (!isRendered(p, id)) {
            p.sendMap(Bukkit.getMap(id));
        }

    }


}
