package cz.aetos.java.jar;

import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Data
public class Jar {

    @Getter
    private final Path path;

    private FileSystem fileSystem;

    @Getter
    private List<Path> files = new ArrayList<>(100);

    private URI rootUri;

    /**
     * Read load current running JAR
     * @return
     */
    public static Jar loadCurrentJAR() throws IOException, URISyntaxException {
//        String jarName = byGetProtectionDomain(ADPApplication.class);
        URL rootUrl = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("/"));
        try (FileSystem fileSystem = FileSystems.newFileSystem(rootUrl.toURI(), Collections.emptyMap())) {
            Jar a =  new Jar(fileSystem.getPath("/"));
            Jar b =  new Jar(fileSystem.getPath("/BOOT-INF/classes"));

            log(a.isJar());
            log(b.isJar());

            logFileWalks(a.getPath());
            logFileWalks(b.getPath());

            return a;
        }
    }

    public static void logFileWalks(Path path) throws IOException {
        Files.walk(path).forEach(Jar::log);
    }

    private static String byGetProtectionDomain(Class clazz) throws URISyntaxException, IOException {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        try (FileSystem fileSystem = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap())) {
//            Path jarPath = Paths.get(url.toURI());
            Path jarPath = fileSystem.getPath(url.getPath());
            assert new File(jarPath.toString()).exists();
            return Paths.get(url.toURI()).toString();
        }
    }

    private boolean isJar() {
        return "jar".equals(getPath().getFileSystem().provider().getScheme());
    }

    public String version() throws IOException {
        // FIXME POUZE TUTO METODU UDLĚAT LUKÁŠI, OSTATNÍ SERE PES TEDKA, TO NECH NA PRASÁKA
        Stream<Path> pathStream = Files.walk(path).filter(p -> p.toString().matches(".*-git.properties$"));

        return "version FIXME";
    }

//    public String jarFileName() {
//        ((ZipFileSystem) jar.getPath().getFileSystem()).getRootDir()
//    }


//            if (rootUri.getScheme().equals("jar")) {
//                try (FileSystem fileSystem = FileSystems.newFileSystem(rootUri, Collections.emptyMap())) {
//                    path = fileSystem.getPath("/");
//                    Files.walk(path).forEach(fil);
//                }
//
//            } else {
                // Not running in a jar, so just use a regular filesystem path
//                path = Paths.get(rootUri);
//                Files.walk(path).forEach(System.out::println);
//            }

//            return new Jar();


    public static void log(String s) {
        System.out.println(s);
    }

    public static void log(Boolean b) {
        log(String.valueOf(Boolean.TRUE.equals(b)));
    }

    public static void log(Path b) {
        if (b != null) {
            log(b.toString());
        }
    }

}
