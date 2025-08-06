package org.ceskaexpedice.processplatform.common.model;

import java.time.LocalDateTime;

// TODO batch
public class BatchFilter {
    public String owner;
    public LocalDateTime from;
    public LocalDateTime until;
    public Integer stateCode;

    public boolean isEmpty() {
        return owner == null &&
                from == null &&
                until == null &&
                stateCode == null
                ;
    }

}
