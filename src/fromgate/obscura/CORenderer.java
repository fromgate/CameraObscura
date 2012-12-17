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

import java.awt.Image;
import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class CORenderer extends MapRenderer {
	Obscura plg;
	Image img;

	public CORenderer (Obscura plg, final BufferedImage img){
		super (true);
		this.plg = plg;
		this.img = img;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player p) {
		if (!plg.rh.isRendered(p, map.getId())){
			for (int j = 0; j < 128; j++) 
				for (int i = 0; i < 128; i++)
					canvas.setPixel(i, j, (byte) 0);
			if (this.img != null) canvas.drawImage(0, 0, img);
			canvas.drawText(2, 127-MinecraftFont.Font.getHeight(), MinecraftFont.Font, "§54;Camera Obscura");
		}
	}
}
