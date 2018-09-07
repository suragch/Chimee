package net.studymongolian.chimee;

import java.util.ArrayList;
import java.util.List;

class FileUtils {

    public static List<String> getFileNames() {
        List<String> dummyList = new ArrayList<>();
        //dummyList.add("asdf");
        //dummyList.add("mongol");
        //dummyList.add("todo");
        return dummyList;

    }

    public static String openFile(String fileName) {
        return "This is dummy text from " + fileName;
    }
}
