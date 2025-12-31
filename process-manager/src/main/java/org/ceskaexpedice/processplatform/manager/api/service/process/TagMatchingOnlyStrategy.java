package org.ceskaexpedice.processplatform.manager.api.service.process;

import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.entity.ProcessEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TagMatchingOnlyStrategy
 * @author ppodsednik
 */
public class TagMatchingOnlyStrategy implements NextScheduledProcessStrategy {

    private final NodeDao nodeDao;

    public TagMatchingOnlyStrategy(NodeDao nodeDao) {
        this.nodeDao = nodeDao;
    }

    @Override
    public Optional<ProcessEntity> selectNext(String workerId, List<ProcessEntity> plannedProcesses) {
        Set<String> tags = nodeDao.getNode(workerId).getTags();
        for (ProcessEntity process : plannedProcesses) {
            if (tags.contains(process.getProfileId())) {
                return Optional.of(process);
            }
        }
        return Optional.empty();
    }

}