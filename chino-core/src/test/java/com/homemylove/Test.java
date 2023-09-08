package com.homemylove;


import java.util.Stack;

public class Test {

    public static void main(String[] args) {

        String str = "echo 哈哈?♬.》 好";
        String regex = "echo {question} {answer}";

        str = str.trim();
        regex = regex.trim();

        Stack<String> strStack = getStack(str);
        Stack<String> regexStack = getStack(regex);

        while (true) {
            String s1 = "";
            String s2 = "";
            // regex
            boolean find = true;
            boolean flag = false; // 找{}
            while (find) {
                String pop = regexStack.pop();
                // 如果是空格，观察栈顶
                if (pop.equals(" ")) {
                    String peek = regexStack.peek();
                    if (!peek.equals(" ")) {
                        s1 = " ";
                        find = false;
                    }
                } else if (pop.equals("}")) {
                    flag = true;
                } else if (pop.equals("{")) {
                    find = false;
                }else if(!flag) {
                    s1 = pop;
                    find = false;
                }
            }

            while (true) {
                String pop = strStack.pop();
                // 如果是空格，观察栈顶
                if (pop.equals(" ")) {
                    String peek = strStack.peek();
                    if (!peek.equals(" ")) {
                        s2 = " ";
                        break;
                    }
                } else {
                    s2 = pop;
                    break;
                }
            }

            System.out.println(">>>>>>>>");
            System.out.println("s1:\t"+s1);
            System.out.println("s2:\t"+s2);

            if (!flag && !s1.equals(s2)) {
                System.out.println("不匹配");
                break;
            }

            if(strStack.empty() && regexStack.empty()){
                System.out.println("匹配");
                break;
            }
        }


    }

    public static Stack<String> getStack(String str) {
        Stack<String> stack = new Stack<>();
        for (char c : str.toCharArray()) {
            stack.push(String.valueOf(c));
        }
        return stack;
    }

}
