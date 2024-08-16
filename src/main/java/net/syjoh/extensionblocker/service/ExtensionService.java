package net.syjoh.extensionblocker.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtensionService {
    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String FIXED_EXTENSION_KEY = "fixedExtensions";

    @PostConstruct
    private void initializeFixedExtensions() {
        for (FixedExtensionType type : FixedExtensionType.values()) {
            FixedExtension extension = fixedExtensionRepository.findById(type)
                    .orElseThrow(() -> new IllegalStateException("Not Found FixedExtension " + type));
            redisTemplate.opsForHash().put(FIXED_EXTENSION_KEY, type.toString(), String.valueOf(extension.isBlocked()));
        }
    }
    // 고정 확장자 변경
    @Transactional
    public void updateFixedExtensionStatus(FixedExtensionType type, boolean isBlocked) {
        redisTemplate.opsForHash().put(FIXED_EXTENSION_KEY, type.toString(), String.valueOf(isBlocked));
    }

    // 캐시에서 고정 확장자 조회
    public List<FixedExtensionDTO> getFixedExtensions() {
        Map<Object, Object> fixedExtensions = redisTemplate.opsForHash().entries(FIXED_EXTENSION_KEY);
        return fixedExtensions.entrySet().stream()
                .map(entry -> new FixedExtensionDTO(entry.getKey().toString(), Boolean.parseBoolean(entry.getValue().toString())))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 동기화
    @Transactional
    public void syncRedisToDb() {
        Map<Object, Object> fixedExtensions = redisTemplate.opsForHash().entries(FIXED_EXTENSION_KEY);
        fixedExtensions.forEach((key, value) -> {
            FixedExtensionType type = FixedExtensionType.valueOf(key.toString());
            boolean isBlocked = Boolean.parseBoolean(value.toString());
            FixedExtension extension = fixedExtensionRepository.findById(type)
                    .orElseThrow(() -> new IllegalStateException("Not Found FixedExtension " + type));
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
        checkExtensionExistence(name);
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
    //확장자 중복 여부
    private void checkExtensionExistence(String name) {
        if (isFixedExtension(name) || customExtensionRepository.existsByName(name)) {
            throw new CustomException(ErrorCode.EXTENSION_ALREADY_EXISTS);
        }
    }
    private boolean isFixedExtension(String name) {
        for (FixedExtensionType type : FixedExtensionType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
