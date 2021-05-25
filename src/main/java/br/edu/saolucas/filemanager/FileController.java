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

@Controller
@RequestMapping("/")
@Validated
public class FileController {

	@Value("${file.path}")
	private String path;

	@GetMapping
	public String index(@RequestParam(required = false) String name,
			@RequestParam(required = false) @Size(max = 1000) String text, Model model) {
		var files = new File(path);
		model.addAttribute("name", name);
		model.addAttribute("text", text);
		model.addAttribute("files", files.list());
		return "index";
	}

	@PostMapping(
			path = "/submit")
	public String saveOrUpdate(@NotBlank String name, @NotBlank @Size(max = 1000) String text) {

		try (var writer = new FileWriter(path + name.toLowerCase().replace(" ", "-") + ".txt")) {
			writer.append(text);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	@GetMapping("/open/{fileName}")
	public String openFile(@PathVariable String fileName) {
		var file = new File(path + fileName);
		if (file.exists()) {
			try {
				var text = Files.readString(Paths.get(path + fileName));
				return "redirect:/?name=" + fileName.replace(".txt", "") + "&text=" + text;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/";
	}

}
