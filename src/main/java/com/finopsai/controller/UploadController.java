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
import java.security.Principal;

@Controller
@RequestMapping
public class UploadController {

    private final CsvParserService csvParserService;
    private final com.finopsai.service.AwsCostExplorerService awsCostExplorerService;

    public UploadController(CsvParserService csvParserService, com.finopsai.service.AwsCostExplorerService awsCostExplorerService) {
        this.csvParserService = csvParserService;
        this.awsCostExplorerService = awsCostExplorerService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadPage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Principal principal) {
        Upload upload = csvParserService.parseAndSave(file, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Processed " + upload.getResources().size() + " resource records.");
        return "redirect:/dashboard/" + upload.getId();
    }

    @PostMapping(value = "/api/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadResponse uploadApi(@RequestParam("file") MultipartFile file, Principal principal) {
        Upload upload = csvParserService.parseAndSave(file, principal.getName());
        return new UploadResponse(upload.getId(), upload.getResources().size());
    }

    @PostMapping("/api/cloud/aws/import")
    @ResponseBody
    public UploadResponse importAws(Principal principal) {
        Upload upload = awsCostExplorerService.importCurrentMonth(principal.getName());
        return new UploadResponse(upload.getId(), upload.getResources().size());
    }
}
