package br.edu.saolucas.filemanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FileController {

	@Value("${file.path}")
	private String path;

	@GetMapping
	public String index() {
		return "index";
	}

	@PostMapping(
			path = "/submit",
			consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public String saveOrUpdate(@NotBlank String name, @NotBlank @Size(max = 1000) String text) {

		try (var writer = new FileWriter(path + name.toLowerCase().replace(" ", "-") + ".txt")) {
			writer.append(text);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}
}
