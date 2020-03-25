package src.main.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CustomLogger {
	
	public static final Logger getGlobal() {
		Logger log = Logger.getGlobal();
		log.setUseParentHandlers(false);
		
		Handler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			
			@Override
			public String format(LogRecord record) {
				StringBuilder sb = new StringBuilder();
				
				// datetime
				LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault());
				sb.append(dateTime.toString());
				
				// level
				for(int i = record.getLevel().getName().length(); i < 7; i++) {
					sb.append(" ");
				}
				sb.append(" [").append(record.getLevel()).append("] ");
				
				// path
				sb.append("[").append(record.getSourceClassName()).append(".");
				sb.append(record.getSourceMethodName()).append("] ");
				
				// message
				sb.append(record.getMessage());
				sb.append("\n");
				
				return sb.toString();
			}
		});
		log.addHandler(handler);
		
		return log;
	}
}