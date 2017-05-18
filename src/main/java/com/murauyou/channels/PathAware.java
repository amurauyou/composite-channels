package com.murauyou.channels;

public interface PathAware {

    String PATH_DELIMITER = ".";
    String NORMALIZATION_REGEX = "[" + "\\:" + "\\" + PATH_DELIMITER +"]";
    String NORMALIZATION_REPLACEMENT = "_";

    String path();

}