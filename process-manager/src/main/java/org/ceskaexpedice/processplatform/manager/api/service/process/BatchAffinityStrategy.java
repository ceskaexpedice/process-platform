package org.ceskaexpedice.processplatform.manager.api.service.process;

import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProcessDao;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * BatchAffinityStrategy
 *
 * @author ppodsednik
 */
public class BatchAffinityStrategy implements NextScheduledProcessStrategy {

    private final NodeDao nodeDao;
    private final ProcessDao processDao;

    public BatchAffinityStrategy(NodeDao nodeDao, ProcessDao processDao) {
        this.nodeDao = nodeDao;
        this.processDao = processDao;
    }

    @Override
    public Optional<ProcessEntity> selectNext(String workerId, List<ProcessEntity> plannedProcesses) {
        if (plannedProcesses.isEmpty()) {
            return Optional.empty();
        }
        Set<String> workerTags = nodeDao.getNode(workerId).getTags();
        ProcessEntity fallback = null;
        for (ProcessEntity process : plannedProcesses) {
            // 1. HARD FILTER: worker must support this process
            if (!workerTags.contains(process.getProfileId())) {
                continue;
            }
            // Remember earliest suitable process as fallback
            if (fallback == null) {
                fallback = process;
            }
            // 2. PRIORITY RULE: batch affinity
            String batchId = process.getBatchId();
            if (batchId == null) {
                continue;
            }
            if (processDao.existsAnotherProcessInBatch(batchId, process.getProcessId())) {
                return Optional.of(process);
            }
        }
        // 3. Fallback: earliest planned suitable process
        return Optional.ofNullable(fallback);
    }
}