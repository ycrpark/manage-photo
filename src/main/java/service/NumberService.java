package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import comm.Constants;
import comm.CustomLogger;
import comm.WindowsExplorerComparator;
import model.Numbering;
import model.NumberingPhotoCriteria;
import model.NumberingPhotoInfo;
import util.Utils;

public class NumberService {
	private static final Logger log = CustomLogger.getGlobal();
	
	private NumberService() {
	}
	
	private static class Loader {
		public static final NumberService INSTANCE = new NumberService();
	}
	
	public static NumberService getInstance() {
		return Loader.INSTANCE;
	}
	
	public static FileService fileService = FileService.getInstance();
	
	public static CommonService commonService = CommonService.getInstance();
	
	/**
	 * rename the photo's file name to specified format
	 * target is photo or all photos in a folder, inside folder...
	 * ex) yyyyMMdd-HHmmss-0001-originName.jpg (taken date)
	 * 
	 * @param source
	 * photo or directory path
	 * @param numbering
	 * numbering or not
	 */
	public void numberingPhotos(NumberingPhotoCriteria criteria) {
		Instant start = Instant.now();
		
		NumberingPhotoInfo info = new NumberingPhotoInfo();
		info.setRenameSources(new LinkedHashMap<>());
		
		
		try {
			List<Numbering> numberings = collectPhotos(criteria.getDirectories(), info);
			info.setCollectedOrigin(true);
			log.info(info.getLog("#####collected origin#####", "#####collected origin#####\n"));
			
			final boolean hasDerived = criteria.getDerivedDirectories() != null && !criteria.getDerivedDirectories().isEmpty();
			
			Map<Integer, Integer> convertNumber = null;
			List<Numbering> derivedNumberings = null;
			if(hasDerived) {
				convertNumber = new HashMap<>();
				int newNumbering = 1;
				for(Numbering numbering : numberings) {
					if(convertNumber.containsKey(numbering.getNumber())) {
						throw new IllegalStateException("duplicate original numbering");
					}
					
					convertNumber.put(numbering.getNumber(), newNumbering++);
				}
				
				derivedNumberings = collectPhotos(criteria.getDerivedDirectories(), info);
				log.info(info.getLog("#####collected derived#####", "#####collected derived#####\n"));
			}
			
			renumbering(numberings, convertNumber, criteria, info);
			info.setRenumberedOrigin(true);
			log.info(info.getLog("#####renumbered origin#####", "#####renumbered origin#####\n"));
			
			if(hasDerived) {
				renumbering(derivedNumberings, convertNumber, criteria, info);
				log.info(info.getLog("#####renumbered derived#####", "#####renumbered derived#####\n"));
			}
			
		} catch(Exception e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
		
		// start logging info
		log.info("--------------------------------------------------------------------------------------------------------------------\n");
		log.info(info.getLog("renumbered result.", null));
		
		Duration dur = Duration.between(start, Instant.now());
		log.info("run-time: " + dur.toMinutes() + "m " + dur.minusMinutes(dur.toMinutes()).getSeconds() + "s");
		
		if(criteria.isTest()) {
			return;
		}
		
		// rollback
		log.info("");
		System.out.print("rollBack? (y/n): ");
		try(Scanner sc = new Scanner(System.in)) {
			if(sc.nextLine().toUpperCase().equals("Y")) {
				commonService.rollbackRenames(info.getRenameSources());
			}
		} catch(IOException e) {
			log.severe(e.toString());
			e.printStackTrace();
		}
	}
	
	private List<Numbering> collectPhotos(List<String> directories, NumberingPhotoInfo info) {
		List<Numbering> numberings = directories.stream()
				.flatMap(directory -> {
					File dir = new File(directory);
					if(!dir.isDirectory()) {
						throw new IllegalArgumentException("invalid directory: " + directory);
					}
					
					if(info.isCollectedOrigin()) {
						info.addReadDerivedDirectoryCount();
					} else {
						info.addReadDirectoryCount();
					}
					log.info(info.getLog("read dir -", "- " + directory));
					return Arrays.stream(dir.listFiles());
				})
				.filter(file -> file.isFile())
				.map(file -> {
					try {
						int pos = file.getName().lastIndexOf(".");
						String name = file.getName().substring(0, pos);
						String extension = file.getName().substring(pos + 1).toLowerCase();
						
						if(info.isCollectedOrigin()) {
							info.addCollectDerivedPhotoCount();
						} else {
							info.addCollectPhotoCount();
						}
						log.info(info.getLog("collect -", "- " + file.getPath()));
						
						if(name.length() >= 37 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15
								&& (name.indexOf("+") == 20 || name.indexOf("-") == 20)) {
							// photoSync contains offset format
							ZonedDateTime.parse(name.substring(0, 25), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_CONTAINS_OFFSET));
							
							Numbering numbering = new Numbering();
							numbering.setSource(file.getPath());
							numbering.setPrefix(name.substring(0, 32));
							numbering.setNumber(Integer.parseInt(name.substring(32, 37)));
							numbering.setSuffix(name.substring(37));
							numbering.setExtension(extension);
							return numbering;
						} else if(name.length() >= 31 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
							// photoSync format
							LocalDateTime.parse(name.substring(0, 19), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT));
							
							Numbering numbering = new Numbering();
							numbering.setSource(file.getPath());
							numbering.setPrefix(name.substring(0, 26));
							numbering.setNumber(Integer.parseInt(name.substring(26, 31)));
							numbering.setSuffix(name.substring(31));
							numbering.setExtension(extension);
							return numbering;
						}
					} catch(Exception e) {
					}
					
					throw new IllegalArgumentException("invalid file name: " + file.getPath());
					
				})
				.sorted(Comparator.comparing(numbering -> Paths.get(numbering.getSource()).getFileName().toString(), new WindowsExplorerComparator()))
				.collect(Collectors.toList());
		
