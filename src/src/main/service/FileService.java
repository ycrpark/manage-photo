package src.main.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

import src.main.comm.Constants;
import src.main.comm.CustomLogger;
import src.main.model.MoveFileInfo;
import src.main.util.Utils;

public class FileService {
	private static final Logger log = CustomLogger.getGlobal();
	
	private FileService() {
	}
	
	private static class Loader {
		public static final FileService INSTANCE = new FileService();
	}
	
	public static FileService getInstance() {
		return Loader.INSTANCE;
	}
	
	/**
	 * move all files inside the folder, inside folder...
	 * files inside the folder are moved, and the folder is deleted.
	 * when duplicate file name, print log without moving.
	 */
	public void moveFiles(String source, String movePath) {
		long start = System.currentTimeMillis();
		File file = new File(source);
		
		MoveFileInfo info = new MoveFileInfo();
		info.setMovePath(movePath);
		info.setRootDirectory(file.isFile() ? file.getParent() : file.getPath());
		info.setDuplicatedFileSources(new ArrayList<>());
		
		try {
			if(!file.exists()) {
				log.severe("not exists. -" + file.getPath());
				return;
			}
			
			File moveFolder = new File(info.getMovePath());
			if(!moveFolder.exists()) {
				moveFolder.mkdir();
				log.info("create folder. - " + moveFolder.getPath());
			}
			
			moveFiles(file, info);
			log.info(info.getLog("#####move file completed.#####", "#####moveFile completed.#####\n"));
		} catch(IOException e) {
			log.severe(e.toString());
			e.printStackTrace();
		}
		log.info("moveFiles run-time: " + (System.currentTimeMillis() - start) + "ms");
		
		for(String src : info.getDuplicatedFileSources()) {
			log.severe("duplicated. " + Utils.skipDir(src, info.getRootDirectory(), Constants.ROOT_DIRECTORY));
		}
	}
	
	/**
	 * move all files inside the folder, inside folder... using DFS
	 * files inside the folder are moved, and the folder is deleted.
	 */
	private void moveFiles(File file, MoveFileInfo info) throws IOException {
		// file
		if (file.isFile()) {
			try {
				moveFile(file.getPath(), info.getMovePath());
			} catch(FileAlreadyExistsException e) {
				info.getDuplicatedFileSources().add(file.getPath());
				return;
			}
			
			info.addMoveFileCount();
			log.info(info.getLog("moved. -", "- " + Utils.skipDir(file.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
			return;
		}
		
		// folder
		File[] subFiles = file.listFiles();
		for (File subFile : subFiles) {
			moveFiles(subFile, info);
		}
		
		if(file.listFiles().length == 0) {
			file.delete();
			info.addDeleteFolderCount();
			log.info(info.getLog("deleteFolder. -", "- " + Utils.skipDir(file.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
		}
	}
	
	/**
	 * rename of the file
	 * 
	 * @param source
	 * @param newName
	 * @return
	 * new source
	 * @throws IOException
	 */
	public String renameFile(String source, String newName) throws IOException {
		File file = new File(source);
		
		Path path = Paths.get(source);
		Files.move(path, path.resolveSibling(newName));
		
		File renamedFile = new File(file.getParent() + "\\" + newName);
		return renamedFile.getPath();
	}
	
	/**
	 * move of the file
	 * 
	 * @param source
	 * @param movePath
	 * @return
	 * moved source
	 * @throws IOException
	 */
	public String moveFile(String source, String movePath) throws IOException {
		Path path = Paths.get(source);
		Path newPath = Paths.get(movePath);
		Files.move(path , newPath.resolve(path.getFileName()));
		
		File movedFile = new File(movePath + "\\" + path.getFileName());
		return movedFile.getPath();
	}
}