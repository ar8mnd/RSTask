package com.task.utils.tasks.taskitems;


import cn.nukkit.item.Item;
import cn.nukkit.item.ConstAirItem;
import cn.nukkit.nbt.tag.CompoundTag;
import com.task.RsTask;
import com.task.items.InstanceItem;
import com.task.items.ItemLib;


/**
 * @author SmallasWater
 */
public class ItemClass implements InstanceItem {

    private Item item;

    public ItemClass(Item item) {
        this.item = item;
    }

    public ItemClass() {
    }


    ItemClass(String id, int mate, int count) {
        this(Item.get(id, mate, count).clone());
    }


    public ItemClass(String id, int mate, int count, String tag) {
        Item item = Item.get(id, mate, count);
        if (!"not".equals(tag)) {
            CompoundTag compoundTag = Item.parseCompoundTag(hexStringToBytes(tag));
            item.setNamedTag(compoundTag);
        }
        this.item = item;
    }

    public static ItemClass get(Item item) {
        return new ItemClass(item);
    }

    public static ItemClass get(String id, int mate, int count, String tag) {
        return new ItemClass(id, mate, count, tag);
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 将物品字符串转换为物品奖励类
     *
     * @param defaultString 奖励物品字符串
     * @return 物品奖励类
     */
    public static ItemClass toItem(String defaultString) {
        if (defaultString == null) {
            return null;
        }
        if (defaultString.split("@").length > 1) {
            if (defaultString.split(":").length < 2) {
                return toItem(new TaskItem(null, defaultString, 0));
            } else {
                if ("item".equals(defaultString.split("@")[1])) {
                    String ts = defaultString.split("@")[0];
                    String[] items = ts.split(":");
                    try {
                        if (items[0].equals("minecraft")) {
                            return new ItemClass(
                                    items[1],
                                    Integer.parseInt(items[2]),
                                    Integer.parseInt(items[3])
                            );
                        } else {
                            return new ItemClass(
                                    items[0],
                                    Integer.parseInt(items[1]),
                                    Integer.parseInt(items[2])
                            );
                        }
                    } catch (Exception e) {
                        return null;
                    }
                } else if ("tag".equals(defaultString.split("@")[1])) {
                    String ts = defaultString.split("@")[0];
                    if (ts.split(":").length > 1) {
                        if (RsTask.getTask().canExistsNumber(ts.split(":")[0])) {
                            ItemClass itemClass = RsTask.getTask().getTagItemsConfig(ts.split(":")[0]);
                            itemClass.getItem().setCount(Integer.parseInt(ts.split(":")[1]));
                            return itemClass;
                        }
                    } else {
                        return RsTask.getTask().getTagItemsConfig(ts);
                    }
                } else if ("lib".equals(defaultString.split("@")[1])) {
                    String ts = defaultString.split("@")[0];
                    String name;
                    int count = 1;
                    if (ts.split(":").length > 1) {
                        name = ts.split(":")[0];
                        try {
                            count = Integer.parseInt(ts.split(":")[1]);
                        } catch (Exception ignore) {
                        }
                        ItemLib lib = ItemLib.getItem(name);
                        if (lib != null) {
                            lib.setCount(count);
                            return lib;
                        }

                    }
                }
            }
        } else {
            String[] items = defaultString.split(":");
            if (items.length < 3) {
                return new ItemClass(
                        items[0],
                        Integer.parseInt(items[1]), 1);
            } else if (items.length == 3) {
                return new ItemClass(
                        items[0],
                        Integer.parseInt(items[1]), Integer.parseInt(items[2])
                );
            }

            try {
                return new ItemClass(items[0],
                        Integer.parseInt(items[1]),
                        Integer.parseInt(items[2]),
                        items[3]
                );
            } catch (Exception e) {
                return null;
            }

        }
        return null;
    }

    /**
     * 将物品字符串转换为物品奖励类
     *
     * @param item 任务目标
     * @return 物品奖励类
     */
    public static ItemClass toItem(TaskItem item) {
        if (item.getTaskTag() == TaskItem.TaskItemTag.defaultItem) {
            String a = item.getTask().split("@")[0];
            if (a.split(":").length > 1) {
                return new ItemClass(a.split(":")[0], Integer.parseInt(a.split(":")[1]), 1);
            } else {
                return new ItemClass(a.split(":")[0], 0, 1);
            }
        } else if (item.getTaskTag() == TaskItem.TaskItemTag.NbtItem) {
            String custom = item.getTask().split("@")[0];
            ItemClass itemClass = RsTask.getTask().getTagItemsConfig(custom);
            if (itemClass == null) {
                Item item1 = new ConstAirItem();
                item1.setCustomName("No data available");
                return new ItemClass(item1);
            }
            return itemClass;
        } else if (item.getTaskTag() == TaskItem.TaskItemTag.lib) {
            String custom = item.getTask().split("@")[0];
            ItemLib lib = ItemLib.getItem(custom);
            if (lib == null) {
                Item item1 = new ConstAirItem();
                item1.setCustomName("No data available");
                return new ItemClass(item1);
            }
            return lib;
        }
        return null;
    }

    /**
     * 获取物品
     *
     * @return 物品 {@link Item}
     */
    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return item.getId() + ":" + item.getDamage() + ":" + item.getCount() + ":"
                + ((item.hasCompoundTag()) ? bytesToHexString(item.getCompoundTag()) : "not");
    }

    public String toTaskItem(boolean defaultType) {
        if (item.hasCompoundTag() && defaultType) {
            String i = RsTask.getTask().saveTagItemsConfig(this);
            return i + "@tag";
        }
        return item.getId() + ":" + item.getDamage() + "@item";
    }

    public String toSaveConfig() {
        return toSaveConfig(item.hasCompoundTag());

    }


    /**
     * 获取名称
     */
    public String toSaveConfig(boolean defaultType) {
        if (item.hasCompoundTag() && defaultType) {
            String i = RsTask.getTask().saveTagItemsConfig(this);
            return i + "@tag";
        }
        return item.getId() + ":" + item.getDamage() + ":" + item.getCount() + "@item";
    }


    public boolean equals(ItemClass itemClass) {
        if (item.hasCompoundTag() || itemClass.getItem().hasCompoundTag()) {
            Item item1 = itemClass.getItem();
            CompoundTag tag1 = item1.getNamedTag();
            CompoundTag tag2 = item.getNamedTag();
            if (item != null) {
                if (tag1 != null && tag2 != null) {
                    if (tag1.equals(tag2)) {
                        return item1.getId() == item.getId() && item1.getDamage() == item.getDamage();
                    }
                }
            }
        } else {
            return (item.getId() + ":" + item.getDamage()).equals(itemClass.item.getId() + ":" + itemClass.item.getDamage());
        }
        return false;
    }
}
