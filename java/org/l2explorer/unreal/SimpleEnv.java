package org.l2explorer.unreal;

import org.l2explorer.io.UnrealPackage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SimpleEnv implements Env {
    private final UnrealPackage unrealPackage;

    public SimpleEnv(UnrealPackage unrealPackage) {
        this.unrealPackage = unrealPackage;
    }

    @Override
    public File getStartDir() {
        return new File(".");
    }

    @Override
    public List<String> getPaths() {
        return Collections.emptyList();
    }

    @Override
    public Optional<UnrealPackage> getPackage(File f) {
        return Optional.of(unrealPackage);
    }

    @Override
    public void markInvalid(String pckg) {
    }

    @Override
    public Stream<UnrealPackage> listPackages(String name) throws IOException {
        if (name.equalsIgnoreCase(unrealPackage.getPackageName())) {
            return Stream.of(unrealPackage);
        }
        return Stream.empty();
    }

    @Override
    public Stream<File> listFiles() {
        return Stream.empty();
    }

    @Override
    public Stream<File> getPackage(String name) {
        return Stream.empty();
    }

    @Override
    public Optional<UnrealPackage.ExportEntry> getExportEntry(String fullName, Predicate<String> fullClassName) throws IOException {
        return unrealPackage.getExportTable().stream()
                .filter(e -> e.getObjectFullName().equalsIgnoreCase(fullName))
                .filter(e -> fullClassName.test(e.getFullClassName()))
                .findFirst();
    }
}