package org.ishmamruhan;

public class Test {
    public static void main(String[] args) {
        String abc = LocalizedAppConstants.LOCALIZED_FIELD_STARTS_WITH+"abc";
        System.out.println(abc.substring(LocalizedAppConstants.LOCALIZED_FIELD_STARTS_WITH.length()));
    }
}
