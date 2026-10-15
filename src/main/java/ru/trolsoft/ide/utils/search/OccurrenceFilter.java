package ru.trolsoft.ide.utils.search;

import java.io.File;

/**
 * Фильтр вхождения. Получает файл, расширение файла и полную строку,
 * в которой найдено вхождение. Возвращает true, если вхождение
 * нужно включить в результат.
 */
@FunctionalInterface
public interface OccurrenceFilter {
    boolean test(File file, String extension, String line);
}