		return numberings;
	}
	
	private void renumbering(List<Numbering> numberings, Map<Integer, Integer> convertNumber, NumberingPhotoCriteria criteria, NumberingPhotoInfo info) throws IOException {
		int number = 1;
		for(Numbering numbering : numberings) {
			Integer newNumber = convertNumber != null ? convertNumber.get(numbering.getNumber()) : number++;
			
			String newName = numbering.getPrefix() + Utils.lPad(String.valueOf(newNumber), 5, "0") + numbering.getSuffix();
			String checkNewName = newName + Constants.MILLISEC_SEPARATOR + numbering.getExtension();
			if(criteria.isAppendOriginal()) {
				newName += Constants.NAME_SEPARATOR + Utils.lPad(String.valueOf(numbering.getNumber()), 5, "0");
			}
			newName += Constants.MILLISEC_SEPARATOR + numbering.getExtension();
			
			if(info.isRenumberedOrigin()) {
				info.addCompletedDerivedPhotoCount();
			} else {
				info.addCompletedPhotoCount();
			}
			
			Path path = Paths.get(numbering.getSource());
			if(!path.getFileName().toString().equals(checkNewName)) {
				String newSource = criteria.isTest() ? path.getParent().toString() + "\\" + newName : fileService.renameFile(numbering.getSource(), newName);
				info.getRenameSources().put(numbering.getSource(), newSource);
				numbering.setSource(newSource);
				if(criteria.isAppendOriginal()) {
					numbering.setSuffix(numbering.getSuffix() + Constants.NAME_SEPARATOR + numbering.getNumber());
				}
				numbering.setNumber(newNumber);
				
				if(info.isRenumberedOrigin()) {
					info.addRenamedDerivedPhotoCount();
				} else {
					info.addRenamedPhotoCount();
				}
				log.info(info.getLog("renamed. -", "- " + path.getParent().toString() + " - " + path.getFileName().toString() +  " -> " + newName));
			}
		}
	}
}