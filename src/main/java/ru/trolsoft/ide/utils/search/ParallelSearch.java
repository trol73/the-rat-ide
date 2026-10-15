package ru.trolsoft.ide.utils.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelSearch {
    private final ExecutorService pool = Executors.newFixedThreadPool(8);

    public List<IdentifierOccurrence> search(
            List<File> directories,
            String identifier,
            List<String> masks,
            boolean caseSensitive,
            boolean skipSubfolders,
            OccurrenceFilter filter) throws InterruptedException, ExecutionException {


        List<Future<List<IdentifierOccurrence>>> futures = new ArrayList<>();

        try {
            for (File dir : directories) {
                Callable<List<IdentifierOccurrence>> task = () -> {
                    FindIdentifierThread t = new FindIdentifierThread(dir, identifier, masks, caseSensitive, skipSubfolders, filter);
                    t.run(); // синхронно, т.к. мы уже в рабочем потоке пула
                    return t.getResults();
                };
                futures.add(pool.submit(task));
            }

            List<IdentifierOccurrence> all = new ArrayList<>();
            for (Future<List<IdentifierOccurrence>> f : futures) {
                all.addAll(f.get()); // блокируется, пока задача не завершится
            }
            return all;

        } finally {
//            pool.shutdown();
        }
    }
}