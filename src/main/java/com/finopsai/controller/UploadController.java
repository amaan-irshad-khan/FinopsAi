package com.finopsai.controller;

import com.finopsai.dto.UploadResponse;
import com.finopsai.model.Upload;
import com.finopsai.service.CsvParserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class UploadController {

    private final CsvParserService csvParserService;

    public UploadController(CsvParserService csvParserService) {
        this.csvParserService = csvParserService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadPage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        Upload upload = csvParserService.parseAndSave(file);
        redirectAttributes.addFlashAttribute("success", "Processed " + upload.getResources().size() + " resource records.");
        return "redirect:/dashboard/" + upload.getId();
    }

    @PostMapping(value = "/api/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadResponse uploadApi(@RequestParam("file") MultipartFile file) {
        Upload upload = csvParserService.parseAndSave(file);
        return new UploadResponse(upload.getId(), upload.getResources().size());
    }
}
