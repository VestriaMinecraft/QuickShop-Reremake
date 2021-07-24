/*
 * This file is a part of project QuickShop, the name is LogWatcher.java
 *  Copyright (C) PotatoCraft Studio and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.maxgamer.quickshop.watcher;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.bukkit.scheduler.BukkitRunnable;
import org.maxgamer.quickshop.QuickShop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class LogWatcher extends BukkitRunnable implements AutoCloseable {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter logFileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    private final Queue<String> logs = new ConcurrentLinkedQueue<>();

    private PrintWriter printWriter = null;

    public LogWatcher(QuickShop plugin, File log) {
        try {
            boolean deleteFailed = false;
            if (!log.exists()) {
                //noinspection ResultOfMethodCallIgnored
                log.createNewFile();
            } else {
                if ((log.length() / 1024f / 1024f) > plugin.getConfig().getDouble("logging.file-size")) {
                    Path logPath = plugin.getDataFolder().toPath().resolve("logs");
                    Files.createDirectories(logPath);
                    //Find a available name
                    Path targetPath;
                    int i = 1;
                    do {
                        targetPath = logPath.resolve(ZonedDateTime.now().format(logFileFormatter) + "-" + i + ".log.gz");
                        i++;
                    } while (Files.exists(targetPath));
                    Files.createFile(targetPath);
                    GzipParameters gzipParameters = new GzipParameters();
                    gzipParameters.setFilename(log.getName());
                    try (GzipCompressorOutputStream archiveOutputStream = new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(targetPath.toFile())), gzipParameters)) {
                        Files.copy(log.toPath(), archiveOutputStream);
                        archiveOutputStream.finish();
                        if (log.delete()) {
                            //noinspection ResultOfMethodCallIgnored
                            log.createNewFile();
                            deleteFailed = false;
                        } else {
                            deleteFailed = true;
                        }
                    }
                }
            }
            FileWriter logFileWriter;
            if (deleteFailed) {
                //If could not delete, just override it
                logFileWriter = new FileWriter(log, false);
            } else {
                //Otherwise append
                logFileWriter = new FileWriter(log, true);
            }

            //TODO log file writer should close after use

            printWriter = new PrintWriter(logFileWriter);
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Log file was not found!", e);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create the log file!", e);
        }
    }

    @Override
    @SneakyThrows
    public void close() {
        if (printWriter != null) {
            printWriter.flush();
            printWriter.close();
        }
    }

    public void log(@NonNull String log) {
        logs.add("[" + dateTimeFormatter.format(Instant.now()) + "] " + log);
    }

    @Override
    public void run() {
        if (printWriter == null) {
            //Waiting for init
            return;
        }
        Iterator<String> iterator = logs.iterator();
        while (iterator.hasNext()) {
            String log = iterator.next();
            printWriter.println(log);
            iterator.remove();
        }
        printWriter.flush();
    }

}
