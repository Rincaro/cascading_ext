package com.liveramp.cascading_ext.fs;

import com.liveramp.cascading_ext.CascadingUtil;
import com.liveramp.cascading_ext.FileSystemHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Trash;
import org.apache.log4j.Logger;

import java.io.IOException;

public class TrashHelper {
  private static final Logger LOG = Logger.getLogger(TrashHelper.class);

  public static boolean moveToTrash(FileSystem fs, Path path) throws IOException {
    boolean move = moveToAppropriateTrash(fs, path, CascadingUtil.get().getJobConf());
    if(!move){
      throw new RuntimeException("Trash disabled or path already in trash: " + path);
    }

    return true;
  }
  
  public static boolean moveToAppropriateTrash(FileSystem fs, Path p, Configuration conf) throws IOException {
    Path fullyResolvedPath = p;
     Trash trash = new Trash(FileSystem.get(fullyResolvedPath.toUri(), conf), conf);
     boolean success =  trash.moveToTrash(fullyResolvedPath);
     if (success) {
       System.out.println("Moved: '" + p + "' to trash at");
     }
     return success;
   }

  public static boolean deleteUsingTrashIfEnabled(FileSystem fs, Path path) throws IOException {
    if(fs.exists(path)){
      if(isEnabled()){
        LOG.info("Moving to trash: " + path);
        return moveToTrash(fs, path);
      }else{
        LOG.info("Deleting: " + path);
        return fs.delete(path, true);
      }
    }

    //  if it wasn't there, consider it a success
    return true;
  }

  public static boolean isEnabled() throws IOException {
    return false;
  }
}