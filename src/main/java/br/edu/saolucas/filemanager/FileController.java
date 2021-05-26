package br.edu.saolucas.filemanager;

import java.io.File;
import java.io.IOException;
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

	@Value("${file.txt.path}")
	private String pathTxt;
	@Value("${file.zip.path}")
	private String pathZip;
	private FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String name,
			@RequestParam(required = false) @Size(max = 1000) String text, Model model) {
		model.addAttribute("name", name);
		model.addAttribute("text", text);
		model.addAttribute("zipFiles", new File(pathZip).list());
		model.addAttribute("files", new File(pathTxt).list());
		return "index";
	}

	@PostMapping(
			path = "/submit")
	public String saveOrUpdate(@NotBlank String name, @NotBlank @Size(max = 1000) String text)
			throws IOException {
		fileService.writeFile(text, pathTxt + name.toLowerCase().replace(" ", "-") + ".txt");
		return "redirect:/";
	}

	@GetMapping("/open/{fileName}")
	public String openFile(@PathVariable String fileName) throws IOException {
		var text = fileService.readFile(pathTxt + fileName);
		return "redirect:/?name=" + fileName.replace(".txt", "") + "&text=" + text;
	}

	@GetMapping("/zip/{fileName}")
	public String zip(@PathVariable String fileName) throws IOException {
		fileService.zipFile(pathTxt + fileName, pathZip + fileName);
		return "redirect:/";
	}

}
