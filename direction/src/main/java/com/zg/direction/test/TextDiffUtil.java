package com.zg.direction.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TextDiffUtil {

    public static void main(String[] args) throws IOException {
        //原始文件
        List<String> original = Files.readAllLines(new File("D:\\Documents\\配置基线\\测试\\DevOps\\p380-nonvehicle-test\\nonveh-policy-int\\nonveh-genpolicy-int\\application.properties").toPath());
        //对比文件
        List<String> revised = Files.readAllLines(new File("D:\\Documents\\配置基线\\测试\\DevOps\\p380-nonvehicle-test\\nonveh-policy-uat\\nonveh-genpolicy-uat\\application.properties").toPath());

        //两文件的不同点
        Patch<String> patch = DiffUtils.diff(original, revised);
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            Logger.info(delta);
        }

    }
}
