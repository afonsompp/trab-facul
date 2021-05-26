package br.edu.saolucas.filemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import br.edu.saolucas.utils.huffmacompress.HuffmanTree;

@Service
public class FileService {
	private HuffmanTree zip;

	public FileService(HuffmanTree zip) {
		this.zip = zip;
	}

	public void writeFile(String content, String path) throws IOException {
		try (var writer = new FileWriter(path)) {
			writer.append(content);
		}
	}

	public Boolean zipFile(String filePath, String destinationPath) throws IOException {
		this.writeFile(zip.crypt(this.readFile(filePath)), destinationPath);
		return true;
	}

	public String readFile(String path) throws IOException {
		var file = new File(path);
		if (!file.exists())
			throw new FileNotFoundException("File not found");

		return Files.readString(Paths.get(path));
	}
}
