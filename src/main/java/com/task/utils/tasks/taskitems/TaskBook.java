package com.task.utils.tasks.taskitems;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWrittenBook;
import cn.nukkit.item.ItemWrittenBook;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import com.task.RsTask;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.PlayerFile;
import com.task.form.CreateMenu;

import java.util.*;

/**
 * @author SmallasWater
 */
public class TaskBook {

    public String title = "";


    public ItemWrittenBook book;


    public List<String> texts = new ArrayList<>();

    public TaskBook(ItemWrittenBook book) {
        this.title = book.getCustomName();
        this.book = book;
    }

    public TaskBook(ItemWrittenBook book, LinkedList<String> texts) {
        this.book = book;
        if (book.hasCompoundTag()) {
            this.title = book.getNamedTag().getString("bookTaskName");
        }
        if ("".equalsIgnoreCase(title)) {
            title = book.getCustomName();
        }
        if (book.getNamedTag() != null) {
            if (!book.getNamedTag().getString("bookTaskName").equalsIgnoreCase("")) {
                this.title = book.getNamedTag().getString("bookTaskName");
            }
        }
        this.texts = texts;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCustomName(String customName) {
        this.book.setCustomName(customName);
    }


    public TaskBook writeIn(String text) {
        texts.add(text);
        return this;
    }

    public TaskBook setText(String[] strings) {
        this.texts = Arrays.asList(strings);
        return this;
    }

    public TaskBook write(String[] texts) {
        this.texts.addAll(Arrays.asList(texts));
        return this;
    }

    public TaskBook setText(int index, String text) {
        texts.set(index, text);
        return this;
    }

    public TaskBook removeIndex(int index) {
        texts.remove(index);
        return this;
    }

    public TaskBook delText(String text) {
        texts.remove(text);
        return this;
    }


    public void cleanAll() {
        this.texts = new LinkedList<>();
    }

    public void upData(TaskFile file, Player player) {
        cleanAll();
        if (file != null) {
            StringBuilder two = new StringBuilder();
            StringBuilder three = new StringBuilder();
            two.append(RsTask.getTask().getLag("task-speed")).append("§r \n");
            TaskItem[] items = file.getTaskItem();
            if (items.length > 0) {
                for (String s : CreateMenu.toTaskItemString(items, player)) {
                    two.append(s);
                }
            } else {
                two.append(RsTask.getTask().getLag("notTasks")).append("§r\n");
            }
            SuccessItem item = file.getSuccessItem();
            if (PlayerFile.getPlayerFile(player.getName()).isFirst(file)) {
                item = file.getFirstSuccessItem();
            }
            three.append("\n").append(RsTask.getTask().getLag("success-item")).append("\n");
            for (StringBuilder s : item.toList()) {
                three.append("§r").append(s.toString()).append("\n");
            }
            three.append(RsTask.getTask().getConfig().getString("book-annotation", "\n\n\n§c (If the contents do not match, please reopen or execute /cbook up)"));
            writeIn((CreateMenu.getTitles(player, file) + "§r\n").replace("§e", "§r")).writeIn(two.toString().replace("§e", "§r")).writeIn(three.toString().replace("§e", "§r"));
        }
    }

    public static TaskBook getTaskBookByItem(ItemWrittenBook book) {
        LinkedList<String> strings = new LinkedList<>();
        Object books = book.getPages();
        List l = null;
        //双核心版本兼容
        if (books != null) {
            if (books instanceof List) {
                l = (List) books;
            } else {
                l = new LinkedList<>(Arrays.asList((String[]) books));
            }
        }
        if (l != null) {
            for (Object o : l) {
                if (o instanceof StringTag) {
                    strings.add(((StringTag) o).parseValue());
                } else {
                    strings.add(o.toString());
                }
            }
        }
        return new TaskBook(book, strings);
    }

    public static boolean canInventory(Player player, String taskName) {
        for (Item item : player.getInventory().getContents().values()) {
            if (item instanceof ItemWrittenBook) {
                if (item.getNamedTag() != null) {
                    if (!item.getNamedTag().getString("bookTaskName").equalsIgnoreCase("")) {
                        if (item.getNamedTag().getString("bookTaskName").equalsIgnoreCase(taskName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBook(ItemWrittenBook itemBookWritten) {
        if (itemBookWritten.getNamedTag() != null) {
            String title = itemBookWritten.getNamedTag().getString("bookTaskName");
            return TaskFile.isFileTask(title);
        }
        return false;
    }


    public ItemWrittenBook toBook() {
        ItemWrittenBook bookWritten = new ItemWrittenBook();
        bookWritten.setCustomName(book.getCustomName());
        String[] strings = new String[]{"§r§b-------------------\",\"§r§b|§eRight click/point ground Open view§b|\",\"§r§b-------------------"};
        bookWritten.setLore(strings);
        bookWritten.writeBook(RsTask.getTask().getLag("title"), title, (texts.size() <= 50 ? texts.toArray(new String[0]) : Arrays.copyOfRange(texts.toArray(new String[0]), 0, 50)));
        CompoundTag tag = bookWritten.getNamedTag();
        tag.putString("bookTaskName", title);
        bookWritten.setNamedTag(tag);
        return bookWritten;
    }
}
