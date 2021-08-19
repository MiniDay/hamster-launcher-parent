package cn.hamster3.application.launcher.entity.library;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;

import java.util.ArrayList;

public class LibraryList {
    private final ArrayList<Library> libraries;

    public LibraryList(JsonArray libraryArray) {
        libraries = new ArrayList<>();
        for (JsonElement element : libraryArray) {
            Library library = new Library(element.getAsJsonObject());
            addLibrary(library);
        }
    }

    private void addLibrary(Library library) {
        for (Library addedLibrary : libraries) {
            if (addedLibrary.equals(library)) {
                addedLibrary.merge(library);
                return;
            }
        }
        libraries.add(library);
    }

    public ArrayList<Library> getLibraries() {
        return libraries;
    }

    public ArrayList<LibraryPath> getClassPath(LaunchOptions options) {
        ArrayList<LibraryPath> libraryPathList = new ArrayList<>();
        for (Library library : libraries) {
            libraryPathList.add(library.getClassPath());

            LibraryPath nativeLibraryPath = library.getNativeClassPath(options);
            if (nativeLibraryPath != null) {
                libraryPathList.add(nativeLibraryPath);
            }
        }
        return libraryPathList;
    }

    @Override
    public String toString() {
        return "LibraryList{" +
                "libraries=" + libraries +
                '}';
    }
}
