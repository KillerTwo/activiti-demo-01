package org.lwt.form;
/**
 * 表示一项表单属性
 * @author Administrator
 *
 */
public class VacationForm {
    // 表单属性的key(用作input标签的id或name)
    private String key;
    // 表单属性的左侧表示
    private String title;
    // 表单属性的value
    private String value;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return "VacationForm [key=" + key + ", title=" + title + ", value=" + value + "]";
    }
}
