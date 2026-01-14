package org.l2explorer.unreal;

import org.l2explorer.io.UnrealPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Env {
    /**
     * Diretório raiz (geralmente a pasta /system ou a raiz do Lineage II)
     */
    File getStartDir();

    /**
     * Lista de caminhos/padrões de busca (ex: "../SysTextures/*.utx")
     */
    List<String> getPaths();

    /**
     * Lista arquivos usando Java NIO PathMatcher em vez de Apache Commons
     */
    default Stream<File> listFiles() {
        return getPaths().stream().flatMap(pattern -> {
            try {
                Path startPath = getStartDir().toPath();
                // Separa o diretório do padrão do arquivo (ex: ../Textures/ do *.utx)
                String dirPart = pattern.contains("/") ? pattern.substring(0, pattern.lastIndexOf('/') + 1) : "";
                String filePart = pattern.substring(pattern.lastIndexOf('/') + 1);
                
                Path searchDir = startPath.resolve(dirPart).normalize();
                
                if (!Files.exists(searchDir)) return Stream.empty();

                // Matcher para Wildcards (glob)
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePart);
                
                return Files.list(searchDir)
                        .filter(path -> matcher.matches(path.getFileName()))
                        .map(Path::toFile);
            } catch (IOException e) {
                return Stream.empty();
            }
        });
    }

    /**
     * Busca um arquivo pelo nome, ignorando a extensão
     */
    default Stream<File> getPackageFiles(String name) {
        return listFiles()
                .filter(file -> {
                    String fileName = file.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    String nameOnly = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
                    return nameOnly.equalsIgnoreCase(name);
                });
    }

    /**
     * Abre um UnrealPackage com segurança
     */
    default Optional<UnrealPackage> openPackage(File f) {
        try {
            // Abrimos sempre em modo read-only para o ambiente
            return Optional.of(new UnrealPackage(f, true));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Retorna uma Stream de pacotes carregados pelo nome
     * @throws IOException 
     */
    default Stream<UnrealPackage> listPackages(String name) throws IOException {
        return getPackageFiles(name)
                .map(this::openPackage)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Busca uma entrada específica (ExportEntry) no ambiente global
     */
    default Optional<UnrealPackage.ExportEntry> getExportEntry(String fullName, Predicate<String> classFilter) throws IOException {
        if (fullName == null || classFilter == null) {
            throw new IllegalArgumentException("Full name and class filter cannot be null");
        }

        String[] path = fullName.split("\\.");
        if (path.length == 0) return Optional.empty();

        // 1. Tenta buscar pelo nome completo (Pacote.Grupo.Objeto)
        Optional<UnrealPackage.ExportEntry> entry = listPackages(path[0])
                .flatMap(up -> up.getExportTable().stream())
                .filter(e -> e.getObjectFullName().equalsIgnoreCase(fullName))
                .filter(e -> classFilter.test(e.getFullClassName()))
                .findAny();

        // 2. Fallback: Busca apenas pelo nome do objeto (última parte) caso o primeiro falhe
        if (!entry.isPresent()) {
            entry = listPackages(path[0])
                    .flatMap(up -> up.getExportTable().stream())
                    .filter(e -> e.getObjectName().getName().equalsIgnoreCase(path[path.length - 1]))
                    .filter(e -> classFilter.test(e.getFullClassName()))
                    .findAny();
        }
        
        return entry;
    }

    void markInvalid(String pckg);

	Stream<File> getPackage(String name);

	Optional<UnrealPackage> getPackage(File f);
}