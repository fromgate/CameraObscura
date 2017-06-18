# CameraObscura

CameraObscura is a plugin for bukkit/spigot.
This plugins brings the photography and the painting in your Minecraft world.

## What is photography and paintings?
Photographs and paintings are... map items that contains an image on it. 
Image can be based on player skin (photo), on picture file uploaded to server, or... 
image based on a pixel-art created using colored wool.

## Video
[![CameraObscura Video](http://img.youtube.com/vi/o6HlHeOFQqY/mqdefault.jpg)](https://youtu.be/o6HlHeOFQqY)

## Features
* Create photo using photo camera (three types of photo: full-size, top-half, face)
* Create photo using tripod-camera (Camera Obscura :)) Economy supported (player must pay for picture)
* Create picture using png-image ploaded to the server
* Create picture using pixel-art created with colored wool
* Copy-protection for maps used as pictures
* Picture owners features: limit of pictures per owner, copy protection, ability to change owner of picture.
* Remove picture and re-use map with same id for future pictures
* Crafting recipes for photo camera, photo paper. Creating tripod-camera without any commands.

## Why do I want it?
If you need to bring additional fun to your server.... Hell, no! If you have a server you need CameraOscura! :)

## How to use it?
* Install
* Configure plugin (edit the config.yml file)
* Upload some backgrounds (recommended size 128x128), upload pictures.
* Start server and made a picture!

