package gangcheng1030.texasholdem.fastgto.core;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class PostflopTreeManager {
    private LRUCache<String, PostflopTreeNode> lruCache;
    public PostflopTreeManager() {
        this.lruCache = new LRUCache<>(1);
    }

    public PostflopTreeNode get(String preflopActions, String cards) {
        String cacheKey = preflopActions + "_" + cards;
        PostflopTreeNode res = lruCache.get(cacheKey);
        if (res != null) {
            return res;
        }

        res = getFromStorage(cacheKey);
        lruCache.put(cacheKey, res);
        return res;
    }

    private PostflopTreeNode getFromStorage(String name) {
        String filePath = "/Volumes/Elements/PostflopRanges_nlhe_100bb/" + name + ".json";

        try {
            // 读取文件内容到字符串
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return JSON.parseObject(content, PostflopTreeNode.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
