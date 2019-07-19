package logic;

public class ErrorManagement {
    public static void error(ErrorLocation e, String msg){
        System.out.println("[Error] (" + e.toString() + ") " + msg);
    }

    public static void log(ErrorLocation e, String msg){
        System.out.println("[Info] (" + e.toString() + ") " + msg);

    }
}