## How to make a picture?
* Photo camera. You can create a photo camera (by default it's a watch item with data equal to 1: you can use "camera" to make a pictures, but you can not use watch to make a picture) and photo paper (by default photo-paper is a paper sheet with data equal to 1: you cannot use regular paper to create pictures). Click any player with a camera. One sheet of paper will be removed from your inventory and picture (map) will be given to you.
* Camera obscura (tripod camera). You must build a tripod camera. Place fence, note block on the top of it, and... camera at the side of note block. Than take a photo paper and click to button (camera lens). Note: Both cameras supported three types of photos defined by distance from camera to player: head photo, top-half photo, full length photo. All photos are printed on the background. Backgrounds are png-images uploaded to specified folder (/CameraObscura/backgrounds/). Camera obscura can be configured to use any predefined background.
* Create photo of any player (even offline player) using commands. Hold one sheet of photo paper in hand and type commands: 
  * `/photo head <PlayerName>`, 
  * `/photo top <PlayerName>`, 
  * `/photo full <PlayerName>`
* Create a picture based on image file. You need to upload image (only png-file supported now) to `/CameraObscura/images/` folder and type /photo image <image name>
* Create a picture based on colored wool pixel-art, You need to create a picture using colored wool.
 Type `/photo brush` to enable brush mode. Left clicking with brush (feather item) will select 1st point (top left!),
  right clicking will select 2nd point (bottom right). Type command:
  * `/photo paint <picture_name>` to create a picture resized to 128x128
  * `/photo paint center <picture_name>` to create a picture and place it at center of the map. Picture will not be resized so it could be too little... Note: you can repaint previously created picture using command /photo repaint {center} <picture_name> (don't forget to hold in hand picture that you need to repaint)

## How to create camera set it on the tripod?
There's some recipes
### Photo paper
![Photo paper recipe](https://media-elerium.cursecdn.com/attachments/131/201/photopaper.png)

### Camera
![Camera recipe](https://media-elerium.cursecdn.com/attachments/131/200/photocamera.png)

### Tripod camera structure
![Tripod camera](https://media-elerium.cursecdn.com/attachments/131/202/tripod.png)

## Commands
* `/photo help [command]` - help page
* `/photo camera` - gives a camera to you
* `/photo paper [amount]` - gives a photo paper to you
* `/photo brush` - toggles brush-mode
* `/photo list [page] [name mask]` - display list of pictures
* `/photo files [page] [filename mask]` - display list of files (images)
* `/photo download <name> <url>` - download picture from <url> and save it with name <name> at images folder (not fully tested)
* `/photo remove <id>` - remove picture with defined id
* `/photo rename <id> <new name>` - rename picture
* `/photo allowcopy [id]` - toggles copy mode for picture
* `/photo owner [id] <new owner>` - set new owner for defined picture
* `/photo head [player]` - create a head-shot photo
* `/photo top [player]` - create a top-half photo
* `/photo full [player]` - create a full-length photo
* `/photo image <filename>` - create a new picture using image
* `/photo image <filename> <dimension|auto>` - create a large picture using image file. For example: /photo image image.jpg 3x4 will create a large picture that contains 3 rows and 4 columns of maps (totally 12 pictures (map items))
* `/photo paint {center} <name>` - paint a pixel-art picture, resized to 128x128
* `/photo repaint {center} <name>` - paint a pixel-art picture at the center of map (not resized)
* `/photo id` - show information about map in your hands
* `/photo give <picture id>` - gives a picture with defined id to you
* `/photo reload` - reload configuration
* `/photo rst [player]` - repaint pictures
* `/photo cfg` - displays configuration

## Permissions
* `camera-obscura.config` - allows to use basic commands
* `camera-obscura.givecamera` - allows to use command /photo camera to give camera to player
* `camera-obscura.givepaper` - allows to use command /photo paper to give photo paper to player
* `camera-obscura.pixelart` - allows to use command /photo paint to create a picture based on the colored wool pixel-art
* `camera-obscura.remove` - allows to use command /photo remove
* `camera-obscura.rename` - allows to use command /photo rename
* `camera-obscura.allowcopy` - allows to use command /photo allowcopy
* `camera-obscura.owner` - allows to use command /photo allowcopy
* `camera-obscura.owner.all` - player with this permission is "owner of all pictures (maps)" (he can remove, repaint and rename pictures created by other players)
* `camera-obscura.photo` - allows to use commands /photo head, /photo top, /photo full
* `camera-obscura.image` - allows to use command /photo image
* `camera-obscura.image.large` - allows to create large pictures using command /photo image <filename> <dimension|auto>
* `camera-obscura.repaint` - allows to use commands /photo repaint
* `camera-obscura.handy.use` - allows to use photo camera
* `camera-obscura.tripod-camera.use` - allows to use tripod camera
* `camera-obscura.set-sign` - allows to set configuration sign for tripod-cameras
* `camera-obscura.tripod-camera.build` - allows to build tripod-cameras
* `camera-obscura.fireproof-wool` - if player has this permission wool will not burn after painting picture
* `camera-obscura.files` - allows to use command /photo files
* `camera-obscura.files.autocreate` - enable personal folder autocreation (requires enabled personal-folders in config.yml)
* `camera-obscura.files.all` - allows to use command /photo files p:<player name> to list personal folder content of any player

## Configuration
You can configure plugin only by editing config.yml file.
```
general:
  check-updates: true  # Check updated version of Camera Obscura
  language: english  # Language. By default supported english and russian
  language-save: false # Save language file
items:
# Brush used to select wool-painting. Default: feather item
  brush-id: 288
# Photo paper used everytime when you creating a picture or photo. Default: PAPER:1
  photo-paper:
    id: 339
    data: 1
# Photo camera used to develop a photo or build a tripod camera. Default: WATCH:1
  photo-camera:
    id: 347
    data: 1
# Enable build-in recipces. If you need to use other recipe manager set this variable to false
  use-recipes: true
# If set to true - tripod camera's button (lens) will turned to photo camera when breaking
  obscura-drop: true
pictures:
# Deleted image will be used when you creating new ones.
  reuse-deleted-maps: true
# Wool pictures with sizes lesser than this values will be not printed at pictures
  minimal-pixel-art-size: 16
# Default backgrounds. Random - will be select on file from the background folder randomly
  default-background: random
# Picture limit. If player reach it (create 15 photoes) he must delete picture to create new
  pictures-per-owner: 15
# Burn wool after painting picture
  burn-pixel-art-wool: true
# You can save any skin at /skins/ directory - this skin will be used if player has now skin or if skin was not downloaded from the server
  default-skin: default_skin.png
# Skin will be downloaded from here:
  skin-url: http://s3.amazonaws.com/MinecraftSkins/

# Configure displaying of picture name
picture-name:
# Default state of show/hide picture names for new images
  show-at-new-pictures: true
# x,y - coordinages for picture name (at canvas)
  x: 1
  y: 122
# Font name (must be installed in system)
  font-name: Serif
# Font size
  font-size: 9
# Font color
  font-color: '#000000'
# Enable stroke (outline picture name)
  stroke: true
# Stroke color
  stroke_color: '#FFFFFF'
```
## Metrics and update checker
CameraObscura includes two features that use your server internet connection. First one is Metrics, using to collect information about plugin (versions of plugin, of Java.. etc.) and second is update checker (required to find newer version of CameraObscura at dev.bukkit.org). If you don't like this features you can easy disable it. To disable update checker you need to set parameter "version-check" to "false" in config.yml. Obtain more information about Metrics and learn how to switch off it, you can read here.
