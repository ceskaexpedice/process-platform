package org.ceskaexpedice.processplatform.manager.api.service.process;

import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.util.List;
import java.util.Optional;

/**
 * NextScheduledProcessStrategy
 * @author ppodsednik
 */
public interface NextScheduledProcessStrategy {

    Optional<ProcessEntity> selectNext(String workerId, List<ProcessEntity> plannedProcesses);

}