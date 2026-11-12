package org.ceskaexpedice.processplatform.manager.api.service.process;

import org.ceskaexpedice.processplatform.manager.db.dao.NodeDao;
import org.ceskaexpedice.processplatform.manager.db.dao.ProcessDao;
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
    private final ProcessDao processDao;

    public TagMatchingOnlyStrategy(NodeDao nodeDao, ProcessDao processDao) {
        this.nodeDao = nodeDao;
        this.processDao = processDao;
    }

    @Override
    public Optional<ProcessEntity> selectNext(String workerId) {
        Set<String> tags = nodeDao.getNode(workerId).getTags();
        return processDao.findNextByTags(tags);
    }

}