package org.l2explorer.unreal;

import org.l2explorer.utils.crypt.L2Crypt;
import org.l2explorer.utils.crypt.rsa.L2Ver41x;
import org.l2explorer.utils.crypt.rsa.L2Ver41xInputStream;

import org.l2explorer.io.BufferedRandomAccessFile;
import org.l2explorer.io.RandomAccess;
import org.l2explorer.io.RandomAccessFile;
import org.l2explorer.io.UnrealPackage;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Environment implements Env {
    private static final Logger log = Logger.getLogger(Environment.class.getName());

    private static final Set<String> BUFFERED_PACKAGES = new HashSet<>(
            Arrays.asList(System.getProperty("L2unreal.bufferedExt", "").split(","))
    );

    private static final Pattern PATHS_PATTERN = Pattern.compile("\\s*Paths=(.*)");

    private final File startDir;
    private final List<String> paths;

    // Caches para performance (L2 tem MUITOS arquivos)
    private final Map<String, List<File>> fileCache = new HashMap<>();
    private final Map<File, UnrealPackage> pckgCache = new HashMap<>();
    private final Map<UnrealPackage, Map<String, UnrealPackage.ExportEntry[]>> entriesCache = new HashMap<>();
    private final Map<UnrealPackage, Map<String, UnrealPackage.ExportEntry[]>> entriesCache2 = new HashMap<>();

    public Environment(File startDir, List<String> paths) {
        this.startDir = Objects.requireNonNull(startDir, "startDir cannot be null");
        this.paths = Objects.requireNonNull(paths, "paths cannot be null");
    }

    /**
     * Lê o L2.ini (ou similar), descriptografa se necessário e extrai os Paths.
     */
    public static Environment fromIni(File ini) throws IOException {
        try (InputStream bis = new BufferedInputStream(new FileInputStream(ini))) {
            InputStream is = bis;
            bis.mark(28);
            
            // Suporte a arquivos criptografados (413 é o padrão do L2)
            if (L2Crypt.readHeader(bis) == 413) {
                BigInteger modulus = L2Ver41x.MODULUS_413;
                BigInteger exponent = L2Ver41x.PRIVATE_EXPONENT_413;

                bis.mark(128);
                try {
                    new L2Ver41xInputStream(bis, modulus, exponent).read();
                } catch (Exception e) {
                    modulus = L2Ver41x.MODULUS_L2ENCDEC;
                    exponent = L2Ver41x.PRIVATE_EXPONENT_L2ENCDEC;
                }

                bis.reset();
                is = new L2Ver41xInputStream(bis, modulus, exponent);
            } else {
                bis.reset();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                List<String> paths = br.lines()
                        .filter(s -> PATHS_PATTERN.matcher(s).matches())
                        .map(s -> s.substring(s.indexOf('=') + 1).trim())
                        .collect(Collectors.toList());
                
                if (paths.isEmpty()) {
                    log.warning(() -> "Nenhum Path encontrado no arquivo INI: " + ini.getName());
                }
                
                return new Environment(ini.getParentFile(), paths);
            }
        }
    }

    public Stream<File> getPackage(String name) {
        if (!fileCache.containsKey(name)) {
            fileCache.put(name, listFiles()
                    .filter(file -> {
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        String nameOnly = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
                        return nameOnly.equalsIgnoreCase(name);
                    })
                    .collect(Collectors.toList()));
        }
        return fileCache.get(name).stream();
    }

    public Optional<UnrealPackage> getPackage(File f) {
        if (!pckgCache.containsKey(f)) {
            log.fine("Carregando pacote: " + f.getPath());

            try {
                RandomAccess ra = createRandomAccess(f);
                UnrealPackage up = new UnrealPackage(ra);
                pckgCache.put(f, up);

                BinaryOperator<UnrealPackage.ExportEntry[]> bo = (e1, e2) -> {
                    UnrealPackage.ExportEntry[] res = new UnrealPackage.ExportEntry[e1.length + e2.length];
                    System.arraycopy(e1, 0, res, 0, e1.length);
                    System.arraycopy(e2, 0, res, e1.length, e2.length);
                    return res;
                };

                entriesCache.put(up, up.getExportTable().stream()
                        .collect(Collectors.toMap(
                                e -> e.getObjectFullName().toLowerCase(), 
                                e -> new UnrealPackage.ExportEntry[]{e}, bo)));
                
                entriesCache2.put(up, up.getExportTable().stream()
                        .collect(Collectors.toMap(
                                e -> e.getObjectName().getName().toLowerCase(), 
                                e -> new UnrealPackage.ExportEntry[]{e}, bo)));

            } catch (Exception e) {
                log.log(Level.WARNING, e, () -> "Erro ao carregar pacote: " + f.getPath());
                return Optional.empty();
            }
        }
        return Optional.ofNullable(pckgCache.get(f));
    }

    @Override
    public Optional<UnrealPackage.ExportEntry> getExportEntry(String fullName, Predicate<String> fullClassName) throws IOException {
        String[] path = fullName.split("\\.");
        if (path.length == 0) return Optional.empty();

        // Busca otimizada via cache
        Optional<UnrealPackage.ExportEntry> entryOptional = listPackages(path[0])
                .map(entriesCache::get)
                .filter(Objects::nonNull)
                .map(map -> map.getOrDefault(fullName.toLowerCase(), new UnrealPackage.ExportEntry[0]))
                .flatMap(Arrays::stream)
                .filter(e -> fullClassName.test(e.getFullClassName()))
                .findAny();

        if (!entryOptional.isPresent()) {
            entryOptional = listPackages(path[0])
                    .map(entriesCache2::get)
                    .filter(Objects::nonNull)
                    .map(map -> map.getOrDefault(path[path.length - 1].toLowerCase(), new UnrealPackage.ExportEntry[0]))
                    .flatMap(Arrays::stream)
                    .filter(e -> fullClassName.test(e.getFullClassName()))
                    .findAny();
        }
        return entryOptional;
    }

    @Override
    public void markInvalid(String pckg) {
        getPackage(pckg).forEach(file -> {
            UnrealPackage toRemove = pckgCache.remove(file);
            if (toRemove != null) {
                entriesCache.remove(toRemove);
                entriesCache2.remove(toRemove);
                try { toRemove.close(); } catch (Exception ignored) {}
            }
            log.fine("Cache limpo para: " + file.getPath());
        });
    }

    protected RandomAccess createRandomAccess(File f) throws IOException {
        String name = f.getName();
        int dot = name.lastIndexOf('.');
        String ext = (dot == -1) ? "" : name.substring(dot + 1);

        if (BUFFERED_PACKAGES.contains(ext)) {
            return new BufferedRandomAccessFile(f, true, UnrealPackage.getDefaultCharset());
        }
        return new RandomAccessFile(f, true, UnrealPackage.getDefaultCharset());
    }

    @Override
    public File getStartDir() {
        return startDir;
    }

    @Override
    public List<String> getPaths() {
        return paths;
    }
}