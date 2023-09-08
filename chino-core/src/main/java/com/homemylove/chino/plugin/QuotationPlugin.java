package com.homemylove.chino.plugin;

import com.google.common.base.Joiner;
import com.homemylove.chino.annotations.Match;
import com.homemylove.chino.annotations.Plugin;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.entities.Result;
import com.homemylove.chino.utils.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Plugin
@Slf4j
public class QuotationPlugin {

    @Value("${chino.data.images}")
    private String IMAGES_PATH;

    private final String CHILD_PATH = "quote";

    @Resource
    private ApiChan apiChan;

    @Match(equals = "群语录")
    public void quote(Long groupId){

        String path = Joiner.on("/")
                .join(IMAGES_PATH, CHILD_PATH, groupId);

        if(!new File(path).exists()) {
            apiChan.sendGroupMsg(groupId,"本群没有群语录");
            return;
        }

        log.info("path:{}",path);
        List<String> fileNames = getFileNames(path);

        assert fileNames != null;
        String quote = ListUtil.getRandomEle(fileNames);

        String file = Joiner.on("/").join(path, quote);

        Result result = apiChan.sendGroupImage(groupId, file);
    }

    private List<String> getFileNames(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<String> fileNames = new ArrayList<>();
        return getFileNames(file, fileNames);
    }

    private List<String> getFileNames(File file, List<String> fileNames) {
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.isDirectory()) {
                getFileNames(f, fileNames);
            } else {
                fileNames.add(f.getName());
            }
        }
        return fileNames;
    }
}
