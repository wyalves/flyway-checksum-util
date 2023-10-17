package dev.wyalves;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.resource.filesystem.FileSystemResource;
import org.flywaydb.core.internal.util.BomFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;

public class FlywayChecksumUtil {

    public static void main(String[] args) throws IOException {
        try (var paths = Files.walk(Paths.get("src/main/resources/"))) {
            paths
                    .filter(Files::isRegularFile)
                    .map(Path::toAbsolutePath)
                    .map(Path::normalize)
                    .map(Path::toString)
                    .forEachOrdered(FlywayChecksumUtil::calculateChecksum);
        }
    }

    private static void calculateChecksum(String filename) {
        var filepath = new File(filename).getAbsolutePath();
        var resource = new FileSystemResource(null, filepath, StandardCharsets.UTF_8);

        final CRC32 crc32 = new CRC32();

        try (BufferedReader bufferedReader = new BufferedReader(resource.read(), 4096)) {
            String line = bufferedReader.readLine();

            if (line != null) {
                line = BomFilter.FilterBomFromString(line);
                do {
                    crc32.update(line.getBytes(StandardCharsets.UTF_8));
                } while ((line = bufferedReader.readLine()) != null);
            }
        } catch (IOException e) {
            throw new FlywayException("Unable to calculate checksum of " + resource.getFilename() + "\r\n" + e.getMessage(), e);
        }

        var checksum = (int) crc32.getValue();
        var file = filepath.substring(filepath.lastIndexOf("/") + 1);

        if (file.length() > 30)
            file = file.substring(0, 30);
        else
            file = String.format("%-30s", file);

        System.out.printf("(%s) -> (%12s)%n", file, checksum);
    }

}
