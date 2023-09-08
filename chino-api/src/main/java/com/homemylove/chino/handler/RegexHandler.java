package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler extends AbstractPluginHandler<String>{

    public RegexHandler(String regex){
        setExpectations(new String[]{regex});
    }

    @Override
    public boolean match(Message message) {
        String rawRegex = getExpectations()[0];

        // 替换 {}
        Pattern compile1 = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher1 = compile1.matcher(rawRegex);
        // 替换为 \w+
        String regex1 = matcher1.replaceAll("[\\\\w\\\\u4E00-\\\\u9FA5\\\\S]+");
        regex1 = "^" + regex1 + "$";

        // 替换 \\s+
        Pattern compile2 = Pattern.compile("\\s+");
        Matcher matcher2 = compile2.matcher(regex1);
        // 替换为 \\\\s+
        String finalRegex = matcher2.replaceAll("\\\\s+");

        // 匹配 message
        Pattern compile = Pattern.compile(finalRegex);
        Matcher matcher = compile.matcher(message.getMessage());

        return matcher.matches();
    }
}
