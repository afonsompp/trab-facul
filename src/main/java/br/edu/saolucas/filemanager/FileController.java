package br.edu.saolucas.filemanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import br.edu.saolucas.utils.huffmacompress.HuffmanTree;

@Controller
@RequestMapping("/")
@Validated
public class FileController {

	@Value("${file.txt.path}")
	private String pathTxt;
	@Value("${file.zip.path}")
	private String pathZip;
	private HuffmanTree zip;

	public FileController(HuffmanTree zip) {
		this.zip = zip;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String name,
			@RequestParam(required = false) @Size(max = 1000) String text, Model model) {
		var files = new File(pathTxt);
		var zipFiles = new File(pathZip);
		model.addAttribute("name", name);
		model.addAttribute("text", text);
		model.addAttribute("files", files.list());
		model.addAttribute("zipFiles", zipFiles.list());
		return "index";
	}

	@PostMapping(
			path = "/submit")
	public String saveOrUpdate(@NotBlank String name, @NotBlank @Size(max = 1000) String text) {
		try (var writer = new FileWriter(pathTxt + name.toLowerCase().replace(" ", "-") + ".txt")) {
			writer.append(text);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	@GetMapping("/open/{fileName}")
	public String openFile(@PathVariable String fileName) {
		var file = new File(pathTxt + fileName);
		if (file.exists()) {
			try {
				var text = Files.readString(Paths.get(pathTxt + fileName));
				return "redirect:/?name=" + fileName.replace(".txt", "") + "&text=" + text;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/";
	}

	@GetMapping("/zip/{fileName}")
	public String zip(@PathVariable String fileName) {
		var file = new File(pathTxt + fileName);
		if (file.exists()) {
			try (var writer = new FileWriter(pathZip + fileName.toLowerCase())) {
				var text = Files.readString(Paths.get(pathTxt + fileName));
				writer.append(zip.crypt(text));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/";
	}

}
