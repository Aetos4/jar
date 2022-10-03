package cz.aetos.java.jar;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

// FIXME co cahce na data?
//@Data
@Getter
public class Jar {

    private boolean cache;
    private final static String ROOT_PATH = "/";
    private final static String CLASSES_PATH = "/BOOT-INF/classes";
    private final static String LIB_PATH = "/BOOT-INF/lib";
    private final FileSystem fileSystem;
    private Path jarPath;

    // TODO ?
    private List<Path> files = new ArrayList<>(100);

    // TODO ?
    private URI rootUri;

    /**
     *
     * @param fileSystem
     */
    public Jar(FileSystem fileSystem) {
        this.fileSystem = fileSystem.getPath(ROOT_PATH).getFileSystem();
        this.jarPath = this.fileSystem.getPath(ROOT_PATH);
    }

    /**
     * Load current running JAR as filesystem.
     * @return Jar metamodel internal representation - {@link Jar}
     */
    public static Jar loadCurrentJAR() throws IOException, URISyntaxException {
        Jar jar;
        URL rootUrl = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(ROOT_PATH));
        try (FileSystem fileSystem = FileSystems.newFileSystem(rootUrl.toURI(), Collections.emptyMap())) {
            jar = new Jar(fileSystem);
            assert jar.isJar();
            logFileWalks(jar.files(LIB_PATH, CLASSES_PATH));
            return jar;
        }
    }

    /**
     *
     * @param pathStream
     */
    private static void logFileWalks(Stream<Path> pathStream){
        pathStream.forEach(Jar::log);
    }

    private Stream<Path> files(String... paths) {

        // filter
        BiFunction<FileSystem, String, Stream<Path>> files = (fileSystem, path) -> {
            try (Stream<Path> pathStream = Files.walk(fileSystem.getPath(path))) {
                return pathStream;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        return Arrays.stream(paths).flatMap(p -> files.apply(fileSystem, p));
    }

    private static String byGetProtectionDomain(Class clazz) throws URISyntaxException, IOException {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        try (FileSystem fileSystem = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap())) {;
            Path jarPath = fileSystem.getPath(url.getPath());
            assert new File(jarPath.toString()).exists();
            return Paths.get(url.toURI()).toString();
        }
    }

    private boolean isJar() {
        return "jar".equals(getJarPath().getFileSystem().provider().getScheme());
    }

    public String componentsVersion() {
        try {
            // FIXME na prasaka
            try (FileSystem fs = FileSystems.newFileSystem(jarPath.toUri(), Collections.emptyMap())) {
                Path path = fs.getPath(CLASSES_PATH);
                try (Stream<Path> pathStream = Files.walk(path).filter(p -> p.toString().matches(".*-git.properties$"))) {
                    Path gitFile = pathStream.findFirst().get();
                    System.out.println(gitFile.getFileName().toString());
                    // read content of file
                    // FIXME krome verze vratit i git hash
                    return Files.lines(gitFile).filter(l -> l.startsWith("git.build.version")).findFirst().orElse("version missing");
                }
            }
        } catch (Throwable e) {
            System.out.println("SOMETHING WRONG WHILE GETTING VERSION OF COMPONENT");
        }

        return "UNKNOWN";
    }

//    public String version(String artifactId, String groupId) {
//        return findArtifact(artifactId, groupId);
//    }
//
//    private Artifact findArtifact(String artifactId, String groupId) {
//
//    }

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


    public Artifact getArtifact(ComponentsEnum componentsEnum) {

        return new Artifact();
    }

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
