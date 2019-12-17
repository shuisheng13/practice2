package com.yhh.frame.test;

import com.yhh.frame.framework.vo.ITaskProcesser;
import com.yhh.frame.framework.vo.TaskResult;
import com.yhh.frame.framework.vo.TaskResultType;
import org.omg.PortableServer.ServantActivatorPOA;

import java.io.*;
import java.util.Random;
public class FileTask  implements ITaskProcesser<Integer,String> {
    private  String oldPath;
    private String newPath;
    public String getOldPath() {
        return oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public FileTask(String oldPath, String newPath) {
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    @Override
    public TaskResult<String> taskExecute(Integer path) throws IOException {
            File file = new File(newPath+"//"+path);
            if(!file.exists()){
                file.mkdir();
            }
            String filePath = newPath+"//"+path+"//"+ new Random().nextInt(1000)+".jpg";
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(oldPath);
                output = new FileOutputStream(filePath);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                input.close();
                output.close();
            }
        return new TaskResult<>(TaskResultType.SUCCESS,"SUCCESS");
    }
}
