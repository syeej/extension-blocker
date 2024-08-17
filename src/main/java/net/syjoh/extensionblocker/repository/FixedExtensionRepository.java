package net.syjoh.extensionblocker.repository;

import net.syjoh.extensionblocker.domain.FixedExtension;
import net.syjoh.extensionblocker.domain.FixedExtensionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedExtensionRepository extends JpaRepository<FixedExtension, FixedExtensionType> {

}
