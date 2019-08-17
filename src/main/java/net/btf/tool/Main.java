package net.btf.tool;

import java.io.*;
import java.util.Scanner;

import org.json.*;

public class Main {

    static String input = "/storage/sdcard1/Android/data/tv.danmaku.bili/download/s_6360/";

    static {
        input = "/storage/sdcard1/Android/data/tv.danmaku.bili/download/";
        input = "/storage/sdcard1/Android/data/tv.danmaku.bili/download/s_5885";

        input = "C:\\Users\\Administrator\\Desktop\\blb";
    }

    static String output = "C:\\Users\\Administrator\\Desktop\\out";


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int mode = 1;

        try {

            System.out.print("请键入输入文件夹路径: ");
            input = s.nextLine();

            while (!new File(input).exists()) {
                System.out.print("文件夹不存在，请重新输入: ");

                input = s.nextLine();
            }

            System.out.print("请键入输出文件夹路径: ");
            output = s.nextLine();

            while (!new File(output).exists()) {
                System.out.print("文件夹不存在，请重新输入: ");
                output = s.nextLine();
            }

            System.out.println("请选择模式");
            System.out.println("1.提取视频");
            System.out.println("2.提取番剧");
            System.out.print("回车确认: ");
            mode = s.nextInt();

            if (mode == 2) {
                stoFlv(input, output);
            } else {
                vtoFlv(input, output);
            }

            System.out.println();


        } catch (JSONException e) {
            System.out.println("目标文件夹json文件格式错误");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("目标文件夹结构错误");
            e.printStackTrace();
        }
    }

    public static void stoFlv(String input, String output) throws JSONException, IOException {
        File root = new File(input);

        for (File sub : root.listFiles()) {
            File entryF = new File(sub.getAbsolutePath() + File.separator + "entry.json");

            JSONObject entry = new JSONObject(read(entryF.getAbsolutePath()));
            File lua = new File(sub.getAbsolutePath() + File.separator + entry.getString("type_tag") + File.separator);
            JSONObject indexJson = new JSONObject(read(lua.getAbsolutePath() + File.separator + "index.json"));

            String mtitle = entry.getString("title");
            String stitle = entry.getJSONObject("ep").getString("index_title");
            int index = entry.getJSONObject("ep").getInt("index");
            int blvnum = (lua.list().length - 1) / 2;

            for (File blv : lua.listFiles()) {
                if (blv.getName().endsWith(".blv")) {
                    String oorder = blv.getName().replaceAll("\\.blv", "");
                    String filename = mtitle + "第" + index + "集-" + stitle + (blvnum == 1 ? "" : "part" + (Integer.parseInt(oorder) + 1)) + ".flv";
                    copy(blv.getAbsolutePath(), output, removeSign(filename));

                    //System.out.println(blv.canWrite());
                }
            }


        }
        System.out.println("已完成所有操作");
    }

    public static void vtoFlv(String input, String output) throws JSONException, IOException {
        File root = new File(input);

        for (File sub : root.listFiles()) {
            File entryF = new File(sub.getAbsolutePath() + File.separator + "1" + File.separator + "entry.json");


            if (!entryF.exists()) {
                System.out.println("跳过番剧:" + entryF);
                continue;
            }

            JSONObject entry = new JSONObject(read(entryF.getAbsolutePath()));
            File lua = new File(sub.getAbsolutePath() + File.separator + "1" + File.separator + entry.getString("type_tag") + File.separator);
            JSONObject indexJson = new JSONObject(read(lua.getAbsolutePath() + File.separator + "index.json"));

            String mtitle = entry.getString("title");
            String stitle = entry.getJSONObject("page_data").getString("part");
            //int index = entry.getJSONObject("ep").getInt("index");
            int blvnum = (lua.list().length - 1) / 2;

            for (File blv : lua.listFiles()) {


                if (blv.getName().endsWith(".blv")) {
                    String oorder = blv.getName().replaceAll("\\.blv", "");
                    String filename = mtitle + stitle + (blvnum == 1 ? "" : "part" + (Integer.parseInt(oorder) + 1)) + ".flv";

                    copy(blv.getAbsolutePath(), output, removeSign(filename));

                    //System.out.println(blv.canWrite());
                }
            }


        }
        System.out.println("已完成所有操作");
    }

    public static String read(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        return new String(buffer, "utf-8");
    }

    public static void copy(String opath, String newpath, String name) throws IOException {
        File objf = new File(newpath + File.separator + name);

        if (!objf.exists())
            objf.createNewFile();
        else {
            System.out.println("已经存在文件:" + name + "，已自动跳过");
            return;
        }


        FileInputStream fis = new FileInputStream(opath);
        FileOutputStream fos = new FileOutputStream(objf);


        byte[] buffer = new byte[1024 * 512];
        int len = 0;
        while ((len = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }

        fos.close();
        fis.close();

        System.out.println("已完成:" + name);
    }

    public static String removeSign(String s) {
        return s.replaceAll("\\/", "").replaceAll(":", "").replaceAll("\\?", "");
    }
}
