package net.syjoh.extensionblocker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.syjoh.extensionblocker.domain.CustomExtension;

@Data
@AllArgsConstructor
public class CustomExtensionDTO {
    private Long id;
    private String name;

    public static CustomExtensionDTO from(CustomExtension customExtension) {
        return new CustomExtensionDTO(customExtension.getId(), customExtension.getName());
    }
}
