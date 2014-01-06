CameraObscura
=============

CameraObscura brings the photography and painting in your Minecraft world.

What is photography and paintings?
-----------
Photographs and paintings are... map items that contains an image on it. Image can be based on player skin (photo), on picture file uploaded to server, or... image based on a pixel-art created using colored wool.

Features
--------
* Creating photo using photo camera (three types of photo: full-size, top-half, face)
* Creating photo using tripod-camera (Camera Obscura :)) Economy supported (player must pay for picture)
* Creating picture using png-image ploaded to the server
* Creating picture using pixel-art created with colored wool
* Copy-protection for maps used as pictures
* Picture owners features: limit of pictures per owner, copy protection, ability to change owner of picture.
* Remove picture and re-use map with same id for future pictures
* Crafting recipes for photo camera, photo paper. Creating tripod-camera without any commands.

How to use it?
--------------

* Install
* Configure plugin (edit the config.yml file)
* Upload some backgrounds (recommended size 128x128), upload pictures.
* Start server and made a picture!

How to make a picture?
----------------------
Main command of plugin is "/react" (Aliases: /rea, /ra)

* **Photo camera.** You can create a photo camera (by default it's a watch item with data equal to 1: you can use "camera" to make a pictures, but you can not use watch to make a picture) and photo paper (by default photo-paper is a paper sheet with data equal to 1: you cannot use regular paper to create pictures). Click any player with a camera. One sheet of paper will be removed from your inventory and picture (map) will be given to you.
* **Camera obscura (tripod camera).** You must build a tripod camera. Place fence, note block on the top of it, and... camera at the side of note block. Than take a photo paper and click to button (camera lens). Note: Both cameras supported three types of photos defined by distance from camera to player: head photo, top-half photo, full length photo. All photos are printed on the background. Backgrounds are png-images uploaded to specified folder (/CameraObscura/backgrounds/). Camera obscura can be configured to use any predefined background.
* **Create photo of any player (even offline player) using commands.** Hold one sheet of photo paper in hand and type commands: /photo head <player name>, /photo top <player name>, * /photo full <player name>
* **Create a picture based on image file.** You need to upload image (only png-file supported now) to /CameraObscura/images/ folder and type /photo image <image name>
* **Create a picture based on colored wool pixel-art,** You need to create a picture using colored wool. Type /photo brush to enable brush mode. Left clicking with brush (feather item) will select 1st point (top left!), right clicking will select 2nd point (bottom right). Type command:
* /photo paint <picture_name> to create a picture resized to 128x128
* /photo paint center <picture_name> to create a picture and place it at center of the map. Picture will not be resized so it could be too little... Note: you can repaint previously created picture using command /photo repaint {center} <picture_name> (don't forget to hold in hand picture that you need to repaint)

How to create camera set it on the tripod?
------------------------------------------
There's some [recipes](http://dev.bukkit.org/server-mods/camera-obscura/pages/main/recipes/)

Commands
--------
You can check help in game using command /photo help or find command list [here](http://dev.bukkit.org/server-mods/camera-obscura/pages/main/commands/)

Permissions
-----------
All permissions listed [here](http://dev.bukkit.org/server-mods/camera-obscura/pages/main/permissions/)

Configuration
-------------
You can configure plugin only by [editing config.yml file](http://dev.bukkit.org/server-mods/camera-obscura/pages/main/configuration/).

Metrics and update checker
--------------------------
PlayEffect includes two features that use your server internet connection. First one is Metrics, using to collect [information about the plugin](http://mcstats.org/plugin/PlayEffect) (versions of plugin, of Java.. etc.) and second is update checker, checks new releases of plugin after PlayEffect startup and every half hour. This feature is using API provided by dev.bukkit.org. If you don't like this features you can easy disable it. To disable update checker you need to set parameter "version-check" to "false" in config.yml. Obtain more information about Metrics and learn how to switch off it, you can read [here](http://mcstats.org/learn-more/).