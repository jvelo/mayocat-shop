package org.mayocat.files.internal;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.files.PermanentFileEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.ObservationManager;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Thread that watches some folders in the permanent directory, and fires {@link PermanentFileEvent} events when file-
 * system events (creation, deletion, modification) occur on files and directories that are watched.
 *
 * @version $Id$
 */
public class FileWatcher extends Thread
{
    private volatile boolean shouldStop;

    private ComponentManager componentManager;

    private FilesSettings filesSettings;

    private Path permanentDirectory;

    private WatchService watchService;

    private ObservationManager observationManager;

    private Logger logger = LoggerFactory.getLogger(FileWatcher.class);

    public FileWatcher(ComponentManager componentManager) throws InitializationException
    {
        this.componentManager = Objects.requireNonNull(componentManager);

        try {
            this.filesSettings = componentManager.getInstance(FilesSettings.class);
            this.observationManager = componentManager.getInstance(ObservationManager.class);
            this.permanentDirectory = Paths.get(filesSettings.getPermanentDirectory());

            watchService = FileSystems.getDefault().newWatchService();
        } catch (ComponentLookupException | IOException e) {
            throw new InitializationException("Failed to initialize the file watcher.", e);
        }
    }

    @Override
    public void run()
    {
        for (String root : Arrays.asList("tenants", "themes", "payments")) {
            Path rootToWatch = permanentDirectory.resolve(root);
            try {
                Files.walkFileTree(rootToWatch, new SimpleFileVisitor<Path>()
                {
                    @Override
                    public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs)
                            throws IOException
                    {
                        directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                this.logger.error("Failed to initialize file watcher", e);
            }
        }

        while (!this.shouldStop) {
            try {
                WatchKey watchKey = watchService.poll();
                if (watchKey != null) {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    Path watched = (Path) watchKey.watchable();
                    for (WatchEvent event : events) {
                        PermanentFileEvent permanentFileEvent = new PermanentFileEvent();
                        Path context = watched.resolve((Path) event.context());
                        WatchEvent.Kind eventKind = event.kind();
                        PermanentFileEvent.Data memo = new PermanentFileEvent.Data(context, eventKind);
                        observationManager.notify(permanentFileEvent, this, memo);
                    }
                    watchKey.reset();
                }
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
