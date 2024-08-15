package net.syjoh.extensionblocker.service;

import lombok.extern.slf4j.Slf4j;
import net.syjoh.extensionblocker.domain.CustomExtension;
import net.syjoh.extensionblocker.domain.FixedExtension;
import net.syjoh.extensionblocker.domain.FixedExtensionType;
import net.syjoh.extensionblocker.dto.CustomExtensionDTO;
import net.syjoh.extensionblocker.dto.FixedExtensionDTO;
import net.syjoh.extensionblocker.exception.CustomException;
import net.syjoh.extensionblocker.exception.ErrorCode;
import net.syjoh.extensionblocker.repository.CustomExtensionRepository;
import net.syjoh.extensionblocker.repository.FixedExtensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExtensionService {
    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;
    private final Map<FixedExtensionType, Boolean> fixedExtensionCache;

    @Autowired
    public ExtensionService(FixedExtensionRepository fixedExtensionRepository, CustomExtensionRepository customExtensionRepository) {
        this.fixedExtensionRepository = fixedExtensionRepository;
        this.customExtensionRepository = customExtensionRepository;
        this.fixedExtensionCache = new ConcurrentHashMap<>();
        initializeFixedExtensions();
    }

    //고정 확장자 초기화(메모리에 미리 로드하여 반복적인 DB 조회 줄임)
    private void initializeFixedExtensions() {
        for (FixedExtensionType type : FixedExtensionType.values()) {
            FixedExtension extension = fixedExtensionRepository.findById(type)
                    .orElseGet(() -> {
                        FixedExtension newExtension = FixedExtension.of(type, false);
                        return fixedExtensionRepository.save(newExtension);
                    });
            fixedExtensionCache.put(type, extension.isBlocked());
        }
    }
    // 고정 확장자 수정 (map만 업데이트)
    @Transactional
    public void updateFixedExtensionStatus(FixedExtensionType type, boolean isBlocked) {
        fixedExtensionCache.put(type, isBlocked);
    }
    // 캐시에서 고정 확장자 조회
    public List<FixedExtensionDTO> getFixedExtensions() {
        return fixedExtensionCache.entrySet().stream()
                .map(entry -> new FixedExtensionDTO(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toList());
    }
    // 주기적으로 캐시의 내용을 DB에 동기화 (50분마다 실행 fixedRate = 3000000)
    @Scheduled(fixedRate = 300000) //지금은 5분
    @Transactional
    public void syncFixedExtensionsToDb() {
        fixedExtensionCache.forEach((type, isBlocked) -> {
            FixedExtension extension = fixedExtensionRepository.findById(type)
                    .orElseGet(() -> FixedExtension.of(type, false));
            extension = extension.updateBlockedStatus(isBlocked);
            fixedExtensionRepository.save(extension);
        });
    }

    //커스텀 확장자 목록 조회
    public List<CustomExtensionDTO> getCustomExtensions() {
        return customExtensionRepository.findAll().stream()
                .map(CustomExtensionDTO::from)
                .collect(Collectors.toList());
    }

    //커스텀 확장자 등록
    @Transactional
    public CustomExtensionDTO addCustomExtension(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        name = name.trim();
        if (name.length() > 20) {
            throw new CustomException(ErrorCode.MAX_INPUT_REACHED);
        }
        if (customExtensionRepository.count() >= 200) {
            throw new CustomException(ErrorCode.MAX_CUSTOM_EXTENSIONS_REACHED);
        }
        CustomExtension customExtension = CustomExtension.builder().name(name).build();
        customExtensionRepository.save(customExtension);
        return CustomExtensionDTO.from(customExtension);
    }

    //커스텀 확장자 삭제
    @Transactional
    public void deleteCustomExtension(Long id) {
        if (!customExtensionRepository.existsById(id)) {
            throw new CustomException(ErrorCode.EXTENSION_NOT_FOUND);
        }
        customExtensionRepository.deleteById(id);
    }

}
