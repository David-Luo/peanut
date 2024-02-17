package com.peanut.diff.runtime;
@SuppressWarnings({ "rawtypes" })
public class Difference<T> {

    private T before;
    private T after;
    private ChangeType changeType;

    public Difference() {
    }
    public Difference(T before, T after, ChangeType changeType) {
        super();
        this.before = before;
        this.after = after;
        this.changeType = changeType;
    }

    public enum ChangeType{
        add,//新增要素
        del,//删除要素
        modify//修改要素
//        attributeChange,//属性修改
//        addElement,//新增子元素
//        deleteElement,//删除子元素
//        elementChange//修改子元素
    }

    public T getBefore() {
        return before;
    }

    public void setBefore(T before) {
        this.before = before;
    }

    public T getAfter() {
        return after;
    }

    public void setAfter(T after) {
        this.after = after;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((before == null) ? 0 : before.hashCode());
        result = prime * result + ((after == null) ? 0 : after.hashCode());
        result = prime * result + ((changeType == null) ? 0 : changeType.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Difference other = (Difference) obj;
        if (before == null) {
            if (other.before != null)
                return false;
        } else if (!before.equals(other.before))
            return false;
        if (after == null) {
            if (other.after != null)
                return false;
        } else if (!after.equals(other.after))
            return false;
        if (changeType != other.changeType)
            return false;
        return true;
    }
    @Override
    public String toString() {
        switch (changeType) {
            case add:
                return "{\"changeType\":\"" + changeType + "\"" + ",\"after\": \"" + after +"\"}";
            case del:
                return "{\"changeType\":\"" + changeType + "\"" + ",\"before\":\"" + before +"\"}";
            case modify:
                return "{\"changeType\":\"" + changeType + "\"" + ",\"before\":\"" + before + "\",\"after\": \"" + after +"\"}";
            default:
                return "unknown";
        }
    }

}
