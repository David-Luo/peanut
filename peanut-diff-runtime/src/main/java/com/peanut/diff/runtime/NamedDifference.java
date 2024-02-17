package com.peanut.diff.runtime;

import java.util.HashMap;
import java.util.Map;
@SuppressWarnings({ "rawtypes" })
public class NamedDifference<T> extends Difference<T> {
    private Map<String, Difference> attributeChange;

    public NamedDifference() {
    }
    public NamedDifference(T before, T after, ChangeType changeType) {
        super(before, after,changeType);
        this.attributeChange = new HashMap<>();
    }

    public NamedDifference(T before, T after, Map<String, Difference> attributeChange) {
        super(before, after,ChangeType.modify);
        this.attributeChange = attributeChange;
    }

    public Map<String, Difference> getAttributeChange() {
        return attributeChange;
    }

    public void setAttributeChange(Map<String, Difference> attributeChange) {
        this.attributeChange = attributeChange;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("{");
        for (Map.Entry entry: attributeChange.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

}