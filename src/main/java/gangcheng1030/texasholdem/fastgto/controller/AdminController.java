package gangcheng1030.texasholdem.fastgto.controller;

import com.alibaba.fastjson.JSON;
import gangcheng1030.texasholdem.fastgto.core.PostflopTreeNode;
import gangcheng1030.texasholdem.fastgto.service.PostflopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
    private static final Map<String, String> preflopConvertMap = new HashMap<>();
    static {
        preflopConvertMap.put("BN_VS_LJ_SRP", "LJ2bet:HJfold:COfold:BNcall:SBfold:BBfold");
        preflopConvertMap.put("LJ_VS_BB_SRP", "LJ2bet:HJfold:COfold:BNfold:SBfold:BBcall");
        preflopConvertMap.put("BN_VS_BB_SRP", "LJfold:HJfold:COfold:BN2bet:SBfold:BBcall");
        preflopConvertMap.put("BB_VS_SB_SRP", "LJfold:HJfold:COfold:BNfold:SB2bet:BBcall");

        preflopConvertMap.put("HJ_VS_LJ_3BET", "LJ2bet:HJ3bet:COfold:BNfold:SBfold:BBfold:LJcall");
        preflopConvertMap.put("CO_VS_LJ_3BET", "LJ2bet:HJfold:CO3bet:BNfold:SBfold:BBfold:LJcall");
        preflopConvertMap.put("CO_VS_HJ_3BET", "LJfold:HJ2bet:CO3bet:BNfold:SBfold:BBfold:HJcall");
        preflopConvertMap.put("BN_VS_LJ_3BET", "LJ2bet:HJfold:COfold:BN3bet:SBfold:BBfold:LJcall");
        preflopConvertMap.put("BN_VS_HJ_3BET", "LJfold:HJ2bet:COfold:BN3bet:SBfold:BBfold:HJcall");
        preflopConvertMap.put("BN_VS_CO_3BET", "LJfold:HJfold:CO2bet:BN3bet:SBfold:BBfold:COcall");
        preflopConvertMap.put("LJ_VS_SB_3BET", "LJ2bet:HJfold:COfold:BNfold:SB3bet:BBfold:LJcall");
        preflopConvertMap.put("HJ_VS_SB_3BET", "LJfold:HJ2bet:COfold:BNfold:SB3bet:BBfold:HJcall");
        preflopConvertMap.put("CO_VS_SB_3BET", "LJfold:HJfold:CO2bet:BNfold:SB3bet:BBfold:COcall");
        preflopConvertMap.put("BN_VS_SB_3BET", "LJfold:HJfold:COfold:BN2bet:SB3bet:BBfold:BNcall");
        preflopConvertMap.put("BB_VS_SB_3BET", "LJfold:HJfold:COfold:BNfold:SB2bet:BB3bet:SBcall");
        preflopConvertMap.put("LJ_VS_BB_3BET", "LJ2bet:HJfold:COfold:BNfold:SBfold:BB3bet:LJcall");
        preflopConvertMap.put("HJ_VS_BB_3BET", "LJfold:HJ2bet:COfold:BNfold:SBfold:BB3bet:HJcall");
        preflopConvertMap.put("CO_VS_BB_3BET", "LJfold:HJfold:CO2bet:BNfold:SBfold:BB3bet:COcall");
        preflopConvertMap.put("BN_VS_BB_3BET", "LJfold:HJfold:COfold:BN2bet:SBfold:BB3bet:BNcall");

        preflopConvertMap.put("HJ_VS_LJ_4BET", "LJ2bet:HJ3bet:COfold:BNfold:SBfold:BBfold:LJ4bet:HJcall");
        preflopConvertMap.put("CO_VS_LJ_4BET", "LJ2bet:HJfold:CO3bet:BNfold:SBfold:BBfold:LJ4bet:COcall");
        preflopConvertMap.put("CO_VS_HJ_4BET", "LJfold:HJ2bet:CO3bet:BNfold:SBfold:BBfold:HJ4bet:COcall");
        preflopConvertMap.put("BN_VS_LJ_4BET", "LJ2bet:HJfold:COfold:BN3bet:SBfold:BBfold:LJ4bet:BNcall");
        preflopConvertMap.put("BN_VS_HJ_4BET", "LJfold:HJ2bet:COfold:BN3bet:SBfold:BBfold:HJ4bet:BNcall");
        preflopConvertMap.put("BN_VS_CO_4BET", "LJfold:HJfold:CO2bet:BN3bet:SBfold:BBfold:CO4bet:BNcall");
        preflopConvertMap.put("LJ_VS_SB_4BET", "LJ2bet:HJfold:COfold:BNfold:SB3bet:BBfold:LJ4bet:SBcall");
        preflopConvertMap.put("HJ_VS_SB_4BET", "LJfold:HJ2bet:COfold:BNfold:SB3bet:BBfold:HJ4bet:SBcall");
        preflopConvertMap.put("CO_VS_SB_4BET", "LJfold:HJfold:CO2bet:BNfold:SB3bet:BBfold:CO4bet:SBcall");
        preflopConvertMap.put("BN_VS_SB_4BET", "LJfold:HJfold:COfold:BN2bet:SB3bet:BBfold:BN4bet:SBcall");
        preflopConvertMap.put("BB_VS_SB_4BET", "LJfold:HJfold:COfold:BNfold:SB2bet:BB3bet:SB4bet:BBcall");
        preflopConvertMap.put("LJ_VS_BB_4BET", "LJ2bet:HJfold:COfold:BNfold:SBfold:BB3bet:LJ4bet:BBcall");
        preflopConvertMap.put("HJ_VS_BB_4BET", "LJfold:HJ2bet:COfold:BNfold:SBfold:BB3bet:HJ4bet:BBcall");
        preflopConvertMap.put("CO_VS_BB_4BET", "LJfold:HJfold:CO2bet:BNfold:SBfold:BB3bet:CO4bet:BBcall");
        preflopConvertMap.put("BN_VS_BB_4BET", "LJfold:HJfold:COfold:BN2bet:SBfold:BB3bet:BN4bet:BBcall");
    }

    @Autowired
    private PostflopService postflopService;

    @PostMapping("/postflop_strategy")
    public String importPostflopStrategy(@RequestParam(name = "dirName") String dirName,
                                         @RequestParam(name = "prefix", required = false) String prefix) throws IOException {

        final String prefixF = prefix == null ? "" : prefix;
        Path path = Paths.get(dirName);
        Files.list(path).forEach(subPath -> {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (Files.isRegularFile(subPath) && subPath.getFileName().toString().startsWith(prefixF)) {
                            long startTime = System.currentTimeMillis();
                            String fileName = subPath.getFileName().toString();
                            System.out.printf("%s 开始处理 \n", fileName);
                            int lastIndex = fileName.lastIndexOf("_");
                            String preflopActions = fileName.substring(0, lastIndex);
                            preflopActions = preflopConvertMap.getOrDefault(preflopActions, preflopActions);
                            String flopCards = fileName.substring(lastIndex + 1, lastIndex + 7);
                            String content = new String(Files.readAllBytes(subPath));
                            PostflopTreeNode postflopTreeNode = JSON.parseObject(content, PostflopTreeNode.class);
                            postflopService.importPostflopTree(postflopTreeNode, preflopActions, flopCards);
                            long endTime = System.currentTimeMillis();
                            System.out.printf("%s 耗时：%d秒 \n", fileName, (endTime - startTime) / 1000);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        return "ok";
    }
}
