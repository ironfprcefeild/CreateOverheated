package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface IChamberAdditionBlockEntity {
    
    ChamberAdditionType getAdditionType();

}
