package net.syjoh.extensionblocker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.syjoh.extensionblocker.dto.FixedExtensionDTO;
import net.syjoh.extensionblocker.service.ExtensionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    //커스텀 확장자 등록

    //커스텀 확장자 삭제
}
