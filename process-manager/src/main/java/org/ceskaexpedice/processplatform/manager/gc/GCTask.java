package org.ceskaexpedice.processplatform.manager.gc;

import org.ceskaexpedice.processplatform.common.RemoteNodeException;
import org.ceskaexpedice.processplatform.common.model.*;
import org.ceskaexpedice.processplatform.manager.api.service.NodeService;
import org.ceskaexpedice.processplatform.manager.api.service.ProcessService;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GCTask
 * @author ppodsednik
 */
public class GCTask extends TimerTask {

    private static final Logger LOGGER = java.util.logging.Logger.getLogger(GCTask.class.getName());

    private ProcessService processService;
    private NodeService nodeService;
    private GCScheduler gcScheduler;

    public GCTask(ProcessService processService, NodeService nodeService, GCScheduler gcScheduler) {
        this.processService = processService;
        this.gcScheduler = gcScheduler;
        this.nodeService = nodeService;
    }

    @Override
    public void run() {
        try {
            // GC workers
            /*
            List<Node> nodes = nodeService.getNodes();
            for (Node node : nodes) {
                try {
                    processService.getWorkerClient().getWorkerInfo(node);
                } catch (RemoteNodeException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    LOGGER.log(Level.SEVERE, "Deregistering node "+node.getNodeId());
                    nodeService.deregisterNode(node);
                }
            }*/


            // GC processes
            List<ProcessInfo> processInfos = processService.getProcesses(ProcessState.RUNNING);
            for (ProcessInfo processInfo : processInfos) {
                try {
                    if (processInfo.getPid() == null) {
                        LOGGER.warning("Found running process without pid: " + processInfo.getProcessId());
                        processService.updateState(processInfo.getProcessId(), ProcessState.FAILED);
                        continue;
                    }
                    // fetch info from the worker server
                    WorkerInfo workerInfo = processService.getWorkerInfo(processInfo.getProcessId());
                    if (workerInfo != null && workerInfo.getState() == WorkerState.RUNNING && workerInfo.isJvmAlive() &&
                            workerInfo.getCurrentPid() != null && workerInfo.getCurrentPid().equals(processInfo.getPid())) {
                        // ok
                        continue;
                    }
                    LOGGER.severe("Process' worker idle or not running the process: " + processInfo.getProcessId());
                    processService.updateState(processInfo.getProcessId(), ProcessState.FAILED);
                } catch (Exception e) {
                    LOGGER.severe("Cannot access worker for the running process: " + processInfo.getProcessId());
                    processService.updateState(processInfo.getProcessId(), ProcessState.FAILED);
                }
            }



            this.gcScheduler.scheduleDoGc();
        } catch (Throwable e) {
            this.gcScheduler.shutdown();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
