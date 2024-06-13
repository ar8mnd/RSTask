package com.task.utils.tasks.taskitems;


import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SmallasWater
 */
public class TaskButton {

    public enum ButtonImageType {
        /**
         * 按键贴图
         */
        Path("Local"),
        Url("Web");
        protected String name;

        ButtonImageType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private ButtonImageType type;


    private String text;

    private String data;

    public TaskButton(String text) {
        this(text, ButtonImageType.Path, "textures/items/book_enchanted");
    }

    private TaskButton(String buttonText, ButtonImageType buttonType, String buttonData) {
        text = buttonText;
        type = buttonType;
        data = buttonData;
    }


    public ElementButton toButton() {
        ElementButton elementButton = new ElementButton(text);
        ElementButtonImageData imageData = new ElementButtonImageData(type == ButtonImageType.Path ? "path" : "url", data);
        elementButton.addImage(imageData);
        return elementButton;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setType(ButtonImageType type) {
        this.type = type;
    }


    public LinkedHashMap<String, Object> toSaveConfig() {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("place", type.getName());
        linkedHashMap.put("trails", data);
        return linkedHashMap;
    }

    public static TaskButton toTaskButton(Map map) {
        if (map == null) {
            return null;
        }
        ButtonImageType type;
        String data;
        if (map.containsKey("place")) {
            type = "Local".equals(map.get("place")) ? ButtonImageType.Path : ButtonImageType.Url;
        } else {
            return null;
        }
        if (map.containsKey("trails")) {
            data = (String) map.get("trails");
        } else {
            return null;
        }
        return new TaskButton("", type, data);

    }
}
