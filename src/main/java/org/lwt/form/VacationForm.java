package org.lwt.form;
/**
 * 表示一个表单项
 * @author Administrator
 *
 */
public class VacationForm {
    private String key;
    private String title;
    private String value;
    private boolean disabled;
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
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
}
