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
        super(true);
        this.plg = plg;
        this.img = img;
    }


    @Override
    public void render(MapView map, MapCanvas canvas, Player p) {
        if (!plg.rh.isRendered(p, map.getId())) {
            for (int j = 0; j < 128; j++)
                for (int i = 0; i < 128; i++)
                    canvas.setPixel(i, j, (byte) 0);
            if (this.img != null) {
                short id = map.getId();
                if ((plg.album.isNameShown(id)))
                    canvas.drawImage(0, 0, plg.ic.writeTextOnImage(img, plg.name_x, plg.name_y, plg.font_name, plg.font_size, plg.name_color, plg.stroke, plg.stroke_color, plg.album.getPictureName(id)));
                else canvas.drawImage(0, 0, img);
                /*if ((plg.album.isNameShown(id))) drawImage(canvas,plg.ic.writeTextOnImage(img, plg.name_x, plg.name_y, plg.font_name, plg.font_size, plg.name_color, plg.stroke,plg.stroke_color, plg.album.getPictureName(id)));
                else drawImage(canvas,img);*/

            }
        }
    }


    public void drawImage(MapCanvas canvas, BufferedImage img) {
        int mx = Math.min(128, img.getWidth());
        int my = Math.min(128, img.getHeight());
        for (int x = 0; x < mx; x++)
            for (int y = 0; y < my; y++)
                canvas.setPixel(x, y, Palette.matchColor(new Color(img.getRGB(x, y))));
    }
}
