package br.edu.saolucas.filemanager;

import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ConstraintViolationExceptionHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	public String handler(ConstraintViolationException e, Model model) {
		var fields = e.getConstraintViolations().stream()
				.map(c -> c.getPropertyPath().toString()
						.substring(c.getPropertyPath().toString().indexOf(".") + 1))
				.collect(Collectors.toList());

		var messages = e.getConstraintViolations().stream().map(c -> c.getMessage())
				.collect(Collectors.toList());
		model.addAttribute("fields", fields);
		model.addAttribute("messages", messages);
		return "bad-request";
	}

}
