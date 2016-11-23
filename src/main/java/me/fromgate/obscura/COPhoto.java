package me.fromgate.obscura;

/**
 * Created by Igor on 23.11.2016.
 */
public class COPhoto {
    String name; //картинки
    String owner; // владелец/создател
    boolean allowcopy; // разрешно/зпрещено копирование
    boolean showname;
    boolean allowrotate;

    public COPhoto(String owner, String name, boolean allowcopy) {
        this.name = name;
        this.allowcopy = allowcopy;
        this.owner = owner;
        this.showname = Obscura.instance.defaultShowName;
        this.allowrotate = false;
    }

    public COPhoto(String owner, String name, boolean allowcopy, boolean showname, boolean allowrotate) {
        this.name = name;
        this.allowcopy = allowcopy;
        this.owner = owner;
        this.showname = showname;
        this.allowrotate = allowrotate;
    }
}
