package tool.xfy9326.appinstaller.Methods;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CommandMethod {

    public static boolean hasRoot() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            int i = process.waitFor();
            if (i == 0) {
                Runtime.getRuntime().exec("su");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    static String runCommand(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        BufferedWriter mOutputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader mInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader mErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        mOutputWriter.write(cmd + "\n");
        mOutputWriter.flush();
        mOutputWriter.write("exit\n");
        mOutputWriter.flush();
        process.waitFor();
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = mInputReader.readLine()) != null) {
            result.append(line).append("\n");
        }
        while ((line = mErrorReader.readLine()) != null) {
            result.append(line).append("\n");
        }
        mOutputWriter.close();
        mInputReader.close();
        process.destroy();
        return result.toString();
    }

}
