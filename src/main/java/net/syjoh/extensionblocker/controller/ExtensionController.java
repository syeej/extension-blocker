package net.syjoh.extensionblocker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.syjoh.extensionblocker.domain.FixedExtensionType;
import net.syjoh.extensionblocker.dto.CustomExtensionDTO;
import net.syjoh.extensionblocker.dto.FixedExtensionDTO;
import net.syjoh.extensionblocker.service.ExtensionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    //페이지 이동
    @GetMapping
    public String showExensionPage(Model model){
        List<FixedExtensionDTO> fixedExtensions = extensionService.getFixedExtensions();
        Map<String, Boolean> fixedExtensionStatus = new HashMap<>();
        for(FixedExtensionDTO dto : fixedExtensions){
            fixedExtensionStatus.put(dto.getName(), dto.isBlocked());
        }
        model.addAttribute("fixedExtensionStatus", fixedExtensionStatus);
        model.addAttribute("customExtensions", extensionService.getCustomExtensions());
        return "extensions";
    }
    //고정 확장자 등록
    @PostMapping("/fixed/{type}")
    public String updateFixedExtension(@PathVariable FixedExtensionType type,
                                       @RequestParam boolean isBlocked,
                                       RedirectAttributes redirectAttributes) {
        extensionService.updateFixedExtensionStatus(type, isBlocked);
        redirectAttributes.addFlashAttribute("message", "udpate success");
        return "redirect:/extensions";
    }
    //커스텀 확장자 등록
    @PostMapping("/custom")
    @ResponseBody
    public ResponseEntity<?> addCustomExtension(@RequestParam String name) {
        CustomExtensionDTO newExtension = extensionService.addCustomExtension(name);
        return ResponseEntity.ok(newExtension);
    }
    //커스텀 확장자 삭제
    @DeleteMapping("/custom/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteCustomExtension(@PathVariable Long id) {
        extensionService.deleteCustomExtension(id);
        return ResponseEntity.ok("success");
    }
}
