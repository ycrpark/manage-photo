package service;

import comm.CustomLogger;
import util.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class CommonService {
    private static final Logger log = CustomLogger.getGlobal();

    private CommonService() {
    }

    private static class Loader {
        public static final CommonService INSTANCE = new CommonService();
    }

    public static CommonService getInstance() {
        return Loader.INSTANCE;
    }

    public static FileService fileService = FileService.getInstance();

    public void rollbackRenames(Map<String, String> renameSources) throws IOException {
        int completed = 0;
        for (Entry<String, String> entry : renameSources.entrySet()) {
            fileService.renameFile(entry.getValue(), Paths.get(entry.getKey()).getFileName().toString());


            log.info("rollback. - total: " + Utils.lPad(String.valueOf(renameSources.size()), 5, " ")
                    + " / completed: " + Utils.lPad(String.valueOf(++completed), 5, " ") + " - "
                    + Paths.get(entry.getValue()).getParent().toString()
                    + " - " + Paths.get(entry.getValue()).getFileName().toString() + " -> " + Paths.get(entry.getKey()).getFileName().toString());
        }

        log.info("");
        log.info("rollback completed. - total: " + Utils.lPad(String.valueOf(renameSources.size()), 5, " ")
                + " / completed: " + Utils.lPad(String.valueOf(completed), 5, " "));
    }
}