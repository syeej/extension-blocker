package net.syjoh.extensionblocker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.syjoh.extensionblocker.domain.FixedExtension;

@Data @Builder
@AllArgsConstructor
public class FixedExtensionDTO {
    private String name;
    private boolean isBlocked;

    public static FixedExtensionDTO from(FixedExtension fixedExtension) {
        return new FixedExtensionDTO(fixedExtension.getName().toString(), fixedExtension.isBlocked());
    }
}
