package com.rcpooley.configloader.model;

import java.util.ArrayList;
import java.util.List;

public class BasicUsage {

    public static class Nested {
        private String nestedString;

        private int nestedInt;

        public String getNestedString() {
            return nestedString;
        }

        public int getNestedInt() {
            return nestedInt;
        }
    }

    public static class NestedElement {
        private String nestedString;

        private int nestedInt;

        public String getNestedString() {
            return nestedString;
        }

        public int getNestedInt() {
            return nestedInt;
        }
    }

    private String aString;

    private int bInt;

    private Nested cNested;

    private List<String> dList;

    private ArrayList<Long> eList;

    private List<NestedElement> fList;

    public String getaString() {
        return aString;
    }

    public int getbInt() {
        return bInt;
    }

    public Nested getcNested() {
        return cNested;
    }

    public List<String> getdList() {
        return dList;
    }

    public ArrayList<Long> geteList() {
        return eList;
    }

    public List<NestedElement> getfList() {
        return fList;
    }
}
