package net.syjoh.extensionblocker.repository;

import net.syjoh.extensionblocker.domain.CustomExtension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {
    boolean existsByName(String name);
}
