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

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("deprecation")
public class CORenderer extends MapRenderer {
    Obscura plg;
    BufferedImage img;

    public CORenderer(Obscura plg, final BufferedImage img) {
        super(false);
        this.plg = plg;
        this.img = img;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player p) {
        if (RenderHistory.isRendered(p, map.getId())) return;
        for (int j = 0; j < 128; j++)
            for (int i = 0; i < 128; i++)
                canvas.setPixel(i, j, (byte) 0);
        if (this.img != null) {
            short id = map.getId();
            if ((Album.isNameShown(id)))
                canvas.drawImage(0, 0, ImageCraft.writeTextOnImage(img, plg.nameX, plg.nameY, plg.fontName, plg.fontSize, plg.nameColor, plg.stroke, plg.strokeColor, Album.getPictureName(id)));
            else canvas.drawImage(0, 0, img);
        }
        p.sendMap(map);
    }

    public BufferedImage getImage() {
        return this.img;
    }


}
