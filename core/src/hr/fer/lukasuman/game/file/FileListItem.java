package hr.fer.lukasuman.game.file;

import com.badlogic.gdx.files.FileHandle;

public class FileListItem {

    public FileHandle file;
    private String text;

    public FileListItem(FileHandle file, String text) {
        this.file = file;
        this.text = text;
    }

    public FileListItem(FileHandle file) {
        this(file, file.name());
    }
}
