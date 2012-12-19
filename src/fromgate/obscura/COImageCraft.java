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
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class COImageCraft {
	Obscura plg;
	COUtil u;
	Map<String,BufferedImage> cache = new HashMap<String,BufferedImage>();
	public COImageCraft(Obscura plg){
		this.plg = plg;
		this.u = plg.u;
	}


	/*
	 *  Сюда добавить изменение размера до 128х128
	 */
	public BufferedImage getImageByURL (String strurl){
		BufferedImage img = null;
		try {
			URL url = new URL (strurl);
			img = ImageIO.read(url);
		} catch (Exception e){
		}

		if (img == null){
			try {
				img = ImageIO.read(this.getClass().getResourceAsStream("default.png"));
			} catch (Exception e3){
			}
		}
		return img;
	}


	public BufferedImage getResizedImageByName (String filename, boolean fullsize){
		BufferedImage tmp = getImageByName (filename);
		if (tmp.getWidth()!=tmp.getHeight()){
			BufferedImage ntmp = new BufferedImage (Math.max(tmp.getWidth(), tmp.getHeight()),Math.max(tmp.getWidth(), tmp.getHeight()),BufferedImage.TYPE_INT_ARGB); 
			ntmp.getGraphics().drawImage(tmp, (ntmp.getWidth()/2)-(tmp.getWidth()/2), (ntmp.getHeight()/2)-(tmp.getHeight()/2), null);
			tmp = ntmp;
		}

		BufferedImage img = new BufferedImage (128,128,BufferedImage.TYPE_INT_ARGB);

		if (fullsize) img.getGraphics().drawImage(tmp.getScaledInstance(128, 128, 1),0,0,null);
		else if ((tmp.getWidth()<128)&&((tmp.getHeight()<128))) img.getGraphics().drawImage(tmp, (img.getWidth()/2)-(tmp.getWidth()/2), (img.getHeight()/2)-(tmp.getHeight()/2), null); 
		else img.getGraphics().drawImage(tmp, 0, 0, null);

		return img;
	}

	
	public BufferedImage getImageByName (String filename){
		return getImageFromDirByName (plg.d_images,filename);
	}
	
	public BufferedImage getBackgroundByName (String filename){
		return getImageFromDirByName (plg.d_backgrounds,filename);
	}


	public BufferedImage getImageFromDirByName (String dir, String filename){
		BufferedImage img = null;
		String fn = filename;
		if (!fn.endsWith(".png")) fn = fn+".png";

		File f = new File (dir+fn);
		if (f.exists())
			try {
				img = ImageIO.read(f);
			} catch (Exception e){
			}
		
		if (img == null) {
			f = new File (dir+"default.png");
			if (f.exists())
				try {
					img = ImageIO.read(f);
				} catch (Exception e){
				}			
		}
		try {
			if (img == null) img = ImageIO.read(this.getClass().getResourceAsStream("default.png"));
		} catch (Exception e){
		}

		return img;
	}



	public BufferedImage getSkinByName (String name){
		BufferedImage img = null;
		String strurl = plg.skinurl+name+".png";
		File f = new File (plg.d_skins+name+".png");  //

		if (f.exists()){
			try {
				img = ImageIO.read(f);
				
			} catch (Exception e){
			}
		}
		if (img == null){
			try {
				URL url = new URL (strurl);
				img = ImageIO.read(url);
			} catch (Exception e){
			}
		}
		
		

		f = new File (plg.d_skins+"default.png");
		if ((img == null)&&f.exists()){
			try {
				img = ImageIO.read(f);
			} catch (Exception e){
			}
		}
		if (img == null){
			try {

				img = ImageIO.read(this.getClass().getResourceAsStream("default_skin.png"));
			} catch (Exception e){
			}

		}
		return img;
	}


	public void updateSkinCache (Player p){
		final String pname = p.getName();
		Bukkit.getScheduler().runTaskLaterAsynchronously(plg, new Runnable(){
			public void run (){
				BufferedImage skin = getSkinByName (pname);
				if (skin!=null) cache.put(pname, skin);
			}
		}, 3);

	}




	public BufferedImage getSkin(Player p){
		return  getSkin (p.getName());
	}


	public BufferedImage getSkin(String pname){
		BufferedImage img = null;
		if (cache.containsKey(pname)) img = cache.get(pname);
		else img = this.getSkinByName(pname); 
		return img;
	}


	/*
	 *  В скине по умолчанию лицо занимает координаты 8..15 х 8..15 (или 7..14 х 7..14)
	 * 
	 */
	public BufferedImage getHeadFromSkin (BufferedImage skin){
		BufferedImage face = null;
		if (skin != null){
			face = new BufferedImage (8,8,BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x<8; x++)
				for (int y = 0; y<8; y++)
					face.setRGB(x, y, skin.getRGB(x+8, y+8));
		}
		return face;
	}

	public BufferedImage getTorsoFromSkin (BufferedImage skin){
		BufferedImage torso = null;
		if (skin != null){
			torso = new BufferedImage (8,12,BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x<8; x++)
				for (int y = 0; y<12; y++)
					torso.setRGB(x, y, skin.getRGB(x+20, y+20));
		}
		return torso;
	}

	public BufferedImage getLeftHandFromSkin (BufferedImage skin){
		BufferedImage hand = null;
		if (skin != null){
			hand = new BufferedImage (4,12,BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x<4; x++)
				for (int y = 0; y<12; y++)
					hand.setRGB(x, y, skin.getRGB(x+44, y+20));
		}
		return hand;
	}

	public BufferedImage getRightHandFromSkin (BufferedImage skin){
		BufferedImage hand = null;
		if (skin != null){
			hand = new BufferedImage (4,12,BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x<4; x++)
				for (int y = 0; y<12; y++)
					hand.setRGB(3-x, y, skin.getRGB(x+44, y+20));
		}
		return hand;
	}

	public BufferedImage getLegsFromSkin (BufferedImage skin){
		BufferedImage legs = null;
		if (skin != null){

			//left leg
			legs = new BufferedImage (8,12,BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x<4; x++)
				for (int y = 0; y<12; y++)
					legs.setRGB(3-x, y, skin.getRGB(x+4, y+20));
			//right leg
			for (int x = 0; x<4; x++)
				for (int y = 0; y<12; y++)
					legs.setRGB(7-x, y, skin.getRGB(x+4, y+20));
		}
		return legs;
	}

	public void saveToPng (BufferedImage img, String filename){
		String fn = filename;
		if (!fn.endsWith(".png")) fn = fn+".png";
		try {
			File imgfile = new File (plg.getDataFolder()+File.separator+"images"+File.separator+fn);
			ImageIO.write(img, "png", imgfile);
		} catch (Exception e){

		}
	}

	/* 
	 *  name - имя игрока, при этом фон создается функцией getBackground() 
	 */
	public BufferedImage createPortrait (String name){
		return createPortrait(getHeadFromSkin(this.getSkin(name)), getBackground().getScaledInstance(128, 128, 0));
	}

	/* 
	 *  name - имя игрока, фон берется из файла 
	 */
	public BufferedImage createPortrait (String name, String background){
		return createPortrait(getHeadFromSkin(this.getSkin(name)), getBackground(background).getScaledInstance(128, 128, 0));
	}


	public BufferedImage createPortrait (String name, Image background){
		return createPortrait(getHeadFromSkin(this.getSkin(name)), background);
	}

	public BufferedImage createPortrait (BufferedImage face, Image paper){
		BufferedImage portrait = null;
		if ((face!=null)&&(paper!=null)){
			portrait = new BufferedImage (128,128,BufferedImage.TYPE_INT_ARGB);
			portrait.getGraphics().drawImage(paper, 0, 0, null);
			portrait.getGraphics().drawImage(face.getScaledInstance(80, 80, 0), 23, 23, null);
		}
		return portrait;
	}



	public BufferedImage getPlayerTopHalfPhoto (BufferedImage skin){
		BufferedImage thphoto = new BufferedImage (16,16,BufferedImage.TYPE_INT_ARGB);
		thphoto.getGraphics().drawImage(getPlayerPhoto (skin), 0, 2, 16, 16, 0, 0, 16, 14, null);
		return thphoto;
	}

	public BufferedImage getPlayerPhoto(BufferedImage skin){
		BufferedImage photo = new BufferedImage (16,32,BufferedImage.TYPE_INT_ARGB);
		BufferedImage face = getHeadFromSkin(skin);
		BufferedImage body = getTorsoFromSkin(skin);
		BufferedImage lhand = getLeftHandFromSkin(skin);
		BufferedImage rhand = getRightHandFromSkin(skin);
		BufferedImage legs = getLegsFromSkin(skin);
		photo.getGraphics().drawImage(face, 4, 0, null);
		photo.getGraphics().drawImage(body, 4, 8, null);
		photo.getGraphics().drawImage(lhand, 0, 8, null);
		photo.getGraphics().drawImage(rhand, 12, 8, null);
		photo.getGraphics().drawImage(legs, 4, 20, null);
		return photo;
	}

	public BufferedImage getBackground (){
		String bg = "default";
		if (plg.default_background.isEmpty()||plg.default_background.equalsIgnoreCase("default")) bg = "default";
		else if (plg.default_background.equalsIgnoreCase("random")) bg = getRandomBackgroundName();
		else bg = plg.default_background;
		return getBackgroundByName(bg);
	}
	
	public BufferedImage getBackground (String background){
		String bg = "default";
		if (background.isEmpty()||background.equalsIgnoreCase("default")) bg = "default";
		else if (background.equalsIgnoreCase("random")) bg = getRandomBackgroundName();
		else bg = background;
		return getBackgroundByName(bg);
	}
	

	private String getRandomBackgroundName() {
		File dir = new File (plg.d_backgrounds);
		if (dir.list().length>0){
			int filenum = plg.u.random.nextInt(dir.list().length);
			return dir.list()[filenum];
		}
		return "default";
	}


	public BufferedImage createPhoto (String name){
		return createPhoto(getSkin(name), getBackground(),3);
	}

	public BufferedImage createPhoto (String name, String background){
		return createPhoto(getSkin(name), getBackground(background).getScaledInstance(128, 128, 0),3);
	}

	public BufferedImage createPhoto (BufferedImage skin, Image paper, int size){
		BufferedImage pph = this.getPlayerPhoto(skin);
		if ((skin==null)||(paper==null)||(pph==null)) return null;
		BufferedImage photo = new BufferedImage (128,128,BufferedImage.TYPE_INT_ARGB);
		photo.getGraphics().drawImage(paper, 0, 0, null);
		photo.getGraphics().drawImage(pph.getScaledInstance(16*size, 32*size, 0), 64-8*size, 64-16*size, null);
		return photo;
	}


	public BufferedImage createTopHalfPhoto (String name){
		return createTopHalfPhoto(getSkin(name), getBackground(),5);
	}

	public BufferedImage createTopHalfPhoto (String name, String background){
		return createTopHalfPhoto(getSkin(name), getBackground(background).getScaledInstance(128, 128,0),5);
	}

	public BufferedImage createTopHalfPhoto (BufferedImage skin, Image paper, float size){
		BufferedImage pph = this.getPlayerTopHalfPhoto(skin);
		if ((skin==null)||(paper==null)||(pph==null)) return null;
		BufferedImage photo = new BufferedImage (128,128,BufferedImage.TYPE_INT_ARGB);
		photo.getGraphics().drawImage(paper, 0, 0, null);
		photo.getGraphics().drawImage(pph.getScaledInstance((int)(16*size), (int)(16*size), 0), (int)(64-8*size), (int)(64-8*size), null);
		return photo;
	}

	public int woolDataToRGB(byte data){
		switch (data){
		case 0:  return 0xFFe4e4e4; 
		case 8:  return 0xFFa0a7a7; 
		case 7:  return 0xFF414141; 
		case 15: return 0xFF181414;
		//case 14: return 0xFF9e2b27;
		case 14: return 0xFFB40000;
		case 1:  return 0xFFea7e35;
		case 4:  return 0xFFc2b51c;
		case 5:  return 0xFF39ba2e;
		//case 13: return 0xFF364b18;
		case 13: return 0xFF005700;
		case 3:  return 0xFF6387d2;
		case 9:  return 0xFF267191;
		case 11: return 0xFF253193;
		case 10: return 0xFF7e34bf;
		case 2:  return 0xFFbe49c9;
		case 6:  return 0xFFd98199;
		case 12: return 0xFF56331c;
		}
		return 0;
	}

	
	private boolean removeWoolAndBurn(Player p, Block b){
		if (b.getType()!=Material.WOOL) return false;
		if ((!p.hasPermission("camera-obscura.fireproof-wool"))&&plg.u.placeBlock(b, p, Material.AIR, (byte) 0, false)) return false;
		b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		return true;
	}
    
	
	public BufferedImage createPixelArt2D (Player p, Location loc1, Location loc2, boolean resize, boolean burn){
		BufferedImage img = null;
		World w = loc1.getWorld();
		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();
		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		int sx =1;
		if (x1>x2) sx=-1;
		int sy =1;
		if (y1>y2) sy=-1;
		int sz =1;
		if (z1>z2) sz=-1;

		if (x1==x2) sx = 0;
		if (y1==y2) sy = 0;
		if (z1==z2) sz = 0;

		int hx = 0;
		int hy = 0;
		int hz = 0;

		if (((sx*sx)+(sy*sy)+(sz*sz))!=2) return null; 

		if (burn) p.getWorld().playSound(p.getLocation(), Sound.FIRE, 1, 1);
		
		switch (getDirection(x1, y1, z1, x2, y2, z2)){
		case 0:
			hx = Math.max(x1, x2)-Math.min(x1, x2)+1;
			hz = Math.max(z1, z2)-Math.min(z1, z2)+1;
			img = new BufferedImage (hx,hz,BufferedImage.TYPE_INT_ARGB);

			for (int x = 0; x<hx; x++)
				for (int z = 0; z<hz; z++){
					int rgbcolor = 0;
					Block b = w.getBlockAt(x1+sx*x, y1, z1+sz*z);
					if (b.getType()==Material.WOOL){
						byte data = b.getData();
						if (burn){
							if (removeWoolAndBurn(p, b)) rgbcolor = woolDataToRGB(data); 
						} else	rgbcolor = woolDataToRGB(data);
					}
					
					img.setRGB(x, z, rgbcolor);
				}
			break;
		case 1:
			hx = Math.max(x1, x2)-Math.min(x1, x2)+1;
			hy = Math.max(y1, y2)-Math.min(y1, y2)+1;
			img = new BufferedImage (hx,hy,BufferedImage.TYPE_INT_ARGB);

			for (int x = 0; x<hx; x++)
				for (int y = 0; y<hy; y++){
					int rgbcolor = 0;
					Block b = w.getBlockAt(x1+sx*x, y1+sy*y, z1);
					if (b.getType()==Material.WOOL){
						byte data = b.getData();
						if (burn){
							if (removeWoolAndBurn(p, b)) rgbcolor = woolDataToRGB(data); 
						} else	rgbcolor = woolDataToRGB(data);
					}
					img.setRGB(x, y, rgbcolor);
				}
			break;
		case 2:
			hz = Math.max(z1, z2)-Math.min(z1, z2)+1;
			hy = Math.max(y1, y2)-Math.min(y1, y2)+1;
			img = new BufferedImage (hz,hy,BufferedImage.TYPE_INT_ARGB);

			for (int z = 0; z<hz; z++)
				for (int y = 0; y<hy; y++){
					int rgbcolor = 0;
					Block b = w.getBlockAt(x1, y1+sy*y, z1+sz*z);
					if (b.getType()==Material.WOOL){
						byte data = b.getData();
						if (burn){
							if (removeWoolAndBurn(p, b)) rgbcolor = woolDataToRGB(data); 
						} else	rgbcolor = woolDataToRGB(data);
					}
					img.setRGB(z, y, rgbcolor);
				}
			break;
		}

		//на всякий случай
		if (img == null) return null;
		if (img.getHeight() != img.getWidth()){
			BufferedImage tmp = new BufferedImage (Math.max(img.getHeight(), img.getWidth()),
					Math.max(img.getHeight(), img.getWidth()),
					BufferedImage.TYPE_INT_ARGB);
			tmp.getGraphics().drawImage(img, (tmp.getWidth()/2)-(img.getWidth()/2), (tmp.getHeight()/2)-(img.getHeight()/2), null);
			img = tmp;
		}

		if ((img.getWidth()<plg.minpixelart)||(img.getHeight()<plg.minpixelart)) return null;
		BufferedImage rst = new BufferedImage (128,128,BufferedImage.TYPE_INT_ARGB);
		if (resize)	rst.getGraphics().drawImage(img.getScaledInstance(128, 128, 1), 0,0, null);
		else rst.getGraphics().drawImage(img, 64-(img.getWidth()/2),64-(img.getHeight()/2), null);

		return rst;
	}

	public int getDirection (int x1, int y1, int z1, int x2, int y2, int z2){
		int result = -1;
		if (y1==y2) return 0; 		// 0  - y1=y2 : x1,z1 to x2,z2 (горизонтальная)
		if (z1==z2) return 1; 		// 1  - z1=z2 : x1,y1 to x2,y2
		if (x1==x2) return 2;  		// 2  - x1=x2 : y1,z1 to x2,z2		
		return result;
	}

}
