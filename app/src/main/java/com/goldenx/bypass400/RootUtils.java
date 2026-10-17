package com.goldenx.bypass400;

import java.io.DataOutputStream;

public class RootUtils {
    // ฟังก์ชันตรวจสอบและขอสิทธิ์ Root
    public static boolean checkAndRequestRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su -c id");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            return exitValue == 0; // ถ้า exit code เป็น 0 แปลว่าได้สิทธิ์ Root
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) os.close();
                if (process != null) process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
